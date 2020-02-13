package com.npu.gmall.demo.service;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class RedisIncrService {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    JedisPool jedisPool;

    @Autowired
    RedissonClient redisson;

    public void useRedissonForLock(){
        //获取一把锁，只要各个代码用的锁名一样即可
        RLock lock = redisson.getLock("lock");
        try {
            //lock.lock(),感知别人删锁，发布订阅模式（实时感知），lock监听redis，redis一旦删锁，就尝试去加锁。不是自旋式
            lock.lock(3,TimeUnit.SECONDS);//加锁带自动解锁
            Jedis jedis = jedisPool.getResource();
            String num=jedis.get("num");
            Integer i=Integer.parseInt(num);
            i=i+1;
            jedis.set("num",i.toString());
            jedis.close();
        } finally {
            lock.unlock();//解锁
        }
    }




    /**
     * synchronized和ReentrantLock单机条件下可以用，分布式会出问题
     */
    public synchronized void incr(){
        //共用一个锁
        //this当前对象。当前service对象一个实例 synchronized (this)
        //synchronized(stringRedisTemplate)也可以
        //synchronized(new Object())锁不住
        //Object obj=new Object();
        //synchronized(obj)锁的住
            String num = stringRedisTemplate.opsForValue().get("num");
            Integer new_num = Integer.parseInt(num);
            new_num+=1;
            stringRedisTemplate.opsForValue().set("num",new_num.toString());
    }

    public void incrDistribute() {
        //redis中setnx为原子性操作，判断和保存是原子的

        //分布式锁的核心：加锁一定是原子性的（即判断没有值，就给redis保存值），锁要自动超时，解锁要要原子性的
        /**
         * 伪代码
         * public void a(){
         *     Integer lock=setnx("lock","111");
         *     if(lock!=0){
         *         //执行业务逻辑，释放锁
         *     }else{
         *          //重试
         *     }
         * }
         * 问题 执行逻辑时出现各种问题导致锁未释放
         * public void a(){
         *     Integer lock=setnx("lock","111");
         *     if(lock!=0){
         *          expire("lock",10s)
         *          //执行业务逻辑，释放锁）
         *     }else{
         *          //自旋
         *     }
         * }
         * 问题 刚拿到锁，还没加超时，出现问题
         * 解决：加锁和设置超时为原子性
         * public void a(){
         *     Integer lock=setnxex("lock",111,"10s");
         *     if(lock=="ok"){
         *         //执行业务逻辑，释放锁
         *     }else{
         *         //自旋
         *     }
         * }
         * 问题 如果业务逻辑超时，导致锁自动删除，业务执行完又删除一遍，导致多个人获取到锁
         * 解决：只删自己的锁
         * public void hello(){
         *     String token=UUID();
         *     String lock=setnxex("lock",token,10s);
         *     if(lock=="ok"){
         *         //执行
         *
         *         //释放
         *         if(get("lock")==token){
         *             del("lock")
         *         }
         *
         *     }else{
         *         //自旋
         *     }
         * }
         * 问题：获取锁的时候，锁的值正在返回，锁过期，且其它人获得锁，又删除了锁。又有多个线程进入了锁
         * 原因：删锁不是原子性的
         * lua脚本可解决
         * 最终的分布式锁代码：
         * public void hello(){
         *     String token=uuid;
         *     String lock=redis.setnxex("lock",token,10s);
         *     if(lock=="ok"){
         *         //执行业务逻辑
         *         //脚本删除锁
         *     }else{
         *         //自旋
         *     }
         * }
         * 可将该加锁过程写成AOP
         */

//        String token= UUID.randomUUID().toString();
//        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent("lock", token, 3, TimeUnit.SECONDS);
//        if(lock){
//            String num = stringRedisTemplate.opsForValue().get("num");
//            Integer new_num = Integer.parseInt(num);
//            new_num+=1;
//            stringRedisTemplate.opsForValue().set("num",new_num.toString());
//            //删除锁
//            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
//            DefaultRedisScript<String> script1=new DefaultRedisScript<>(script);
//            stringRedisTemplate.execute(script1,Arrays.asList("lock"),token);
//            System.out.println("删除锁完成");
//        }else{
//            incrDistribute();
//        }
        /**
         * 锁的更多考虑
         *  1）自旋 自旋次数 自旋超时
         *  2）锁设置
         *      锁粒度:细，记录级别，
         *          1）各自服务各自锁
         *          2）分析好粒度，不要锁珠无关数据，一种数据一种锁，一个数据一个锁
         *  3）锁类型
         *      读写锁。
         *
         *
         *      查询商品详情：进缓存--->击穿，穿透，雪崩
         *      查商品
         *   public Product productInfo(){
         *      Product cache=jedis.get("product-1")
         *      if(cache!=null) return cache;
         *      else{
         *          //查数据库
         *          SetParams setParams = SetParams.setParams().ex(3).nx();
         *          //各自数据各自锁
         *          String lock = jedis.set("lock"+productId, token, setParams);
         *          if(lock!=null){
         *              Product product=getFromDB();
         *              jedis.set("product-1",product);
         *          }else{
         *              //拿不到锁
         *              return productInfo();
         *          }
         *      }
         *  }
         */
        Jedis jedis = jedisPool.getResource();
        try {
            String token = UUID.randomUUID().toString();
            SetParams setParams = SetParams.setParams().ex(3).nx();
            String lock = jedis.set("lock", token, setParams);
            if(lock!=null&&lock.equalsIgnoreCase("OK")){
                //ok
                String num = jedis.get("num");
                Integer i = Integer.parseInt(num);
                i=i+1;
                jedis.set("num",i.toString());
                //删除锁
                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                jedis.eval(script, Collections.singletonList("lock"),Collections.singletonList(token));
                System.out.println("删除锁ok ");
            }else{
                incrDistribute();
            }
        } finally {
            jedis.close();
        }

    }

}
