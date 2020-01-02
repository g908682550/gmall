package com.npu.gmall.pms.entity;

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
 * 
 * </p>
 *
 * @author Lfy
 * @since 2020-01-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("pms_product_operate_log")
@ApiModel(value="ProductOperateLog对象", description="")
public class ProductOperateLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("product_id")
    private Long productId;

    @TableField("price_old")
    private BigDecimal priceOld;

    @TableField("price_new")
    private BigDecimal priceNew;

    @TableField("sale_price_old")
    private BigDecimal salePriceOld;

    @TableField("sale_price_new")
    private BigDecimal salePriceNew;

    @ApiModelProperty(value = "赠送的积分")
    @TableField("gift_point_old")
    private Integer giftPointOld;

    @TableField("gift_point_new")
    private Integer giftPointNew;

    @TableField("use_point_limit_old")
    private Integer usePointLimitOld;

    @TableField("use_point_limit_new")
    private Integer usePointLimitNew;

    @ApiModelProperty(value = "操作人")
    @TableField("operate_man")
    private String operateMan;

    @TableField("create_time")
    private Date createTime;


}
