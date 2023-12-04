package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
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
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据菜品id查询菜品信息
     * @param id
     * @return
     */
    @Select("select dish.id, dish.name, dish.category_id, dish.price, dish.image, dish.description, dish.status, dish.update_time, category.name category_name from dish,category where dish.id=#{id} and category_id=category.id;")
    DishVO selectById(Long id);



    /**
     * 更新菜品信息
     * @param dish
     */
    @AutoFill(OperationType.UPDATE)
    void updateDish(Dish dish);


    /**
     * 批量删除菜品
     * @param ids
     */
    void deleteDishes(Long[] ids);

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @Select("select * from dish where category_id=#{categoryId};")
    List<Dish> selectByCategoryId(Long categoryId);

    /**
     * 根据菜品名称查询菜品shuliang
     * @param dishName
     * @return
     */
    @Select("select count(*) from dish where name=#{dishName};")
    Long selectByDishName(String dishName);
}
