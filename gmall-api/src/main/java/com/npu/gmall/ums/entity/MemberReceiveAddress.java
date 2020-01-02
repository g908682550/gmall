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
 * 会员收货地址表
 * </p>
 *
 * @author Lfy
 * @since 2020-01-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ums_member_receive_address")
@ApiModel(value="MemberReceiveAddress对象", description="会员收货地址表")
public class MemberReceiveAddress implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("member_id")
    private Long memberId;

    @ApiModelProperty(value = "收货人名称")
    @TableField("name")
    private String name;

    @TableField("phone_number")
    private String phoneNumber;

    @ApiModelProperty(value = "是否为默认")
    @TableField("default_status")
    private Integer defaultStatus;

    @ApiModelProperty(value = "邮政编码")
    @TableField("post_code")
    private String postCode;

    @ApiModelProperty(value = "省份/直辖市")
    @TableField("province")
    private String province;

    @ApiModelProperty(value = "城市")
    @TableField("city")
    private String city;

    @ApiModelProperty(value = "区")
    @TableField("region")
    private String region;

    @ApiModelProperty(value = "详细地址(街道)")
    @TableField("detail_address")
    private String detailAddress;


}
