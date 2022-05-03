package com.liner.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liner.reggie.entity.Employee;
import com.liner.reggie.service.EmployeeService;
import com.liner.reggie.mapper.EmployeeMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【employee(员工信息)】的数据库操作Service实现
* @createDate 2022-05-01 16:44:42
*/
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee>
implements EmployeeService{

}
