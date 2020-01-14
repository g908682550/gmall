package com.npu.gmall.pms.service;

import com.npu.gmall.pms.entity.Product;
import com.baomidou.mybatisplus.extension.service.IService;
import com.npu.gmall.pms.entity.SkuStock;
import com.npu.gmall.to.es.EsProduct;
import com.npu.gmall.vo.PageInfoVo;
import com.npu.gmall.vo.product.PmsProductParam;
import com.npu.gmall.vo.product.PmsProductQueryParam;

import java.util.List;

/**
 * <p>
 * 商品信息 服务类
 * </p>
 *
 * @author Lfy
 * @since 2020-01-02
 */
public interface ProductService extends IService<Product> {

    /**
     * 查询商品详情
     * @param id
     * @return
     */
    Product productInfo(Long id);

    /**
     * 根据复杂查询条件返回分页数据
     * @param productQueryParam
     * @return
     */
    PageInfoVo productPageInfo(PmsProductQueryParam productQueryParam);

    /**
     * 保存商品数据
     * @param productParam
     */
    void saveProduct(PmsProductParam productParam);

    /**
     * 批量上下架
     * @param ids
     * @param publishStatus
     */
    void updatePublishStatus(List<Long> ids, Integer publishStatus);

    EsProduct productAllInfo(Long id);

    EsProduct productSkuInfo(Long id);

}
