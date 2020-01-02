package com.npu.gmall.sms.entity;

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
 * 优惠券和产品分类关系表
 * </p>
 *
 * @author Lfy
 * @since 2020-01-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sms_coupon_product_category_relation")
@ApiModel(value="CouponProductCategoryRelation对象", description="优惠券和产品分类关系表")
public class CouponProductCategoryRelation implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("coupon_id")
    private Long couponId;

    @TableField("product_category_id")
    private Long productCategoryId;

    @ApiModelProperty(value = "产品分类名称")
    @TableField("product_category_name")
    private String productCategoryName;

    @ApiModelProperty(value = "父分类名称")
    @TableField("parent_category_name")
    private String parentCategoryName;


}
