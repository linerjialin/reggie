package com.liner.reggie.mapper;

import com.liner.reggie.entity.Category;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Administrator
* @description 针对表【category(菜品及套餐分类)】的数据库操作Mapper
* @createDate 2022-05-01 16:44:42
* @Entity com.liner.reggie.entity.Category
*/
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {


}
