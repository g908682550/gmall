package com.npu.gmall.oms.component;

import com.alibaba.fastjson.JSON;
import com.npu.gmall.constant.SysCacheConstant;
import com.npu.gmall.ums.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class MemberComponent {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    public Member getMemberByAccessToken(String accessToken){
        String memberJson = stringRedisTemplate.opsForValue().get(SysCacheConstant.LOGIN_MEMBER + accessToken);
        if(!StringUtils.isEmpty(memberJson)) return JSON.parseObject(memberJson, Member.class);
        return null;
    }

}
