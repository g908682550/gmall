package com.npu.gmall.pms.mapper;

import com.npu.gmall.pms.entity.ProductAttribute;
import com.npu.gmall.pms.entity.ProductAttributeValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.npu.gmall.to.es.EsProductAttributeValue;

import java.util.List;

/**
 * <p>
 * 存储产品参数信息的表 Mapper 接口
 * </p>
 *
 * @author Lfy
 * @since 2020-01-02
 */
public interface ProductAttributeValueMapper extends BaseMapper<ProductAttributeValue> {

    List<EsProductAttributeValue> selectProductBaseAttributeAndValue(Long id);

    List<ProductAttribute> selectProductSaleAttrName(Long id);
}
