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
 * 商品信息
 * </p>
 *
 * @author Lfy
 * @since 2020-01-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("pms_product")
@ApiModel(value="Product对象", description="商品信息")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("brand_id")
    private Long brandId;

    @TableField("product_category_id")
    private Long productCategoryId;

    @TableField("feight_template_id")
    private Long feightTemplateId;

    @TableField("product_attribute_category_id")
    private Long productAttributeCategoryId;

    @TableField("name")
    private String name;

    @TableField("pic")
    private String pic;

    @ApiModelProperty(value = "货号")
    @TableField("product_sn")
    private String productSn;

    @ApiModelProperty(value = "删除状态：0->未删除；1->已删除")
    @TableField("delete_status")
    private Integer deleteStatus;

    @ApiModelProperty(value = "上架状态：0->下架；1->上架")
    @TableField("publish_status")
    private Integer publishStatus;

    @ApiModelProperty(value = "新品状态:0->不是新品；1->新品")
    @TableField("new_status")
    private Integer newStatus;

    @ApiModelProperty(value = "推荐状态；0->不推荐；1->推荐")
    @TableField("recommand_status")
    private Integer recommandStatus;

    @ApiModelProperty(value = "审核状态：0->未审核；1->审核通过")
    @TableField("verify_status")
    private Integer verifyStatus;

    @ApiModelProperty(value = "排序")
    @TableField("sort")
    private Integer sort;

    @ApiModelProperty(value = "销量")
    @TableField("sale")
    private Integer sale;

    @TableField("price")
    private BigDecimal price;

    @ApiModelProperty(value = "促销价格")
    @TableField("promotion_price")
    private BigDecimal promotionPrice;

    @ApiModelProperty(value = "赠送的成长值")
    @TableField("gift_growth")
    private Integer giftGrowth;

    @ApiModelProperty(value = "赠送的积分")
    @TableField("gift_point")
    private Integer giftPoint;

    @ApiModelProperty(value = "限制使用的积分数")
    @TableField("use_point_limit")
    private Integer usePointLimit;

    @ApiModelProperty(value = "副标题")
    @TableField("sub_title")
    private String subTitle;

    @ApiModelProperty(value = "商品描述")
    @TableField("description")
    private String description;

    @ApiModelProperty(value = "市场价")
    @TableField("original_price")
    private BigDecimal originalPrice;

    @ApiModelProperty(value = "库存")
    @TableField("stock")
    private Integer stock;

    @ApiModelProperty(value = "库存预警值")
    @TableField("low_stock")
    private Integer lowStock;

    @ApiModelProperty(value = "单位")
    @TableField("unit")
    private String unit;

    @ApiModelProperty(value = "商品重量，默认为克")
    @TableField("weight")
    private BigDecimal weight;

    @ApiModelProperty(value = "是否为预告商品：0->不是；1->是")
    @TableField("preview_status")
    private Integer previewStatus;

    @ApiModelProperty(value = "以逗号分割的产品服务：1->无忧退货；2->快速退款；3->免费包邮")
    @TableField("service_ids")
    private String serviceIds;

    @TableField("keywords")
    private String keywords;

    @TableField("note")
    private String note;

    @ApiModelProperty(value = "画册图片，连产品图片限制为5张，以逗号分割")
    @TableField("album_pics")
    private String albumPics;

    @TableField("detail_title")
    private String detailTitle;

    @TableField("detail_desc")
    private String detailDesc;

    @ApiModelProperty(value = "产品详情网页内容")
    @TableField("detail_html")
    private String detailHtml;

    @ApiModelProperty(value = "移动端网页详情")
    @TableField("detail_mobile_html")
    private String detailMobileHtml;

    @ApiModelProperty(value = "促销开始时间")
    @TableField("promotion_start_time")
    private Date promotionStartTime;

    @ApiModelProperty(value = "促销结束时间")
    @TableField("promotion_end_time")
    private Date promotionEndTime;

    @ApiModelProperty(value = "活动限购数量")
    @TableField("promotion_per_limit")
    private Integer promotionPerLimit;

    @ApiModelProperty(value = "促销类型：0->没有促销使用原价;1->使用促销价；2->使用会员价；3->使用阶梯价格；4->使用满减价格；5->限时购")
    @TableField("promotion_type")
    private Integer promotionType;

    @ApiModelProperty(value = "品牌名称")
    @TableField("brand_name")
    private String brandName;

    @ApiModelProperty(value = "商品分类名称")
    @TableField("product_category_name")
    private String productCategoryName;

}
