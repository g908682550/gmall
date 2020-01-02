package com.npu.gmall.oms.entity;

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
 * 订单中所包含的商品
 * </p>
 *
 * @author Lfy
 * @since 2020-01-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("oms_order_item")
@ApiModel(value="OrderItem对象", description="订单中所包含的商品")
public class OrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "订单id")
    @TableField("order_id")
    private Long orderId;

    @ApiModelProperty(value = "订单编号")
    @TableField("order_sn")
    private String orderSn;

    @TableField("product_id")
    private Long productId;

    @TableField("product_pic")
    private String productPic;

    @TableField("product_name")
    private String productName;

    @TableField("product_brand")
    private String productBrand;

    @TableField("product_sn")
    private String productSn;

    @ApiModelProperty(value = "销售价格")
    @TableField("product_price")
    private BigDecimal productPrice;

    @ApiModelProperty(value = "购买数量")
    @TableField("product_quantity")
    private Integer productQuantity;

    @ApiModelProperty(value = "商品sku编号")
    @TableField("product_sku_id")
    private Long productSkuId;

    @ApiModelProperty(value = "商品sku条码")
    @TableField("product_sku_code")
    private String productSkuCode;

    @ApiModelProperty(value = "商品分类id")
    @TableField("product_category_id")
    private Long productCategoryId;

    @ApiModelProperty(value = "商品的销售属性")
    @TableField("sp1")
    private String sp1;

    @TableField("sp2")
    private String sp2;

    @TableField("sp3")
    private String sp3;

    @ApiModelProperty(value = "商品促销名称")
    @TableField("promotion_name")
    private String promotionName;

    @ApiModelProperty(value = "商品促销分解金额")
    @TableField("promotion_amount")
    private BigDecimal promotionAmount;

    @ApiModelProperty(value = "优惠券优惠分解金额")
    @TableField("coupon_amount")
    private BigDecimal couponAmount;

    @ApiModelProperty(value = "积分优惠分解金额")
    @TableField("integration_amount")
    private BigDecimal integrationAmount;

    @ApiModelProperty(value = "该商品经过优惠后的分解金额")
    @TableField("real_amount")
    private BigDecimal realAmount;

    @TableField("gift_integration")
    private Integer giftIntegration;

    @TableField("gift_growth")
    private Integer giftGrowth;

    //@ApiModelProperty(value = "商品销售属性:[{"key":"颜色","value":"颜色"},{"key":"容量","value":"4G"}]")
    @TableField("product_attr")
    private String productAttr;


}
