package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import com.sky.utils.AliOssUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;


    /**
     * 新增菜品
     * @param dishDTO
     */
    @Override
    public void createDish(DishDTO dishDTO) {
        Dish dish=new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dish.setStatus(StatusConstant.ENABLE);
//        dish.setCreateUser(BaseContext.getCurrentId());
//        dish.setUpdateUser(BaseContext.getCurrentId());
//        dish.setCreateTime(LocalDateTime.now());
//        dish.setUpdateTime(LocalDateTime.now());
        dishMapper.insert(dish);
    }
}
