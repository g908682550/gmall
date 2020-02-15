package com.npu.gmall.portal.controller.payment;


import com.alibaba.dubbo.config.annotation.Reference;
import com.npu.gmall.oms.service.OrderService;
import com.npu.gmall.payment.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@RestController
@Slf4j
public class PaymentController {

    @Reference
    PaymentService PaymentService;


    /**
     * 去支付，支付宝支付
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/ali/pay",produces = {"text/html"})
    public String pay(@RequestParam("orderSn") String orderSn,
                      @RequestParam("accessToken") String accessToken){
        String string=PaymentService.pay(orderSn,accessToken);
        return string;
    }

    /**
     * 接受支付宝的异步通知
     */
    @ResponseBody
    @RequestMapping("/ali/pay/success/async")
    public String paySuccess(HttpServletRequest request) throws UnsupportedEncodingException {

        log.debug("支付宝支付异步通知进来了....");
        // 修改订单的状态
        // 支付宝收到了success说明处理完成，不会再通知

        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            // 乱码解决，这段代码在出现乱码时使用
            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        String result=PaymentService.resolvePayResult(params);
        return result;
    }
}
