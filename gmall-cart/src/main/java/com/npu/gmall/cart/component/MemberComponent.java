package com.npu.gmall.cart.component;

import com.alibaba.fastjson.JSON;
import com.npu.gmall.cart.vo.Cart;
import com.npu.gmall.cart.vo.UserCartKey;
import com.npu.gmall.constant.CartConstant;
import com.npu.gmall.constant.SysCacheConstant;
import com.npu.gmall.ums.entity.Member;
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

    /**
     * 获取到购物车的key
     * @param accessToken
     * @param cartKey
     * @return
     */
    public UserCartKey getCartKey(String accessToken, String cartKey){

        Member member=null;

        if(!StringUtils.isEmpty(accessToken)){
            member=getMemberByAccessToken(accessToken);
        }
        UserCartKey userCartKey = new UserCartKey();
        if(member!=null){
            //获取到在线用户
            userCartKey.setLogin(true);
            userCartKey.setUserId(member.getId());
            userCartKey.setFinalCartKey(CartConstant.USER_CART_KEY_PREFIX+member.getId());

        }else if(!StringUtils.isEmpty(cartKey)){
            userCartKey.setLogin(false);
            userCartKey.setFinalCartKey(CartConstant.TEMP_CART_KEY_PREFIX+cartKey);
        }else{
            String replace = UUID.randomUUID().toString().replace("-", "");
            userCartKey.setLogin(false);
            userCartKey.setFinalCartKey(CartConstant.TEMP_CART_KEY_PREFIX+replace);
            userCartKey.setTempCartKey(replace);
        }
        return userCartKey;
    }
}
