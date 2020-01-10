package com.npu.gmall.demo.controller;

import com.npu.gmall.demo.service.RedissonLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LockTestController {

    @Autowired
    RedissonLockService redissonLockService;

    @GetMapping("/tc")
    public Boolean park(){
        return redissonLockService.tc();
    }

    @GetMapping("/rc")
    public Boolean rc(){
        return redissonLockService.rc();
    }


    @GetMapping("/lock")
    public String lock(){
        redissonLockService.lock();
        return "ok";
    }

    @GetMapping
    public String unlock(){
        redissonLockService.unlock();
        return "ok";
    }
    @GetMapping("/read")
    public String read(){
        return redissonLockService.read();
    }

    /**
     * 写锁是一个排它锁（独占锁）
     * 读锁是一个共享锁
     *
     * 有写锁，写锁以后的读都不可以，写锁释放才能读
     * 多个写，有写锁存在，必须竞争写锁
     *
     * 两个及两个以上的服务操作相同的数据，如果涉及读写，读加读锁，写加写锁。
     * @return
     */
    @GetMapping("/write")
    public String write(){
        return redissonLockService.write();
    }
}
