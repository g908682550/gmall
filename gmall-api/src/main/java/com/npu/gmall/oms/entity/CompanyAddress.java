package com.npu.gmall.oms.entity;

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
 * 公司收发货地址表
 * </p>
 *
 * @author Lfy
 * @since 2020-01-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("oms_company_address")
@ApiModel(value="CompanyAddress对象", description="公司收发货地址表")
public class CompanyAddress implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "地址名称")
    @TableField("address_name")
    private String addressName;

    @ApiModelProperty(value = "默认发货地址：0->否；1->是")
    @TableField("send_status")
    private Integer sendStatus;

    @ApiModelProperty(value = "是否默认收货地址：0->否；1->是")
    @TableField("receive_status")
    private Integer receiveStatus;

    @ApiModelProperty(value = "收发货人姓名")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "收货人电话")
    @TableField("phone")
    private String phone;

    @ApiModelProperty(value = "省/直辖市")
    @TableField("province")
    private String province;

    @ApiModelProperty(value = "市")
    @TableField("city")
    private String city;

    @ApiModelProperty(value = "区")
    @TableField("region")
    private String region;

    @ApiModelProperty(value = "详细地址")
    @TableField("detail_address")
    private String detailAddress;


}
