package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Slf4j
@RestController("adminDishController")
@RequestMapping("admin/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping
    public Result createDish(@RequestBody DishDTO dishDTO){
        log.info("新增菜品：{}",dishDTO.getName());
        dishService.createDish(dishDTO);
        //清除缓存
        cleanCache("dish_"+dishDTO.getCategoryId());
        return Result.success();
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询");
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据id查询菜品信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("根据菜品id查询菜品信息，id:{}",id);
        DishVO dishVO = dishService.getById(id);
        return Result.success(dishVO);
    }

    /**
     * 修改菜品信息
     * @param dishDTO
     * @return
     */
    @PutMapping
    public Result editDish(@RequestBody DishDTO dishDTO){
        log.info("修改菜品信息，菜品名：{}",dishDTO.getName());
        dishService.editDish(dishDTO);
        //清除缓存
        cleanCache("dish_*");
        return Result.success();
    }

    /**
     * 批量删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result deleteDishes(Long[] ids){
        log.info("批量删除菜品{}",ids);
        dishService.deleteDishes(ids);
        //清除缓存
        cleanCache("dish_*");
        return Result.success();
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    public Result<List<Dish>> getByCategoryId(Long categoryId){
        log.info("根据分类id查询菜品，id：{}",categoryId);
        List<Dish> dishes = dishService.getByCategoryId(categoryId);
        return Result.success(dishes);
    }

    /**
     * 停售或启售菜品
     * @param status
     * @return
     */
    @PostMapping("/status/{status}")
    public Result changeStatus(@PathVariable Integer status,Long id){
        log.info("停售或启售菜品");
        dishService.changStatus(status,id);
        //清除缓存
        cleanCache("dish_*");
        return Result.success();
    }

    private void cleanCache(String pattern){
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);

    }
}
