package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DishNameDuplicateException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;

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
        if (dishMapper.selectByDishName(dishDTO.getName()) != 0) {
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
     * @param id
     * @return
     */
    @Override
    public DishDTO getById(Long id) {
        DishDTO dishDTO = dishMapper.selectById(id);
        if (dishDTO != null) {
            List<DishFlavor> dishFlavors = dishFlavorMapper.selectFlavor(id);
            dishDTO.setFlavors(dishFlavors);
        }
        return dishDTO;
    }

    /**
     * 修改菜品信息
     *
     * @param dishDTO
     */
    @Override
    @Transactional
    public void editDish(DishDTO dishDTO) {
        try {
            dishMapper.updateDish(dishDTO);
        } catch (Exception e) {
            throw new DishNameDuplicateException(MessageConstant.DISH_NAME_DUPLICATE);
        }
        log.info("修改菜品信息前，删除菜品口味信息");
        Long[] ids={dishDTO.getId()};
        dishFlavorMapper.deleteDishFlavor(ids);
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors!=null&&flavors.size()>0) {
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
        dishMapper.deleteDishes(ids);
        dishFlavorMapper.deleteDishFlavor(ids);
    }

    /**
     * 根据分类id查询菜品
     *
     * @param id
     * @return
     */
    @Override
    public List<Dish> getByCategoryId(Long id) {
        return dishMapper.selectByCategoryId(id);
    }

    /**
     * 停售或启售菜品
     *
     * @param status
     */
    @Override
    public void changStatus(Integer status, Long id) {
        DishDTO dishDTO = DishDTO.builder().status(status).id(id).build();
        dishMapper.updateDish(dishDTO);
    }
}
