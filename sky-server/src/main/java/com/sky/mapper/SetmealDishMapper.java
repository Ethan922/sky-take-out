package com.sky.mapper;

import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
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

    /**
     * 根据套餐id查询套餐关联的菜品信息
     * @param setmealId
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id=#{setmealId};")
    List<SetmealDish> selectBySetmealId(Long setmealId);

    /**
     * 根据套餐id批量删除套餐关联的菜品信息
     * @param setmealIds
     */
    void deleteDishesBySetmealId(Long[] setmealIds);

    /**
     * 根据套餐id查询套餐关联的菜品id
     * @param setmealId
     * @return
     */
    @Select("select dish_id from setmeal_dish where setmeal_id=#{setmealId};")
    Long[] selectDishIdsBySetmealId(Long setmealId);

    /**
     * 根据分类id查询套餐
     * @param categoryId
     * @return
     */
    @Select("select * from setmeal where category_id=#{categoryId};")
    List<Setmeal> selectByCategoryId(Long categoryId);
}
