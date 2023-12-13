package com.sky.service;

import com.sky.dto.DataOverViewQueryDTO;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

public interface DataStatisticService {

    OrderReportVO orderStatistic(DataOverViewQueryDTO dataOverViewQueryDTO);

    TurnoverReportVO turnoverStatistic(DataOverViewQueryDTO dataOverViewQueryDTO);

    UserReportVO userStatistic(DataOverViewQueryDTO dataOverViewQueryDTO);

    SalesTop10ReportVO getTop10Goods(DataOverViewQueryDTO dataOverViewQueryDTO);
}
