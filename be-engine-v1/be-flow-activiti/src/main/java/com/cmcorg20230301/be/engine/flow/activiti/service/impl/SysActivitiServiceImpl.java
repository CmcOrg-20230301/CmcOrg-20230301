package com.cmcorg20230301.be.engine.flow.activiti.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.DeploymentQuery;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.be.engine.flow.activiti.model.dto.SysActivitiDeployInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.flow.activiti.model.dto.SysActivitiDeployPageDTO;
import com.cmcorg20230301.be.engine.flow.activiti.service.SysActivitiService;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyStringSet;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.util.UserUtil;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;

@Service
public class SysActivitiServiceImpl implements SysActivitiService {

    @Resource
    RepositoryService repositoryService;

    /**
     * 部署-新增/修改
     */
    @Override
    public String deployInsertOrUpdate(SysActivitiDeployInsertOrUpdateDTO dto) {

        Long tenantId = UserUtil.getCurrentTenantIdDefault();

        String url = dto.getUrl();

        String fileName = FileNameUtil.getName(url);

        byte[] downloadByteArr = HttpUtil.downloadBytes(url);

        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().addBytes(fileName, downloadByteArr);

        deploymentBuilder.name(fileName);

        deploymentBuilder.tenantId(tenantId.toString());

        Deployment deployment = deploymentBuilder.deploy();

        return deployment.getId();

    }

    /**
     * 部署-分页排序查询
     */
    @Override
    public Page<Deployment> deployPage(SysActivitiDeployPageDTO dto) {

        Long tenantId = UserUtil.getCurrentTenantIdDefault();

        DeploymentQuery deploymentQuery =
            repositoryService.createDeploymentQuery().deploymentTenantId(tenantId.toString());

        if (StrUtil.isNotBlank(dto.getId())) {
            deploymentQuery.deploymentId(dto.getId());
        }

        if (StrUtil.isNotBlank(dto.getName())) {
            deploymentQuery.deploymentNameLike(dto.getName());
        }

        long count = deploymentQuery.count();

        Page<Deployment> page = dto.page(true);

        long firstResult = (page.getCurrent() - 1) * page.getSize();

        deploymentQuery.orderByDeploymentId().desc();

        List<Deployment> deploymentList = deploymentQuery.listPage((int)firstResult, (int)page.getSize());

        return new Page<Deployment>().setTotal(count).setRecords(deploymentList);

    }

    /**
     * 部署-批量删除
     */
    @Override
    public String deployDeleteByIdSet(NotEmptyStringSet notEmptyStringSet) {

        for (String item : notEmptyStringSet.getIdSet()) {

            repositoryService.deleteDeployment(item);

        }

        return BaseBizCodeEnum.OK;

    }

}
