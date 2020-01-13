package com.npu.gmall.cart.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class CartResponse implements Serializable {

    private Cart cart;//整个购物车

    private CartItem cartItem;//某一项

    private String cartKey;//临时生成的

}
