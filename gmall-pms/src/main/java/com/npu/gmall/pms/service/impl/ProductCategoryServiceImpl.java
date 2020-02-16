package com.npu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.npu.gmall.constant.SysCacheConstant;
import com.npu.gmall.pms.entity.ProductCategory;
import com.npu.gmall.pms.mapper.ProductCategoryMapper;
import com.npu.gmall.pms.service.ProductCategoryService;
import com.npu.gmall.vo.product.PmsProductCategoryWithChildrenItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 产品分类 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2020-01-02
 */
@Slf4j
@Service
@Component
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory> implements ProductCategoryService {

    @Autowired
    ProductCategoryMapper categoryMapper;

    @Autowired
    RedisTemplate<Object,Object> redisTemplate;
    /**
     * 将菜单缓存起来，以后查询直接去缓存中拿即可
     * 分布式缓存用redis来做
     * @param i
     * @return
     */
    @Override
    public List<PmsProductCategoryWithChildrenItem> listCatelogWithChildren(Integer i) {
        //加入缓存逻辑，先查看缓存中是否存在系统菜单值
        Object cacheMenu = redisTemplate.opsForValue().get(SysCacheConstant.CATEGORY_MENU_CACHE_KEY);
        List<PmsProductCategoryWithChildrenItem> items;
        if(cacheMenu==null){
            items = categoryMapper.listCatelogWithChildren(i);
            //放入缓存中,redis;
            redisTemplate.opsForValue().set(SysCacheConstant.CATEGORY_MENU_CACHE_KEY,items);
        }else{
            //缓存中有
            log.debug("菜单数据命中缓存...");
            items=(List<PmsProductCategoryWithChildrenItem>) cacheMenu;
        }
        return items;
    }
}
