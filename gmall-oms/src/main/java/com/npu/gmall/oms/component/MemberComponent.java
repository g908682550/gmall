package com.npu.gmall.oms.component;

import com.alibaba.fastjson.JSON;
import com.npu.gmall.constant.CartConstant;
import com.npu.gmall.constant.SysCacheConstant;
import com.npu.gmall.ums.entity.Member;
import com.npu.gmall.vo.cart.UserCartKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Component
public class MemberComponent {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    //0、根据accessToken获取用户的id
    public Member getMemberByAccessToken(String accessToken){
        String userJson = stringRedisTemplate.opsForValue().get(SysCacheConstant.LOGIN_MEMBER + accessToken);
        Member member = JSON.parseObject(userJson, Member.class);
        return member;
    }
}
