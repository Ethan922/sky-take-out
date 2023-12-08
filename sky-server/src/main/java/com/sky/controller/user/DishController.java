package com.sky.controller.user;

import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userDishController")
@Slf4j
@RequestMapping("/user/dish")
public class DishController {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private DishService dishService;
    @GetMapping("/list")
    public Result<List<DishVO>> getDishesByCategory(Long categoryId){
        log.info("根据分类id查询菜品，categoryId:{}",categoryId);
        List<DishVO> dishes;
        String key="dish_"+categoryId;
        //缓存菜品到redis中
        dishes = (List<DishVO>) redisTemplate.opsForValue().get(key);
        //redis存在
        if (dishes!=null&&dishes.size()>0){
            return Result.success(dishes);
        }
        //redis中不存在，将菜品缓存到redis中
        dishes = dishService.getByCategoryIdForUser(categoryId);
        redisTemplate.opsForValue().set(key,dishes);
        return Result.success(dishes);
    }
}
