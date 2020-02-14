package com.npu.gmall.vo.cart;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Setter
public class Cart implements Serializable {
    @Getter
    List<CartItem> cartItems;//所有的购物项

    private Integer count;//商品总数
    private BigDecimal totalPrice;//已选中商品的总价

    public Integer getCount(){

        if(cartItems!=null){
            AtomicInteger all=new AtomicInteger(0);

            cartItems.forEach(cartItem -> {
                all.getAndAdd(cartItem.getCount());
            });
            return all.get();
        }else{
            return 0;
        }
    }

    public BigDecimal getTotalPrice(){

        if(cartItems!=null){
            AtomicReference<BigDecimal> allTotal= new AtomicReference<>(new BigDecimal("0"));

            cartItems.forEach(cartItem -> {
                BigDecimal add = allTotal.get().add(cartItem.getTotalPrice());
                allTotal.set(add);
            });
            return allTotal.get();
        }else{
            return new BigDecimal("0");
        }
    }

    //购物车数据返回给前端
}
