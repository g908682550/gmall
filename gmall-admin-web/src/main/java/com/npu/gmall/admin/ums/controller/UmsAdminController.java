package com.npu.gmall.admin.ums.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.npu.gmall.admin.utils.JwtTokenUtil;
import com.npu.gmall.ums.entity.Admin;
import com.npu.gmall.ums.service.AdminService;
import com.npu.gmall.to.CommonResult;
import com.npu.gmall.vo.ums.UmsAdminLoginParam;
import com.npu.gmall.vo.ums.UmsAdminParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 后台用户管理
 * SpringMVC支持使用JSR303方式进行校验
 * 使用JSR303的三大步
 * 1、给需要校验数据的javaBean上标注校验注解
 * 2、告诉SpringBoot，这个需要校验 @Valid 声明需要校验 SpringMVC进入方法前就会校验，如果校验出错，直接返回错误，不执行controller
 * 3、如何感知校验成功还是失败；只需要给开启了校验的javaBean参数后面，紧跟一个BindingResult对象就可以获取到校验结果,只要有BindingResult，即使校验错了，方法也会执行。我们需要手动处理。
 */
@Slf4j
@CrossOrigin
@RestController
@Api(tags = "AdminController", description = "后台用户管理")
@RequestMapping("/admin")
public class UmsAdminController {
    @Reference
    private AdminService adminService;
    @Value("${gmall.jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${gmall.jwt.tokenHead}")
    private String tokenHead;
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    /**
     * 1、注册成功返回用户的所有信息：{code，message，data：{username，icon，email}}
     * 2、数据校验失败：{code，message，data}
     * @param umsAdminParam
     * @param result
     * @return
     */
    @ApiOperation(value = "用户注册")
    @PostMapping(value = "/register")
    public Object register(@Valid @RequestBody UmsAdminParam umsAdminParam, BindingResult result) {
        Admin admin = new Admin();
        //TODO 完成注册功能
        log.debug("需要注册的用户详情:{},校验错误数:{}",umsAdminParam);
        BeanUtils.copyProperties(umsAdminParam,admin);
        admin.setCreateTime(new Date());
        admin.setStatus(1);
        adminService.register(admin);
        return new CommonResult().success(admin);
    }

    /**
     * 登录成功以后用户的信息以jwt令牌的方式返回给前端
     * @param umsAdminLoginParam
     * @param result
     * @return
     *
     * 1、如果前端发过来的是json字符串，要封装对象
     * public Object login(@RequestBody UmsAdminLoginParam UmsAdminLoginParam umsAdminLoginParam)
     * 2、如果前端发过来的是K=v&k=v字符串，要封装对象
     * public Object login(UmsAdminLoginParam umsAdminLoginParam)
     */
    @ApiOperation(value = "登录以后返回token")
    @PostMapping(value = "/login")
    public Object login(@Valid @RequestBody UmsAdminLoginParam umsAdminLoginParam, BindingResult result) {
        //去数据库登陆
        Admin admin = adminService.login(umsAdminLoginParam.getUsername(), umsAdminLoginParam.getPassword());
        //登陆成功生成token，此token携带基本用户信息，以后就不用去数据库了
        String token = jwtTokenUtil.generateToken(admin);
        if (token == null) {
            return new CommonResult().validateFailed("用户名或密码错误");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);
        return new CommonResult().success(tokenMap);
    }

    @ApiOperation(value = "刷新token")
    @GetMapping(value = "/token/refresh")
    public Object refreshToken(HttpServletRequest request) {
        //1、获取请求头中的Authorization完整值
        String oldToken = request.getHeader(tokenHeader);
        String refreshToken = "";

        //2、从请求头中的Authorization中分离出jwt的值
        String token = oldToken.substring(tokenHead.length());

        //3、是否可以进行刷新（未过刷新时间）
        if (jwtTokenUtil.canRefresh(token)) {
            refreshToken =  jwtTokenUtil.refreshToken(token);
        }else  if(refreshToken == null && "".equals(refreshToken)){
            return new CommonResult().failed();
        }

        //将新的token交给前端
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", refreshToken);
        tokenMap.put("tokenHead", tokenHead);
        return new CommonResult().success(tokenMap);
    }

    @ApiOperation(value = "获取当前登录用户信息")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ResponseBody
    public Object getAdminInfo(HttpServletRequest request) {
        String oldToken = request.getHeader(tokenHeader);
        String userName = jwtTokenUtil.getUserNameFromToken(oldToken.substring(tokenHead.length()));
        Admin umsAdmin=adminService.getUserInfo(userName);
        Map<String, Object> data = new HashMap<>();
        data.put("username", umsAdmin.getUsername());
        data.put("roles", new String[]{"TEST"});
        data.put("icon", umsAdmin.getIcon());
        return new CommonResult().success(data);
    }

}
