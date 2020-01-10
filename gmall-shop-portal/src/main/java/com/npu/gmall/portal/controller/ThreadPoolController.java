package com.npu.gmall.portal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

@RestController
public class ThreadPoolController {

    @Qualifier("mainThreadPoolExecutor")
    @Autowired
    ThreadPoolExecutor mainThreadPoolExecutor;

    @Qualifier("otherThreadPoolExecutor")
    @Autowired
    ThreadPoolExecutor otherThreadPoolExecutor;

    @GetMapping("/mainThread/status")
    public Map mainThreadPoolStatus(){
        Map<String,Object> map=new HashMap<>();
        map.put("ActiveCount",mainThreadPoolExecutor.getActiveCount());
        map.put("CoreSize",mainThreadPoolExecutor.getCorePoolSize());
        return map;
    }
    @GetMapping("/otherThread/status")
    public Map otherThreadPoolStatus(){
        Map<String,Object> map=new HashMap<>();
        map.put("ActiveCount",mainThreadPoolExecutor.getActiveCount());
        map.put("CoreSize",mainThreadPoolExecutor.getCorePoolSize());
        return map;
    }
}
