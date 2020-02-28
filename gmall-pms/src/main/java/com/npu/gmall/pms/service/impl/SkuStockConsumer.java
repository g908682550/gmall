package com.npu.gmall.pms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.npu.gmall.pms.entity.SkuStock;
import com.npu.gmall.pms.service.SkuStockService;
import com.npu.gmall.vo.product.SkuStockInfo;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class SkuStockConsumer {

    @Autowired
    SkuStockService skuStockService;

    @Autowired
    SkuStockProvider skuStockProvider;

    /**
     * 监听到减库存的消息
     * @param message
     * @param skuStockInfos
     * @param channel
     * @throws IOException
     */
    @RabbitListener(queues = {"skuStockQueue"})
    public void receiveSkuStockMessage(Message message, List<SkuStockInfo> skuStockInfos, Channel channel) throws IOException {
        try{
            skuStockInfos.forEach(skuStockInfo -> {
                SkuStock skuStock=skuStockService.getById(skuStockInfo.getSkuId());
                Integer num=skuStock.getStock()-skuStockInfo.getNum();
                skuStock.setStock(num);
                skuStockService.update(skuStock,new QueryWrapper<SkuStock>().eq("id",skuStockInfo.getSkuId()));
                try {
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            //扣减成功后给订单系统发送消息
            skuStockProvider.sendOrderFinishInfo(skuStockInfos.get(0).getOrderSn());
        }catch (Exception e){
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,false);
        }
    }

}
