package com.sky.controller.user;

import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    /**
     * 根据套餐id查询包含的菜品
     * @return
     */
    @GetMapping("/dish/{id}")
    public Result<List<DishItemVO>> getDishesBySetmealId(@PathVariable Long id){
        log.info("根据套餐id查询包含的菜品,套餐id：{}",id);
        List<DishItemVO> dishItemVOList = setmealService.selectDishesBySetmealId(id);
        return Result.success(dishItemVOList);
    }
}
