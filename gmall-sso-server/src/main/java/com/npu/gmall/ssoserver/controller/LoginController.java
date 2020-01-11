package com.npu.gmall.ssoserver.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.npu.gmall.constant.SysCacheConstant;
import com.npu.gmall.to.CommonResult;
import com.npu.gmall.ums.entity.Member;
import com.npu.gmall.ums.service.MemberService;
import com.npu.gmall.vo.ums.LoginResponseVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
public class LoginController {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Reference
    MemberService memberService;

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

    @PostMapping("/applogin")
    @ResponseBody
    public CommonResult loginForGmall(@RequestParam("username") String username,
                                      @RequestParam("password") String password){

        Member member=memberService.login(username,password);
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


    @GetMapping("/login")
    public String login(@RequestParam("redirect_url") String redirect_url,
                        @CookieValue(value = "sso_user",required = false) String ssoUser,
                        Model model){
        //1、判断之前是否登录过
        if(!StringUtils.isEmpty(ssoUser)){
            //登录过,回到之前的地方，并且把ssoserver获取到的cookie以url方式传递给其它域名
            return "redirect:"+redirect_url+"?sso_user="+ssoUser;
        }else{
            //没有登录过
            model.addAttribute("redirect_url",redirect_url);
            return "login";
        }
    }

    @PostMapping("/doLogin")
    public String doLogin(String username, String password, HttpServletResponse response,String redirect_url){
        //模拟用户登录
        Map<String,Object> map=new HashMap<>();
        map.put("username",username);
        map.put("email",username+"@qq.com");

        //以上表示用户登录成功，将用户信息放入redis中
        String token = UUID.randomUUID().toString().replace("-","");

        stringRedisTemplate.opsForValue().set(token,JSON.toJSONString(map));

        //处理完后做两件事 1、命令浏览器把当前的token保存为cookie sso_user=token
        //                2、命令浏览器重定向到它之前的位置
        Cookie cookie=new Cookie("sso_user",token);
        response.addCookie(cookie);

        return "redirect:"+redirect_url+"?sso_user"+token;
    }

}
