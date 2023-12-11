package com.sky.service;

import com.sky.dto.DataOverViewQueryDTO;
import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;

public interface DataStatisticService {

    OrderReportVO orderStatistic(DataOverViewQueryDTO dataOverViewQueryDTO);

    TurnoverReportVO turnoverStatistic(DataOverViewQueryDTO dataOverViewQueryDTO);
}
