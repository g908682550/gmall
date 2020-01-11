package com.npu.gmall.cart.service;


import com.npu.gmall.cart.vo.CartItem;
import com.npu.gmall.cart.vo.CartResponse;

/**
 * 购物车服务
 */
public interface CartService {
    /**
     * 添加商品至购物车
     * @param skuId
     * @param cartKey
     * @param accessToken
     * @return
     */
    CartResponse addToCart(Long skuId, String cartKey, String accessToken);
}
