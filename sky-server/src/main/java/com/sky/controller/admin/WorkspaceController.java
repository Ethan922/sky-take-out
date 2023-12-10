package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.WorkspaceService;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/workspace")
@Slf4j
public class WorkspaceController {
    @Autowired
    private WorkspaceService workspaceService;

    /**
     * 套餐信息总览
     * @return
     */
    @GetMapping("/overviewSetmeals")
    public Result<SetmealOverViewVO> overviewSetmeals(){
        log.info("套餐信息总览");
        SetmealOverViewVO setmealOverViewVO = workspaceService.overviewSetmeals();
        return Result.success(setmealOverViewVO);
    }

    /**
     * 菜品信息总览
     * @return
     */
    @GetMapping("overviewDishes")
    public Result<DishOverViewVO> overviewDishes(){
        log.info("菜品信息总览");
        DishOverViewVO dishOverViewVO = workspaceService.overviewDishes();
        return Result.success(dishOverViewVO);
    }

    /**
     * 订单信息总览
     * @return
     */
    @GetMapping("/overviewOrders")
    public Result<OrderOverViewVO> overviewOrders(){
        log.info("订单信息总览");
        OrderOverViewVO orderOverViewVO=workspaceService.overviewOrders();
        return Result.success(orderOverViewVO);
    }

}
