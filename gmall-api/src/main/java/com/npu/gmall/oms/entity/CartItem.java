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
 * 购物车表
 * </p>
 *
 * @author Lfy
 * @since 2020-01-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("oms_cart_item")
@ApiModel(value="CartItem对象", description="购物车表")
public class CartItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("product_id")
    private Long productId;

    @TableField("product_sku_id")
    private Long productSkuId;

    @TableField("member_id")
    private Long memberId;

    @ApiModelProperty(value = "购买数量")
    @TableField("quantity")
    private Integer quantity;

    @ApiModelProperty(value = "添加到购物车的价格")
    @TableField("price")
    private BigDecimal price;

    @ApiModelProperty(value = "销售属性1")
    @TableField("sp1")
    private String sp1;

    @ApiModelProperty(value = "销售属性2")
    @TableField("sp2")
    private String sp2;

    @ApiModelProperty(value = "销售属性3")
    @TableField("sp3")
    private String sp3;

    @ApiModelProperty(value = "商品主图")
    @TableField("product_pic")
    private String productPic;

    @ApiModelProperty(value = "商品名称")
    @TableField("product_name")
    private String productName;

    @ApiModelProperty(value = "商品副标题（卖点）")
    @TableField("product_sub_title")
    private String productSubTitle;

    @ApiModelProperty(value = "商品sku条码")
    @TableField("product_sku_code")
    private String productSkuCode;

    @ApiModelProperty(value = "会员昵称")
    @TableField("member_nickname")
    private String memberNickname;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_date")
    private Date createDate;

    @ApiModelProperty(value = "修改时间")
    @TableField("modify_date")
    private Date modifyDate;

    @ApiModelProperty(value = "是否删除")
    @TableField("delete_status")
    private Integer deleteStatus;

    @ApiModelProperty(value = "商品分类")
    @TableField("product_category_id")
    private Long productCategoryId;

    @TableField("product_brand")
    private String productBrand;

    @TableField("product_sn")
    private String productSn;

    //@ApiModelProperty(value = "商品销售属性:[{"key":"颜色","value":"颜色"},{"key":"容量","value":"4G"}]")
    @TableField("product_attr")
    private String productAttr;


}
