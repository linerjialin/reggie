package com.liner.reggie.mapper;

import com.liner.reggie.entity.Employee;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Administrator
* @description 针对表【employee(员工信息)】的数据库操作Mapper
* @createDate 2022-05-01 16:44:42
* @Entity com.liner.reggie.entity.Employee
*/
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {


}
