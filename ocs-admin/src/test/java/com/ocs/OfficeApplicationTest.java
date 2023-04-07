package com.ocs;

import cn.hutool.db.meta.MetaUtil;
import cn.hutool.db.meta.Table;
import com.ocs.busi.domain.entity.BankFlowLog;
import com.ocs.busi.service.BankFlowLogService;
import com.ocs.common.constant.CommonConstants;
import com.ocs.web.controller.task.TaskController;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

/**
 * @author tangyixiang
 * @Date 2022/10/13
 */
@Slf4j
@SpringBootTest
public class OfficeApplicationTest {

    @Autowired
    DataSource dataSource;

    @Autowired
    BankFlowLogService bankFlowLogService;



    @Test
    void getParameterName() throws Exception {
        Class cls = TaskController.class;
        Method method = cls.getMethod("test", String.class, Integer.class);
        Parameter[] parameters = method.getParameters();
        for (Parameter parameter : parameters) {
            System.out.println(parameter.getName());
        }
    }

    @Test
    void getDbTableInfo() {
        Table table = MetaUtil.getTableMeta(dataSource, "apply_check");
        log.info("{}", table);
    }

    @Test
    void aaaa() {
        List<BankFlowLog> bankFlowLogs = bankFlowLogService.findByPeriod("202301", CommonConstants.RECEIVABLE_OPERATE, "eq");
        log.info(bankFlowLogs.toString());
    }



}
