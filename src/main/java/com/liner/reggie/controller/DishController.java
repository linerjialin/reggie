package com.liner.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liner.reggie.common.R;
import com.liner.reggie.dto.DishDto;
import com.liner.reggie.entity.Category;
import com.liner.reggie.entity.Dish;
import com.liner.reggie.entity.DishFlavor;
import com.liner.reggie.service.CategoryService;
import com.liner.reggie.service.DishFlavorService;
import com.liner.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/** 菜品管理 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

  @Autowired private DishService dishService;

  @Autowired private DishFlavorService dishFlavorService;

  @Autowired private CategoryService categoryService;

  @Autowired private RedisTemplate redisTemplate;

  /**
   * 新增菜品
   *
   * @param dishDto
   * @return
   */
  @PostMapping
  public R<String> save(@RequestBody DishDto dishDto) {
    log.info(dishDto.toString());
    dishService.saveWithFlavor(dishDto);

    // 清理菜品缓存数据
    /* Set keys = redisTemplate.keys("dish_*");
          redisTemplate.delete(keys);
    */
    // 精确清理
    // 动态构造key
    String key = "dish_" + dishDto.getCategoryId() + "_1";
    redisTemplate.delete(key);
    return R.success("新增菜品成功");
  }

  /**
   * 根据id查询菜品信息和口味信息
   *
   * @param id
   * @return
   */
  @GetMapping("/{id}")
  public R<DishDto> get(@PathVariable Long id) {
    DishDto dishDto = dishService.getByIdWithFlavor(id);
    return R.success(dishDto);
  }

  /**
   * 更新菜品信息
   *
   * @param dishDto
   * @return
   */
  @PutMapping
  public R<String> update(@RequestBody DishDto dishDto) {
    log.info(dishDto.toString());
    dishService.updateWithFlavor(dishDto);

    // 清理菜品缓存数据
    /* Set keys = redisTemplate.keys("dish_*");
          redisTemplate.delete(keys);
    */
    // 精确清理
    // 动态构造key
    String key = "dish_" + dishDto.getCategoryId() + "_1";
    redisTemplate.delete(key);

    return R.success("更改菜品成功");
  }

  /** */
  // @PostMapping("/status/{status}")

  /**
   * 菜品分类分页查询
   *
   * @param page
   * @param pageSize
   * @return
   */
  @GetMapping("/page")
  public R<Page> page(int page, int pageSize, String name) {
    // log.info("page={},pageSize={}", page, pageSize);

    // 构造分页构造器
    Page<Dish> pageInfo = new Page<>(page, pageSize);
    Page<DishDto> dishDtoPage = new Page<>();

    // 构造条件构造器
    LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

    // 添加过滤条件
    queryWrapper.like(name != null, Dish::getName, name);

    // 排序条件
    queryWrapper.orderByDesc(Dish::getUpdateTime);

    // 执行查询
    dishService.page(pageInfo, queryWrapper);

    // 对象拷贝
    BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

    List<Dish> records = pageInfo.getRecords();
    List<DishDto> list =
        records.stream()
            .map(
                (item) -> {
                  DishDto dishDto = new DishDto();

                  BeanUtils.copyProperties(item, dishDto);

                  Long categoryId = item.getCategoryId(); // 分类id
                  // 根据id查询分类对象
                  Category category = categoryService.getById(categoryId);
                  String categoryName = category.getName();
                  dishDto.setCategoryName(categoryName);
                  return dishDto;
                })
            .collect(Collectors.toList());
    dishDtoPage.setRecords(list);

    return R.success(dishDtoPage);
  }

  /**
   * 根据条件查询分类数据
   *
   * @param dish
   * @return
   */
  /* @GetMapping("/list")
  public R<List<Dish>> list(Dish dish){
      //条件构造器
      LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
      //添加条件
      queryWrapper.eq(dish.getCategoryId() != null ,Dish::getCategoryId,dish.getCategoryId());
      queryWrapper.eq(Dish::getStatus,1);

      //添加排序条件
      queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

      List<Dish> list = dishService.list(queryWrapper);

      return R.success(list);
  }*/

  /**
   * 根据条件查询分类数据 改造后
   *
   * @param dish
   * @return
   */
  @GetMapping("/list")
  public R<List<DishDto>> list(Dish dish) {
    List<DishDto> dishDtoList = null;

    // 动态构造key
    String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();
    // 从redis中获取缓存数据
    dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);

    if (dishDtoList != null) {
      // 获取到了就直接返回，不用查询数据库
      return R.success(dishDtoList);
    }

    // 条件构造器
    LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
    // 添加条件
    queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
    queryWrapper.eq(Dish::getStatus, 1);

    // 添加排序条件
    queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

    List<Dish> list = dishService.list(queryWrapper);
    dishDtoList =
        list.stream()
            .map(
                (item) -> {
                  DishDto dishDto = new DishDto();

                  BeanUtils.copyProperties(item, dishDto);

                  Long categoryId = item.getCategoryId(); // 分类id
                  // 根据id查询分类对象
                  Category category = categoryService.getById(categoryId);
                  if (category != null) {
                    String categoryName = category.getName();
                    dishDto.setCategoryName(categoryName);
                  }

                  Long dishId = item.getId(); // 菜品id

                  LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper =
                      new LambdaQueryWrapper<>();
                  dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId, dishId);
                  List<DishFlavor> dishFlavorList =
                      dishFlavorService.list(dishFlavorLambdaQueryWrapper);
                  dishDto.setFlavors(dishFlavorList);

                  return dishDto;
                })
            .collect(Collectors.toList());

    // 没找到就查询数据库
    redisTemplate.opsForValue().set(key, dishDtoList, 60, TimeUnit.MINUTES);

    return R.success(dishDtoList);
  }
}
