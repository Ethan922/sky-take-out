package com.sky.controller.user;

import com.sky.entity.Category;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userCategoryController")
@Slf4j
@RequestMapping("/user/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    @GetMapping("/list")
    public Result<List<Category>> getCategoryByType(Integer type){
        log.info("根据type获取分类，type：{}",type);
        List<Category> categories = categoryService.typeQuery(type);
        return Result.success(categories);
    }
}
