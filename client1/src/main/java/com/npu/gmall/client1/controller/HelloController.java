package com.npu.gmall.client1.controller;

import com.npu.gmall.client1.config.SsoConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class HelloController {

    @Autowired
    SsoConfig ssoConfig;

    /**
     * sso服务器登录成了会在url后面给我们带一个cookie
     * @param model
     * @param ssoUserCookie
     * @param request
     * @return
     */
    @GetMapping("/")
    public String index(Model model,
                        @CookieValue(value = "sso_user",required =false) String ssoUserCookie,
                        HttpServletRequest request,
                        HttpServletResponse response,
                        @RequestParam(value = "sso_user",required =false) String ssoUserParam){
        if(!StringUtils.isEmpty(ssoUserParam)){
            //没有调用认证服务器登录后跳转回来,说明远程登录了
            Cookie cookie = new Cookie("sso_user", ssoUserParam);
            response.addCookie(cookie);
            return "index";
        }
        StringBuffer requestURL = request.getRequestURL();

        //1、判断是否登录了
        if(StringUtils.isEmpty(ssoUserCookie)){
            //没登录,重定向到登陆服务器
            return "redirect:"+ssoConfig.getUrl()+ssoConfig.getLoginPath()+"?redirect_url="+requestURL.toString();
        }else{
            //登录了,redis.get(ssoUserCookie)获取到用户信息
            model.addAttribute("loginUser","张三");
            return "index";
        }
    }

}
