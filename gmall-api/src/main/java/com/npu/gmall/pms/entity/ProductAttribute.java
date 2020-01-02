package com.npu.gmall.pms.entity;

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
 * 商品属性参数表
 * </p>
 *
 * @author Lfy
 * @since 2020-01-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("pms_product_attribute")
@ApiModel(value="ProductAttribute对象", description="商品属性参数表")
public class ProductAttribute implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("product_attribute_category_id")
    private Long productAttributeCategoryId;

    @TableField("name")
    private String name;

    @ApiModelProperty(value = "属性选择类型：0->唯一；1->单选；2->多选")
    @TableField("select_type")
    private Integer selectType;

    @ApiModelProperty(value = "属性录入方式：0->手工录入；1->从列表中选取")
    @TableField("input_type")
    private Integer inputType;

    @ApiModelProperty(value = "可选值列表，以逗号隔开")
    @TableField("input_list")
    private String inputList;

    @ApiModelProperty(value = "排序字段：最高的可以单独上传图片")
    @TableField("sort")
    private Integer sort;

    @ApiModelProperty(value = "分类筛选样式：1->普通；1->颜色")
    @TableField("filter_type")
    private Integer filterType;

    @ApiModelProperty(value = "检索类型；0->不需要进行检索；1->关键字检索；2->范围检索")
    @TableField("search_type")
    private Integer searchType;

    @ApiModelProperty(value = "相同属性产品是否关联；0->不关联；1->关联")
    @TableField("related_status")
    private Integer relatedStatus;

    @ApiModelProperty(value = "是否支持手动新增；0->不支持；1->支持")
    @TableField("hand_add_status")
    private Integer handAddStatus;

    @ApiModelProperty(value = "属性的类型；0->规格；1->参数")
    @TableField("type")
    private Integer type;


}
