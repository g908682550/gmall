package com.npu.gmall.cart.component;

import com.alibaba.fastjson.JSON;
import com.npu.gmall.constant.SysCacheConstant;
import com.npu.gmall.ums.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

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
