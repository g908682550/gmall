package com.npu.gmall.pms.service;

import com.npu.gmall.pms.entity.ProductCategory;
import com.baomidou.mybatisplus.extension.service.IService;
import com.npu.gmall.vo.product.PmsProductCategoryWithChildrenItem;

import java.util.List;

/**
 * <p>
 * 产品分类 服务类
 * </p>
 *
 * @author Lfy
 * @since 2020-01-02
 */
public interface ProductCategoryService extends IService<ProductCategory> {

    /**
     * 查询这个菜单以及它的子菜单
     * @param i
     * @return
     */
    List<PmsProductCategoryWithChildrenItem> listCatelogWithChildren(Integer i);
}
