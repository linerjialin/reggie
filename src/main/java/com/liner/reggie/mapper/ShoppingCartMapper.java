package com.liner.reggie.mapper;

import com.liner.reggie.entity.ShoppingCart;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Administrator
* @description 针对表【shopping_cart(购物车)】的数据库操作Mapper
* @createDate 2022-05-01 16:44:43
* @Entity com.liner.reggie.entity.ShoppingCart
*/
@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {


}
