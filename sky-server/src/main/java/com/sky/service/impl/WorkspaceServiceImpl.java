package com.sky.service.impl;

import com.alibaba.druid.sql.visitor.functions.If;
import com.sky.constant.StatusConstant;
import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.service.WorkspaceService;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private DishMapper dishMapper;

    /**
     * 统计启售套餐与未启售套餐的数量
     * @return
     */
    @Override
    public SetmealOverViewVO overviewSetmeals() {
        Integer discontinued = setmealMapper.selectCountOfSetmealsByStatus(StatusConstant.DISABLE);
        Integer sold = setmealMapper.selectCountOfSetmealsByStatus(StatusConstant.ENABLE);
        return SetmealOverViewVO.builder().discontinued(discontinued).sold(sold).build();
    }

    /**
     * 统计起售和未启售菜品的数量
     * @return
     */
    @Override
    public DishOverViewVO overviewDishes() {
        Integer discontinued = dishMapper.selectCountOfDishesByStatus(StatusConstant.DISABLE);
        Integer sold = dishMapper.selectCountOfDishesByStatus(StatusConstant.ENABLE);
        return DishOverViewVO.builder().discontinued(discontinued).sold(sold).build();
    }

    /**
     * 订单信息总览
     * @return
     */
    @Override
    public OrderOverViewVO overviewOrders() {
        OrderOverViewVO orderOverViewVO=new OrderOverViewVO();
        List<Orders> ordersList = orderMapper.getAllOrders();
        if (ordersList!=null&&ordersList.size()>0){
            Integer waitingOrders=0;
            Integer deliveredOrders=0;
            Integer completedOrders=0;
            Integer cancelledOrders=0;
            for (Orders orders : ordersList) {
                Integer status=orders.getStatus();
                if (status==Orders.TO_BE_CONFIRMED){
                    waitingOrders++;
                }else if (status==Orders.CONFIRMED){
                    deliveredOrders++;
                }else if (status==Orders.COMPLETED){
                    completedOrders++;
                }else if (status==Orders.CANCELLED){
                    cancelledOrders++;
                }
            }
            orderOverViewVO.setCompletedOrders(completedOrders);
            orderOverViewVO.setCancelledOrders(cancelledOrders);
            orderOverViewVO.setWaitingOrders(waitingOrders);
            orderOverViewVO.setDeliveredOrders(deliveredOrders);
            orderOverViewVO.setAllOrders(ordersList.size());
        }
        return orderOverViewVO;
    }
}
