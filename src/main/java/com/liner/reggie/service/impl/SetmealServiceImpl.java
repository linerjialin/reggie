package com.liner.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liner.reggie.common.exception.CustomException;
import com.liner.reggie.dto.SetmealDto;
import com.liner.reggie.entity.Setmeal;
import com.liner.reggie.entity.SetmealDish;
import com.liner.reggie.service.SetmealDishService;
import com.liner.reggie.service.SetmealService;
import com.liner.reggie.mapper.SetmealMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @description 针对表【setmeal(套餐)】的数据库操作Service实现
 * @createDate 2022-05-01 16:44:43
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal>
    implements SetmealService {

  @Autowired private SetmealDishService setmealDishService;

  @Override
  @Transactional
  public void saveWithDish(SetmealDto setmealDto) {
    // 保存套餐的基本信息 setmeal 执行insert
    this.save(setmealDto);

    List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
    setmealDishes.stream()
        .map(
            (item) -> {
              item.setSetmealId(setmealDto.getId());
              return item;
            })
        .collect(Collectors.toList());

    // 保存套餐和菜品的关联信息 操作setmeal_dish  执行insert
    setmealDishService.saveBatch(setmealDto.getSetmealDishes());
  }

  @Override
  @Transactional
  public void removeWithDish(List<Long> ids) {
    // 查询套餐状态，确保是否可以删除
    LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

    queryWrapper.in(Setmeal::getId, ids);
    queryWrapper.eq(Setmeal::getStatus, 1);

    int count = this.count(queryWrapper);
    if (count > 0) {
      // 不能删除，抛出一个异常
      throw new CustomException("套餐正在售卖中，不能删除");
    }

    // 可以删除，删除套餐中的数据
    this.removeByIds(ids);

    // 删除表中关系
    LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
    lambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);

    setmealDishService.remove(lambdaQueryWrapper);
  }
}
