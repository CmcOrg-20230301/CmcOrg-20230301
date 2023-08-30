package com.cmcorg20230301.be.engine.request.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.be.engine.model.model.constant.OperationDescriptionConstant;
import com.cmcorg20230301.be.engine.request.mapper.SysRequestMapper;
import com.cmcorg20230301.be.engine.request.model.dto.SysRequestPageDTO;
import com.cmcorg20230301.be.engine.request.model.dto.SysRequestSelfLoginRecordPageDTO;
import com.cmcorg20230301.be.engine.request.model.vo.SysRequestAllAvgVO;
import com.cmcorg20230301.be.engine.request.service.SysRequestService;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.model.entity.SysRequestDO;
import com.cmcorg20230301.be.engine.security.util.TenantUtil;
import com.cmcorg20230301.be.engine.security.util.UserUtil;
import org.springframework.stereotype.Service;

@Service
public class SysRequestServiceImpl extends ServiceImpl<SysRequestMapper, SysRequestDO> implements SysRequestService {

    /**
     * 分页排序查询
     */
    @Override
    public Page<SysRequestDO> myPage(SysRequestPageDTO dto) {

        // 处理：MyTenantPageDTO
        TenantUtil.handleMyTenantPageDTO(dto);

        return lambdaQuery().like(StrUtil.isNotBlank(dto.getUri()), SysRequestDO::getUri, dto.getUri())
            .like(StrUtil.isNotBlank(dto.getName()), SysRequestDO::getName, dto.getName())
            .like(StrUtil.isNotBlank(dto.getIp()), SysRequestDO::getIp, dto.getIp())
            .like(StrUtil.isNotBlank(dto.getRegion()), SysRequestDO::getRegion, dto.getRegion())
            .like(StrUtil.isNotBlank(dto.getType()), SysRequestDO::getType, dto.getType())
            .le(dto.getEndCostMs() != null, SysRequestDO::getCostMs, dto.getEndCostMs())
            .ge(dto.getBeginCostMs() != null, SysRequestDO::getCostMs, dto.getBeginCostMs())
            .le(dto.getCtEndTime() != null, SysRequestDO::getCreateTime, dto.getCtEndTime())
            .ge(dto.getCtBeginTime() != null, SysRequestDO::getCreateTime, dto.getCtBeginTime())
            .eq(dto.getCategory() != null, SysRequestDO::getCategory, dto.getCategory())
            .eq(dto.getCreateId() != null, BaseEntity::getCreateId, dto.getCreateId())
            .eq(dto.getSuccessFlag() != null, SysRequestDO::getSuccessFlag, dto.getSuccessFlag())
            .in(BaseEntityNoId::getTenantId, dto.getTenantIdSet()) //
            .orderByDesc(BaseEntity::getCreateTime)
            .select(SysRequestDO::getIp, SysRequestDO::getUri, SysRequestDO::getSuccessFlag, SysRequestDO::getCostMsStr,
                BaseEntityNoId::getCreateTime, BaseEntityNoId::getCreateId, SysRequestDO::getName,
                SysRequestDO::getCategory, SysRequestDO::getIp, SysRequestDO::getRegion, SysRequestDO::getErrorMsg,
                BaseEntity::getId, BaseEntityNoId::getTenantId).page(dto.page(true));

    }

    /**
     * 所有请求的平均耗时-增强：增加筛选项
     */
    @Override
    public SysRequestAllAvgVO allAvgPro(SysRequestPageDTO dto) {

        // 处理：MyTenantPageDTO
        TenantUtil.handleMyTenantPageDTO(dto);

        return baseMapper.allAvgPro(dto);

    }

    /**
     * 当前用户：登录记录
     */
    @Override
    public Page<SysRequestDO> selfLoginRecord(SysRequestSelfLoginRecordPageDTO dto) {

        Long currentUserId = UserUtil.getCurrentUserId();

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        SysRequestPageDTO sysRequestPageDTO = new SysRequestPageDTO();

        sysRequestPageDTO.setType(OperationDescriptionConstant.SIGN_IN);
        sysRequestPageDTO.setCreateId(currentUserId);
        sysRequestPageDTO.setCategory(dto.getCategory());
        sysRequestPageDTO.setRegion(dto.getRegion());
        sysRequestPageDTO.setIp(dto.getIp());
        sysRequestPageDTO.setCurrent(dto.getCurrent());
        sysRequestPageDTO.setPageSize(dto.getPageSize());
        sysRequestPageDTO.setOrder(dto.getOrder());

        sysRequestPageDTO.setTenantId(currentTenantIdDefault);

        return myPage(sysRequestPageDTO);

    }

}
