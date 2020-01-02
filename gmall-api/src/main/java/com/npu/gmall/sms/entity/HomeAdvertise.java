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
 * 首页轮播广告表
 * </p>
 *
 * @author Lfy
 * @since 2020-01-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sms_home_advertise")
@ApiModel(value="HomeAdvertise对象", description="首页轮播广告表")
public class HomeAdvertise implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("name")
    private String name;

    @ApiModelProperty(value = "轮播位置：0->PC首页轮播；1->app首页轮播")
    @TableField("type")
    private Integer type;

    @TableField("pic")
    private String pic;

    @TableField("start_time")
    private Date startTime;

    @TableField("end_time")
    private Date endTime;

    @ApiModelProperty(value = "上下线状态：0->下线；1->上线")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "点击数")
    @TableField("click_count")
    private Integer clickCount;

    @ApiModelProperty(value = "下单数")
    @TableField("order_count")
    private Integer orderCount;

    @ApiModelProperty(value = "链接地址")
    @TableField("url")
    private String url;

    @ApiModelProperty(value = "备注")
    @TableField("note")
    private String note;

    @ApiModelProperty(value = "排序")
    @TableField("sort")
    private Integer sort;


}
