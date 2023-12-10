package com.sky.service;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {
    OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);

    OrderVO getOrderDetailById(Long id);

    PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    void cancelOrder(OrdersCancelDTO ordersCancelDTO);

    void oneMoreOrder(Long id);

    OrderStatisticsVO getOrderStatistics();
}
