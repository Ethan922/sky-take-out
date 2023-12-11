package com.sky.service.impl;

import com.alibaba.druid.sql.visitor.functions.If;
import com.sky.constant.StatusConstant;
import com.sky.entity.Orders;
import com.sky.entity.User;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private DishMapper dishMapper;

    /**
     * 统计启售套餐与未启售套餐的数量
     *
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
     *
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
        OrderOverViewVO orderOverViewVO = new OrderOverViewVO();
        List<Orders> ordersList = orderMapper.getAllOrders();
        Integer waitingOrders = 0;
        Integer deliveredOrders = 0;
        Integer completedOrders = 0;
        Integer cancelledOrders = 0;
        Integer allOrders=0;
        if (ordersList != null && ordersList.size() > 0) {
            allOrders=ordersList.size();
            for (Orders orders : ordersList) {
                Integer status = orders.getStatus();
                if (status == Orders.TO_BE_CONFIRMED) {
                    waitingOrders++;
                } else if (status == Orders.CONFIRMED) {
                    deliveredOrders++;
                } else if (status == Orders.COMPLETED) {
                    completedOrders++;
                } else if (status == Orders.CANCELLED) {
                    cancelledOrders++;
                }
            }
        }
        orderOverViewVO.setCompletedOrders(completedOrders);
        orderOverViewVO.setCancelledOrders(cancelledOrders);
        orderOverViewVO.setWaitingOrders(waitingOrders);
        orderOverViewVO.setDeliveredOrders(deliveredOrders);
        orderOverViewVO.setAllOrders(allOrders);

        return orderOverViewVO;
    }

    /**
     * 统计今日营业数据
     *
     * @return
     */
    @Override
    public BusinessDataVO getBusinessData() {
        BusinessDataVO businessDataVO = new BusinessDataVO();
        LocalDateTime begin = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        List<Orders> ordersList = orderMapper.getOrdersWithTimeBounds(begin, end);
        //数据库中无订单信息
        if (ordersList == null || ordersList.size() == 0) {
            return businessDataVO;
        }
        //今日总订单数
        Integer orderCount = ordersList.size();
        //有效订单数
        Integer validOrderCount = 0;
        //营业额
        Double turnover = 0.0;
        //订单完成率
        Double orderCompletionRate;
        //平均客单价
        Double unitPrice;
        for (Orders orders : ordersList) {
            Integer status = orders.getStatus();
            if (status == Orders.COMPLETED) {
                validOrderCount++;
                turnover += orders.getAmount().doubleValue();
            }
        }
        orderCompletionRate = validOrderCount * 1.0 / orderCount;
        //格式化平均客单价
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        //有效订单数为0，则平均客单价设为null
        unitPrice = validOrderCount == 0 ? null : turnover / validOrderCount;
        unitPrice= Double.valueOf(decimalFormat.format(unitPrice));
        //新增用户数
        Integer newUsers = getNewUsers(begin, end);

        businessDataVO.setTurnover(turnover);
        businessDataVO.setUnitPrice(unitPrice);
        businessDataVO.setValidOrderCount(validOrderCount);
        businessDataVO.setOrderCompletionRate(orderCompletionRate);
        businessDataVO.setNewUsers(newUsers);
        return businessDataVO;
    }

    //获取今日新用户数量
    private Integer getNewUsers(LocalDateTime begin, LocalDateTime end) {
        List<User> allUsers = userMapper.getAllUsers();
        Integer newUsers = 0;
        for (User user : allUsers) {
            LocalDateTime createTime = user.getCreateTime();
            if (createTime.isAfter(begin) && createTime.isBefore(end)) {
                newUsers++;
            }
        }
        return newUsers;
    }
}
