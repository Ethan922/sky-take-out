package com.sky.service.impl;

import com.sky.dto.DataOverViewQueryDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.DataStatisticService;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.OrderReportVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class DataStatisticServiceImpl implements DataStatisticService {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 订单数据统计
     * @param dataOverViewQueryDTO
     * @return
     */
    @Override
    public OrderReportVO orderStatistic(DataOverViewQueryDTO dataOverViewQueryDTO) {
        LocalDateTime begin = dataOverViewQueryDTO.getBegin().atStartOfDay();
        LocalDateTime end = LocalDateTime.of(dataOverViewQueryDTO.getEnd(), LocalTime.MAX);

        OrderReportVO orderReportVO = new OrderReportVO();
        //获取指定时间内的订单
        List<Orders> ordersList = orderMapper.getOrdersWithTimeBounds(begin, end);


        //日期字符串
        StringJoiner dateList = new StringJoiner(",");
        StringJoiner orderCountList = new StringJoiner(",");
        StringJoiner validOrderCountList = new StringJoiner(",");
        //指定时间范围内没有订单数据
        if (ordersList == null || ordersList.size() == 0) {
            String dateRangeString = getDateRangeString(begin, end);
            orderReportVO.setDateList(dateRangeString);
            for (int i = 0; i < dateRangeString.split(",").length; i++) {
                orderCountList.add(""+0);
                validOrderCountList.add(""+0);
            }
            orderReportVO.setOrderCountList(orderCountList.toString());
            orderReportVO.setValidOrderCountList(validOrderCountList.toString());
            orderReportVO.setValidOrderCount(0);
            orderReportVO.setOrderCompletionRate(0.0);
            orderReportVO.setTotalOrderCount(0);
            return orderReportVO;
        }


        // 格式化器，定义日期格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        //数据统计
        Integer orderCountDaily;//每日订单数

        Integer validOrderCountDaily;//每日有效订单数

        Integer totalOrderCount =  ordersList.size();//总订单数

        Integer totalValidOrderCount = 0;//总有效订单数
        //遍历指定日期范围内的每一天
        while (!begin.isAfter(end)) {
            orderCountDaily = 0;
            validOrderCountDaily=0;
            for (Orders orders : ordersList) {
                Integer status = orders.getStatus();
                //如果日期大于本日日期，则无需遍历订单列表
                if (begin.isAfter(LocalDateTime.now())){
                    break;
                }
                //订单的下单时间不在该天的范围内
                if (orders.getOrderTime().isBefore(begin) || orders.getOrderTime().isAfter(begin.plusDays(1))) {
                    continue;
                }
                orderCountDaily++;
                //订单的状态为完成则视为有效订单
                if (status == Orders.COMPLETED) {
                    validOrderCountDaily++;
                }
            }
            totalValidOrderCount+=validOrderCountDaily;
            validOrderCountList.add(validOrderCountDaily.toString());
            orderCountList.add(orderCountDaily.toString());
            dateList.add(begin.format(formatter));
            begin = begin.plusDays(1);
        }


        Double orderCompletionRate =  totalValidOrderCount * 1.0 / totalOrderCount;//订单完成率

        orderReportVO.setOrderCountList(orderCountList.toString());
        orderReportVO.setValidOrderCountList(validOrderCountList.toString());
        orderReportVO.setValidOrderCount(totalValidOrderCount);
        orderReportVO.setOrderCompletionRate(orderCompletionRate);
        orderReportVO.setTotalOrderCount(totalOrderCount);
        orderReportVO.setDateList(dateList.toString());
        return orderReportVO;
    }

    /**
     * 获取指定时间区间内的日期字符
     *
     * @param startDate
     * @param endDate
     * @return
     */
    private static String getDateRangeString(LocalDateTime startDate, LocalDateTime endDate) {
        List<String> dateStrings = new ArrayList<>();
        LocalDateTime currentDate = startDate;

        // 格式化器，定义日期格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        while (!currentDate.isAfter(endDate)) {
            dateStrings.add(currentDate.format(formatter));
            currentDate = currentDate.plusDays(1); // 增加一天
        }

        // 使用逗号连接日期字符串
        return String.join(",", dateStrings);
    }
}
