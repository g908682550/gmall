package com.npu.gmall.ums.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
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
 * 会员积分成长规则表
 * </p>
 *
 * @author Lfy
 * @since 2020-01-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ums_member_rule_setting")
@ApiModel(value="MemberRuleSetting对象", description="会员积分成长规则表")
public class MemberRuleSetting implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "连续签到天数")
    @TableField("continue_sign_day")
    private Integer continueSignDay;

    @ApiModelProperty(value = "连续签到赠送数量")
    @TableField("continue_sign_point")
    private Integer continueSignPoint;

    @ApiModelProperty(value = "每消费多少元获取1个点")
    @TableField("consume_per_point")
    private BigDecimal consumePerPoint;

    @ApiModelProperty(value = "最低获取点数的订单金额")
    @TableField("low_order_amount")
    private BigDecimal lowOrderAmount;

    @ApiModelProperty(value = "每笔订单最高获取点数")
    @TableField("max_point_per_order")
    private Integer maxPointPerOrder;

    @ApiModelProperty(value = "类型：0->积分规则；1->成长值规则")
    @TableField("type")
    private Integer type;


}
