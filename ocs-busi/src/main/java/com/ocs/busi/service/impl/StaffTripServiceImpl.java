package com.ocs.busi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ocs.busi.domain.entity.StaffTrip;
import com.ocs.busi.service.StaffTripService;
import com.ocs.busi.mapper.StaffTripMapper;
import org.springframework.stereotype.Service;

/**
* @author tangyx
* @description 针对表【staff_trip(员工外出表)】的数据库操作Service实现
* @createDate 2022-10-25 15:59:54
*/
@Service
public class StaffTripServiceImpl extends ServiceImpl<StaffTripMapper, StaffTrip>
    implements StaffTripService{

}




