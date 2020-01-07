package com.npu.gmall.to.es;

import lombok.Data;

import java.io.Serializable;

@Data
public class EsProductAttributeValue implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private Long productAttributeId;
    //属性值
    private String value;//3G
    //属性参数：0->规格；1->参数
    private Integer type;//规格，销售属性；参数，筛选参数
   // private Integer searchType;
    //属性名称
    private String name;//网络制式

    private Long productId;

}
