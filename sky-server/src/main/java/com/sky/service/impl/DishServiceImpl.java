package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DishNameDuplicateException;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.utils.AliOssUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;

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
        try {
            Dish dish=new Dish();
            BeanUtils.copyProperties(dishDTO,dish);
            dish.setStatus(StatusConstant.ENABLE);
            dishMapper.insert(dish);
            List<DishFlavor> flavors = dishDTO.getFlavors();
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(dish.getId());
                dishMapper.insertFlavor(flavor);
            }
        } catch (Exception e) {
            throw new DishNameDuplicateException(MessageConstant.DISH_NAME_DUPLICATE);
        }
//        dish.setCreateUser(BaseContext.getCurrentId());
//        dish.setUpdateUser(BaseContext.getCurrentId());
//        dish.setCreateTime(LocalDateTime.now());
//        dish.setUpdateTime(LocalDateTime.now());
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<Dish> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }
}
