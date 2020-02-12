package com.npu.gmall.ums.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.npu.gmall.ums.entity.Admin;
import com.npu.gmall.ums.mapper.AdminMapper;
import com.npu.gmall.ums.service.AdminService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.npu.gmall.vo.ums.UmsAdminParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

/**
 * <p>
 * 后台用户表 服务实现类
 * </p>
 *
 * @author gy
 * @since 2020-01-03
 */
@Component
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Autowired
    AdminMapper adminMapper;

    /**
     * 获取用户详情
     * @param userName
     * @return
     */
    @Override
    public Admin getUserInfo(String userName) {
        return adminMapper.selectOne(new QueryWrapper<Admin>().eq("username",userName));
    }

    /**
     * 管理员登录
     * @param username
     * @param password
     * @return
     */
    @Override
    public Admin login(String username, String password) {

        String s= DigestUtils.md5DigestAsHex(password.getBytes());

        QueryWrapper<Admin> wrapper = new QueryWrapper<Admin>().eq("username", username).eq("password", s);

        Admin admin = adminMapper.selectOne(wrapper);

        return admin;
    }

    /**
     * 管理员注册
     * @param admin
     * @return
     */
    @Override
    public Integer register(Admin admin) {

        return adminMapper.insert(admin);
    }
}
