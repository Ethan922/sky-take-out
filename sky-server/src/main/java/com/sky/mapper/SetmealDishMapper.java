package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品id查询套餐中包含的菜品数量
     * @param ids
     * @return
     */
    Long selectByDishId(Long[] ids);

    /**
     * 插入套餐中关联的菜品信息
     * @param setmealDishes
     */
    void insert(List<SetmealDish> setmealDishes);

    @Select("select * from setmeal_dish where setmeal_id=#{setmealId};")
    List<SetmealDish> selectBySetmealId(Long setmealId);
}
