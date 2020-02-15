package com.npu.gmall.oms;

import com.npu.gmall.oms.entity.Order;
import com.npu.gmall.oms.mapper.OrderMapper;
import com.npu.gmall.oms.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GmallOmsApplicationTests {

    @Autowired
    OrderService orderService;

    @Test
    void contextLoads() {
        Order order=orderService.selectOne("202002151400156621228559628657348609");
        System.out.println(order);
    }

}
