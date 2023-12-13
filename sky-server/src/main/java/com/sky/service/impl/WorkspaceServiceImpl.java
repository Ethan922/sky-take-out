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
     *
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
        Integer allOrders = 0;
        if (ordersList != null && ordersList.size() > 0) {
            allOrders = ordersList.size();
            for (Orders orders : ordersList) {
                Integer status = orders.getStatus();
                if (Orders.TO_BE_CONFIRMED.equals(status)) {
                    waitingOrders++;
                } else if (Orders.CONFIRMED.equals(status)) {
                    deliveredOrders++;
                } else if (Orders.COMPLETED.equals(status)) {
                    completedOrders++;
                } else if (Orders.CANCELLED.equals(status)) {
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
        LocalDateTime begin = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        Map map = new HashMap<>();
        map.put("begin", begin);
        map.put("end", end);
        map.put("status", Orders.COMPLETED);

        Double turnover = orderMapper.getTurnoverByMap(map);//营业额
        Integer validOrderCount = getOrderCount(begin, end, Orders.COMPLETED);//有效订单数
        Integer totalOrderCount = getOrderCount(begin, end, null);
        Double orderCompletionRate = totalOrderCount == 0 ? 0.0 : validOrderCount * 1.0 / totalOrderCount;//订单完成率
        Double unitPrice=validOrderCount==0?0.0:turnover/validOrderCount;//平均客单价
        Integer newUsers=getNewUsers(begin,end);//新增用户数

        //保留平均客单价小数点后两位
        DecimalFormat decimalFormat=new DecimalFormat("#.##");
        unitPrice= Double.valueOf(decimalFormat.format(unitPrice));

        return BusinessDataVO.builder()
                .unitPrice(unitPrice)
                .turnover(turnover)
                .orderCompletionRate(orderCompletionRate)
                .validOrderCount(validOrderCount)
                .newUsers(newUsers)
                .build();
    }

    private Integer getUserCount(LocalDateTime begin, LocalDateTime end) {
        Map map = new HashMap<>();
        map.put("begin", begin);
        map.put("end", end);

        return userMapper.getUserCountByMap(map);
    }

    private Integer getOrderCount(LocalDateTime begin, LocalDateTime end, Integer status) {
        Map map = new HashMap<>();
        map.put("begin", begin);
        map.put("end", end);
        map.put("status", status);
        return orderMapper.statisticByMap(map);
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
