package com.npu.gmall.to.es;

import com.npu.gmall.pms.entity.SkuStock;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class EsSkuProductInfo extends SkuStock implements Serializable {

    //sku的特定标题
    private String skuTitle;

    /**
     * 每个sku不同的属性以及它的值
     *
     * 颜色：黑色
     * 内存：128
     */
    List<EsProductAttributeValue> attributeValues;

}
