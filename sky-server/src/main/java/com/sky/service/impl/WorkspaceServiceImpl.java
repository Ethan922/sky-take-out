package com.sky.service.impl;

import com.sky.mapper.SetmealMapper;
import com.sky.mapper.WorkspaceMapper;
import com.sky.service.SetmealService;
import com.sky.service.WorkspaceService;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {

    @Autowired
    private WorkspaceMapper workspaceMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 统计启售套餐与未启售套餐的数量
     * @return
     */
    @Override
    public SetmealOverViewVO overviewSetmeals() {
        Integer discontinued = setmealMapper.selectCountOfDisableSetmeals();
        Integer sold = setmealMapper.selectCountOfEnableSetmeals();
        return SetmealOverViewVO.builder().discontinued(discontinued).sold(sold).build();
    }
}
