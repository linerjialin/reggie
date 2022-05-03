package com.liner.reggie.mapper;

import com.liner.reggie.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Administrator
* @description 针对表【user(用户信息)】的数据库操作Mapper
* @createDate 2022-05-01 16:44:43
* @Entity com.liner.reggie.entity.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {


}
