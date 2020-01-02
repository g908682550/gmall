package com.npu.gmall.ums.entity;

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
 * 会员等级表
 * </p>
 *
 * @author Lfy
 * @since 2020-01-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ums_member_level")
@ApiModel(value="MemberLevel对象", description="会员等级表")
public class MemberLevel implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("name")
    private String name;

    @TableField("growth_point")
    private Integer growthPoint;

    @ApiModelProperty(value = "是否为默认等级：0->不是；1->是")
    @TableField("default_status")
    private Integer defaultStatus;

    @ApiModelProperty(value = "免运费标准")
    @TableField("free_freight_point")
    private BigDecimal freeFreightPoint;

    @ApiModelProperty(value = "每次评价获取的成长值")
    @TableField("comment_growth_point")
    private Integer commentGrowthPoint;

    @ApiModelProperty(value = "是否有免邮特权")
    @TableField("priviledge_free_freight")
    private Integer priviledgeFreeFreight;

    @ApiModelProperty(value = "是否有签到特权")
    @TableField("priviledge_sign_in")
    private Integer priviledgeSignIn;

    @ApiModelProperty(value = "是否有评论获奖励特权")
    @TableField("priviledge_comment")
    private Integer priviledgeComment;

    @ApiModelProperty(value = "是否有专享活动特权")
    @TableField("priviledge_promotion")
    private Integer priviledgePromotion;

    @ApiModelProperty(value = "是否有会员价格特权")
    @TableField("priviledge_member_price")
    private Integer priviledgeMemberPrice;

    @ApiModelProperty(value = "是否有生日特权")
    @TableField("priviledge_birthday")
    private Integer priviledgeBirthday;

    @TableField("note")
    private String note;


}
