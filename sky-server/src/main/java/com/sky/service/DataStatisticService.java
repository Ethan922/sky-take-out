package com.sky.service;

import com.sky.dto.DataOverViewQueryDTO;
import com.sky.vo.OrderReportVO;

public interface DataStatisticService {

    OrderReportVO orderStatistic(DataOverViewQueryDTO dataOverViewQueryDTO);
}
