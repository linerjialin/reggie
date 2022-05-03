package com.liner.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liner.reggie.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Administrator
* @description 针对表【address_book(地址管理)】的数据库操作Mapper
* @createDate 2022-05-01 16:44:42
* @Entity com.liner.reggie.entity.AddressBook
*/
@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {


}
