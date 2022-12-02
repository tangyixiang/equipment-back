package com.ocs.web.controller.examine;

import com.ocs.busi.domain.entity.StaffTrip;
import com.ocs.busi.service.StaffTripService;
import com.ocs.common.constant.FlowStatusConstants;
import com.ocs.common.core.domain.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author tangyixiang
 * @Date 2022/10/25
 */
@RestController
@RequestMapping("/staff/trip")
public class StaffTripController {

    @Autowired
    private StaffTripService staffTripService;


    @PostMapping("/add")
    public Result add(@RequestBody @Validated StaffTrip staffTrip) {

        staffTripService.save(staffTrip);
        if (staffTrip.getStatus().equals(FlowStatusConstants.SUBMIT)) {
            // TODO 发起流程

        }

        return Result.success();
    }

    @PostMapping("/{id}")
    public Result getById(@PathVariable("id") String id) {
        StaffTrip staffTrip = staffTripService.getById(id);
        return Result.success(staffTrip);
    }


    @PostMapping("/update")
    public Result update(@RequestBody StaffTrip staffTrip) {

        staffTripService.updateById(staffTrip);

        return Result.success();
    }

    @PostMapping("/approve")
    public Result approve() {
        // TODO 审批通过
        return Result.success();
    }


}
