package com.sky.service.impl;

import com.sky.dto.DataOverViewQueryDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.DataStatisticService;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import io.netty.util.internal.StringUtil;
import org.apache.commons.lang3.StringUtils;
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
        //获取日期列表
        List<LocalDate> dateList=getDateList(dataOverViewQueryDTO.getBegin(),dataOverViewQueryDTO.getEnd());
        List<Integer> orderCountList=new ArrayList<>();
        List<Integer> validOrderCountList=new ArrayList<>();

        for (LocalDate beginDate : dateList) {
            //获取当前日期的时间范围
            LocalDateTime begin=LocalDateTime.of(beginDate,LocalTime.MIN);
            LocalDateTime end=LocalDateTime.of(beginDate,LocalTime.MAX);

            Integer totalOrderCount=getOrderCount(begin,end,null);
            Integer validOrderCount=getOrderCount(begin,end,Orders.COMPLETED);

            orderCountList.add(totalOrderCount);
            validOrderCountList.add(validOrderCount);

        }

        Integer totalOrderCount=orderCountList.stream().reduce(Integer::sum).get();
        Integer validOrderCount=validOrderCountList.stream().reduce(Integer::sum).get();

        Double orderCompletionRate=totalOrderCount==0?0.0:validOrderCount*1.0/totalOrderCount;

        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .orderCountList(StringUtils.join(orderCountList,","))
                .validOrderCountList(StringUtils.join(validOrderCountList,","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    private Integer getOrderCount(LocalDateTime begin, LocalDateTime end, Integer status) {
        Map map=new HashMap<>();
        map.put("begin",begin);
        map.put("end",end);
        map.put("status",status);
        return orderMapper.statisticByMap(map);
    }


    @Override
    public TurnoverReportVO turnoverStatistic(DataOverViewQueryDTO dataOverViewQueryDTO) {
        List<LocalDate> dateList=getDateList(dataOverViewQueryDTO.getBegin(),dataOverViewQueryDTO.getEnd());
        List<Double> turnoverList=new ArrayList<>();

        for (LocalDate beginDate : dateList) {
            //获取当前日期的时间范围
            LocalDateTime begin=LocalDateTime.of(beginDate,LocalTime.MIN);
            LocalDateTime end=LocalDateTime.of(beginDate,LocalTime.MAX);

            Map map=new HashMap<>();
            map.put("begin",begin);
            map.put("end",end);
            map.put("status",Orders.COMPLETED);
            Double turnover=orderMapper.getTurnoverByMap(map);
            turnover=turnover==null?0.0:turnover;

            turnoverList.add(turnover);

        }

        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .turnoverList(StringUtils.join(turnoverList,","))
                .build();
    }

    /**
     * 获取指定时间区间内的日期列表
     *
     * @param beginDate
     * @param endDate
     * @return
     */
    private static List<LocalDate> getDateList(LocalDate beginDate, LocalDate endDate) {
        List<LocalDate> dateList = new ArrayList<>();

        while (!beginDate.isAfter(endDate)) {
            dateList.add(beginDate);
            beginDate = beginDate.plusDays(1); // 增加一天
        }

        return dateList;
    }
}
