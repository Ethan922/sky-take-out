package com.sky.controller.admin;

import com.sky.dto.DataOverViewQueryDTO;
import com.sky.result.Result;
import com.sky.service.DataStatisticService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/admin/report")
@Slf4j
public class DataStatisticController {

    @Autowired
    private DataStatisticService dataStatisticService;

    /**
     * 订单数据统计
     *
     * @param dataOverViewQueryDTO
     * @return
     */
    @GetMapping("/ordersStatistics")
    public Result<OrderReportVO> orderStatistic(DataOverViewQueryDTO dataOverViewQueryDTO) {
        log.info("订单数据统计时间范围：{}", dataOverViewQueryDTO);
        OrderReportVO orderReportVO = dataStatisticService.orderStatistic(dataOverViewQueryDTO);
        return Result.success(orderReportVO);
    }

    /**
     * 营业额数据统计
     *
     * @param dataOverViewQueryDTO
     * @return
     */
    @GetMapping("/turnoverStatistics")
    public Result<TurnoverReportVO> turnoverStatistic(DataOverViewQueryDTO dataOverViewQueryDTO) {
        log.info("营业额数据统计，时间范围：{}", dataOverViewQueryDTO);
        TurnoverReportVO turnoverReportVO = dataStatisticService.turnoverStatistic(dataOverViewQueryDTO);
        return Result.success(turnoverReportVO);
    }

    /**
     * 用户数据统计
     *
     * @param dataOverViewQueryDTO
     * @return
     */
    @GetMapping("/userStatistics")
    public Result<UserReportVO> userStatistic(DataOverViewQueryDTO dataOverViewQueryDTO) {
        log.info("营业额数据统计，时间范围：{}", dataOverViewQueryDTO);
        UserReportVO userReportVO = dataStatisticService.userStatistic(dataOverViewQueryDTO);
        return Result.success(userReportVO);
    }

    @GetMapping("/top10")
    public Result<SalesTop10ReportVO> getTop10Dishes(DataOverViewQueryDTO dataOverViewQueryDTO){
        log.info("获取销量排行前十的菜品，时间范围：{}", dataOverViewQueryDTO);
        SalesTop10ReportVO salesTop10ReportVO=dataStatisticService.getTop10Goods(dataOverViewQueryDTO);
        return Result.success(salesTop10ReportVO);
    }

}
