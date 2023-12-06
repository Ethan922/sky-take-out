package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.WorkspaceMapper;
import com.sky.service.SetmealService;
import com.sky.service.WorkspaceService;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {

    @Autowired
    private WorkspaceMapper workspaceMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private DishMapper dishMapper;

    /**
     * 统计启售套餐与未启售套餐的数量
     * @return
     */
    @Override
    public SetmealOverViewVO overviewSetmeals() {
        Integer discontinued = setmealMapper.selectCountOfSetmealsByStatus(StatusConstant.DISABLE);
        Integer sold = setmealMapper.selectCountOfSetmealsByStatus(StatusConstant.ENABLE);
        return SetmealOverViewVO.builder().discontinued(discontinued).sold(sold).build();
    }

    /**
     * 统计起售和未启售菜品的数量
     * @return
     */
    @Override
    public DishOverViewVO overviewDishes() {
        Integer discontinued = dishMapper.selectCountOfDishesByStatus(StatusConstant.DISABLE);
        Integer sold = dishMapper.selectCountOfDishesByStatus(StatusConstant.ENABLE);
        return DishOverViewVO.builder().discontinued(discontinued).sold(sold).build();
    }
}
