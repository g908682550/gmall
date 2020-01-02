package com.npu.gmall.sms.entity;

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
 * 优惠券使用、领取历史表
 * </p>
 *
 * @author Lfy
 * @since 2020-01-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sms_coupon_history")
@ApiModel(value="CouponHistory对象", description="优惠券使用、领取历史表")
public class CouponHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("coupon_id")
    private Long couponId;

    @TableField("member_id")
    private Long memberId;

    @TableField("coupon_code")
    private String couponCode;

    @ApiModelProperty(value = "领取人昵称")
    @TableField("member_nickname")
    private String memberNickname;

    @ApiModelProperty(value = "获取类型：0->后台赠送；1->主动获取")
    @TableField("get_type")
    private Integer getType;

    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty(value = "使用状态：0->未使用；1->已使用；2->已过期")
    @TableField("use_status")
    private Integer useStatus;

    @ApiModelProperty(value = "使用时间")
    @TableField("use_time")
    private Date useTime;

    @ApiModelProperty(value = "订单编号")
    @TableField("order_id")
    private Long orderId;

    @ApiModelProperty(value = "订单号码")
    @TableField("order_sn")
    private String orderSn;


}
