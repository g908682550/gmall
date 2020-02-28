package com.npu.gmall.vo.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkuStockInfo implements Serializable {

    /**
     * 订单模块发送给商品模块减库存的对象
     */
    Long skuId;
    Integer num;
    String orderSn;
}
