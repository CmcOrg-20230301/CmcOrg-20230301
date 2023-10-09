package com.cmcorg20230301.be.engine.wallet.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.util.BooleanUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.be.engine.model.model.dto.ChangeBigDecimalNumberDTO;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import com.cmcorg20230301.be.engine.redisson.util.IdGeneratorUtil;
import com.cmcorg20230301.be.engine.redisson.util.RedissonUtil;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoIdFather;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.util.SysTenantUtil;
import com.cmcorg20230301.be.engine.security.util.UserUtil;
import com.cmcorg20230301.be.engine.wallet.configuration.SysUserWalletUserSignConfiguration;
import com.cmcorg20230301.be.engine.wallet.mapper.SysUserWalletMapper;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserWalletInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserWalletPageDTO;
import com.cmcorg20230301.be.engine.wallet.model.entity.SysUserWalletDO;
import com.cmcorg20230301.be.engine.wallet.model.entity.SysUserWalletLogDO;
import com.cmcorg20230301.be.engine.wallet.model.enums.SysUserWalletLogTypeEnum;
import com.cmcorg20230301.be.engine.wallet.service.SysUserWalletService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class SysUserWalletServiceImpl extends ServiceImpl<SysUserWalletMapper, SysUserWalletDO>
    implements SysUserWalletService {

    @Resource
    SysUserWalletUserSignConfiguration sysUserWalletUserSignConfiguration;

    /**
     * 新增/修改
     */
    @Override
    public String insertOrUpdate(SysUserWalletInsertOrUpdateDTO dto) {

        // 处理：BaseTenantInsertOrUpdateDTO
        SysTenantUtil.handleBaseTenantInsertOrUpdateDTO(dto, getCheckIllegalFunc1(CollUtil.newHashSet(dto.getId())),
            getTenantIdBaseEntityFunc1());

        SysUserWalletDO sysUserWalletDO = new SysUserWalletDO();

        sysUserWalletDO.setId(dto.getId());

        if (dto.getId() == null) {

            sysUserWalletDO.setTotalMoney(BigDecimal.ZERO);
            sysUserWalletDO.setWithdrawableMoney(BigDecimal.ZERO);

        }

        sysUserWalletDO.setEnableFlag(BooleanUtil.isTrue(dto.getEnableFlag()));
        sysUserWalletDO.setDelFlag(false);
        sysUserWalletDO.setRemark("");

        saveOrUpdate(sysUserWalletDO); // 操作数据库

        return BaseBizCodeEnum.OK;

    }

    /**
     * 分页排序查询
     */
    @Override
    public Page<SysUserWalletDO> myPage(SysUserWalletPageDTO dto) {

        // 处理：MyTenantPageDTO
        SysTenantUtil.handleMyTenantPageDTO(dto, true);

        return lambdaQuery().eq(dto.getId() != null, SysUserWalletDO::getId, dto.getId())
            .eq(dto.getEnableFlag() != null, BaseEntityNoId::getEnableFlag, dto.getEnableFlag())
            .in(BaseEntityNoId::getTenantId, dto.getTenantIdSet()) //
            .orderByDesc(SysUserWalletDO::getUpdateTime).page(dto.page(true));

    }

    /**
     * 通过主键id，查看详情
     */
    @Override
    public SysUserWalletDO infoById(NotNullId notNullId) {

        // 获取：用户关联的租户
        Set<Long> queryTenantIdSet = SysTenantUtil.getUserRefTenantIdSet();

        return lambdaQuery().eq(SysUserWalletDO::getId, notNullId.getId())
            .in(BaseEntityNoId::getTenantId, queryTenantIdSet).one();

    }

    /**
     * 通过主键id，查看详情-用户
     */
    @Override
    public SysUserWalletDO infoByIdUserSelf() {

        Long currentUserId = UserUtil.getCurrentUserId();

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        SysUserWalletDO sysUserWalletDO = lambdaQuery().eq(SysUserWalletDO::getId, currentUserId).one();

        if (sysUserWalletDO == null) {

            sysUserWalletDO =
                (SysUserWalletDO)sysUserWalletUserSignConfiguration.signUp(currentUserId, currentTenantIdDefault);

        }

        return sysUserWalletDO;

    }

    /**
     * 批量删除
     */
    @Override
    public String deleteByIdSet(NotEmptyIdSet notEmptyIdSet) {

        Set<Long> idSet = notEmptyIdSet.getIdSet();

        if (CollUtil.isEmpty(idSet)) {
            return BaseBizCodeEnum.OK;
        }

        // 检查：是否非法操作
        SysTenantUtil.checkIllegal(idSet, getCheckIllegalFunc1(idSet));

        removeByIds(idSet); // 根据 idSet删除

        return BaseBizCodeEnum.OK;

    }

    /**
     * 通用：处理：SysUserWalletLogDO
     */
    private void commonHandleSysUserWalletLogDO(SysUserWalletLogDO sysUserWalletLogDO) {

        sysUserWalletLogDO
            .setTotalMoneyChange(sysUserWalletLogDO.getTotalMoneySuf().subtract(sysUserWalletLogDO.getTotalMoneyPre()));

        sysUserWalletLogDO.setWithdrawableMoneyChange(
            sysUserWalletLogDO.getWithdrawableMoneySuf().subtract(sysUserWalletLogDO.getWithdrawableMoneyPre()));

    }

    /**
     * 通过主键 idSet，加减可提现的钱
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
        return doAddWithdrawableMoney(currentUserId, new Date(), dto.getIdSet(), changeNumber, sysUserWalletLogTypeEnum,
            false);

    }

    /**
     * 执行：通过主键 idSet，加减可提现的钱
     */
    @Override
    @NotNull
    @DSTransactional
    public String doAddWithdrawableMoney(Long currentUserId, Date date, Set<Long> idSet, BigDecimal changeNumber,
        SysUserWalletLogTypeEnum sysUserWalletLogTypeEnum, boolean lowErrorFlag) {

        if (changeNumber.equals(BigDecimal.ZERO)) {
            return BaseBizCodeEnum.OK;
        }

        // 日志集合
        List<SysUserWalletLogDO> sysUserWalletLogDoList = new ArrayList<>();

        RedissonUtil.doMultiLock(BaseRedisKeyEnum.PRE_USER_WALLET.name(), idSet, () -> {

            List<SysUserWalletDO> sysUserWalletDOList = lambdaQuery().in(SysUserWalletDO::getId, idSet)
                .select(SysUserWalletDO::getId, SysUserWalletDO::getWithdrawableMoney, BaseEntityNoId::getVersion,
                    BaseEntityNoIdFather::getTenantId, SysUserWalletDO::getTotalMoney).list();

            for (SysUserWalletDO item : sysUserWalletDOList) {

                BigDecimal preTotalMoney = item.getTotalMoney();
                BigDecimal preWithdrawableMoney = item.getWithdrawableMoney();

                item.setTotalMoney(item.getTotalMoney().add(changeNumber)); // 修改：数字
                item.setWithdrawableMoney(item.getWithdrawableMoney().add(changeNumber)); // 修改：数字

                if (item.getWithdrawableMoney().compareTo(BigDecimal.ZERO) < 0) {
                    if (lowErrorFlag) {
                        ApiResultVO.error("操作失败：可提现余额不足", item.getId());
                    } else {
                        item.setWithdrawableMoney(BigDecimal.ZERO);
                    }
                }

                SysUserWalletLogDO sysUserWalletLogDO = new SysUserWalletLogDO();

                sysUserWalletLogDO.setUserId(item.getId());
                sysUserWalletLogDO.setName(sysUserWalletLogTypeEnum.getName());
                sysUserWalletLogDO.setType(sysUserWalletLogTypeEnum);

                sysUserWalletLogDO.setTotalMoneyPre(preTotalMoney);
                sysUserWalletLogDO.setTotalMoneySuf(item.getTotalMoney());

                sysUserWalletLogDO.setWithdrawableMoneyPre(preWithdrawableMoney);
                sysUserWalletLogDO.setWithdrawableMoneySuf(item.getWithdrawableMoney());

                sysUserWalletLogDO.setId(IdGeneratorUtil.nextId());
                sysUserWalletLogDO.setEnableFlag(true);
                sysUserWalletLogDO.setDelFlag(false);
                sysUserWalletLogDO.setRemark("");
                sysUserWalletLogDO.setTenantId(item.getTenantId());
                sysUserWalletLogDO.setCreateId(currentUserId);
                sysUserWalletLogDO.setCreateTime(date);
                sysUserWalletLogDO.setUpdateId(currentUserId);
                sysUserWalletLogDO.setUpdateTime(date);

                // 通用：处理：SysUserWalletLogDO
                commonHandleSysUserWalletLogDO(sysUserWalletLogDO);

                sysUserWalletLogDoList.add(sysUserWalletLogDO);

            }

            updateBatchById(sysUserWalletDOList);

        });

        for (SysUserWalletLogDO item : sysUserWalletLogDoList) {

            SysUserWalletLogServiceImpl.add(item); // 保存日志

        }

        return BaseBizCodeEnum.OK;

    }

    /**
     * 获取：检查：是否非法操作的 getCheckIllegalFunc1
     */
    @NotNull
    private Func1<Set<Long>, Long> getCheckIllegalFunc1(Set<Long> idSet) {

        return tenantIdSet -> lambdaQuery().in(SysUserWalletDO::getId, idSet)
            .in(BaseEntityNoId::getTenantId, tenantIdSet).count();

    }

    /**
     * 获取：检查：是否非法操作的 getTenantIdBaseEntityFunc1
     */
    @NotNull
    private Func1<Long, BaseEntityNoIdFather> getTenantIdBaseEntityFunc1() {

        return id -> lambdaQuery().eq(SysUserWalletDO::getId, id).select(SysUserWalletDO::getTenantId).one();

    }

}
