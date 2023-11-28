package com.sky.service;

import com.sky.dto.CategoryDTO;
import org.springframework.stereotype.Service;

public interface CategoryService {

    void createCategory(CategoryDTO categoryDTO);

    void changeStatus(Integer status,Long id);
}
