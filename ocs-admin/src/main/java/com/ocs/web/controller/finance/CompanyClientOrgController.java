package com.ocs.web.controller.finance;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ocs.busi.domain.dto.CompanyClientOrgDto;
import com.ocs.busi.domain.entity.ClientFinanceInfoItem;
import com.ocs.busi.domain.entity.CompanyClientOrg;
import com.ocs.busi.helper.CrudHelper;
import com.ocs.busi.service.ClientFinanceInfoItemService;
import com.ocs.busi.service.CompanyClientOrgService;
import com.ocs.common.constant.CommonConstants;
import com.ocs.common.core.controller.BaseController;
import com.ocs.common.core.domain.Result;
import com.ocs.common.core.page.TableDataInfo;
import com.ocs.common.utils.TemplateDownloadUtils;
import com.ocs.common.helper.QueryHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author tangyixiang
 * @Date 2022/10/25
 */
@RestController
@RequestMapping("/company/client")
public class CompanyClientOrgController extends BaseController {

    @Autowired
    private CompanyClientOrgService companyClientOrgService;
    @Autowired
    private ClientFinanceInfoItemService clientFinanceInfoItemService;

    @RequestMapping("/upload")
    public Result importOrg(MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        companyClientOrgService.importOrg(inputStream);
        return Result.success();
    }


    @RequestMapping("/template/download")
    public void templateDownload(String fileName, HttpServletResponse response) {
        TemplateDownloadUtils.downloadByFileName("客户导入模板.xlsx", response);
    }

    @GetMapping("/list")
    public TableDataInfo list(CompanyClientOrg companyClientOrg) {
        startPage("create_time desc");
        QueryWrapper<CompanyClientOrg> queryWrapper = QueryHelper.dynamicCondition(companyClientOrg, CommonConstants.QUERY_LIKE, false);
        List<CompanyClientOrg> list = companyClientOrgService.list(queryWrapper);
        return getDataTable(list);
    }

    @DeleteMapping("/del/{ids}")
    @Transactional
    public Result delete(@PathVariable String[] ids) {
        new CrudHelper<CompanyClientOrg>().deleteByIds(ids, companyClientOrgService);
        return Result.success();
    }

    @PostMapping("/update")
    @Transactional
    public Result update(@RequestBody CompanyClientOrgDto companyClientOrgDto) {
        CompanyClientOrg companyClientOrg = new CompanyClientOrg();
        companyClientOrg.setId(companyClientOrgDto.getId());
        companyClientOrg.setContactName(companyClientOrgDto.getContactName());
        companyClientOrg.setContactPhone(companyClientOrgDto.getContactPhone());

        companyClientOrgService.updateById(companyClientOrg);

        companyClientOrgDto.getItemInfo().forEach(item -> item.setClientOrgId(companyClientOrgDto.getId()));

        clientFinanceInfoItemService.saveOrUpdateBatch(companyClientOrgDto.getItemInfo());
        return Result.success();
    }


    @GetMapping("/financeItem")
    public Result getClientFinanceItem(String clientOrgId){
        List<ClientFinanceInfoItem> list = clientFinanceInfoItemService.list(new LambdaQueryWrapper<ClientFinanceInfoItem>()
                .eq(ClientFinanceInfoItem::getClientOrgId, clientOrgId).orderByDesc(ClientFinanceInfoItem::getCreateTime));
        return Result.success(list);
    }

}
