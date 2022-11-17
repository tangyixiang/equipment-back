package com.ocs.busi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ocs.busi.domain.entity.CompanyCar;
import com.ocs.busi.mapper.CompanyCarMapper;
import com.ocs.busi.service.CompanyCarService;
import org.springframework.stereotype.Service;

/**
 * @author tangyixiang
 * @Date 2022/10/18
 */
@Service
public class CompanyCarServiceImpl extends ServiceImpl<CompanyCarMapper, CompanyCar> implements CompanyCarService {
}
