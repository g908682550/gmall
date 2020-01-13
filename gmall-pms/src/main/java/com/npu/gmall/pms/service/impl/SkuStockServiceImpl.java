package com.npu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.npu.gmall.pms.entity.SkuStock;
import com.npu.gmall.pms.mapper.SkuStockMapper;
import com.npu.gmall.pms.service.SkuStockService;
import org.springframework.stereotype.Component;

/**
 * <p>
 * sku的库存 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2020-01-02
 */
@Service
@Component
public class SkuStockServiceImpl extends ServiceImpl<SkuStockMapper, SkuStock> implements SkuStockService {

}
