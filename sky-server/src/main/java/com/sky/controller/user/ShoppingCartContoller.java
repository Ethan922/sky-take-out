package com.sky.controller.user;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("userShoppiingCartController")
@Slf4j
@RequestMapping("/user/shoppingCart")
public class ShoppingCartContoller {


    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     * @param shoppingCartDTO
     * @return
     */
    @PostMapping("/add")
    public Result addShoppingCart(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("添加购物车，商品信息为：{}",shoppingCartDTO);
        shoppingCartService.addShoppingCart(shoppingCartDTO);
        return Result.success();
    }

    /**
     * 查看购物车
     * @return
     */
    @GetMapping("/list")
    public Result<List<ShoppingCart>> getShoppingCartList(){
        log.info("查看购物车,当前用户id：{}",BaseContext.getCurrentId());
        List<ShoppingCart> shoppingCartList = shoppingCartService.getShoppingCartList();
        return Result.success(shoppingCartList);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public Result cleanShopingCart(){
        log.info("清空购物车,当前用户id：{}", BaseContext.getCurrentId());
        shoppingCartService.cleanShoppingCart();
        return Result.success();
    }
}
