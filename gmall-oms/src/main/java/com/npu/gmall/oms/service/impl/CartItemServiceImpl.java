package com.npu.gmall.oms.service.impl;

import com.npu.gmall.oms.entity.CartItem;
import com.npu.gmall.oms.mapper.CartItemMapper;
import com.npu.gmall.oms.service.CartItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 购物车表 服务实现类
 * </p>
 *
 * @author gy
 * @since 2020-01-14
 */
@Service
public class CartItemServiceImpl extends ServiceImpl<CartItemMapper, CartItem> implements CartItemService {

}
