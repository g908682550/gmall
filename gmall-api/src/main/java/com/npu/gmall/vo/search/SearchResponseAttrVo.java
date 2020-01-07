package com.npu.gmall.vo.search;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class SearchResponseAttrVo implements Serializable {

    private Long productAttributeId;
    //当前属性值的所有值
    private List<String> value = new ArrayList<>();
    //属性名称
    private String name;//网络制式
}
