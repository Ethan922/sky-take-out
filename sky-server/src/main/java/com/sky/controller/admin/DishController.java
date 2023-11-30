package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.print.DocFlavor;

@Slf4j
@RestController
@RequestMapping("admin/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping
    public Result createDish(@RequestBody DishDTO dishDTO){
        log.info("新增菜品：{}",dishDTO.getName());
        dishService.createDish(dishDTO);
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
    public Result<DishDTO> getById(@PathVariable Long id){
        log.info("根据菜品id查询菜品信息，id:{}",id);
        DishDTO dishDTO = dishService.getById(id);
        return Result.success(dishDTO);
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
        return Result.success();
    }

    /**
     * 批量删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result deleteDishes(Long[] ids){
        dishService.deleteDishes(ids);
        return Result.success();
    }
}
