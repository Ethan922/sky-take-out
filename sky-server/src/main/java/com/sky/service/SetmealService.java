package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {

    void createSetmeal(SetmealDTO setmealDTO);

    SetmealVO selectBySetmealId(Long id);

    PageResult page(SetmealPageQueryDTO setmealPageQueryDTO);

    void editSetmeal(SetmealVO setmealVO);

    void changstatus(Integer status, Long id);

    void deleteSetmeals(Long[] ids);

    List<Setmeal> getByCategoryId(Long categoryId);

    List<DishItemVO> selectDishesBySetmealId(Long dishId);
}
