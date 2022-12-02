package com.ocs.busi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ocs.busi.domain.entity.BankAccount;
import com.ocs.busi.mapper.BankAccountMapper;
import com.ocs.busi.service.BankAccountService;
import org.springframework.stereotype.Service;

/**
 * @author tangyx
 * @description 针对表【bank_account(银行账号)】的数据库操作Service实现
 * @createDate 2022-11-01 15:54:02
 */
@Service
public class BankAccountServiceImpl extends ServiceImpl<BankAccountMapper, BankAccount>
        implements BankAccountService {

}




