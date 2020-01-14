package com.npu.gmall.vo.order;

import com.npu.gmall.cart.vo.CartItem;
import com.npu.gmall.sms.entity.Coupon;
import com.npu.gmall.ums.entity.MemberReceiveAddress;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderConfirmVo implements Serializable {
    List<CartItem> cartItems;//购物项
    List<MemberReceiveAddress> addresses;//地址列表
    List<Coupon> coupons;//优惠券信息
    private String orderToken;//订单令牌，提交订单需带上

    private BigDecimal productTotalPrice=new BigDecimal("0");//商品总额

    private BigDecimal totalPrice=new BigDecimal("10");//订单总额

    private Integer count=0;//商品总数

    private BigDecimal couponPrice=new BigDecimal("10");//优惠券减免

    private BigDecimal transPrice=new BigDecimal("10");//运费

    //其它支付方式、配送方式
}
