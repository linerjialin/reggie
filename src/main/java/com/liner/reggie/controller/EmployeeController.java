package com.liner.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liner.reggie.common.R;
import com.liner.reggie.entity.Employee;
import com.liner.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

  @Autowired private EmployeeService employeeService;

  /**
   * 员工登录
   *
   * @param request
   * @param employee
   * @return
   */
  @PostMapping("/login")
  public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
    // 将页面提交的密码MD5加密
    String password = employee.getPassword();
    password = DigestUtils.md5DigestAsHex(password.getBytes());

    // 根据用户名查询用户数据库
    LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(Employee::getUsername, employee.getUsername());
    Employee emp = employeeService.getOne(queryWrapper);

    // 判断查询失败
    if (emp == null) {
      return R.error("登录失败");
    }

    // 密码比对 不一致
    if (!emp.getPassword().equals(password)) {
      return R.error("登录失败");
    }

    // 查看员工状态  0为禁用 1为启用
    if (emp.getStatus() == 0) {
      return R.error("账号已禁用");
    }

    // 登陆成功  将员工id存入Session并返回结果
    request.getSession().setAttribute("employee", emp.getId());
    return R.success(emp);
  }

  /**
   * 员工退出
   *
   * @param request
   * @return
   */
  @PostMapping("/logout")
  public R<String> logout(HttpServletRequest request) {
    request.getSession().removeAttribute("employee");
    return R.success("退出成功");
  }

  /**
   * 添加员工信息
   *
   * @param request
   * @param employee
   * @return
   */
  @PostMapping("")
  public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
    // log.info("新增员工信息{}",employee.toString());

    // 设置MD5加密的初始密码
    employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//    employee.setCreateTime(LocalDateTime.now());
//    employee.setUpdateTime(LocalDateTime.now());

    // 获得当前对象的id
    Long empId = (Long) request.getSession().getAttribute("employee");
//    employee.setCreateUser(empId);
//    employee.setUpdateUser(empId);

    employeeService.save(employee);
    log.info("新增员工信息{}", employee.toString());

    return R.success("新增员工成功");
  }

  /**
   * 根据id查询员工消息
   *
   * @param id
   * @return
   */
  @GetMapping("/{id}")
  public R<Employee> getById(@PathVariable Long id) {
    log.info("根据员工id获取员工信息");
    // 获得当前对象的id
    Employee employee = employeeService.getById(id);
    if (employee != null) {
      return R.success(employee);
    }

    return R.error("没有查询到员工消息");
  }

  /**
   * 员工信息分页查询
   *
   * @param page
   * @param pageSize
   * @param name
   * @return
   */
  @GetMapping("/page")
  public R<Page> page(int page, int pageSize, String name) {
    log.info("page={},pageSize={},name={}", page, pageSize, name);

    // 构造分页构造器
    Page pageInfo = new Page(page, pageSize);

    // 构造条件构造器
    LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();

    // 过滤条件
    queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getUsername, name);
    // 排序条件
    queryWrapper.orderByDesc(Employee::getUpdateTime);

    // 执行查询
    employeeService.page(pageInfo, queryWrapper);

    return R.success(pageInfo);
  }

  /**
   * 根据id修改员工信息
   *
   * @param request
   * @param employee
   * @return
   */
  @PutMapping("")
  public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
    // log.info("员工信息{}",employee.toString());

    // 获得当前对象的id
    Long empId = (Long) request.getSession().getAttribute("employee");
    employee.setUpdateTime(LocalDateTime.now());
    employee.setUpdateUser(empId);

    employeeService.updateById(employee);
    log.info("修改员工信息{}", employee.toString());

    return R.success("修改员工信息成功");
  }
}
