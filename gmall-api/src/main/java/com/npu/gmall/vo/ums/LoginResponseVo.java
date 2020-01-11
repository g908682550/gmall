package com.npu.gmall.vo.ums;

import lombok.Data;

@Data
public class LoginResponseVo {

    private String username;

    private Long memberLevelId;

    private String nickname;

    private String phone;

    private String accessToken;//访问令牌

}
