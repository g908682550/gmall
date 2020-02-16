package com.npu.gmall.portal.controller.pms;

import com.alibaba.dubbo.config.annotation.Reference;
import com.npu.gmall.constant.ProductInfoConstant;
import com.npu.gmall.constant.SysCacheConstant;
import com.npu.gmall.pms.service.ProductService;
import com.npu.gmall.to.es.EsProduct;
import com.sun.xml.internal.ws.util.CompletedFuture;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@RestController
public class ProductItemController {

    @Reference
    ProductService productService;

    @Autowired
    RedissonClient redisson;

    @Autowired
    RedisTemplate<Object,Object> redisTemplate;

    @Qualifier("mainThreadPoolExecutor")
    @Autowired
    ThreadPoolExecutor mainThreadPoolExecutor;

    @Qualifier("otherThreadPoolExecutor")
    @Autowired
    ThreadPoolExecutor otherThreadPoolExecutor;

    /**
     * 根据产品id查询所有商品的详情
     * @param id
     * @return
     */
    @GetMapping("/item/{id}.html")
    public EsProduct productInfo(@PathVariable("id") Long id){
        EsProduct esProduct=productService.productAllInfo(id);
        return esProduct;
    }

    /**
     * 根据skuId查询单个商品的详情，先去缓存中查，如果缓存中没有，获取分布式锁去es查再放入缓存中，保证es中的数据始终为新值
     * @param id
     * @return
     */
    @GetMapping("/item/sku/{id}.html")
    public EsProduct productSkuInfo(@PathVariable("id") Long id){

        EsProduct esProduct=null;

        /**
         * 先去缓存中查询
         */
        esProduct=(EsProduct)redisTemplate.opsForValue().get(ProductInfoConstant.SKU_PRODUCT_INFO+id);

        /**
         * 如果缓存中不存在（未缓存或者出现缓存击穿问题(大量并发去数据库查同一条数据)，防止大量并发去es/mysql），需要先去获取分布式锁
         */
        if(esProduct==null){
            //获取分布式锁去es中查询，为使得锁粒度更细，锁名skuInfoLock_id
            RLock lock=redisson.getLock("skuInfoLock"+id);
            try{
                lock.lock(3, TimeUnit.SECONDS);
                esProduct=productService.productSkuInfo(id);
                //查到数据后将数据放到缓存中，设置一个默认过期时间
                redisTemplate.opsForValue().set(ProductInfoConstant.SKU_PRODUCT_INFO+id,esProduct,1L,TimeUnit.DAYS);
            }finally {
                lock.unlock();
            }
        }
        return esProduct;
    }

    /**
     * 数据库（商品的基本信息表，商品的属性表，商品的促销表）和es（info/attr/sale)
     *
     * 查加缓存
     * 1、第一次查，肯定长
     * @param id
     */
    public EsProduct productInfo2(Long id){
        //商品基本数据（名字介绍等）
        CompletableFuture.supplyAsync(()->{

            return "";
        },mainThreadPoolExecutor).whenComplete((r,e)->{
            System.out.println("处理结果"+r);
            System.out.println("处理异常"+e);
        });
        //商品属性数据
        //商品的营销数据
        //商品的配送数据
        //商品的增值服务数据
        //开启异步化，取决于最长的服务调用
        //高并发系统的优化 1、加缓存 2、开异步
        return null;
    }

}
