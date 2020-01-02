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
 * 限时购场次表
 * </p>
 *
 * @author Lfy
 * @since 2020-01-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sms_flash_promotion_session")
@ApiModel(value="FlashPromotionSession对象", description="限时购场次表")
public class FlashPromotionSession implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "场次名称")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "每日开始时间")
    @TableField("start_time")
    private Date startTime;

    @ApiModelProperty(value = "每日结束时间")
    @TableField("end_time")
    private Date endTime;

    @ApiModelProperty(value = "启用状态：0->不启用；1->启用")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;


}
