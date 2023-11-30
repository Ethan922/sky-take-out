package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.annotation.AutoFill;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import com.sky.exception.DishNameDuplicateException;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
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
        Page<DishDTO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 根据菜品id查询菜品信息
     * @param id
     * @return
     */
    @Override
    public DishDTO getById(Long id) {
        DishDTO dishDTO = dishMapper.selectById(id);
        List<DishFlavor> dishFlavors = dishMapper.selectFlavor(id);
        dishDTO.setFlavors(dishFlavors);
        return dishDTO;
    }

    /**
     * 修改菜品信息
     * @param dishDTO
     */
    @Override
    public void editDish(DishDTO dishDTO) {
        try {
            dishMapper.updateDish(dishDTO);
        } catch (Exception e) {
            throw new DishNameDuplicateException(MessageConstant.DISH_NAME_DUPLICATE);
        }
        log.info("修改菜品信息前，删除菜品口味信息");
        dishMapper.deleteDishFlavor(dishDTO.getId());
        List<DishFlavor> flavors = dishDTO.getFlavors();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishDTO.getId());
            //重新插入菜品口味信息
            log.info("重新插入菜品口味信息");
            dishMapper.insertFlavor(flavor);
        }

    }

    /**
     * 批量删除菜品，同时删除菜品的所有口味信息
     * @param ids
     */
    @Override
    @Transactional
    public void deleteDishes(Long[] ids) {
        dishMapper.deleteDishes(ids);
        for (Long id : ids) {
            dishMapper.deleteDishFlavor(id);
        }
    }

    /**
     * 根据分类id查询菜品
     * @param id
     * @return
     */
    @Override
    public List<Dish> getByCategoryId(Long id) {
        return dishMapper.selectByCategoryId(id);
    }

    /**
     * 停售或启售菜品
     * @param status
     */
    @Override
    public void changStatus(Integer status,Long id) {
        DishDTO dishDTO = DishDTO.builder().status(status).id(id).build();
        dishMapper.updateDish(dishDTO);
    }
}
