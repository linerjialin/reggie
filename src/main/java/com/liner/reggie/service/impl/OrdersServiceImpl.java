package com.liner.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liner.reggie.common.BaseContext;
import com.liner.reggie.common.exception.CustomException;
import com.liner.reggie.entity.*;
import com.liner.reggie.service.*;
import com.liner.reggie.mapper.OrdersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @description 针对表【orders(订单表)】的数据库操作Service实现
 * @createDate 2022-05-01 16:44:43
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

  @Autowired private ShoppingCartService shoppingCartService;

  @Autowired private UserService userService;

  @Autowired private AddressBookService addressBookService;

  @Autowired private OrderDetailService orderDetailService;
  /**
   * 用户下单
   *
   * @param orders
   */
  @Override
  @Transactional
  public void submit(Orders orders) {
    // 获得当前用户id
    Long userId = BaseContext.getCurrentId();

    // 查询当前用户的购物车数据
    LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(ShoppingCart::getUserId, userId);
    List<ShoppingCart> shoppingCarts = shoppingCartService.list(wrapper);

    if (shoppingCarts == null || shoppingCarts.size() == 0) {
      throw new CustomException("购物车为空,无法下单");
    }

    // 查询用户数据
    User user = userService.getById(userId);

    // 查询用户地址数据
    Long addressBookId = orders.getAddressBookId();
    AddressBook addressBook = addressBookService.getById(addressBookId);

    if (addressBook == null) {
      throw new CustomException("地址信息有误,无法下单");
    }

    // 完成下单 插入数据 一条
    long orderId = IdWorker.getId(); // 订单号
    // 向订单号插入数据  一条数据

    // 金额的遍历
    AtomicInteger amount = new AtomicInteger(0); // 原子操作保证线程安全

    List<OrderDetail> orderDetails =
        shoppingCarts.stream()
            .map(
                item -> {
                  OrderDetail orderDetail = new OrderDetail();
                  orderDetail.setOrderId(orderId);
                  orderDetail.setNumber(item.getNumber());
                  orderDetail.setDishFlavor(item.getDishFlavor());
                  orderDetail.setDishId(item.getDishId());
                  orderDetail.setSetmealId(item.getSetmealId());
                  orderDetail.setName(item.getName());
                  orderDetail.setImage(item.getImage());
                  orderDetail.setAmount(item.getAmount());
                  amount.addAndGet(
                      item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
                  return orderDetail;
                })
            .collect(Collectors.toList());

    orders.setId(orderId);
    orders.setOrderTime(LocalDateTime.now());
    orders.setCheckoutTime(LocalDateTime.now());
    orders.setStatus(2);
    orders.setAmount(new BigDecimal(amount.get())); // 总金额
    orders.setUserId(userId);
    orders.setNumber(String.valueOf(orderId));
    orders.setUserName(user.getName());
    orders.setConsignee(addressBook.getConsignee());
    orders.setPhone(addressBook.getPhone());
    orders.setAddress(
        (addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
            + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
            + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
            + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
    // 向订单表插入数据，一条数据
    this.save(orders);

    // 向明细表插入数据  多条
    orderDetailService.saveBatch(orderDetails);

    // 清空购物车
    shoppingCartService.remove(wrapper);
  }
}
