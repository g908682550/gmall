package com.npu.gmall.pms;

import com.npu.gmall.pms.entity.Brand;
import com.npu.gmall.pms.entity.Product;
import com.npu.gmall.pms.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootTest
class GmallPmsApplicationTests {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    ProductService productService;

    @Autowired
    RedisTemplate<Object,Object> redisTemplateObj;

    @Test
    void contextLoads() {
        Product byId = productService.getById(1);
        System.out.println(byId);
    }

    @Test
    void redisTemplate(){
        //操作redis中的String类型
//        redisTemplate.opsForValue()
        redisTemplate.opsForValue().set("hello","world");
        System.out.println("保存了数据");
        String hello = redisTemplate.opsForValue().get("hello");
        System.out.println("刚才保存的值是: "+hello);
    }

    /**
     * Redis中存对象默认是使用序列化方式，把对象弄过去
     */
    @Test
    void redisTemplateObj(){
        //以后要存对象先将对象转为json字符串
        //去redis中取出来，再反序列化
        Brand brand=new Brand();
        brand.setName("aaa");
        redisTemplateObj.opsForValue().set("abc",brand);
        Brand abc = (Brand)redisTemplateObj.opsForValue().get("abc");
        System.out.println("刚才保存对象的值是"+abc.getName());
    }
}
