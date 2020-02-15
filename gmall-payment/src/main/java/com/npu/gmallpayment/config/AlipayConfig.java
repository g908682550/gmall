package com.npu.gmallpayment.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AlipayConfig implements InitializingBean {

    public static String app_id;
    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key ;
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key;
    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url;
    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //支付成功以后跳到哪里 http://tfkv0cljsb.51http.tech/
    public static String return_url;
    // 签名方式
    public static String sign_type;
    // 字符编码格式
    public static String charset;

    // 支付宝网关  https://openapi.alipaydev.com/gateway.do 这是正式地址
    public static String gatewayUrl;


    @Value("${alipay.app_id}")
    public  String appid;
    @Value("${alipay.merchant_private_key}")
    public  String merchantprivatekey ;
    @Value("${alipay.alipay_public_key}")
    public  String alipaypublickey;
    @Value("${alipay.notify_url}")
    public  String notifyurl;
    @Value("${alipay.return_url}")
    public  String returnurl;
    @Value("${alipay.sign_type}")
    public  String signtype;
    @Value("${alipay.charset}")
    public  String charset2;
    @Value("${alipay.gatewayUrl}")
    public  String gatewayUrl2;


    //Spring给这个bean的所有属性设置好值以后
    @Override
    public void afterPropertiesSet() throws Exception {
        // 为所有的静态属性赋值为Spring获取到的值
        app_id = appid;
        merchant_private_key = merchantprivatekey;
        alipay_public_key = alipaypublickey;
        notify_url = notifyurl;
        return_url = returnurl;
        sign_type = signtype;
        charset = charset2;
        gatewayUrl = gatewayUrl2;
    }

}
