package com.cmcorg20230301.be.engine.im.session.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.be.engine.im.session.mapper.SysImSessionMapper;
import com.cmcorg20230301.be.engine.im.session.model.dto.SysImSessionInsertOrUpDateDTO;
import com.cmcorg20230301.be.engine.im.session.model.dto.SysImSessionPageDTO;
import com.cmcorg20230301.be.engine.im.session.model.entity.SysImSessionDO;
import com.cmcorg20230301.be.engine.im.session.service.SysImSessionService;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.util.MyEntityUtil;
import com.cmcorg20230301.be.engine.security.util.SysTenantUtil;
import com.cmcorg20230301.be.engine.security.util.UserUtil;
import com.cmcorg20230301.be.engine.util.util.NicknameUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class SysImSessionServiceImpl extends ServiceImpl<SysImSessionMapper, SysImSessionDO>
        implements SysImSessionService {

    /**
     * 新增/修改
     */
    @Override
    public Long insertOrUpdate(SysImSessionInsertOrUpDateDTO dto) {

        // 处理：BaseTenantInsertOrUpdateDTO
        SysTenantUtil.handleBaseTenantInsertOrUpdateDTO(dto, getCheckIllegalFunc1(CollUtil.newHashSet(dto.getId())),
                getTenantIdBaseEntityFunc1());

        SysImSessionDO sysImSessionDO = new SysImSessionDO();

        sysImSessionDO.setName(MyEntityUtil.getNotNullStr(dto.getName(), NicknameUtil.getDateTimeNickname("会话-")));

        if (dto.getId() == null) { // 只有：新增时才有效

            sysImSessionDO.setType(dto.getType());

            sysImSessionDO.setBelongId(UserUtil.getCurrentUserId());

        }

        sysImSessionDO.setId(dto.getId());

        sysImSessionDO.setEnableFlag(true);
        sysImSessionDO.setDelFlag(false);

        sysImSessionDO.setRemark(MyEntityUtil.getNotNullStr(dto.getRemark()));

        saveOrUpdate(sysImSessionDO);

        return sysImSessionDO.getId();

    }

    /**
     * 分页排序查询
     */
    @Override
    public Page<SysImSessionDO> myPage(SysImSessionPageDTO dto) {

        // 处理：MyTenantPageDTO
        SysTenantUtil.handleMyTenantPageDTO(dto, true);

        return lambdaQuery().like(StrUtil.isNotBlank(dto.getName()), SysImSessionDO::getName, dto.getName())
                .eq(dto.getType() != null, SysImSessionDO::getType, dto.getType())
                .in(BaseEntityNoId::getTenantId, dto.getTenantIdSet()) //
                .page(dto.createTimeDescDefaultOrderPage(true));

    }

    /**
     * 获取：检查：是否非法操作的 getCheckIllegalFunc1
     */
    @NotNull
    private Func1<Set<Long>, Long> getCheckIllegalFunc1(Set<Long> idSet) {

        return tenantIdSet -> lambdaQuery().in(BaseEntity::getId, idSet).in(BaseEntityNoId::getTenantId, tenantIdSet)
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
