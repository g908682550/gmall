package com.npu.gmall.vo.order;

import com.npu.gmall.cart.vo.CartItem;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 结算页需要的orderVo数据
 */
@Data
public class OrderCreateVo implements Serializable {
    private String orderSn;//订单号
    private BigDecimal totalPrice;//订单总额

    private Long addressId;//用户的收货地址
    private String detailInfo;//详情描述

    private Long memberId;//订单的会员id
    private List<CartItem> cartItems;

    private Boolean limit;//验价成功才能支付

    private String token;//令牌是否正确
}
