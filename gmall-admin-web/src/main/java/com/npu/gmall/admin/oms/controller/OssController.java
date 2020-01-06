package com.npu.gmall.admin.oms.controller;


import com.npu.gmall.admin.oms.component.OssComponent;
import com.npu.gmall.to.CommonResult;
import com.npu.gmall.to.OssPolicyResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Oss相关操作接口
 * 1、阿里云上传
 * 		前端form表单文档上传--->后台（收到文件流）--->ossClient.upload到阿里云
 * 2、如果要配成自己阿里云
 * 		1、前端项目中更改成自己阿里云的上传地址
 * 		2、更改配置
 * 		3、开始跨域
 */
//@CrossOrigin(origins = "www.baidu.com")//限制某些可以跨域的来源
@CrossOrigin
@Controller
@Api(tags = "OssController",description = "Oss管理")
@RequestMapping("/aliyun/oss")
public class OssController {
	@Autowired
	private OssComponent ossComponent;

	@ApiOperation(value = "oss上传签名生成")
	@GetMapping(value = "/policy")
	@ResponseBody
	public Object policy() {
		OssPolicyResult result = ossComponent.policy();
		return new CommonResult().success(result);
	}

}
