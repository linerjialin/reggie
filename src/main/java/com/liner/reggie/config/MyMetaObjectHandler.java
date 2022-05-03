package com.liner.reggie.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.liner.reggie.common.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
  /**
   * 插入自动填充
   * @param metaObject
   */
  @Override
  public void insertFill(MetaObject metaObject) {
    log.info("公共字段自动填充【insert】");
    log.info(metaObject.toString());
    metaObject.setValue("createTime", LocalDateTime.now());
    metaObject.setValue("updateTime", LocalDateTime.now());
    metaObject.setValue("createUser", BaseContext.getThreadLocal());
    metaObject.setValue("updateUser", BaseContext.getThreadLocal());


  }

  /**
   * 更新自动填充
   * @param metaObject
   */
  @Override
  public void updateFill(MetaObject metaObject) {
    log.info("公共字段自动填充【update】");
    log.info(metaObject.toString());
    metaObject.setValue("updateTime", LocalDateTime.now());
    metaObject.setValue("updateUser", BaseContext.getThreadLocal());
  }
}
