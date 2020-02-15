package com.npu.gmall.portal.controller.oms;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.fastjson.JSON;
import com.npu.gmall.constant.SysCacheConstant;
import com.npu.gmall.oms.service.OrderService;
import com.npu.gmall.to.CommonResult;
import com.npu.gmall.ums.entity.Member;
import com.npu.gmall.vo.order.OrderConfirmVo;
import com.npu.gmall.vo.order.OrderCreateVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Api(tags = "订单服务")
@RequestMapping("/order")
@RestController
@Slf4j
public class OrderController {


    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Reference
    OrderService orderService;


    /**
     * 当信息确认完成以后下一步要提交订单，必须做防重复验证，即接口的幂等性设计
     * 1）利用防重的令牌机制
     * 接口幂等性设计：
     *      select：
     *      insert/delete/update:需要幂等性设计
     * 2）数据库层面怎么幂等？可用数据库的锁机制保证在数据库层面多次请求幂等
     *      insert();如果id不是自增，传入id
     *      delete();在数据库如果带id删除，幂等操作
     *      update();乐观锁 且带版本
     * 3）业务方面：
     *      分布式锁+令牌防重
     *      分布式锁防并发下单
     *
     * @param accessToken
     * @return
     */
    @ApiOperation("订单确认")
    @GetMapping("/confirm")
    public CommonResult confirmOrder(@RequestParam("accessToken") String accessToken){

        //检查用户是否存在
        String memberJson = stringRedisTemplate.opsForValue().get(SysCacheConstant.LOGIN_MEMBER + accessToken);

        if(StringUtils.isEmpty(accessToken)||StringUtils.isEmpty(memberJson)){
            //用户未登录
            CommonResult commonResult = new CommonResult();
            commonResult.setMessage("用户未登录，请先登录");
            return commonResult.failed();
        }
        Member member = JSON.parseObject(memberJson, Member.class);

        /**
         * 返回如下json数据
         * 1、当前用户的可选地址列表
         * 2、当前购物车选中的商品信息
         * 3、可用的优惠券信息
         * 4、支付方式、配送方式、发票信息
         */
        //采用dubbo的隐式传参,将accessToken传递给远程服务
        RpcContext.getContext().setAttachment("accessToken",accessToken);
        OrderConfirmVo confirmVo = orderService.orderConfirm(member.getId());
        return new CommonResult().success(confirmVo);
    }

    /**
     * 创建订单的时候必须用到确认订单的那些数据
     * @param totalPrice 为了比价
     * @param accessToken
     * @return
     */
    @ApiOperation("下单")
    @PostMapping("/create")
    public CommonResult createOrder(@RequestParam("totalPrice") BigDecimal totalPrice,@RequestParam("accessToken") String accessToken,
                                    @RequestParam("addressId") Long addressId,
                                    @RequestParam(value = "notes",required = false) String notes,
                                    @RequestParam(value="orderToken") String orderToken){
        RpcContext.getContext().setAttachment("accessToken",accessToken);
        RpcContext.getContext().setAttachment("orderToken",orderToken);
        //1、创建订单要生成订单和订单项（购物车中的商品）
        //防重复提交
        OrderCreateVo orderCreateVo=orderService.createOrder(totalPrice,addressId,notes);

        if(!StringUtils.isEmpty(orderCreateVo.getToken())){
            CommonResult failed = new CommonResult().failed();
            failed.setMessage(orderCreateVo.getToken());
            return failed;
        }
        return new CommonResult().success(orderCreateVo);
    }

}
