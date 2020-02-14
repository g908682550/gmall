package com.npu.gmall.portal.controller.pms;

import com.alibaba.dubbo.config.annotation.Reference;
import com.npu.gmall.pms.service.ProductService;
import com.npu.gmall.to.es.EsProduct;
import com.sun.xml.internal.ws.util.CompletedFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@RestController
public class ProductItemController {

    @Reference
    ProductService productService;

    @Qualifier("mainThreadPoolExecutor")
    @Autowired
    ThreadPoolExecutor mainThreadPoolExecutor;

    @Qualifier("otherThreadPoolExecutor")
    @Autowired
    ThreadPoolExecutor otherThreadPoolExecutor;

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


    /**
     * 商品的详情
     * @param id
     * @return
     */
    @GetMapping("/item/{id}.html")
    public EsProduct productInfo(@PathVariable("id") Long id){
        EsProduct esProduct=productService.productAllInfo(id);

        return esProduct;
    }

    @GetMapping("/item/sku/{id}.html")
    public EsProduct productSkuInfo(@PathVariable("id") Long id){
        return productService.productSkuInfo(id);
    }

}
