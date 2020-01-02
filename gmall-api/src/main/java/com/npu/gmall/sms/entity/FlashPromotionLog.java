package com.npu.gmall.sms.entity;

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
 * 限时购通知记录
 * </p>
 *
 * @author Lfy
 * @since 2020-01-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sms_flash_promotion_log")
@ApiModel(value="FlashPromotionLog对象", description="限时购通知记录")
public class FlashPromotionLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("member_id")
    private Integer memberId;

    @TableField("product_id")
    private Long productId;

    @TableField("member_phone")
    private String memberPhone;

    @TableField("product_name")
    private String productName;

    @ApiModelProperty(value = "会员订阅时间")
    @TableField("subscribe_time")
    private Date subscribeTime;

    @TableField("send_time")
    private Date sendTime;


}
