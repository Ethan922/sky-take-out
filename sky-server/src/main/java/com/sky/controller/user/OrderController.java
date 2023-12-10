package com.sky.controller.user;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("userOrderController")
@RequestMapping("/user/order")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    @PostMapping("/submit")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        log.info("用户下单，订单信息：{}",ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.submit(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    /**
     * 查询订单详细信息
     * @param id
     * @return
     */
    @GetMapping("/orderDetail/{id}")
    public Result<OrderVO> getOrderDetailById(@PathVariable Long id){
        log.info("查询顶单详，订单id：{}",id);
        OrderVO orderVO=orderService.getOrderDetailById(id);
        return Result.success(orderVO);
    }

    /**
     * 查询历史订单
     * @param ordersPageQueryDTO
     * @return
     */
    @GetMapping("/historyOrders")
    public Result<PageResult> historyOrders(OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("查询历史订单");
        PageResult pageResult = orderService.pageQuery(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 取消订单
     * @param id
     * @return
     */
    @PutMapping("/cancel/{id}")
    public Result cancelOrder(@PathVariable Long id){
        log.info("取消订单，订单id：{}",id);
        orderService.cancelOrder(id);
        return Result.success();
    }

    /**
     * 再来一单
     * @param id
     * @return
     */
    @PostMapping("/repetition/{id}")
    public Result oneMoreOrder(@PathVariable Long id){
        log.info("再来一单，订单id：{}",id);
        orderService.oneMoreOrder(id);
        return Result.success();
    }
}
