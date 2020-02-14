package com.npu.gmall.portal.controller.ums;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.npu.gmall.constant.SysCacheConstant;
import com.npu.gmall.to.CommonResult;
import com.npu.gmall.ums.entity.Member;
import com.npu.gmall.ums.service.MemberService;
import com.npu.gmall.vo.ums.LoginResponseVo;
import com.npu.gmall.vo.ums.UserMemberLoginParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/member")
public class MemberController {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Reference
    MemberService memberService;

    @PostMapping("/applogin")
    @ResponseBody
    public CommonResult loginForGmall(@Valid UserMemberLoginParam userMemberLoginParam,BindingResult result){

        Member member=memberService.login(userMemberLoginParam.getUsername(),userMemberLoginParam.getPassword());
        if(member==null){
            //用户没有
            CommonResult commonResult = new CommonResult().failed();
            commonResult.setMessage("账号密码不匹配，请重新登录");
            return commonResult;
        }else{
            String token = UUID.randomUUID().toString().replace("-","");
            String memberJson = JSON.toJSONString(member);
            stringRedisTemplate.opsForValue()
                    .set(SysCacheConstant.LOGIN_MEMBER+token,memberJson,SysCacheConstant.LOGIN_MEMBER_TIMEOUT, TimeUnit.MINUTES);
            LoginResponseVo loginResponseVo = new LoginResponseVo();
            BeanUtils.copyProperties(member,loginResponseVo);
            //设置访问令牌
            loginResponseVo.setAccessToken(token);
            return new CommonResult().success(loginResponseVo);
        }
    }

    /**
     * 通过token获得用户的全量信息
     * @param token
     * @return
     */
    @ResponseBody
    @GetMapping("/userinfo")
    public CommonResult getUserInfo(@RequestParam("accessToken") String token){

        String redisKey=SysCacheConstant.LOGIN_MEMBER+token;

        String member = stringRedisTemplate.opsForValue().get(redisKey);

        Member loginMember = JSON.parseObject(member, Member.class);

        loginMember.setId(null);
        loginMember.setPassword(null);

        return new CommonResult().success(loginMember);
    }

}
