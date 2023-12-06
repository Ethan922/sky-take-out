package com.sky.service;

import com.sky.vo.DishOverViewVO;
import com.sky.vo.SetmealOverViewVO;

public interface WorkspaceService {
    SetmealOverViewVO overviewSetmeals();

    DishOverViewVO overviewDishes();
}
