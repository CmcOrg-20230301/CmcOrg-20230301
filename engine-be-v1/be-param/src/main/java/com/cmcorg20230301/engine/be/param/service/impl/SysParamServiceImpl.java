package com.cmcorg20230301.engine.be.param.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.engine.be.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.engine.be.model.model.dto.NotNullId;
import com.cmcorg20230301.engine.be.param.model.dto.SysParamInsertOrUpdateDTO;
import com.cmcorg20230301.engine.be.param.model.dto.SysParamPageDTO;
import com.cmcorg20230301.engine.be.param.service.SysParamService;
import com.cmcorg20230301.engine.be.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.engine.be.security.mapper.SysParamMapper;
import com.cmcorg20230301.engine.be.security.model.entity.BaseEntity;
import com.cmcorg20230301.engine.be.security.model.entity.SysParamDO;
import com.cmcorg20230301.engine.be.security.model.vo.ApiResultVO;
import com.cmcorg20230301.engine.be.security.util.MyEntityUtil;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class SysParamServiceImpl extends ServiceImpl<SysParamMapper, SysParamDO> implements SysParamService {

    private final Set<Long> notDeleteIdSet = CollUtil.newHashSet(1L, 2L); // 不允许删除的 idSet

    /**
     * 新增/修改
     */
    @Override
    public String insertOrUpdate(SysParamInsertOrUpdateDTO dto) {

        SysParamDO sysParamDO = new SysParamDO();
        sysParamDO.setName(dto.getName());
        sysParamDO.setValue(dto.getValue());
        sysParamDO.setRemark(MyEntityUtil.getNotNullStr(dto.getRemark()));
        sysParamDO.setEnableFlag(BooleanUtil.isTrue(dto.getEnableFlag()));
        sysParamDO.setDelFlag(false);
        sysParamDO.setId(dto.getId());

        saveOrUpdate(sysParamDO);

        return BaseBizCodeEnum.OK;

    }

    /**
     * 分页排序查询
     */
    @Override
    public Page<SysParamDO> myPage(SysParamPageDTO dto) {

        return lambdaQuery().like(StrUtil.isNotBlank(dto.getName()), SysParamDO::getName, dto.getName())
            .like(StrUtil.isNotBlank(dto.getRemark()), BaseEntity::getRemark, dto.getRemark())
            .eq(dto.getEnableFlag() != null, BaseEntity::getEnableFlag, dto.getEnableFlag())
            .orderByDesc(BaseEntity::getUpdateTime).page(dto.page(true));

    }

    /**
     * 通过主键id，查看详情
     */
    @Override
    public SysParamDO infoById(NotNullId notNullId) {
        return getById(notNullId.getId());
    }

    /**
     * 批量删除
     */
    @Override
    public String deleteByIdSet(NotEmptyIdSet notEmptyIdSet) {

        for (Long item : notDeleteIdSet) {
            if (notEmptyIdSet.getIdSet().contains(item)) {
                ApiResultVO.error("操作失败：id【{}】不允许删除", item);
            }
        }

        removeByIds(notEmptyIdSet.getIdSet());

        return BaseBizCodeEnum.OK;

    }
}




