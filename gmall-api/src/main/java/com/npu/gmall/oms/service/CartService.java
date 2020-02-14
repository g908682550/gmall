package com.npu.gmall.oms.service;


import com.npu.gmall.vo.cart.CartItem;
import com.npu.gmall.vo.cart.CartResponse;

import java.util.List;

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
    CartResponse addToCart(Long skuId, Integer num, String cartKey, String accessToken);

    /**
     * 更新商品数量
     * @param skuId
     * @param num
     * @param cartKey
     * @param accessToken
     * @return
     */
    CartResponse updateCartItem(Long skuId, Integer num, String cartKey, String accessToken);

    /**
     * 获取购物车的所有数据
     * @param cartKey
     * @param accessToken
     * @return
     */
    CartResponse listCart(String cartKey, String accessToken);

    /**
     * 移除某项
     * @param skuId
     * @param cartKey
     * @param accessToken
     * @return
     */
    CartResponse delCartItem(Long skuId, String cartKey, String accessToken);

    /**
     * 清空购物车
     * @param cartKey
     * @param accessToken
     * @return
     */
    CartResponse clearCart(String cartKey, String accessToken);

    /**
     * 选中还是不选中某些商品
     * @param skuIds
     * @param ops
     * @param cartKey
     * @param accessToken
     * @return
     */
    CartResponse checkCartItems(String skuIds, Integer ops, String cartKey, String accessToken);

    /**
     * 获取某个用户的购物车中选中的商品
     * @param accessToken
     * @return
     */
    List<CartItem> getCartItemForOrder(String accessToken);

    /**
     * 删除购物车中已经下单的商品
     * @param accessToken
     * @param skuIds
     */
    void removeCartItem(String accessToken, List<Long> skuIds);
}
