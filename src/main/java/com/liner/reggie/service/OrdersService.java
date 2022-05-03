package com.liner.reggie.service;

import com.liner.reggie.entity.Orders;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Administrator
* @description 针对表【orders(订单表)】的数据库操作Service
* @createDate 2022-05-01 16:44:43
*/
public interface OrdersService extends IService<Orders> {

    void submit(Orders orders);
}
