package com.cmcorg20230301.be.engine.wallet.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullLong;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoIdSuper;
import com.cmcorg20230301.be.engine.security.model.entity.SysTenantDO;
import com.cmcorg20230301.be.engine.security.util.MyTreeUtil;
import com.cmcorg20230301.be.engine.security.util.SysTenantUtil;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserBankCardInsertOrUpdateUserSelfDTO;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserBankCardPageDTO;
import com.cmcorg20230301.be.engine.wallet.model.entity.SysUserBankCardDO;
import com.cmcorg20230301.be.engine.wallet.service.SysTenantBankCardService;
import com.cmcorg20230301.be.engine.wallet.service.SysUserBankCardService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SysTenantBankCardServiceImpl implements SysTenantBankCardService {

    @Resource
    SysUserBankCardService sysUserBankCardService;

    /**
     * 新增/修改-租户
     */
    @Override
    public String insertOrUpdateTenant(SysUserBankCardInsertOrUpdateUserSelfDTO dto) {

        SysTenantUtil.checkTenantId(dto.getTenantId());

        // 执行
        return sysUserBankCardService.doInsertOrUpdate(dto, true, null);

    }

    /**
     * 分页排序查询
     */
    @Override
    public Page<SysUserBankCardDO> myPage(SysUserBankCardPageDTO dto) {

        // 执行
        return sysUserBankCardService.doMyPage(dto, true);

    }

    /**
     * 查询：树结构
     */
    @Override
    public List<SysUserBankCardDO> tree(SysUserBankCardPageDTO dto) {

        dto.setPageSize(-1); // 不分页

        // 执行
        List<SysUserBankCardDO> sysUserBankCardDOList = myPage(dto).getRecords();

        if (sysUserBankCardDOList.size() == 0) {
            return new ArrayList<>();
        }

        List<SysUserBankCardDO> allList =
            sysUserBankCardService.lambdaQuery()
                .in(BaseEntityNoIdSuper::getTenantId, dto.getTenantIdSet())
                .eq(SysUserBankCardDO::getId, BaseConstant.TENANT_USER_ID) //
                .list();

        if (allList.size() == 0) {
            return new ArrayList<>();
        }

        // 用于组装：树形结构
        Map<Long, SysTenantDO> sysTenantCacheMap = SysTenantUtil.getSysTenantCacheMap(true);

        Map<Long, SysTenantDO> allSysTenantDoMap =
            sysTenantCacheMap.entrySet().stream()
                .filter(it -> dto.getTenantIdSet().contains(it.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // 处理：集合
        handleSysUserBankCardDoIterator(allSysTenantDoMap, sysUserBankCardDOList.iterator());

        // 处理：集合
        handleSysUserBankCardDoIterator(allSysTenantDoMap, allList.iterator());

        return MyTreeUtil.getFullTreeByDeepNode(sysUserBankCardDOList, allList,
            BaseConstant.NEGATIVE_ONE);

    }

    /**
     * 处理：SysUserWalletDO的迭代器
     */
    private void handleSysUserBankCardDoIterator(Map<Long, SysTenantDO> allSysTenantDoMap,
        Iterator<SysUserBankCardDO> iterator) {

        while (iterator.hasNext()) {

            SysUserBankCardDO sysUserBankCardDO = iterator.next();

            SysTenantDO sysTenantDO = allSysTenantDoMap.get(sysUserBankCardDO.getTenantId());

            if (sysTenantDO == null) {

                iterator.remove();

                continue;

            }

            sysUserBankCardDO.setId(sysTenantDO.getId()); // 用于组装：树形结构
            sysUserBankCardDO.setParentId(sysTenantDO.getParentId()); // 用于组装：树形结构

        }

    }

    /**
     * 通过租户主键id，查看详情
     */
    @Override
    public SysUserBankCardDO infoById(NotNullLong notNullLong) {

        // 获取：用户关联的租户
        Set<Long> queryTenantIdSet = SysTenantUtil.getUserRefTenantIdSet();

        return sysUserBankCardService.lambdaQuery()
            .eq(BaseEntityNoIdSuper::getTenantId, notNullLong.getValue())
            .eq(SysUserBankCardDO::getId, BaseConstant.TENANT_USER_ID)
            .in(BaseEntityNoId::getTenantId, queryTenantIdSet)
            .one();

    }

}
