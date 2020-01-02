package com.npu.gmall.ums.entity;

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
 * 会员统计信息
 * </p>
 *
 * @author Lfy
 * @since 2020-01-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ums_member_statistics_info")
@ApiModel(value="MemberStatisticsInfo对象", description="会员统计信息")
public class MemberStatisticsInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("member_id")
    private Long memberId;

    @ApiModelProperty(value = "累计消费金额")
    @TableField("consume_amount")
    private BigDecimal consumeAmount;

    @ApiModelProperty(value = "订单数量")
    @TableField("order_count")
    private Integer orderCount;

    @ApiModelProperty(value = "优惠券数量")
    @TableField("coupon_count")
    private Integer couponCount;

    @ApiModelProperty(value = "评价数")
    @TableField("comment_count")
    private Integer commentCount;

    @ApiModelProperty(value = "退货数量")
    @TableField("return_order_count")
    private Integer returnOrderCount;

    @ApiModelProperty(value = "登录次数")
    @TableField("login_count")
    private Integer loginCount;

    @ApiModelProperty(value = "关注数量")
    @TableField("attend_count")
    private Integer attendCount;

    @ApiModelProperty(value = "粉丝数量")
    @TableField("fans_count")
    private Integer fansCount;

    @TableField("collect_product_count")
    private Integer collectProductCount;

    @TableField("collect_subject_count")
    private Integer collectSubjectCount;

    @TableField("collect_topic_count")
    private Integer collectTopicCount;

    @TableField("collect_comment_count")
    private Integer collectCommentCount;

    @TableField("invite_friend_count")
    private Integer inviteFriendCount;

    @ApiModelProperty(value = "最后一次下订单时间")
    @TableField("recent_order_time")
    private Date recentOrderTime;


}
