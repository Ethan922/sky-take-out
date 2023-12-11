package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {

    void insert(Orders orders);

    @Select("select * from orders where id=#{id};")
    Orders getById(Long id);

    /**
     * 查询历史订单
     * @param ordersPageQueryDTO
     * @return
     */
    Page<OrderVO> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 更新订单数据
     * @param orders
     */
    void update(Orders orders);

    /**
     * 获取所有订单
     * @return
     */
    @Select("select * from orders;")
    List<Orders> getAllOrders();

    @Select("select * from orders where order_time between #{begin} and #{end};")
    List<Orders> getOrdersWithTimeBounds(LocalDateTime begin,LocalDateTime end);
}
