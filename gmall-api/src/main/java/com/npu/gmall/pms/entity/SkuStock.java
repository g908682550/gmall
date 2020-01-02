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
 * sku的库存
 * </p>
 *
 * @author Lfy
 * @since 2020-01-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("pms_sku_stock")
@ApiModel(value="SkuStock对象", description="sku的库存")
public class SkuStock implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("product_id")
    private Long productId;

    @ApiModelProperty(value = "sku编码")
    @TableField("sku_code")
    private String skuCode;

    @TableField("price")
    private BigDecimal price;

    @ApiModelProperty(value = "库存")
    @TableField("stock")
    private Integer stock;

    @ApiModelProperty(value = "预警库存")
    @TableField("low_stock")
    private Integer lowStock;

    @ApiModelProperty(value = "销售属性1")
    @TableField("sp1")
    private String sp1;

    @TableField("sp2")
    private String sp2;

    @TableField("sp3")
    private String sp3;

    @ApiModelProperty(value = "展示图片")
    @TableField("pic")
    private String pic;

    @ApiModelProperty(value = "销量")
    @TableField("sale")
    private Integer sale;

    @ApiModelProperty(value = "单品促销价格")
    @TableField("promotion_price")
    private BigDecimal promotionPrice;

    @ApiModelProperty(value = "锁定库存")
    @TableField("lock_stock")
    private Integer lockStock;


}
