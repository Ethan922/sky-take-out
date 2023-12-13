package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.annotation.AutoFill;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.exception.CategoryNameDuplicateException;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.plaf.basic.BasicSeparatorUI;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private DishMapper dishMapper;

    /**
     * 新增分类
     *
     * @param categoryDTO
     */
    @Override
    public void createCategory(CategoryDTO categoryDTO) {
        Long categoryId = categoryMapper.selectCategorIdByCategoryName(categoryDTO.getName());
        if (categoryId != null) {
            throw new CategoryNameDuplicateException(MessageConstant.CATEGORY_NAME_DUPLICATE);
        }
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        category.setStatus(StatusConstant.ENABLE);
        categoryMapper.insert(category);
    }

    /**
     * 禁用或启用分类
     *
     * @param status
     * @param id
     */
    @Override
    public void changeStatus(Integer status, Long id) {
        Category category = Category.builder()
                .status(status)
                .id(id)
                .build();

        categoryMapper.update(category);
    }

    /**
     * 分类分页查询
     *
     * @param categoryPageQueryDTO
     * @return
     */
    @Override
    public PageResult page(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        Page<Category> page = categoryMapper.pageQuery(categoryPageQueryDTO);

        return PageResult.builder()
                .total(page.getTotal())
                .records(page.getResult()).build();
    }

    /**
     * 根据id删除分类,同时删除该分类下的菜品
     *
     * @param id
     */
    @Override
    @Transactional
    public void deleteCategory(Long id) {
        //判断该分类下是否关联了菜品
        List<Dish> dishes = dishMapper.selectByCategoryId(id);
        if (dishes.size() != 0) {
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }
        int setmealCount = setmealMapper.selectByCategoryId(id);
        if (setmealCount!=0){
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }
        categoryMapper.delete(id);
    }

    /**
     * 根据类型查询分类
     *
     * @param type
     * @return
     */
    @Override
    public List<Category> typeQuery(Integer type) {
        return categoryMapper.typeQuery(type);
    }

    /**
     * 修改分类信息
     *
     * @param categoryDTO
     */
    @Override
    public void editCategory(CategoryDTO categoryDTO) {
        Long categoryId = categoryMapper.selectCategorIdByCategoryName(categoryDTO.getName());
        if (categoryId != null && !categoryId.equals(categoryDTO.getId())) {
            throw new CategoryNameDuplicateException(MessageConstant.CATEGORY_NAME_DUPLICATE);
        }
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        categoryMapper.update(category);
    }

    /**
     * 用户端根据类型查询enable状态下的分类
     * @param type
     * @return
     */
    @Override
    public List<Category> typeQueryOfEnable(Integer type) {
        return categoryMapper.typeQueryOfEnable(type);
    }
}
