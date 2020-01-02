package com.npu.gmall.pms.entity;

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
 * 商品评价表
 * </p>
 *
 * @author Lfy
 * @since 2020-01-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("pms_comment")
@ApiModel(value="Comment对象", description="商品评价表")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("product_id")
    private Long productId;

    @TableField("member_nick_name")
    private String memberNickName;

    @TableField("product_name")
    private String productName;

    @ApiModelProperty(value = "评价星数：0->5")
    @TableField("star")
    private Integer star;

    @ApiModelProperty(value = "评价的ip")
    @TableField("member_ip")
    private String memberIp;

    @TableField("create_time")
    private Date createTime;

    @TableField("show_status")
    private Integer showStatus;

    @ApiModelProperty(value = "购买时的商品属性")
    @TableField("product_attribute")
    private String productAttribute;

    @TableField("collect_couont")
    private Integer collectCouont;

    @TableField("read_count")
    private Integer readCount;

    @TableField("content")
    private String content;

    @ApiModelProperty(value = "上传图片地址，以逗号隔开")
    @TableField("pics")
    private String pics;

    @ApiModelProperty(value = "评论用户头像")
    @TableField("member_icon")
    private String memberIcon;

    @TableField("replay_count")
    private Integer replayCount;


}
