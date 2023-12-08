package com.sky.controller.user;

import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController("userSetmealController")
@Slf4j
@RequestMapping("user/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;
    @GetMapping("/list")
    public Result<List<Setmeal>> getSetmealsByCategoryId(Long categoryId){
        log.info("根据分类id查询套餐，分类id：{}",categoryId);
        List<Setmeal> setmeals = setmealService.getByCategoryId(categoryId);
        return Result.success(setmeals);
    }
}
