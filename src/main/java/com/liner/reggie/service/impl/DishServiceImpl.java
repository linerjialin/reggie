package com.liner.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liner.reggie.dto.DishDto;
import com.liner.reggie.entity.Dish;
import com.liner.reggie.entity.DishFlavor;
import com.liner.reggie.service.DishFlavorService;
import com.liner.reggie.service.DishService;
import com.liner.reggie.mapper.DishMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @description 针对表【dish(菜品管理)】的数据库操作Service实现
 * @createDate 2022-05-01 16:44:42
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

  @Autowired private DishFlavorService dishFlavorService;

  @Override
  @Transactional
  public void saveWithFlavor(DishDto dishDto) {
    // 保存菜品的基本信息到菜品表dish
    this.save(dishDto);

    Long dishId = dishDto.getId(); // 菜品id

    // 菜品口味
    List<DishFlavor> flavors = dishDto.getFlavors();

    flavors.stream()
        .map(
            (item) -> {
              item.setDishId(dishId);
              return item;
            })
        .collect(Collectors.toList());

    // 保存菜品口味到菜品口味表dish_flavor
    dishFlavorService.saveBatch(dishDto.getFlavors());
  }

  @Override
  public DishDto getByIdWithFlavor(Long id) {
    // 查询菜品基本信息
    Dish dish = this.getById(id);

    DishDto dishDto = new DishDto();
    BeanUtils.copyProperties(dish, dishDto);

    // 查询当前菜品对应口味信息
    LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(DishFlavor::getDishId, dish.getId());
    List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
    dishDto.setFlavors(flavors);

    return dishDto;
  }

  @Override
  @Transactional
  public void updateWithFlavor(DishDto dishDto) {
    // 更新菜品的基本信息到菜品表dish
    this.updateById(dishDto);

    // 清理当前菜品的对应菜品口味
    LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
    dishFlavorService.remove(queryWrapper);

    //添加当前提交过来的口味数据
    List<DishFlavor> flavors = dishDto.getFlavors();
    flavors.stream()
            .map(
                    (item) -> {
                      item.setDishId(dishDto.getId());
                      return item;
                    })
            .collect(Collectors.toList());

    // 保存菜品口味到菜品口味表dish_flavor
    dishFlavorService.saveBatch(flavors);
  }
}
