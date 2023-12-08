package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.DishNameDuplicateException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.jdbc.Null;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    /**
     * 新增菜品
     *
     * @param dishDTO
     */
    @Transactional//保证菜品信息和口味数据均插入成功
    @Override
    public void createDish(DishDTO dishDTO) {
        //判断菜品名称是否重复
        if (dishMapper.selectDishIdByDishName(dishDTO.getName()) != null) {
            //根据修改的菜品名称查询菜品id，如果菜品id不为空,说明菜品名与表中其他菜品名重复
            throw new DishNameDuplicateException(MessageConstant.DISH_NAME_DUPLICATE);
        }
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        //设置菜品的默认状态为停售
        dish.setStatus(StatusConstant.DISABLE);
        dishMapper.insert(dish);
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            Long dishId = dish.getId();
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(dishId));
            dishFlavorMapper.insertFlavor(flavors);
        }
    }

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 根据菜品id查询菜品信息
     *
     * @param id
     * @return
     */
    @Override
    public DishVO getById(Long id) {
        DishVO dishVO = dishMapper.selectById(id);
        if (dishVO != null) {
            List<DishFlavor> dishFlavors = dishFlavorMapper.selectFlavor(id);
            dishVO.setFlavors(dishFlavors);
        }
        return dishVO;
    }

    /**
     * 修改菜品信息
     *
     * @param dishDTO
     */
    @Override
    @Transactional
    public void editDish(DishDTO dishDTO) {
        Long dishId = dishMapper.selectDishIdByDishName(dishDTO.getName());
        if (dishId!=null&&dishId!=dishDTO.getId()) {
            //根据修改的菜品名称查询菜品id，如果菜品id不为空，并且不等于菜品原来的id，说明修改的名称与表中其他菜品的名称重复
            throw new DishNameDuplicateException(MessageConstant.DISH_NAME_DUPLICATE);
        }
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.updateDish(dish);
        log.info("修改菜品信息前，删除菜品口味信息");
        Long[] dishIds = {dishDTO.getId()};
        dishFlavorMapper.deleteDishFlavor(dishIds);
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            log.info("重新插入菜品口味信息");
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(dishDTO.getId());
                //重新插入菜品口味信息
            }
            dishFlavorMapper.insertFlavor(flavors);
        }

    }

    /**
     * 批量删除菜品，同时删除菜品的所有口味信息
     *
     * @param ids
     */
    @Override
    @Transactional
    public void deleteDishes(Long[] ids) {
        //判断是否是启售菜品
        for (Long id : ids) {
            DishVO dishVO = dishMapper.selectById(id);
            if (dishVO.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //判断菜品是否关联了套餐
        Long dishCount = setmealDishMapper.selectByDishId(ids);
        if (dishCount!=0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //删除口味信息
        dishFlavorMapper.deleteDishFlavor(ids);
        dishMapper.deleteDishes(ids);

    }

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @Override
    public List<Dish> getByCategoryId(Long categoryId) {
        return dishMapper.selectByCategoryId(categoryId);
    }

    /**
     * 用户端根据分类id查询菜品
     * @param categoryId
     * @return
     */
    public List<DishVO> getByCategoryIdForUser(Long categoryId){
        List<DishVO> dishes = dishMapper.selectByCategoryIdForUser(categoryId);
        if (dishes!=null){
            for (DishVO dish : dishes) {
                List<DishFlavor> dishFlavors = dishFlavorMapper.selectFlavor(dish.getId());
                dish.setFlavors(dishFlavors);
            }
        }
        return dishes;
    }

    /**
     * 停售或启售菜品
     *
     * @param status
     */
    @Override
    public void changStatus(Integer status, Long id) {
        Dish dish = Dish.builder().status(status).id(id).build();
        dishMapper.updateDish(dish);
    }
}
