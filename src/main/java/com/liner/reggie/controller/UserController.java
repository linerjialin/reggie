package com.liner.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.liner.reggie.common.R;
import com.liner.reggie.entity.User;
import com.liner.reggie.service.UserService;
import com.liner.reggie.utils.SMSUtils;
import com.liner.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@RequestMapping("/user")
@RestController
public class UserController {

  @Autowired private UserService userService;

  @PostMapping("/sendMsg")
  public R<String> sendMsg(@RequestBody User user, HttpSession session) {
    // 获取手机号
    String phone = user.getPhone();
    if (StringUtils.isNotEmpty(phone)) {
      // 生成验证码
      String code = ValidateCodeUtils.generateValidateCode(4).toString();
      log.info("code={}", code);
      // 调用工具类 发送短信
      // SMSUtils.sendMessage("外卖", "", phone, code);
      // 生成的验证码保存Session
      session.setAttribute(phone, code);

      return R.success("手机验证码短信发送成功");
    }

    return R.error("手机验证码短信发送失败");
  }

  @PostMapping("/login")
  public R<User> sendMsg(@RequestBody Map map, HttpSession session) {

    log.info(map.toString());

    // 获取手机号
    String phone = map.get("phone").toString();
    // 获取验证码
    String code = map.get("code").toString();
    // Session中获取保存的验证码
    Object codeInSession = session.getAttribute(phone);
    // 验证码校验
    if (codeInSession != null && codeInSession.equals(code)) {
      // 比对成功则登陆成功
      // 新用户则自动注册
      LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
      queryWrapper.eq(User::getPhone, phone);
      User user = userService.getOne(queryWrapper);
      if (user == null) {
        // 新用户则自动注册
        user = new User();
        user.setPhone(phone);
        user.setStatus(1);
        userService.save(user);
      }
      session.setAttribute("user",user.getId());
      return R.success(user);
    }
    return R.error("登录失败");
  }
}
