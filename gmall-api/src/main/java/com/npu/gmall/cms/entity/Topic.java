package com.npu.gmall.cms.entity;

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
 * 话题表
 * </p>
 *
 * @author Lfy
 * @since 2020-01-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("cms_topic")
@ApiModel(value="Topic对象", description="话题表")
public class Topic implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("category_id")
    private Long categoryId;

    @TableField("name")
    private String name;

    @TableField("create_time")
    private Date createTime;

    @TableField("start_time")
    private Date startTime;

    @TableField("end_time")
    private Date endTime;

    @ApiModelProperty(value = "参与人数")
    @TableField("attend_count")
    private Integer attendCount;

    @ApiModelProperty(value = "关注人数")
    @TableField("attention_count")
    private Integer attentionCount;

    @TableField("read_count")
    private Integer readCount;

    @ApiModelProperty(value = "奖品名称")
    @TableField("award_name")
    private String awardName;

    @ApiModelProperty(value = "参与方式")
    @TableField("attend_type")
    private String attendType;

    @ApiModelProperty(value = "话题内容")
    @TableField("content")
    private String content;


}
