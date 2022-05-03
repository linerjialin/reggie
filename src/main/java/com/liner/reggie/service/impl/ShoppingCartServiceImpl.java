package com.liner.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liner.reggie.entity.ShoppingCart;
import com.liner.reggie.service.ShoppingCartService;
import com.liner.reggie.mapper.ShoppingCartMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【shopping_cart(购物车)】的数据库操作Service实现
* @createDate 2022-05-01 16:44:43
*/
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart>
implements ShoppingCartService{

}
