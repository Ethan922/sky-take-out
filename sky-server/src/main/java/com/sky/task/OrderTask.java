package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class OrderTask {


    @Autowired
    private OrderMapper orderMapper;

    /**
     * 每分钟检查一次是否存在支付超时订单，自动取消支付超时订单
     */
    @Scheduled(cron = "0 * * * * ?")
    public void autoCancelTimeoutOrder(){
        log.info("自动取消，支付超时订单{}",new Date());

        LocalDateTime orderTime= LocalDateTime.now().minusMinutes(15);
        List<Orders> ordersList= orderMapper.getOrdersByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT,orderTime);

        if (ordersList==null||ordersList.size()==0) return;

        for (Orders orders : ordersList) {
            orders.setStatus(Orders.CANCELLED);
            orders.setCancelReason("订单支付超时，自动取消");
            orders.setCancelTime(LocalDateTime.now());

            orderMapper.update(orders);
        }
    }

    /**
     * 每天凌晨一点处理一直处于派送中的订单
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void autoCompleteOrder(){
        log.info("处理一直处于派送中的订单{}",new Date());

        LocalDateTime orderTime= LocalDateTime.now().minusMinutes(60);
        List<Orders> ordersList= orderMapper.getOrdersByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS,orderTime);

        if (ordersList==null||ordersList.size()==0) return;

        for (Orders orders : ordersList) {
            orders.setStatus(Orders.COMPLETED);

            orderMapper.update(orders);
        }
    }

}
