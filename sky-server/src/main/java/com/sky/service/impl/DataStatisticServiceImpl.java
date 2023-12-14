package com.sky.service.impl;

import com.sky.dto.DataOverViewQueryDTO;
import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.DataStatisticService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import io.netty.util.internal.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class DataStatisticServiceImpl implements DataStatisticService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WorkspaceService workspaceService;

    /**
     * 订单数据统计
     *
     * @param dataOverViewQueryDTO
     * @return
     */
    @Override
    public OrderReportVO orderStatistic(DataOverViewQueryDTO dataOverViewQueryDTO) {
        //获取日期列表
        List<LocalDate> dateList = getDateList(dataOverViewQueryDTO.getBegin(), dataOverViewQueryDTO.getEnd());
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();

        for (LocalDate beginDate : dateList) {
            //获取当前日期的时间范围
            LocalDateTime begin = LocalDateTime.of(beginDate, LocalTime.MIN);
            LocalDateTime end = LocalDateTime.of(beginDate, LocalTime.MAX);

            Integer totalOrderCount = getOrderCount(begin, end, null);
            Integer validOrderCount = getOrderCount(begin, end, Orders.COMPLETED);

            orderCountList.add(totalOrderCount);
            validOrderCountList.add(validOrderCount);

        }

        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();

        Double orderCompletionRate = totalOrderCount == 0 ? 0.0 : validOrderCount * 1.0 / totalOrderCount;

        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }


    private Integer getOrderCount(LocalDateTime begin, LocalDateTime end, Integer status) {
        Map map = new HashMap<>();
        map.put("begin", begin);
        map.put("end", end);
        map.put("status", status);
        return orderMapper.statisticByMap(map);
    }


    /**
     * 营业额数据统计
     * @param dataOverViewQueryDTO
     * @return
     */
    @Override
    public TurnoverReportVO turnoverStatistic(DataOverViewQueryDTO dataOverViewQueryDTO) {
        List<LocalDate> dateList = getDateList(dataOverViewQueryDTO.getBegin(), dataOverViewQueryDTO.getEnd());
        List<Double> turnoverList = new ArrayList<>();

        for (LocalDate beginDate : dateList) {
            //获取当前日期的时间范围
            LocalDateTime begin = LocalDateTime.of(beginDate, LocalTime.MIN);
            LocalDateTime end = LocalDateTime.of(beginDate, LocalTime.MAX);

            Map map = new HashMap<>();
            map.put("begin", begin);
            map.put("end", end);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.getTurnoverByMap(map);
            turnover = turnover == null ? 0.0 : turnover;

            turnoverList.add(turnover);

        }

        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    /**
     * 用户数据统计
     * @param dataOverViewQueryDTO
     * @return
     */
    @Override
    public UserReportVO userStatistic(DataOverViewQueryDTO dataOverViewQueryDTO) {
        List<LocalDate> dateList = getDateList(dataOverViewQueryDTO.getBegin(), dataOverViewQueryDTO.getEnd());
        List<Integer> totalUserList = new ArrayList<>();
        List<Integer> newUserList = new ArrayList<>();


        for (LocalDate beginDate : dateList) {
            //获取当前日期的时间范围
            LocalDateTime begin = LocalDateTime.of(beginDate, LocalTime.MIN);
            LocalDateTime end = LocalDateTime.of(beginDate, LocalTime.MAX);

            Integer totalUser = getUserCount(null,end);
            Integer newUser = getUserCount(begin,end);

            totalUserList.add(totalUser);
            newUserList.add(newUser);
        }

        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .totalUserList(StringUtils.join(totalUserList,","))
                .newUserList(StringUtils.join(newUserList,","))
                .build();
    }

    /**
     * 获取指定时间范围内销量排行前十的菜品
     * @param dataOverViewQueryDTO
     * @return
     */
    @Override
    public SalesTop10ReportVO getTop10Goods(DataOverViewQueryDTO dataOverViewQueryDTO) {
        LocalDateTime begin=LocalDateTime.of(dataOverViewQueryDTO.getBegin(),LocalTime.MIN);
        LocalDateTime end=LocalDateTime.of(dataOverViewQueryDTO.getEnd(),LocalTime.MAX);

        List<String> nameList=new ArrayList<>();
        List<Integer> numberList=new ArrayList<>();

        List<GoodsSalesDTO> goodsSalesDTOList=orderMapper.getTop10(begin,end);

        for (GoodsSalesDTO goodsSalesDTO : goodsSalesDTOList) {
            nameList.add(goodsSalesDTO.getName());
            numberList.add(goodsSalesDTO.getNumber());
        }


        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(nameList,","))
                .numberList(StringUtils.join(numberList,","))
                .build();
    }

    /**
     * 导出Excel报表
     * @param response
     */
    @Override
    public void export(HttpServletResponse response) throws IOException {
        //读取模板表
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("template\\运营数据报表模板.xlsx");
        XSSFWorkbook xssfWorkbook=new XSSFWorkbook(is);

        //指定时间
        LocalDate begin=LocalDate.now().minusDays(30);
        LocalDate end=LocalDate.now().minusDays(1);


        //创建表单
        XSSFSheet sheet = xssfWorkbook.getSheet("Sheet1");

        //填入时间区间
        XSSFRow row = sheet.getRow(1);
        row.getCell(1).setCellValue("时间:"+begin+"至"+end);

        //获取运营数据
        BusinessDataVO businessDataVO = workspaceService.getBusinessData(LocalDateTime.of(begin, LocalTime.MIN), LocalDateTime.of(end, LocalTime.MAX));

        //填入概览数据
        row=sheet.getRow(3);
        row.getCell(2).setCellValue(businessDataVO.getTurnover());
        row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
        row.getCell(6).setCellValue(businessDataVO.getNewUsers());
        row=sheet.getRow(4);
        row.getCell(2).setCellValue(businessDataVO.getValidOrderCount());
        row.getCell(4).setCellValue(businessDataVO.getUnitPrice());

        for (int i = 0; i < 30; i++) {
            row=sheet.getRow(7+i);
            LocalDate date=begin.plusDays(i);
            BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
            row.getCell(1).setCellValue(date.toString());
            row.getCell(2).setCellValue(businessData.getTurnover());
            row.getCell(3).setCellValue(businessData.getValidOrderCount());
            row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row.getCell(5).setCellValue(businessData.getUnitPrice());
            row.getCell(6).setCellValue(businessData.getNewUsers());
        }

        //将文件写入响应输出流
        xssfWorkbook.write(response.getOutputStream());

        is.close();
        xssfWorkbook.close();
    }

    private Integer getUserCount(LocalDateTime begin, LocalDateTime end) {
        Map map=new HashMap<>();
        map.put("begin",begin);
        map.put("end",end);

        return userMapper.getUserCountByMap(map);
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
