package com.npu.gmall.pms.entity;

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
 * 运费模版
 * </p>
 *
 * @author Lfy
 * @since 2020-01-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("pms_feight_template")
@ApiModel(value="FeightTemplate对象", description="运费模版")
public class FeightTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("name")
    private String name;

    @ApiModelProperty(value = "计费类型:0->按重量；1->按件数")
    @TableField("charge_type")
    private Integer chargeType;

    @ApiModelProperty(value = "首重kg")
    @TableField("first_weight")
    private BigDecimal firstWeight;

    @ApiModelProperty(value = "首费（元）")
    @TableField("first_fee")
    private BigDecimal firstFee;

    @TableField("continue_weight")
    private BigDecimal continueWeight;

    @TableField("continme_fee")
    private BigDecimal continmeFee;

    @ApiModelProperty(value = "目的地（省、市）")
    @TableField("dest")
    private String dest;


}
