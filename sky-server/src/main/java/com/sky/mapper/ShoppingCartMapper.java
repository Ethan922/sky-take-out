package com.sky.mapper;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ShoppingCartMapper {


    /**
     * 添加购物车
     * @param shoppingCart
     */
    void insert(ShoppingCart shoppingCart);

    /**
     * 根据菜品id或套餐id查询购物车信息
     * @param shoppingCart
     * @return
     */
    ShoppingCart selectByDishIdOrSetmealId(ShoppingCart shoppingCart);

    /**
     * 更新同样的菜品或者套餐的数量
     * @param shoppingCart
     */
    @Update("update shopping_cart set number = #{number} where id=#{id};")
    void updateNumber(ShoppingCart shoppingCart);

}
