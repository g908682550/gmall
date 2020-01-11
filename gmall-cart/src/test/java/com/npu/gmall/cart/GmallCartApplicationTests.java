package com.npu.gmall.cart;

import com.alibaba.fastjson.JSON;
import com.npu.gmall.cart.vo.Cart;
import com.npu.gmall.cart.vo.CartItem;
import org.junit.jupiter.api.Test;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Arrays;

@SpringBootTest
class GmallCartApplicationTests {

    @Autowired
    RedissonClient redissonClient;

    @Test
    public void userRedissonMap(){
        RMap<String, String> cart = redissonClient.getMap("cart");
        CartItem cartItem = new CartItem();
        cartItem.setPrice(new BigDecimal("12.98"));
        cartItem.setSkuId(1L);
        cartItem.setCount(1);
        String item = JSON.toJSONString(cartItem);
        cart.put("2",item);
    }

    @Test
    void contextLoads() {
        CartItem cartItem = new CartItem();
        cartItem.setCount(2);
        cartItem.setPrice(new BigDecimal("10.98"));

        CartItem cartItem2 = new CartItem();
        cartItem2.setCount(1);
        cartItem2.setPrice(new BigDecimal("11.38"));

        Cart cart = new Cart();
        cart.setCartItems(Arrays.asList(cartItem,cartItem2));

        System.out.println(cart.getCount());
        System.out.println(cart.getTotalPrice());
    }

}
