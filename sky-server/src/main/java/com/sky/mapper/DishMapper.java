package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 新增菜品
     * @param dish
     */
    @AutoFill(OperationType.INSERT)
    void insert(Dish dish);


    /**
     * 新增菜品时插入菜品口味
     * @param dishFlavor
     */
    void insertFlavor(DishFlavor dishFlavor);

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    Page<Dish> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据菜品id查询菜品信息
     * @param id
     * @return
     */
    @Select("select dish.id, dish.name, dish.category_id, dish.price, dish.image, dish.description, dish.status, dish.update_time, category.name category_name from dish,category where dish.id=#{id} and category_id=category.id;")
    DishDTO selectById(Long id);

    /**
     * 根据菜品id查询菜品口味
     * @param id
     * @return
     */
    @Select("select * from dish_flavor where dish_id=#{id};")
    List<DishFlavor> selectFlavor(Long id);
}
