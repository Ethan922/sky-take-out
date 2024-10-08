package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart=new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentUserId());
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.selectShoppingCartList(shoppingCart);
        //购物车中存在同样的商品则数量加1
        if (shoppingCartList!=null&&shoppingCartList.size()>0){
            ShoppingCart cart = shoppingCartList.get(0);
            cart.setNumber(cart.getNumber()+1);
            shoppingCartMapper.updateNumber(cart);
        }else {
            Long dishId=shoppingCartDTO.getDishId();
            if (dishId!=null) {
                DishVO dishVO = dishMapper.selectById(dishId);
                shoppingCart.setName(dishVO.getName());
                shoppingCart.setImage(dishVO.getImage());
                shoppingCart.setAmount(dishVO.getPrice());
            }
            Long setmealId=shoppingCartDTO.getSetmealId();
            if (setmealId!=null) {
                SetmealVO setmealVO = setmealMapper.selectBySetmealId(setmealId);
                shoppingCart.setName(setmealVO.getName());
                shoppingCart.setImage(setmealVO.getImage());
                shoppingCart.setAmount(setmealVO.getPrice());
            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        }
    }

    /**
     * 查看购物车
     * @return
     */
    @Override
    public List<ShoppingCart> getShoppingCartList() {
        return shoppingCartMapper.selectShoppingCartList(ShoppingCart.builder()
                .userId(BaseContext.getCurrentUserId())
                .build());
    }

    /**
     * 清空购物车
     */
    @Override
    public void cleanShoppingCart() {
        shoppingCartMapper.delete(ShoppingCart.builder()
                .userId(BaseContext.getCurrentUserId())
                .build());
    }

    /**
     * 删除购物车中的一件商品
     * @param shoppingCartDTO
     */
    @Override
    public void deleteOne(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart=new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        //查询该商品在购物车中有几份，如果大于一份，更新购物车中的商品数量
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.selectShoppingCartList(shoppingCart);
        if (shoppingCartList==null)return;
        ShoppingCart cart = shoppingCartList.get(0);
        if (cart.getNumber()>1){
            cart.setNumber(cart.getNumber()-1);
            shoppingCartMapper.updateNumber(cart);
        }else {
            shoppingCart.setUserId(BaseContext.getCurrentUserId());
            shoppingCartMapper.delete(shoppingCart);
        }

    }
}
