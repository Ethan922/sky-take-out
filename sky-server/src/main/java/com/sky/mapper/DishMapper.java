package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
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
    Page<DishDTO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

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

    /**
     * 更新菜品信息
     * @param dishDTO
     */
    @AutoFill(OperationType.UPDATE)
    void updateDish(DishDTO dishDTO);

    /**
     * 更新菜品时根据菜品id删除所有口味信息
     * @param id
     */
    @Delete("delete from dish_flavor where dish_id=#{id}")
    void deleteDishFlavor(Long id);

    /**
     * 批量删除菜品
     * @param ids
     */
    void deleteDishes(Long[] ids);

    /**
     * 根据分类id查询菜品
     * @param id
     * @return
     */
    @Select("select * from dish where category_id=#{id};")
    List<Dish> selectByCategoryId(Long id);
}
