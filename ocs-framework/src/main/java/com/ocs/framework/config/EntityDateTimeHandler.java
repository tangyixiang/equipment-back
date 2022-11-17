package com.ocs.framework.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.ocs.common.utils.SecurityUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class EntityDateTimeHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        // setFieldValByName方法中参数分别为实体类的属性名、要填充的值，元数据对象
        this.setFieldValByName("createTime", LocalDateTime.now(), metaObject);
        this.setFieldValByName("createBy", SecurityUtils.getUsername() + "", metaObject);
        // this.setFieldValByName("updateBy", SecurityUtils.getUserId() + "", metaObject);
        // this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
        this.setFieldValByName("updateBy", SecurityUtils.getUsername() + "", metaObject);
    }
}
