package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;

import java.util.List;

public interface DishService {

    void createDish(DishDTO dishDTO);

    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    DishDTO getById(Long id);

    void editDish(DishDTO dishDTO);

    void deleteDishes(Long[] ids);

    List<Dish> getByCategoryId(Long id);
}
