package com.ocs.busi.helper;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ocs.common.constant.CommonConstants;

/**
 * @author tangyixiang
 * @Date 2022/11/2
 */
public class CrudHelper<T> {


    public void deleteByIds(String[] ids, IService<T> service) {
        for (String id : ids) {
            UpdateWrapper <T> updateWrapper = new UpdateWrapper<T>();
            updateWrapper.eq("id", id);
            updateWrapper.set("del", CommonConstants.STATUS_DEL);
            service.update(null, updateWrapper);
        }
    }


}
