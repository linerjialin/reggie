package com.liner.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liner.reggie.common.R;
import com.liner.reggie.entity.Category;
import com.liner.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 添加分类
     * @param category
     * @return
     */
    @PostMapping("")
    public R<String> save(@RequestBody Category category) {
        // log.info("添加分类{}",employee.toString());
        categoryService.save(category);

        return R.success("新增分类成功");
    }

    /**
     * 根据id删除菜品消息
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<Category> delete(Long ids) {
        //log.info("根据员工id获取员工信息");
        // 获得当前对象的id
        //categoryService.removeById(id);
        categoryService.remove(ids);



        return R.error("分类信息删除成功");
    }

    /**
     * 菜品分类分页查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize) {
        //log.info("page={},pageSize={}", page, pageSize);

        // 构造分页构造器
        Page<Category> pageInfo = new Page<>(page,pageSize);

        // 构造条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

        // 排序条件
        queryWrapper.orderByAsc(Category::getSort);

        // 执行查询
        categoryService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 根据id修改菜品信息
     */
    @PutMapping("")
    public R<String> update(@RequestBody Category category) {
        // log.info("员工信息{}",employee.toString());

        // 获得当前对象的id
      categoryService.updateById(category);
        log.info("修改分类信息{}", category.toString());

        return R.success("修改分类信息成功");
    }

    /**
     * 根据条件查询分类数据
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.eq(category.getType() != null ,Category::getType,category.getType());

        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(queryWrapper);
        log.info(list.toString());
        return R.success(list);
    }
}


