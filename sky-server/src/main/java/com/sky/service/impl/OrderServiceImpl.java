package com.sky.service.impl;

import com.fasterxml.jackson.databind.util.BeanUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Override
    @Transactional
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {
        Long userId = ordersSubmitDTO.getAddressBookId();
        //判断收收货地址是否为空，为空抛出异常
        AddressBook addressBook = addressBookMapper.getById(userId);
        if (addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        //判断购物车是否为空，为空抛出异常
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.selectShoppingCartList(ShoppingCart.builder()
                .userId(BaseContext.getCurrentId())
                .build());
        if (shoppingCartList == null || shoppingCartList.size() == 0) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        //插入订单数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPhone(addressBook.getPhone());
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setPayStatus(Orders.UN_PAID);
        orders.setConsignee(addressBook.getConsignee());
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setUserId(userId);
        String address = addressBook.getProvinceName() + addressBook.getCityName() + addressBook.getDistrictName() + addressBook.getDetail();
        orders.setAddress(address);
        log.info("开始插入订单数据...");
        orderMapper.insert(orders);
        //插入订单详细数据
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (ShoppingCart shoppingCart : shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(shoppingCart, orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetailList.add(orderDetail);
        }
        log.info("开始插入订单详细数据...");
        orderDetailMapper.insertBatch(orderDetailList);
        //清空购物车
        log.info("下单后删除购物车中的数据");
        shoppingCartMapper.delete(ShoppingCart.builder().userId(userId).build());
        //封装VO对象返回
        return OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .orderTime(LocalDateTime.now())
                .build();
    }

    /**
     * 查询订单详情
     *
     * @param id
     * @return
     */
    @Override
    public OrderVO getOrderDetailById(Long id) {
        Orders orders = orderMapper.getById(id);
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(orderDetailList);
        return orderVO;
    }

    /**
     * 查询历史订单
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO) {
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<OrderVO> page = orderMapper.pageQuery(ordersPageQueryDTO);
        for (OrderVO orderVO : page) {
            List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orderVO.getId());
            StringJoiner orderDishes=new StringJoiner("、");
            for (OrderDetail orderDetail : orderDetailList) {
                if (orderDetail.getDishFlavor()!=null){
                    orderDishes.add(orderDetail.getName()+"-"+orderDetail.getDishFlavor());
                }else {
                    orderDishes.add(orderDetail.getName());
                }
            }
            orderVO.setOrderDetailList(orderDetailList);
            orderVO.setOrderDishes(orderDishes.toString());
        }
        return PageResult.builder()
                .total(page.getTotal())
                .records(page.getResult())
                .build();
    }

    /**
     * 取消订单
     * @param ordersCancelDTO
     */
    @Override
    public void cancelOrder(OrdersCancelDTO ordersCancelDTO) {
        Orders orders = Orders.builder()
                .status(Orders.CANCELLED)
                .id(ordersCancelDTO.getId())
                .cancelReason(ordersCancelDTO.getCancelReason())
                .cancelTime(LocalDateTime.now())
                .build();
        orderMapper.update(orders);
    }

    /**
     * 再来一单
     * @param id
     */
    @Override
    @Transactional
    public void oneMoreOrder(Long id) {
        //根据订单id查询订单信息
        Orders orders = orderMapper.getById(id);
        //订单不存在抛出异常
        if (orders==null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        //订单存在修改基础信息
        orders.setOrderTime(LocalDateTime.now());
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setPayStatus(Orders.UN_PAID);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setEstimatedDeliveryTime(LocalDateTime.now().plusHours(1));
        orders.setCancelTime(null);
        orders.setCancelReason(null);
        orders.setRejectionReason(null);
        orders.setDeliveryTime(null);
        //重新插入订单信息
        orderMapper.insert(orders);
        //查询订单的详细信息
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);
        for (OrderDetail orderDetail : orderDetailList) {
            orderDetail.setOrderId(orders.getId());
        }
        //重新插入订单的详细信息
        orderDetailMapper.insertBatch(orderDetailList);
    }

    /**
     * 各个状态的订单数量统计
     * @return
     */
    @Override
    public OrderStatisticsVO getOrderStatistics() {
        OrderStatisticsVO orderStatisticsVO=new OrderStatisticsVO();
        List<Integer> statusList=orderMapper.getAllOrderStatus();
        if (statusList!=null&&statusList.size()>0) {
            Integer toBeConfirmedCount=0;
            Integer confirmedCount=0;
            Integer deliveryInProgressCount=0;
            for (Integer status : statusList) {
                if (status==Orders.TO_BE_CONFIRMED){
                    toBeConfirmedCount++;
                }else if (status==Orders.CONFIRMED){
                    confirmedCount++;
                }else if (status==Orders.DELIVERY_IN_PROGRESS){
                    deliveryInProgressCount++;
                }
            }
            orderStatisticsVO.setToBeConfirmed(toBeConfirmedCount);
            orderStatisticsVO.setConfirmed(confirmedCount);
            orderStatisticsVO.setDeliveryInProgress(deliveryInProgressCount);
        }
        return orderStatisticsVO;
    }

    /**
     * 接单
     * @param ordersConfirmDTO
     */
    @Override
    public void confirmOrder(OrdersConfirmDTO ordersConfirmDTO) {
        Orders orders=new Orders();
        orders.setId(ordersConfirmDTO.getId());
        orders.setStatus(Orders.CONFIRMED);
        orderMapper.update(orders);
    }
}
