package com.cmcorg20230301.be.engine.wallet.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import com.cmcorg20230301.be.engine.redisson.util.RedissonUtil;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoIdFather;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.util.SysTenantUtil;
import com.cmcorg20230301.be.engine.security.util.UserUtil;
import com.cmcorg20230301.be.engine.wallet.mapper.SysUserWalletWithdrawLogMapper;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserWalletWithdrawLogInsertOrUpdateUserSelfDTO;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserWalletWithdrawLogPageDTO;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserWalletWithdrawLogPageUserSelfDTO;
import com.cmcorg20230301.be.engine.wallet.model.entity.SysUserWalletWithdrawLogDO;
import com.cmcorg20230301.be.engine.wallet.model.enums.SysUserWalletWithdrawStatusEnum;
import com.cmcorg20230301.be.engine.wallet.service.SysUserWalletWithdrawLogService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Set;

@Service
public class SysUserWalletWithdrawLogServiceImpl
    extends ServiceImpl<SysUserWalletWithdrawLogMapper, SysUserWalletWithdrawLogDO>
    implements SysUserWalletWithdrawLogService {

    /**
     * 分页排序查询
     */
    @Override
    public Page<SysUserWalletWithdrawLogDO> myPage(SysUserWalletWithdrawLogPageDTO dto) {

        // 处理：MyTenantPageDTO
        SysTenantUtil.handleMyTenantPageDTO(dto, true);

        return lambdaQuery().eq(dto.getUserId() != null, SysUserWalletWithdrawLogDO::getUserId, dto.getUserId())
            .like(StrUtil.isNotBlank(dto.getBankName()), SysUserWalletWithdrawLogDO::getBankName, dto.getBankName()) //
            .like(StrUtil.isNotBlank(dto.getAccountName()), SysUserWalletWithdrawLogDO::getAccountName,
                dto.getAccountName()) //
            .like(StrUtil.isNotBlank(dto.getBankCardNo()), SysUserWalletWithdrawLogDO::getBankCardNo,
                dto.getBankCardNo()) //
            .like(StrUtil.isNotBlank(dto.getOpenBankName()), SysUserWalletWithdrawLogDO::getOpenBankName,
                dto.getOpenBankName()) //
            .eq(dto.getWithdrawStatus() != null, SysUserWalletWithdrawLogDO::getWithdrawStatus,
                dto.getWithdrawStatus()) //
            .in(BaseEntityNoId::getTenantId, dto.getTenantIdSet()) //
            .orderByDesc(SysUserWalletWithdrawLogDO::getUpdateTime).page(dto.page(true));

    }

    /**
     * 通过主键id，查看详情
     */
    @Override
    public SysUserWalletWithdrawLogDO infoById(NotNullId notNullId) {

        // 获取：用户关联的租户
        Set<Long> queryTenantIdSet = SysTenantUtil.getUserRefTenantIdSet();

        return lambdaQuery().eq(SysUserWalletWithdrawLogDO::getId, notNullId.getId())
            .in(BaseEntityNoId::getTenantId, queryTenantIdSet).one();

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

        // 执行：批量删除
        return deleteByIdSetCommonHandle(idSet);

    }

    /**
     * 执行：批量删除
     */
    private String deleteByIdSetCommonHandle(Set<Long> idSet) {

        return RedissonUtil.doMultiLock(BaseRedisKeyEnum.PRE_USER_WALLET_WITHDRAW_LOG.name(), idSet, () -> {

            // 只有草稿状态的提现记录才可以删除
            Long count = lambdaQuery().in(BaseEntity::getId, idSet)
                .eq(SysUserWalletWithdrawLogDO::getWithdrawStatus, SysUserWalletWithdrawStatusEnum.DRAFT).count();

            if (count != idSet.size()) {
                ApiResultVO.errorMsg("操作失败：只能删除草稿状态的提现记录");
            }

            removeByIds(idSet); // 根据 idSet删除

            return BaseBizCodeEnum.OK;

        });

    }

    /**
     * 新增/修改-用户
     */
    @Override
    public String insertOrUpdateUserSelf(SysUserWalletWithdrawLogInsertOrUpdateUserSelfDTO dto) {

        if (dto.getWithdrawMoney().compareTo(BigDecimal.ZERO) <= 0) { // 如果：提现金额 <= 0
            ApiResultVO.errorMsg("操作失败：提现金额不能小于等于 0");
        }

        // 处理：BaseTenantInsertOrUpdateDTO
        SysTenantUtil.handleBaseTenantInsertOrUpdateDTO(dto, getCheckIllegalFunc1(CollUtil.newHashSet(dto.getId())),
            getTenantIdBaseEntityFunc1());

        // 只能修改：草稿状态的提现记录
        if (dto.getId() != null) {

            RedissonUtil.doLock(BaseRedisKeyEnum.PRE_USER_WALLET_WITHDRAW_LOG.name() + dto.getId(), () -> {

                boolean exists = lambdaQuery().eq(BaseEntity::getId, dto.getId())
                    .eq(SysUserWalletWithdrawLogDO::getWithdrawStatus, SysUserWalletWithdrawStatusEnum.DRAFT).exists();

                if (!exists) {
                    ApiResultVO.error("操作失败：只能修改草稿状态的提现记录", dto.getId());
                }

                // 执行
                doInsertOrUpdateUserSelf(dto);

            });

        } else {

            // 执行
            doInsertOrUpdateUserSelf(dto);

        }

        return BaseBizCodeEnum.OK;

    }

    /**
     * 执行：新增/修改-用户
     */
    private void doInsertOrUpdateUserSelf(SysUserWalletWithdrawLogInsertOrUpdateUserSelfDTO dto) {

        Long currentUserId = UserUtil.getCurrentUserId();

        SysUserWalletWithdrawLogDO sysUserWalletWithdrawLogDO = new SysUserWalletWithdrawLogDO();

        sysUserWalletWithdrawLogDO.setUserId(currentUserId);
        sysUserWalletWithdrawLogDO.setWithdrawMoney(dto.getWithdrawMoney());
        sysUserWalletWithdrawLogDO.setBankName(dto.getBankName());
        sysUserWalletWithdrawLogDO.setAccountName(dto.getAccountName());
        sysUserWalletWithdrawLogDO.setBankCardNo(dto.getBankCardNo());
        sysUserWalletWithdrawLogDO.setOpenBankName(dto.getOpenBankName());

        sysUserWalletWithdrawLogDO.setWithdrawStatus(SysUserWalletWithdrawStatusEnum.DRAFT);

        sysUserWalletWithdrawLogDO.setRejectReason("");
        sysUserWalletWithdrawLogDO.setId(dto.getId());
        sysUserWalletWithdrawLogDO.setEnableFlag(true);
        sysUserWalletWithdrawLogDO.setDelFlag(false);
        sysUserWalletWithdrawLogDO.setRemark("");

        saveOrUpdate(sysUserWalletWithdrawLogDO); // 操作数据库

    }

    /**
     * 提交-用户
     */
    @Override
    @DSTransactional
    public String commitUserSelf(NotEmptyIdSet notEmptyIdSet) {

        return BaseBizCodeEnum.OK;

    }

    /**
     * 分页排序查询-用户
     */
    @Override
    public Page<SysUserWalletWithdrawLogDO> myPageUserSelf(SysUserWalletWithdrawLogPageUserSelfDTO dto) {

        Long currentUserId = UserUtil.getCurrentUserId();

        SysUserWalletWithdrawLogPageDTO sysUserWalletWithdrawLogPageDTO =
            BeanUtil.copyProperties(dto, SysUserWalletWithdrawLogPageDTO.class);

        sysUserWalletWithdrawLogPageDTO.setUserId(currentUserId);

        return myPage(sysUserWalletWithdrawLogPageDTO);

    }

    /**
     * 通过主键id，查看详情-用户
     */
    @Override
    public SysUserWalletWithdrawLogDO infoByIdUserSelf(NotNullId notNullId) {

        Long currentUserId = UserUtil.getCurrentUserId();

        return lambdaQuery().eq(SysUserWalletWithdrawLogDO::getId, notNullId.getId())
            .in(SysUserWalletWithdrawLogDO::getUserId, currentUserId).one();

    }

    /**
     * 批量删除-用户
     */
    @Override
    public String deleteByIdSetUserSelf(NotEmptyIdSet notEmptyIdSet) {

        Set<Long> idSet = notEmptyIdSet.getIdSet();

        if (CollUtil.isEmpty(idSet)) {
            return BaseBizCodeEnum.OK;
        }

        Long currentUserId = UserUtil.getCurrentUserId();

        Long checkCount =
            lambdaQuery().in(BaseEntity::getId, idSet).eq(SysUserWalletWithdrawLogDO::getUserId, currentUserId).count();

        if (checkCount != idSet.size()) {
            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST);
        }

        // 执行：批量删除
        return deleteByIdSetCommonHandle(idSet);

    }

    /**
     * 获取：检查：是否非法操作的 getCheckIllegalFunc1
     */
    @NotNull
    private Func1<Set<Long>, Long> getCheckIllegalFunc1(Set<Long> idSet) {

        return tenantIdSet -> lambdaQuery().in(SysUserWalletWithdrawLogDO::getId, idSet)
            .in(BaseEntityNoId::getTenantId, tenantIdSet).count();

    }

    /**
     * 获取：检查：是否非法操作的 getTenantIdBaseEntityFunc1
     */
    @NotNull
    private Func1<Long, BaseEntityNoIdFather> getTenantIdBaseEntityFunc1() {

        return id -> lambdaQuery().eq(SysUserWalletWithdrawLogDO::getId, id)
            .select(SysUserWalletWithdrawLogDO::getTenantId).one();

    }

}
