package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
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

    void confirmOrder(OrdersConfirmDTO ordersConfirmDTO);

    void rejectOrder(OrdersRejectionDTO ordersRejectionDTO);

    void delivery(Long id);

    void orderComplete(Long id);

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    void reminder(Long id);
}
