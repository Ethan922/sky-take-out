package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /**
     * 批量插入菜品的口味信息
     * @param dishFlavors
     */
    void insertFlavor(List<DishFlavor> dishFlavors);

    /**
     * 根据菜品id批量删除口味信息
     * @param dishIds
     */
    void deleteDishFlavor(Long[] dishIds);

    /**
     * 根据菜品id查询菜品口味
     * @param dishId
     * @return
     */
    @Select("select * from dish_flavor where dish_id=#{dishId};")
    List<DishFlavor> selectFlavor(Long dishId);
}
