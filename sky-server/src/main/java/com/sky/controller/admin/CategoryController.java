package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     * @param categoryDTO
     * @return
     */
    @PostMapping
    public Result createCategory(@RequestBody CategoryDTO categoryDTO){
        log.info("新增分类，分类名称：{}",categoryDTO.getName());
        categoryService.createCategory(categoryDTO);
        return Result.success();
    }

    /**
     * 启用或禁用分类
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    public Result changeStatus(@PathVariable Integer status, Long id){
        log.info("启用或禁用分类，分类id：{}",id);
        categoryService.changeStatus(status,id);
        return Result.success();
    }

}
