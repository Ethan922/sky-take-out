package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.result.PageResult;
import org.springframework.stereotype.Service;

public interface CategoryService {

    void createCategory(CategoryDTO categoryDTO);

    void changeStatus(Integer status,Long id);

    PageResult page(CategoryPageQueryDTO categoryPageQueryDTO);
}
