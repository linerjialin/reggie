package com.liner.reggie.service;

import com.liner.reggie.dto.DishDto;
import com.liner.reggie.entity.Dish;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Administrator
* @description 针对表【dish(菜品管理)】的数据库操作Service
* @createDate 2022-05-01 16:44:42
*/
public interface DishService extends IService<Dish> {

    //新增菜品,同时插入菜品对应的口味数据  操作两张表
    public void saveWithFlavor(DishDto dishDto);

    //根据id查询菜品信息和对应的口味信息
    public DishDto getByIdWithFlavor(Long id);

    //修改菜品,同时更新菜品对应的口味数据  操作两张表
    public void updateWithFlavor(DishDto dishDto);


}
