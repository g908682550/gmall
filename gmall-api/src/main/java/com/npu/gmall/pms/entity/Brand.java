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
 * 品牌表
 * </p>
 *
 * @author Lfy
 * @since 2020-01-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("pms_brand")
@ApiModel(value="Brand对象", description="品牌表")
public class Brand implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("name")
    private String name;

    @ApiModelProperty(value = "首字母")
    @TableField("first_letter")
    private String firstLetter;

    @TableField("sort")
    private Integer sort;

    @ApiModelProperty(value = "是否为品牌制造商：0->不是；1->是")
    @TableField("factory_status")
    private Integer factoryStatus;

    @TableField("show_status")
    private Integer showStatus;

    @ApiModelProperty(value = "产品数量")
    @TableField("product_count")
    private Integer productCount;

    @ApiModelProperty(value = "产品评论数量")
    @TableField("product_comment_count")
    private Integer productCommentCount;

    @ApiModelProperty(value = "品牌logo")
    @TableField("logo")
    private String logo;

    @ApiModelProperty(value = "专区大图")
    @TableField("big_pic")
    private String bigPic;

    @ApiModelProperty(value = "品牌故事")
    @TableField("brand_story")
    private String brandStory;


}
