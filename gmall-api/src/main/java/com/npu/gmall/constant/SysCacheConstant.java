package com.npu.gmall.constant;

/**
 * 系统中使用的常量
 */
public class SysCacheConstant {

    //系统菜单
    public static final String CATEGORY_MENU_CACHE_KEY="sys_category";
    //登录的用户 login:member:token={userObject}
    public static final String LOGIN_MEMBER="login:member:";

    public static final Long LOGIN_MEMBER_TIMEOUT=30L;

    //订单的唯一检查令牌
    public static final String ORDER_UNIQUE_TOKEN="order:unique:token";

}
