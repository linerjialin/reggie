package com.liner.reggie.mapper;

import com.liner.reggie.entity.OrderDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【order_detail(订单明细表)】的数据库操作Mapper
* @createDate 2022-05-01 16:44:42
* @Entity com.liner.reggie.entity.OrderDetail
*/
@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {


}
