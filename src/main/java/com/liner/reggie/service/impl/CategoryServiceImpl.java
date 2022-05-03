package com.liner.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liner.reggie.common.exception.CustomException;
import com.liner.reggie.entity.Category;
import com.liner.reggie.entity.Dish;
import com.liner.reggie.entity.Setmeal;
import com.liner.reggie.service.CategoryService;
import com.liner.reggie.mapper.CategoryMapper;
import com.liner.reggie.service.DishService;
import com.liner.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Administrator
 * @description 针对表【category(菜品及套餐分类)】的数据库操作Service实现
 * @createDate 2022-05-01 16:44:42
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
    implements CategoryService {

  @Autowired private DishService dishService;

  @Autowired private SetmealService setmealService;

  /**
   * 根据id删除分类，删除之前进行判断
   *
   * @param id
   */
  @Override
  public void remove(Long id) {
    LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
    // 查询关联菜品，已经关联抛出异常
    dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
    int count1 = dishService.count(dishLambdaQueryWrapper);
    if (count1 > 0) {
      // 已经关联的菜品，抛出业务异常
      throw new CustomException("当前分类下关联了菜品，不能删除");
    }

    LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
    // 查询关联套餐，已经关联抛出异常
    setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
    int count2 = setmealService.count(setmealLambdaQueryWrapper);
    if (count2 > 0) {
      // 已经关联的套餐，抛出业务异常
      throw new CustomException("当前分类下关联了套餐，不能删除");
    }

    // 正常删除
    super.removeById(id);
  }
}
