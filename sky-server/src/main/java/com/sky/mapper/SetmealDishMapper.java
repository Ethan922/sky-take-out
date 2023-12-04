package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品id查询套餐中包含的菜品数量
     * @param ids
     * @return
     */
    Long selectByDishId(Long[] ids);
}
