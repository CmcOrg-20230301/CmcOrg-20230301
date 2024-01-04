package com.cmcorg20230301.be.engine.im.session.service.impl;

import cn.hutool.core.lang.func.Func1;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.be.engine.im.session.mapper.SysImSessionContentMapper;
import com.cmcorg20230301.be.engine.im.session.model.dto.SysImSessionContentListDTO;
import com.cmcorg20230301.be.engine.im.session.model.dto.SysImSessionContentSendListDTO;
import com.cmcorg20230301.be.engine.im.session.model.entity.SysImSessionContentDO;
import com.cmcorg20230301.be.engine.im.session.service.SysImSessionContentService;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class SysImSessionContentServiceImpl extends ServiceImpl<SysImSessionContentMapper, SysImSessionContentDO>
        implements SysImSessionContentService {

    /**
     * 发送内容
     */
    @Override
    public String send(SysImSessionContentSendListDTO dto) {

        return BaseBizCodeEnum.OK;

    }

    /**
     * 查询会话内容
     */
    @Override
    public List<SysImSessionContentDO> myList(SysImSessionContentListDTO dto) {

        return null;

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
