package com.npu.gmall.demo.service;

import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class RedissonLockService {

    @Autowired
    RedissonClient redissonClient;

    private String hello="he";



    public void lock() {
        RLock lock = redissonClient.getLock("lock");
        //lock.lock();//默认是阻塞
        //lock.trylock();//是非阻塞的，尝试一下，拿不到就算了
        //lock.trylock(100,10,TimeUnits.SECONDS);100s内只要能获取到锁，这个锁就10s超时，如果没获取到就算了，会等待100s

        //哪个线程加的锁一定要在这个线程解

        //加锁
        //业务
        //解锁
    }

    public void unlock() {
        RLock lock = redissonClient.getLock("lock");
        lock.unlock();
    }

    public String read(){
        RReadWriteLock readLock = redissonClient.getReadWriteLock("helloValue");
        RLock rLock = readLock.readLock();
        rLock.lock();
        String a=hello;
        rLock.unlock();
        return a;
    }
    public String write() {
        RReadWriteLock readLock = redissonClient.getReadWriteLock("helloValue");
        RLock wLock = readLock.writeLock();
        wLock.lock();
        try{
            TimeUnit.SECONDS.sleep(5);} catch(InterruptedException e){e.printStackTrace();}
        hello= UUID.randomUUID().toString();
        wLock.unlock();
        return hello;
    }

    public Boolean tc() {
        RSemaphore park = redissonClient.getSemaphore("park");
        try {
            park.acquire();
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean rc() {
        RSemaphore park = redissonClient.getSemaphore("park");
        park.release(2);
        return true;
    }
}
