package com.npu.gmall.vo.product;


import lombok.Data;

import java.io.Serializable;

/**
 * 商品分类对应属性信息
 */
@Data
public class ProductAttrInfo implements Serializable {
    private Long attributeId;
    private Long attributeCategoryId;

}
