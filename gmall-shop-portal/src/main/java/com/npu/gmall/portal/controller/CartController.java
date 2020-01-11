package com.npu.gmall.portal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.npu.gmall.cart.service.CartService;
import com.npu.gmall.cart.vo.CartItem;
import com.npu.gmall.to.CommonResult;
import org.springframework.web.bind.annotation.*;

/**
 * 购物车
 */
@RequestMapping("/cart")
@RestController
public class CartController {

    @Reference
    CartService cartService;

    /**
     * 返回当前添加的购物项的详细信息
     * @param skuId
     * @param cartKey
     * @param accessToken
     * @return
     */
    @ResponseBody
    @PostMapping("/add")
    public CartItem addCart(@RequestParam("skuId") Long skuId,
                          @RequestParam(value = "cartKey",required = false) String cartKey,
                          @RequestParam(value = "accessToken",required = false) String accessToken){
        return null;
    }

}
