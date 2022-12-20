package com.ocs.busi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ocs.busi.domain.dto.CompanyReceivablesDto;
import com.ocs.busi.domain.entity.CompanyReceivables;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author tangyx
 */
@Repository
public interface CompanyReceivablesMapper extends BaseMapper<CompanyReceivables> {

    List<CompanyReceivables> findByCondition(CompanyReceivablesDto companyReceivablesDto);

}




