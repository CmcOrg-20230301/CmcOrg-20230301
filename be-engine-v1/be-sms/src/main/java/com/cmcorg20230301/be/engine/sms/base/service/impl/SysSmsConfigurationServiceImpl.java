package com.cmcorg20230301.be.engine.sms.base.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.sms.base.mapper.SysSmsConfigurationMapper;
import com.cmcorg20230301.be.engine.sms.base.model.dto.SysSmsConfigurationInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.sms.base.model.dto.SysSmsConfigurationPageDTO;
import com.cmcorg20230301.be.engine.sms.base.model.entity.SysSmsConfigurationDO;
import com.cmcorg20230301.be.engine.sms.base.service.SysSmsConfigurationService;
import org.springframework.stereotype.Service;

@Service
public class SysSmsConfigurationServiceImpl extends ServiceImpl<SysSmsConfigurationMapper, SysSmsConfigurationDO>
    implements SysSmsConfigurationService {

    /**
     * 新增/修改
     */
    @Override
    public String insertOrUpdate(SysSmsConfigurationInsertOrUpdateDTO dto) {
        return null;
    }

    /**
     * 分页排序查询
     */
    @Override
    public Page<SysSmsConfigurationDO> myPage(SysSmsConfigurationPageDTO dto) {
        return null;
    }

    /**
     * 通过主键id，查看详情
     */
    @Override
    public SysSmsConfigurationDO infoById(NotNullId notNullId) {
        return null;
    }

    /**
     * 批量删除
     */
    @Override
    public String deleteByIdSet(NotEmptyIdSet notEmptyIdSet) {
        return null;
    }

}
