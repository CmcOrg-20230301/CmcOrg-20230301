package com.cmcorg20230301.be.engine.wallet.service.impl;

import cn.hutool.core.lang.func.Func1;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.model.model.dto.ChangeBigDecimalNumberDTO;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullLong;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoIdFather;
import com.cmcorg20230301.be.engine.security.model.entity.SysTenantDO;
import com.cmcorg20230301.be.engine.security.util.MyTreeUtil;
import com.cmcorg20230301.be.engine.security.util.SysTenantUtil;
import com.cmcorg20230301.be.engine.security.util.UserUtil;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserWalletPageDTO;
import com.cmcorg20230301.be.engine.wallet.model.entity.SysUserWalletDO;
import com.cmcorg20230301.be.engine.wallet.model.enums.SysUserWalletLogTypeEnum;
import com.cmcorg20230301.be.engine.wallet.service.SysTenantWalletService;
import com.cmcorg20230301.be.engine.wallet.service.SysUserWalletService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SysTenantWalletServiceImpl implements SysTenantWalletService {

    @Resource
    SysUserWalletService sysUserWalletService;

    /**
     * 批量冻结
     */
    @Override
    public String frozenByIdSet(NotEmptyIdSet notEmptyIdSet) {

        // 改变：钱包冻结状态
        return sysUserWalletService.changeEnableFlag(notEmptyIdSet, false, true);

    }

    /**
     * 批量解冻
     */
    @Override
    public String thawByIdSet(NotEmptyIdSet notEmptyIdSet) {

        // 改变：钱包冻结状态
        return sysUserWalletService.changeEnableFlag(notEmptyIdSet, true, true);

    }

    /**
     * 分页排序查询
     */
    @Override
    public Page<SysUserWalletDO> myPage(SysUserWalletPageDTO dto) {

        // 执行
        return sysUserWalletService.doMyPage(dto, true);

    }

    /**
     * 查询：树结构
     */
    @Override
    public List<SysUserWalletDO> tree(SysUserWalletPageDTO dto) {

        dto.setPageSize(-1); // 不分页

        // 执行
        List<SysUserWalletDO> sysUserWalletDOList = myPage(dto).getRecords();

        if (sysUserWalletDOList.size() == 0) {
            return new ArrayList<>();
        }

        List<SysUserWalletDO> allList =
            sysUserWalletService.lambdaQuery().in(BaseEntityNoIdFather::getTenantId, dto.getTenantIdSet())
                .eq(SysUserWalletDO::getId, BaseConstant.TENANT_USER_ID) //
                .groupBy(BaseEntityNoIdFather::getTenantId) // 备注：因为 totalMoney是聚合函数算出来的，所以这里需要分组
                .list();

        if (allList.size() == 0) {
            return new ArrayList<>();
        }

        // 用于组装：树形结构
        Map<Long, SysTenantDO> sysTenantCacheMap = SysTenantUtil.getSysTenantCacheMap(true);

        Map<Long, SysTenantDO> allSysTenantDoMap =
            sysTenantCacheMap.entrySet().stream().filter(it -> dto.getTenantIdSet().contains(it.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // 处理：集合
        handleSysUserWalletDoIterator(allSysTenantDoMap, sysUserWalletDOList.iterator());

        // 处理：集合
        handleSysUserWalletDoIterator(allSysTenantDoMap, allList.iterator());

        return MyTreeUtil.getFullTreeByDeepNode(sysUserWalletDOList, allList, BaseConstant.NEGATIVE_ONE);

    }

    /**
     * 处理：SysUserWalletDO的迭代器
     */
    private void handleSysUserWalletDoIterator(Map<Long, SysTenantDO> allSysTenantDoMap,
        Iterator<SysUserWalletDO> iterator) {

        while (iterator.hasNext()) {

            SysUserWalletDO sysUserWalletDO = iterator.next();

            SysTenantDO sysTenantDO = allSysTenantDoMap.get(sysUserWalletDO.getTenantId());

            if (sysTenantDO == null) {

                iterator.remove();

                continue;

            }

            sysUserWalletDO.setId(sysTenantDO.getId()); // 用于组装：树形结构
            sysUserWalletDO.setParentId(sysTenantDO.getParentId()); // 用于组装：树形结构

        }

    }

    /**
     * 通过租户主键id，查看详情
     */
    @Override
    public SysUserWalletDO infoById(NotNullLong notNullLong) {

        // 获取：用户关联的租户
        Set<Long> queryTenantIdSet = SysTenantUtil.getUserRefTenantIdSet();

        return sysUserWalletService.lambdaQuery().eq(SysUserWalletDO::getTenantId, notNullLong.getValue())
            .eq(SysUserWalletDO::getId, BaseConstant.TENANT_USER_ID).in(BaseEntityNoId::getTenantId, queryTenantIdSet)
            .one();

    }

    /**
     * 通过租户主键 idSet，加减可提现的钱
     */
    @Override
    @DSTransactional
    public String addWithdrawableMoneyBackground(ChangeBigDecimalNumberDTO dto) {

        Long currentUserId = UserUtil.getCurrentUserId();

        BigDecimal changeNumber = dto.getNumber();

        // 检查：是否非法操作
        SysTenantUtil.checkIllegal(dto.getIdSet(), getCheckIllegalFunc1(dto.getIdSet()));

        SysUserWalletLogTypeEnum sysUserWalletLogTypeEnum =
            changeNumber.compareTo(BigDecimal.ZERO) > 0 ? SysUserWalletLogTypeEnum.ADD_BACKGROUND :
                SysUserWalletLogTypeEnum.REDUCE_BACKGROUND;

        // 执行
        return sysUserWalletService
            .doAddWithdrawableMoney(currentUserId, new Date(), dto.getIdSet(), changeNumber, sysUserWalletLogTypeEnum,
                false, false, true, null, null, true, null);

    }

    /**
     * 获取：检查：是否非法操作的 getCheckIllegalFunc1
     */
    @NotNull
    private Func1<Set<Long>, Long> getCheckIllegalFunc1(Set<Long> idSet) {

        return tenantIdSet -> sysUserWalletService.lambdaQuery().in(SysUserWalletDO::getTenantId, idSet)
            .eq(SysUserWalletDO::getId, BaseConstant.TENANT_USER_ID).in(BaseEntityNoId::getTenantId, tenantIdSet)
            .count();

    }

}
