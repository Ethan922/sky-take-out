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
import org.springframework.core.annotation.Order;
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

        Integer waitingOrders = orderMapper.getOrderCountByStatus(Orders.TO_BE_CONFIRMED);
        Integer deliveredOrders = orderMapper.getOrderCountByStatus(Orders.DELIVERY_IN_PROGRESS);
        Integer completedOrders = orderMapper.getOrderCountByStatus(Orders.COMPLETED);
        Integer cancelledOrders = orderMapper.getOrderCountByStatus(Orders.CANCELLED);
        Integer allOrders = orderMapper.getOrderCountByStatus(null);

        return OrderOverViewVO.builder()
                .waitingOrders(waitingOrders)
                .deliveredOrders(deliveredOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .allOrders(allOrders)
                .build();
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
        Integer newUsers=getNewUserCount(begin,end);//新增用户数

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

    private Integer getNewUserCount(LocalDateTime begin, LocalDateTime end) {
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

}
