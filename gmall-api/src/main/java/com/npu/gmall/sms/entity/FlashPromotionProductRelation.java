package com.npu.gmall.sms.entity;

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
 * 商品限时购与商品关系表
 * </p>
 *
 * @author Lfy
 * @since 2020-01-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sms_flash_promotion_product_relation")
@ApiModel(value="FlashPromotionProductRelation对象", description="商品限时购与商品关系表")
public class FlashPromotionProductRelation implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("flash_promotion_id")
    private Long flashPromotionId;

    @ApiModelProperty(value = "编号")
    @TableField("flash_promotion_session_id")
    private Long flashPromotionSessionId;

    @TableField("product_id")
    private Long productId;

    @ApiModelProperty(value = "限时购价格")
    @TableField("flash_promotion_price")
    private BigDecimal flashPromotionPrice;

    @ApiModelProperty(value = "限时购数量")
    @TableField("flash_promotion_count")
    private Integer flashPromotionCount;

    @ApiModelProperty(value = "每人限购数量")
    @TableField("flash_promotion_limit")
    private Integer flashPromotionLimit;

    @ApiModelProperty(value = "排序")
    @TableField("sort")
    private Integer sort;


}
