package com.liner.reggie.service;

import com.liner.reggie.dto.SetmealDto;
import com.liner.reggie.entity.Setmeal;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Administrator
* @description 针对表【setmeal(套餐)】的数据库操作Service
* @createDate 2022-05-01 16:44:43
*/
public interface SetmealService extends IService<Setmeal> {

   //新增套餐，同时保存和套餐和菜品的关联关系
    public void saveWithDish(SetmealDto setmealDto);


    //删除套餐，同时保存和套餐和菜品的关联关系
    public void removeWithDish(List<Long> ids);

}
