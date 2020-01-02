package com.npu.gmall.ums.entity;

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
 * 积分消费设置
 * </p>
 *
 * @author Lfy
 * @since 2020-01-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ums_integration_consume_setting")
@ApiModel(value="IntegrationConsumeSetting对象", description="积分消费设置")
public class IntegrationConsumeSetting implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "每一元需要抵扣的积分数量")
    @TableField("deduction_per_amount")
    private Integer deductionPerAmount;

    @ApiModelProperty(value = "每笔订单最高抵用百分比")
    @TableField("max_percent_per_order")
    private Integer maxPercentPerOrder;

    @ApiModelProperty(value = "每次使用积分最小单位100")
    @TableField("use_unit")
    private Integer useUnit;

    @ApiModelProperty(value = "是否可以和优惠券同用；0->不可以；1->可以")
    @TableField("coupon_status")
    private Integer couponStatus;


}
