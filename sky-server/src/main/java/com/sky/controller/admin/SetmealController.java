package com.sky.controller.admin;

import com.github.pagehelper.Page;
import com.google.j2objc.annotations.RetainedWith;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/admin/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @PostMapping
    public Result createSetmeal(@RequestBody SetmealDTO setmealDTO){
        log.info("新增套餐{}",setmealDTO);
        setmealService.createSetmeal(setmealDTO);
        return Result.success();
    }

    /**
     * 根据套餐id查询套餐信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<SetmealVO> selectBySetmealId(@PathVariable Long id){
        log.info("根据套餐id查询套餐信息，id{}",id);
        SetmealVO setmealVO = setmealService.selectBySetmealId(id);
        return Result.success(setmealVO);
    }

    /**
     * 分页查询套餐信息
     * @param setmealPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO){
        PageResult pageResult = setmealService.page(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 修改套餐信息
     * @param setmealVO
     * @return
     */
    @PutMapping
    public Result editSetmeal(@RequestBody SetmealVO setmealVO){
        log.info("修改套餐信息，id：{}",setmealVO.getId());
        setmealService.editSetmeal(setmealVO);
        return Result.success();

    }
}
