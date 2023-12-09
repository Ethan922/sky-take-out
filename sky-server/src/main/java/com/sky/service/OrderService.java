package com.sky.service;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {
    OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);

    OrderVO getOrderDetailById(Long id);

    PageResult historyOrders(OrdersPageQueryDTO ordersPageQueryDTO);

    void cancelOrder(Long id);

    void oneMoreOrder(Long id);
}
