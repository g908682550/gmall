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
 * 用户举报表
 * </p>
 *
 * @author Lfy
 * @since 2020-01-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("cms_member_report")
@ApiModel(value="MemberReport对象", description="用户举报表")
public class MemberReport implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField("id")
    private Long id;

    @ApiModelProperty(value = "举报类型：0->商品评价；1->话题内容；2->用户评论")
    @TableField("report_type")
    private Integer reportType;

    @ApiModelProperty(value = "举报人")
    @TableField("report_member_name")
    private String reportMemberName;

    @TableField("create_time")
    private Date createTime;

    @TableField("report_object")
    private String reportObject;

    @ApiModelProperty(value = "举报状态：0->未处理；1->已处理")
    @TableField("report_status")
    private Integer reportStatus;

    @ApiModelProperty(value = "处理结果：0->无效；1->有效；2->恶意")
    @TableField("handle_status")
    private Integer handleStatus;

    @TableField("note")
    private String note;


}
