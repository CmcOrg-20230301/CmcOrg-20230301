package com.cmcorg20230301.be.engine.param.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.param.model.dto.SysParamInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.param.model.dto.SysParamPageDTO;
import com.cmcorg20230301.be.engine.param.service.SysParamService;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.mapper.SysParamMapper;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.model.entity.SysParamDO;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.util.MyEntityUtil;
import com.cmcorg20230301.be.engine.security.util.SysParamUtil;
import com.cmcorg20230301.be.engine.security.util.SysTenantUtil;
import com.cmcorg20230301.be.engine.security.util.UserUtil;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class SysParamServiceImpl extends ServiceImpl<SysParamMapper, SysParamDO> implements
    SysParamService {

    /**
     * 新增/修改 备注：这里修改了，租户管理那边也要一起修改
     */
    @Override
    public String insertOrUpdate(SysParamInsertOrUpdateDTO dto) {

        // 检查：是否可以新增
        SysTenantUtil.checkInsert(dto);

        // 处理：BaseTenantInsertOrUpdateDTO
        SysTenantUtil.handleBaseTenantInsertOrUpdateDTO(dto,
            getCheckIllegalFunc1(CollUtil.newHashSet(dto.getId())),
            getTenantIdBaseEntityFunc1());

        // 检查：是否可以修改一些属性
        dto = checkUpdate(dto, dto.getId());

        SysParamDO sysParamDO = new SysParamDO();

        sysParamDO.setName(dto.getName());
        sysParamDO.setValue(dto.getValue());
        sysParamDO.setEnableFlag(BooleanUtil.isTrue(dto.getEnableFlag()));
        sysParamDO.setDelFlag(false);
        sysParamDO.setId(dto.getId());

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        if (UserUtil.getCurrentTenantTopFlag(currentTenantIdDefault)) { // 如果是：顶层租户

            sysParamDO.setSystemFlag(BooleanUtil.isTrue(dto.getSystemFlag()));
            sysParamDO.setRemark(MyEntityUtil.getNotNullStr(dto.getRemark()));

        } else {

            if (dto.getId() == null) {
                sysParamDO.setSystemFlag(false);
            }

        }

        saveOrUpdate(sysParamDO);

        return BaseBizCodeEnum.OK;

    }

    /**
     * 检查：是否可以修改一些属性
     */
    private SysParamInsertOrUpdateDTO checkUpdate(SysParamInsertOrUpdateDTO dto, Long id) {

        if (id == null) {
            return dto;
        }

        // 检查：是否可以修改
        if (SysTenantUtil.checkUpdate()) {
            return dto;
        }

        SysParamInsertOrUpdateDTO sysParamInsertOrUpdateDTO = new SysParamInsertOrUpdateDTO();

        sysParamInsertOrUpdateDTO.setName(null); // 不允许修改
        sysParamInsertOrUpdateDTO.setValue(
            dto.getValue()); // 允许修改，备注：现在只允许修改 value字段，如果新允许了其他字段，则：租户参数同步也要进行修改
        sysParamInsertOrUpdateDTO.setEnableFlag(true); // 不允许修改
        sysParamInsertOrUpdateDTO.setTenantId(dto.getTenantId());
        sysParamInsertOrUpdateDTO.setId(id);

        return sysParamInsertOrUpdateDTO;

    }

    /**
     * 分页排序查询
     */
    @Override
    public Page<SysParamDO> myPage(SysParamPageDTO dto) {

        // 处理：MyTenantPageDTO
        SysTenantUtil.handleMyTenantPageDTO(dto, true);

        return lambdaQuery().like(StrUtil.isNotBlank(dto.getName()), SysParamDO::getName,
                dto.getName())
            .like(StrUtil.isNotBlank(dto.getRemark()), BaseEntity::getRemark, dto.getRemark())
            .eq(dto.getEnableFlag() != null, BaseEntity::getEnableFlag, dto.getEnableFlag())
            .in(BaseEntityNoId::getTenantId, dto.getTenantIdSet()) //
            .orderByDesc(BaseEntity::getUpdateTime).page(dto.page(true));

    }

    /**
     * 通过主键id，查看详情
     */
    @Override
    public SysParamDO infoById(NotNullId notNullId) {

        // 获取：用户关联的租户
        Set<Long> queryTenantIdSet = SysTenantUtil.getUserRefTenantIdSet();

        return lambdaQuery().eq(BaseEntity::getId, notNullId.getId())
            .in(BaseEntityNoId::getTenantId, queryTenantIdSet)
            .one();

    }

    /**
     * 批量删除
     */
    @Override
    public String deleteByIdSet(NotEmptyIdSet notEmptyIdSet, boolean checkDeleteFlag) {

        Set<Long> idSet = notEmptyIdSet.getIdSet();

        if (CollUtil.isEmpty(idSet)) {
            return BaseBizCodeEnum.OK;
        }

        if (checkDeleteFlag) {

            // 检查：是否非法操作
            SysTenantUtil.checkIllegal(idSet, getCheckIllegalFunc1(idSet));

            // 检查：是否可以删除
            SysTenantUtil.checkDelete();

        }

        for (String item : SysParamUtil.SYSTEM_PARAM_NOT_DELETE_ID_SET) {

            if (idSet.contains(Convert.toLong(item))) {

                ApiResultVO.errorMsg("操作失败：id【{}】不允许删除", item);

            }

        }

        removeByIds(idSet); // 根据 idSet删除

        return BaseBizCodeEnum.OK;

    }

    /**
     * 获取：检查：是否非法操作的 getCheckIllegalFunc1
     */
    @NotNull
    private Func1<Set<Long>, Long> getCheckIllegalFunc1(Set<Long> idSet) {

        return tenantIdSet -> lambdaQuery().in(BaseEntity::getId, idSet)
            .in(BaseEntityNoId::getTenantId, tenantIdSet)
            .count();

    }

    /**
     * 获取：检查：是否非法操作的 getTenantIdBaseEntityFunc1
     */
    @NotNull
    private Func1<Long, BaseEntity> getTenantIdBaseEntityFunc1() {

        return id -> lambdaQuery().eq(BaseEntity::getId, id).select(BaseEntity::getTenantId).one();

    }

}




