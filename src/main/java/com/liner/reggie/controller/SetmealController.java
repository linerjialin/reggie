package com.liner.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liner.reggie.common.R;
import com.liner.reggie.dto.SetmealDto;
import com.liner.reggie.entity.Category;
import com.liner.reggie.entity.Dish;
import com.liner.reggie.entity.Setmeal;
import com.liner.reggie.service.CategoryService;
import com.liner.reggie.service.SetmealDishService;
import com.liner.reggie.service.SetmealService;
import com.sun.org.apache.bcel.internal.generic.NEW;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

  @Autowired private SetmealService setmealService;

  @Autowired private SetmealDishService setmealDishService;

  @Autowired private CategoryService categoryService;

  @GetMapping("/page")
  public R<Page> page(int page, int pageSize, String name) {
    // 分页构造器
    Page<Setmeal> pageInfo = new Page<>(page, pageSize);
    Page<SetmealDto> setmealDtoPage = new Page<>();

    // 条件构造器
    LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
    // 添加条件
    queryWrapper.like(name != null, Setmeal::getName, name);

    // 排序条件
    queryWrapper.orderByDesc(Setmeal::getUpdateTime);

    // 执行查询
    setmealService.page(pageInfo, queryWrapper);

    // 对象拷贝
    BeanUtils.copyProperties(pageInfo, setmealDtoPage, "records");
    List<Setmeal> records = pageInfo.getRecords();

    List<SetmealDto> list =
        records.stream()
            .map(
                (item) -> {
                  SetmealDto setmealDto = new SetmealDto();
                  BeanUtils.copyProperties(item, setmealDto);
                  Long categoryId = item.getCategoryId(); // 分类id
                  // 根据分类id查询分类对象
                  Category category = categoryService.getById(categoryId);
                  if (category != null) {
                    // 分类名称
                    String categoryName = category.getName();
                    setmealDto.setCategoryName(categoryName);
                  }

                  return setmealDto;
                })
            .collect(Collectors.toList());

    setmealDtoPage.setRecords(list);
    return R.success(setmealDtoPage);
  }

  /**
   * 新增套餐
   *
   * @return
   */
  @PostMapping
  public R<String> save(@RequestBody SetmealDto setmealDto) {
    log.info("新增套餐{}", setmealDto);
    setmealService.saveWithDish(setmealDto);

    return R.success("新增套餐成功");
  }

  /***
   * 删除套餐
   * @param ids
   * @return
   */
  @DeleteMapping()
  public R<String> delete(@RequestParam List<Long> ids) {
    log.info("ids{}", ids);
    setmealService.removeWithDish(ids);
    return R.success("套餐数据删除成功");
  }

  /**
   * 根据条件查询套餐的数据
   * @param setmeal
   * @return
   */
  @GetMapping("/list")
  public R<List<Setmeal>> list( Setmeal setmeal){
    LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
    queryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());
    queryWrapper.orderByDesc(Setmeal::getUpdateTime);

    List<Setmeal> list = setmealService.list(queryWrapper);
    return R.success(list);
  }

}
