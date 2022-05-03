package com.liner.reggie.service;

import com.liner.reggie.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Administrator
* @description 针对表【category(菜品及套餐分类)】的数据库操作Service
* @createDate 2022-05-01 16:44:42
*/
public interface CategoryService extends IService<Category> {

    public void remove(Long id);
}
