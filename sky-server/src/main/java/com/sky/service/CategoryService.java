package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.result.PageResult;

public interface CategoryService {

    void createCategory(CategoryDTO categoryDTO);

    void changeStatus(Integer status,Long id);

    PageResult page(CategoryPageQueryDTO categoryPageQueryDTO);

    void deleteCategory(Long id);
}
