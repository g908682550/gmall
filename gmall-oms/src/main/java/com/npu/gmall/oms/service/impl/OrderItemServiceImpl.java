package com.npu.gmall.oms.service.impl;

import com.npu.gmall.oms.entity.OrderItem;
import com.npu.gmall.oms.mapper.OrderItemMapper;
import com.npu.gmall.oms.service.OrderItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单中所包含的商品 服务实现类
 * </p>
 *
 * @author gy
 * @since 2020-01-14
 */
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService {

}
