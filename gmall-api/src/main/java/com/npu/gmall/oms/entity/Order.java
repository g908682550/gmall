package com.npu.gmall.oms.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 订单表
 * </p>
 *
 * @author Lfy
 * @since 2020-01-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("oms_order")
@ApiModel(value="Order对象", description="订单表")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "订单id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("member_id")
    private Long memberId;

    @TableField("coupon_id")
    private Long couponId;

    @ApiModelProperty(value = "订单编号")
    @TableField("order_sn")
    private String orderSn;

    @ApiModelProperty(value = "提交时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty(value = "用户帐号")
    @TableField("member_username")
    private String memberUsername;

    @ApiModelProperty(value = "订单总金额")
    @TableField("total_amount")
    private BigDecimal totalAmount;

    @ApiModelProperty(value = "应付金额（实际支付金额）")
    @TableField("pay_amount")
    private BigDecimal payAmount;

    @ApiModelProperty(value = "运费金额")
    @TableField("freight_amount")
    private BigDecimal freightAmount;

    @ApiModelProperty(value = "促销优化金额（促销价、满减、阶梯价）")
    @TableField("promotion_amount")
    private BigDecimal promotionAmount;

    @ApiModelProperty(value = "积分抵扣金额")
    @TableField("integration_amount")
    private BigDecimal integrationAmount;

    @ApiModelProperty(value = "优惠券抵扣金额")
    @TableField("coupon_amount")
    private BigDecimal couponAmount;

    @ApiModelProperty(value = "管理员后台调整订单使用的折扣金额")
    @TableField("discount_amount")
    private BigDecimal discountAmount;

    @ApiModelProperty(value = "支付方式：0->未支付；1->支付宝；2->微信")
    @TableField("pay_type")
    private Integer payType;

    @ApiModelProperty(value = "订单来源：0->PC订单；1->app订单")
    @TableField("source_type")
    private Integer sourceType;

    @ApiModelProperty(value = "订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "订单类型：0->正常订单；1->秒杀订单")
    @TableField("order_type")
    private Integer orderType;

    @ApiModelProperty(value = "物流公司(配送方式)")
    @TableField("delivery_company")
    private String deliveryCompany;

    @ApiModelProperty(value = "物流单号")
    @TableField("delivery_sn")
    private String deliverySn;

    @ApiModelProperty(value = "自动确认时间（天）")
    @TableField("auto_confirm_day")
    private Integer autoConfirmDay;

    @ApiModelProperty(value = "可以获得的积分")
    @TableField("integration")
    private Integer integration;

    @ApiModelProperty(value = "可以活动的成长值")
    @TableField("growth")
    private Integer growth;

    @ApiModelProperty(value = "活动信息")
    @TableField("promotion_info")
    private String promotionInfo;

    @ApiModelProperty(value = "发票类型：0->不开发票；1->电子发票；2->纸质发票")
    @TableField("bill_type")
    private Integer billType;

    @ApiModelProperty(value = "发票抬头")
    @TableField("bill_header")
    private String billHeader;

    @ApiModelProperty(value = "发票内容")
    @TableField("bill_content")
    private String billContent;

    @ApiModelProperty(value = "收票人电话")
    @TableField("bill_receiver_phone")
    private String billReceiverPhone;

    @ApiModelProperty(value = "收票人邮箱")
    @TableField("bill_receiver_email")
    private String billReceiverEmail;

    @ApiModelProperty(value = "收货人姓名")
    @TableField("receiver_name")
    private String receiverName;

    @ApiModelProperty(value = "收货人电话")
    @TableField("receiver_phone")
    private String receiverPhone;

    @ApiModelProperty(value = "收货人邮编")
    @TableField("receiver_post_code")
    private String receiverPostCode;

    @ApiModelProperty(value = "省份/直辖市")
    @TableField("receiver_province")
    private String receiverProvince;

    @ApiModelProperty(value = "城市")
    @TableField("receiver_city")
    private String receiverCity;

    @ApiModelProperty(value = "区")
    @TableField("receiver_region")
    private String receiverRegion;

    @ApiModelProperty(value = "详细地址")
    @TableField("receiver_detail_address")
    private String receiverDetailAddress;

    @ApiModelProperty(value = "订单备注")
    @TableField("note")
    private String note;

    @ApiModelProperty(value = "确认收货状态：0->未确认；1->已确认")
    @TableField("confirm_status")
    private Integer confirmStatus;

    @ApiModelProperty(value = "删除状态：0->未删除；1->已删除")
    @TableField("delete_status")
    private Integer deleteStatus;

    @ApiModelProperty(value = "下单时使用的积分")
    @TableField("use_integration")
    private Integer useIntegration;

    @ApiModelProperty(value = "支付时间")
    @TableField("payment_time")
    private Date paymentTime;

    @ApiModelProperty(value = "发货时间")
    @TableField("delivery_time")
    private Date deliveryTime;

    @ApiModelProperty(value = "确认收货时间")
    @TableField("receive_time")
    private Date receiveTime;

    @ApiModelProperty(value = "评价时间")
    @TableField("comment_time")
    private Date commentTime;

    @ApiModelProperty(value = "修改时间")
    @TableField("modify_time")
    private Date modifyTime;


}
