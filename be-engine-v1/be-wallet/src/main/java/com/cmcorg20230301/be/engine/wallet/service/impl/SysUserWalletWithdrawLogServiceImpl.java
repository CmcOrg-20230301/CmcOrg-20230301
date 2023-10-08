package com.cmcorg20230301.be.engine.wallet.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullIdAndStringValue;
import com.cmcorg20230301.be.engine.model.model.vo.DictIntegerVO;
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
import com.cmcorg20230301.be.engine.wallet.model.enums.SysUserWalletLogTypeEnum;
import com.cmcorg20230301.be.engine.wallet.model.enums.SysUserWalletWithdrawStatusEnum;
import com.cmcorg20230301.be.engine.wallet.service.SysUserWalletService;
import com.cmcorg20230301.be.engine.wallet.service.SysUserWalletWithdrawLogService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class SysUserWalletWithdrawLogServiceImpl
    extends ServiceImpl<SysUserWalletWithdrawLogMapper, SysUserWalletWithdrawLogDO>
    implements SysUserWalletWithdrawLogService {

    @Resource
    SysUserWalletService sysUserWalletService;

    /**
     * 下拉列表-提现状态
     */
    @Override
    public Page<DictIntegerVO> withdrawStatusDictList() {

        List<DictIntegerVO> dictVOList = new ArrayList<>();

        for (SysUserWalletWithdrawStatusEnum item : SysUserWalletWithdrawStatusEnum.values()) {

            dictVOList.add(new DictIntegerVO(item.getCode(), item.getName()));

        }

        return new Page<DictIntegerVO>().setTotal(dictVOList.size()).setRecords(dictVOList);

    }

    /**
     * 分页排序查询
     */
    @Override
    public Page<SysUserWalletWithdrawLogDO> myPage(SysUserWalletWithdrawLogPageDTO dto) {

        // 处理：MyTenantPageDTO
        SysTenantUtil.handleMyTenantPageDTO(dto, true);

        Page<SysUserWalletWithdrawLogDO> page =
            lambdaQuery().eq(dto.getUserId() != null, SysUserWalletWithdrawLogDO::getUserId, dto.getUserId())
                .like(StrUtil.isNotBlank(dto.getBankCardNo()), SysUserWalletWithdrawLogDO::getBankCardNo,
                    dto.getBankCardNo()) //
                .like(StrUtil.isNotBlank(dto.getOpenBankName()), SysUserWalletWithdrawLogDO::getOpenBankName,
                    dto.getOpenBankName()) //
                .like(StrUtil.isNotBlank(dto.getBranchBankName()), SysUserWalletWithdrawLogDO::getOpenBankName,
                    dto.getBranchBankName()) //
                .like(StrUtil.isNotBlank(dto.getPayeeName()), SysUserWalletWithdrawLogDO::getPayeeName,
                    dto.getPayeeName()) //
                .eq(dto.getWithdrawStatus() != null, SysUserWalletWithdrawLogDO::getWithdrawStatus,
                    dto.getWithdrawStatus()) //
                .le(dto.getCtEndTime() != null, SysUserWalletWithdrawLogDO::getCreateTime, dto.getCtEndTime())
                .ge(dto.getCtBeginTime() != null, SysUserWalletWithdrawLogDO::getCreateTime, dto.getCtBeginTime())
                .in(BaseEntityNoId::getTenantId, dto.getTenantIdSet()) //
                .orderByDesc(SysUserWalletWithdrawLogDO::getUpdateTime).page(dto.page(true));

        for (SysUserWalletWithdrawLogDO item : page.getRecords()) {

            item.setBankCardNo(DesensitizedUtil.bankCard(item.getBankCardNo())); // 脱敏

            item.setBranchBankName(DesensitizedUtil
                .desensitized(item.getBranchBankName(), DesensitizedUtil.DesensitizedType.ADDRESS)); // 脱敏

            item.setPayeeName(DesensitizedUtil.chineseName(item.getPayeeName())); // 脱敏

        }

        return page;

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
        sysUserWalletWithdrawLogDO.setOpenBankName(dto.getOpenBankName());
        sysUserWalletWithdrawLogDO.setPayeeName(dto.getPayeeName());
        sysUserWalletWithdrawLogDO.setBankCardNo(dto.getBankCardNo());
        sysUserWalletWithdrawLogDO.setBranchBankName(dto.getBranchBankName());

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
    public String commitUserSelf(NotNullId notNullId) {

        Long currentUserId = UserUtil.getCurrentUserId();

        return RedissonUtil.doLock(BaseRedisKeyEnum.PRE_USER_WALLET_WITHDRAW_LOG.name() + notNullId.getId(), () -> {

            SysUserWalletWithdrawLogDO sysUserWalletWithdrawLogDO =
                lambdaQuery().eq(BaseEntity::getId, notNullId.getId())
                    .eq(SysUserWalletWithdrawLogDO::getUserId, currentUserId).one();

            if (sysUserWalletWithdrawLogDO == null) {
                ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST, notNullId.getId());
            }

            // 只有草稿状态的提现记录，才可以提交
            if (!SysUserWalletWithdrawStatusEnum.DRAFT.equals(sysUserWalletWithdrawLogDO.getWithdrawStatus())) {
                ApiResultVO.error("操作失败：只能提交草稿状态的提现记录", notNullId.getId());
            }

            sysUserWalletWithdrawLogDO.setWithdrawStatus(SysUserWalletWithdrawStatusEnum.COMMIT);

            updateById(sysUserWalletWithdrawLogDO); // 先更新提现记录状态，原因：如果后面报错了，则会回滚该更新

            // 检查和增加：用户钱包的可提现余额
            sysUserWalletService.doAddTotalMoney(currentUserId, new Date(), CollUtil.newHashSet(notNullId.getId()),
                sysUserWalletWithdrawLogDO.getWithdrawMoney().negate(), SysUserWalletLogTypeEnum.REDUCE_WITHDRAW, true);

            return BaseBizCodeEnum.OK;

        });

    }

    /**
     * 撤回-用户
     */
    @Override
    public String revokeUserSelf(NotNullId notNullId) {

        Long currentUserId = UserUtil.getCurrentUserId();

        return RedissonUtil.doLock(BaseRedisKeyEnum.PRE_USER_WALLET_WITHDRAW_LOG.name() + notNullId.getId(), () -> {

            SysUserWalletWithdrawLogDO sysUserWalletWithdrawLogDO =
                lambdaQuery().eq(BaseEntity::getId, notNullId.getId())
                    .eq(SysUserWalletWithdrawLogDO::getUserId, currentUserId).one();

            if (sysUserWalletWithdrawLogDO == null) {
                ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST, notNullId.getId());
            }

            // 只有待受理状态的提现记录，才可以撤回
            if (!SysUserWalletWithdrawStatusEnum.COMMIT.equals(sysUserWalletWithdrawLogDO.getWithdrawStatus())) {
                ApiResultVO.error("操作失败：只能撤回待受理状态的提现记录", notNullId.getId());
            }

            sysUserWalletWithdrawLogDO.setWithdrawStatus(SysUserWalletWithdrawStatusEnum.DRAFT);

            updateById(sysUserWalletWithdrawLogDO); // 先更新提现记录状态，原因：如果后面报错了，则会回滚该更新

            // 检查和增加：用户钱包的可提现余额
            sysUserWalletService.doAddTotalMoney(currentUserId, new Date(), CollUtil.newHashSet(notNullId.getId()),
                sysUserWalletWithdrawLogDO.getWithdrawMoney(), SysUserWalletLogTypeEnum.REDUCE_WITHDRAW, true);

            return BaseBizCodeEnum.OK;

        });

    }

    /**
     * 受理-用户的提现记录
     */
    @Override
    public String accept(NotNullId notNullId) {

        Long currentUserId = UserUtil.getCurrentUserId();

        return RedissonUtil.doLock(BaseRedisKeyEnum.PRE_USER_WALLET_WITHDRAW_LOG.name() + notNullId.getId(), () -> {

            SysUserWalletWithdrawLogDO sysUserWalletWithdrawLogDO =
                lambdaQuery().eq(BaseEntity::getId, notNullId.getId())
                    .eq(SysUserWalletWithdrawLogDO::getUserId, currentUserId).one();

            if (sysUserWalletWithdrawLogDO == null) {
                ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST, notNullId.getId());
            }

            // 只有待受理状态的提现记录，才可以受理
            if (!SysUserWalletWithdrawStatusEnum.COMMIT.equals(sysUserWalletWithdrawLogDO.getWithdrawStatus())) {
                ApiResultVO.error("操作失败：只能受理待受理状态的提现记录", notNullId.getId());
            }

            sysUserWalletWithdrawLogDO.setWithdrawStatus(SysUserWalletWithdrawStatusEnum.ACCEPT);

            updateById(sysUserWalletWithdrawLogDO); // 先更新提现记录状态，原因：如果后面报错了，则会回滚该更新

            return BaseBizCodeEnum.OK;

        });

    }

    /**
     * 成功-用户的提现记录
     */
    @Override
    public String success(NotNullId notNullId) {

        Long currentUserId = UserUtil.getCurrentUserId();

        return RedissonUtil.doLock(BaseRedisKeyEnum.PRE_USER_WALLET_WITHDRAW_LOG.name() + notNullId.getId(), () -> {

            SysUserWalletWithdrawLogDO sysUserWalletWithdrawLogDO =
                lambdaQuery().eq(BaseEntity::getId, notNullId.getId())
                    .eq(SysUserWalletWithdrawLogDO::getUserId, currentUserId).one();

            if (sysUserWalletWithdrawLogDO == null) {
                ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST, notNullId.getId());
            }

            // 只有受理中状态的提现记录，才可以成功
            if (!SysUserWalletWithdrawStatusEnum.ACCEPT.equals(sysUserWalletWithdrawLogDO.getWithdrawStatus())) {
                ApiResultVO.error("操作失败：只能成功受理中状态的提现记录", notNullId.getId());
            }

            sysUserWalletWithdrawLogDO.setWithdrawStatus(SysUserWalletWithdrawStatusEnum.SUCCESS);

            updateById(sysUserWalletWithdrawLogDO); // 先更新提现记录状态，原因：如果后面报错了，则会回滚该更新

            return BaseBizCodeEnum.OK;

        });

    }

    /**
     * 拒绝-用户的提现记录
     */
    @Override
    public String reject(NotNullIdAndStringValue notNullIdAndStringValue) {

        Long currentUserId = UserUtil.getCurrentUserId();

        return RedissonUtil
            .doLock(BaseRedisKeyEnum.PRE_USER_WALLET_WITHDRAW_LOG.name() + notNullIdAndStringValue.getId(), () -> {

                SysUserWalletWithdrawLogDO sysUserWalletWithdrawLogDO =
                    lambdaQuery().eq(BaseEntity::getId, notNullIdAndStringValue.getId())
                        .eq(SysUserWalletWithdrawLogDO::getUserId, currentUserId).one();

                if (sysUserWalletWithdrawLogDO == null) {
                    ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST, notNullIdAndStringValue.getId());
                }

                // 只有受理中状态的提现记录，才可以拒绝
                if (!SysUserWalletWithdrawStatusEnum.ACCEPT.equals(sysUserWalletWithdrawLogDO.getWithdrawStatus())) {
                    ApiResultVO.error("操作失败：只能拒绝受理中状态的提现记录", notNullIdAndStringValue.getId());
                }

                sysUserWalletWithdrawLogDO.setWithdrawStatus(SysUserWalletWithdrawStatusEnum.REJECT);
                sysUserWalletWithdrawLogDO.setRejectReason(notNullIdAndStringValue.getValue());

                updateById(sysUserWalletWithdrawLogDO); // 先更新提现记录状态，原因：如果后面报错了，则会回滚该更新

                // 检查和增加：用户钱包的可提现余额
                sysUserWalletService
                    .doAddTotalMoney(currentUserId, new Date(), CollUtil.newHashSet(notNullIdAndStringValue.getId()),
                        sysUserWalletWithdrawLogDO.getWithdrawMoney(), SysUserWalletLogTypeEnum.REDUCE_WITHDRAW, true);

                return BaseBizCodeEnum.OK;

            });

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
