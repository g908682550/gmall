package com.npu.gmall.admin.ums.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Null;

/**
 * 用户登录参数
 * Created by atguigu 4/26.
 */
@Getter
@Setter
@ToString
public class UmsAdminParam {

    /**
     * 能使用的校验注解
     * 1、hibernate org.hibernate.validator.constraints 里面的所有
     * 2、JSR303规范规定的都可以
     *      javax.validation.constraints包下的也可以
     */
    @Length(min=6,max=18,message = "用户名长度必须是6-18位")
    @ApiModelProperty(value = "用户名", required = true)
    private String username;
    @ApiModelProperty(value = "密码", required = true)
    private String password;

    @NotEmpty
    @ApiModelProperty(value = "用户头像")
    private String icon;

    @ApiModelProperty(value = "邮箱")
    @Email(message = "邮箱格式不合法")
    private String email;

    @Null
    @ApiModelProperty(value = "用户昵称")
    private String nickName;
    @ApiModelProperty(value = "备注")
    private String note;
}
