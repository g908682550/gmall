package com.npu.gmall.vo.cart;

import lombok.Data;

@Data
public class UserCartKey {

    private Boolean login;//用户是否登录

    private Long userId;//用户如果登录的ID

    private String tempCartKey;//用户没有登录而且没有临时购物车的key

    private String finalCartKey;//用户最终用哪个购物车
}
