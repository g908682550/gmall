package com.npu.gmall.ums.entity;

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
 * 会员任务表
 * </p>
 *
 * @author Lfy
 * @since 2020-01-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ums_member_task")
@ApiModel(value="MemberTask对象", description="会员任务表")
public class MemberTask implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("name")
    private String name;

    @ApiModelProperty(value = "赠送成长值")
    @TableField("growth")
    private Integer growth;

    @ApiModelProperty(value = "赠送积分")
    @TableField("intergration")
    private Integer intergration;

    @ApiModelProperty(value = "任务类型：0->新手任务；1->日常任务")
    @TableField("type")
    private Integer type;


}
