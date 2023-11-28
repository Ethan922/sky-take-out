package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

public interface CategoryService {

    void createCategory(CategoryDTO categoryDTO);

    void changeStatus(Integer status,Long id);

    PageResult page(CategoryPageQueryDTO categoryPageQueryDTO);

    void deleteCategory(Long id);

    List<Category> typeQuery(Integer type);

    void editCategory(CategoryDTO categoryDTO);
}
