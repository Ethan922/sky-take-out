package com.sky.controller.admin;

import com.sky.dto.DataOverViewQueryDTO;
import com.sky.result.Result;
import com.sky.service.DataStatisticService;
import com.sky.vo.OrderReportVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/admin/report")
@Slf4j
public class DataStatisticController {

    @Autowired
    private DataStatisticService dataStatisticService;
    @GetMapping("/ordersStatistics")
    public Result<OrderReportVO> orderStatistic(DataOverViewQueryDTO dataOverViewQueryDTO){
        log.info("订单数据统计");
        OrderReportVO orderReportVO=dataStatisticService.orderStatistic(dataOverViewQueryDTO);
        return Result.success(orderReportVO);
    }
}
