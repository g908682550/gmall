package com.npu.gmall.sms.entity;

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
 * 优惠卷表
 * </p>
 *
 * @author Lfy
 * @since 2020-01-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sms_coupon")
@ApiModel(value="Coupon对象", description="优惠卷表")
public class Coupon implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "优惠卷类型；0->全场赠券；1->会员赠券；2->购物赠券；3->注册赠券")
    @TableField("type")
    private Integer type;

    @TableField("name")
    private String name;

    @ApiModelProperty(value = "使用平台：0->全部；1->移动；2->PC")
    @TableField("platform")
    private Integer platform;

    @ApiModelProperty(value = "数量")
    @TableField("count")
    private Integer count;

    @ApiModelProperty(value = "金额")
    @TableField("amount")
    private BigDecimal amount;

    @ApiModelProperty(value = "每人限领张数")
    @TableField("per_limit")
    private Integer perLimit;

    @ApiModelProperty(value = "使用门槛；0表示无门槛")
    @TableField("min_point")
    private BigDecimal minPoint;

    @TableField("start_time")
    private Date startTime;

    @TableField("end_time")
    private Date endTime;

    @ApiModelProperty(value = "使用类型：0->全场通用；1->指定分类；2->指定商品")
    @TableField("use_type")
    private Integer useType;

    @ApiModelProperty(value = "备注")
    @TableField("note")
    private String note;

    @ApiModelProperty(value = "发行数量")
    @TableField("publish_count")
    private Integer publishCount;

    @ApiModelProperty(value = "已使用数量")
    @TableField("use_count")
    private Integer useCount;

    @ApiModelProperty(value = "领取数量")
    @TableField("receive_count")
    private Integer receiveCount;

    @ApiModelProperty(value = "可以领取的日期")
    @TableField("enable_time")
    private Date enableTime;

    @ApiModelProperty(value = "优惠码")
    @TableField("code")
    private String code;

    @ApiModelProperty(value = "可领取的会员类型：0->无限时")
    @TableField("member_level")
    private Integer memberLevel;


}
