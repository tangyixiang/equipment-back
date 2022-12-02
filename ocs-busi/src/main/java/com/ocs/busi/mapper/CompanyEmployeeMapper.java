package com.ocs.busi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ocs.busi.domain.entity.CompanyEmployee;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author tangyx
 * @description 针对表【company_employee(职员信息表)】的数据库操作Mapper
 * @createDate 2022-11-29 12:12:56
 * @Entity com.ocs.busi.domain.entity.CompanyEmployee
 */
@Repository
public interface CompanyEmployeeMapper extends BaseMapper<CompanyEmployee> {

    List<CompanyEmployee> findByNickName(@Param("nickName") String nickName);

}




