package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.SetmealNameDulpicateException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;
    /**
     * 新增套餐信息
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void createSetmeal(SetmealDTO setmealDTO) {
        if (setmealMapper.selectSetmealIdBySetmealName(setmealDTO.getName())!=null){
            //根据修改的套餐名查询套餐id，判断是否为空，如果不为空则说明修改的套餐名在数据库中已存在
            throw new SetmealNameDulpicateException(MessageConstant.SETMEAL_NAME_DUPLICATE);
        }
        Setmeal setmeal=new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        //设置套餐的默认状态为停售
        setmeal.setStatus(StatusConstant.DISABLE);
        //插入套餐的基本信息
        setmealMapper.insert(setmeal);
        //插入套餐包含的菜品信息
        List<SetmealDish> setmealDishes=setmealDTO.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmeal.getId());
        }
        setmealDishMapper.insert(setmealDishes);
    }

    /**
     * 根据套餐id查询套餐信息
     * @param id
     */
    @Override
    public SetmealVO selectBySetmealId(Long id) {
        //查询套餐的基本信息
        SetmealVO setmealVO = setmealMapper.selectBySetmealId(id);
        //查询套餐关联的菜品信息
        List<SetmealDish> setmealDishes = setmealDishMapper.selectBySetmealId(id);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    /**
     * 分页查询套餐信息
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult page(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> result = setmealMapper.pageQuery(setmealPageQueryDTO);
        return PageResult.builder().total(result.getTotal()).records(result.getResult()).build();
    }

    /**
     * 修改套餐信息
     * @param setmealVO
     */
    @Override
    @Transactional
    public void editSetmeal(SetmealVO setmealVO) {
        Long id = setmealMapper.selectSetmealIdBySetmealName(setmealVO.getName());
        if (id!=null&&id!=setmealVO.getId()){
            //根据修改的套餐名查询套餐id，判断与原id是否一致，如果不一致则说明修改的套餐名与原套餐名不一致，且在表中已存在
            throw new SetmealNameDulpicateException(MessageConstant.SETMEAL_NAME_DUPLICATE);
        }
        //更新套餐的基本信息（setmeal表）
        Setmeal setmeal=new Setmeal();
        BeanUtils.copyProperties(setmealVO,setmeal);
        setmealMapper.update(setmeal);
        //先将套餐原来关联的菜品信息删除（setmeal_dish表）
        Long[] setmealIds={setmealVO.getId()};
        setmealDishMapper.deleteDishesBySetmealId(setmealIds);
        //重新插入套餐关联的菜品信息（setmeal_dish表）
        List<SetmealDish> setmealDishes = setmealVO.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealVO.getId());
        }
        setmealDishMapper.insert(setmealDishes);
    }

    /**
     * 启售或停售套餐
     * @param status
     * @param id
     */
    @Override
    public void changstatus(Integer status, Long id) {
        Setmeal setmeal = Setmeal.builder().status(status).id(id).build();
        setmealMapper.update(setmeal);
    }
}
