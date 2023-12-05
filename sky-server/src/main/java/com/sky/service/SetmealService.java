package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.vo.SetmealVO;

public interface SetmealService {

    void createSetmeal(SetmealDTO setmealDTO);

    SetmealVO selectBySetmealId(Long id);
}
