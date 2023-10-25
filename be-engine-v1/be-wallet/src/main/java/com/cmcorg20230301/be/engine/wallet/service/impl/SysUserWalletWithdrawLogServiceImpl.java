package com.cmcorg20230301.be.engine.wallet.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullIdAndStringValue;
import com.cmcorg20230301.be.engine.model.model.vo.DictIntegerVO;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import com.cmcorg20230301.be.engine.redisson.util.RedissonUtil;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.mapper.SysUserMapper;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoIdFather;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.util.SysTenantUtil;
import com.cmcorg20230301.be.engine.security.util.UserUtil;
import com.cmcorg20230301.be.engine.wallet.mapper.SysUserBankCardMapper;
import com.cmcorg20230301.be.engine.wallet.mapper.SysUserWalletWithdrawLogMapper;
import com.cmcorg20230301.be.engine.wallet.model.dto.*;
import com.cmcorg20230301.be.engine.wallet.model.entity.SysUserBankCardDO;
import com.cmcorg20230301.be.engine.wallet.model.entity.SysUserWalletDO;
import com.cmcorg20230301.be.engine.wallet.model.entity.SysUserWalletWithdrawLogDO;
import com.cmcorg20230301.be.engine.wallet.model.enums.SysUserWalletLogTypeEnum;
import com.cmcorg20230301.be.engine.wallet.model.enums.SysUserWalletWithdrawStatusEnum;
import com.cmcorg20230301.be.engine.wallet.model.enums.SysUserWalletWithdrawTypeEnum;
import com.cmcorg20230301.be.engine.wallet.service.SysUserWalletService;
import com.cmcorg20230301.be.engine.wallet.service.SysUserWalletWithdrawLogService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SysUserWalletWithdrawLogServiceImpl
    extends ServiceImpl<SysUserWalletWithdrawLogMapper, SysUserWalletWithdrawLogDO>
    implements SysUserWalletWithdrawLogService {

    @Resource
    SysUserWalletService sysUserWalletService;

    @Resource
    SysUserBankCardMapper sysUserBankCardMapper;

    @Resource
    SysUserMapper sysUserMapper;

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
     * 新增/修改
     */
    @Override
    @DSTransactional
    public String insertOrUpdate(SysUserWalletWithdrawLogInsertOrUpdateDTO dto) {

        Long userId = dto.getUserId();

        Set<Long> userIdSet = CollUtil.newHashSet(userId);

        // 检查：是否非法操作
        SysTenantUtil.checkIllegal(userIdSet,
            tenantIdSet -> ChainWrappers.lambdaQueryChain(sysUserMapper).eq(BaseEntity::getId, userId)
                .in(BaseEntityNoId::getTenantId, tenantIdSet).count());

        // 执行
        return doInsertOrUpdate(dto, userId, false);

    }

    /**
     * 取消
     */
    @Override
    @DSTransactional
    public String cancel(NotNullId notNullId) {

        Set<Long> idSet = CollUtil.newHashSet(notNullId.getId());

        // 检查：idSet所在的租户，是否是当前用户所管理的租户
        SysTenantUtil.checkIllegal(idSet, getCheckIllegalFunc1(idSet));

        // 执行
        return doCancel(notNullId, false, false);

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

                .eq(dto.getId() != null, SysUserWalletWithdrawLogDO::getId, dto.getId())

                .like(StrUtil.isNotBlank(dto.getBankCardNo()), SysUserWalletWithdrawLogDO::getBankCardNo,
                    dto.getBankCardNo()) //

                .like(StrUtil.isNotBlank(dto.getOpenBankName()), SysUserWalletWithdrawLogDO::getOpenBankName,
                    dto.getOpenBankName()) //

                .like(StrUtil.isNotBlank(dto.getBranchBankName()), SysUserWalletWithdrawLogDO::getOpenBankName,
                    dto.getBranchBankName()) //

                .like(StrUtil.isNotBlank(dto.getPayeeName()), SysUserWalletWithdrawLogDO::getPayeeName,
                    dto.getPayeeName()) //

                .like(StrUtil.isNotBlank(dto.getRejectReason()), SysUserWalletWithdrawLogDO::getRejectReason,
                    dto.getRejectReason()) //

                .eq(dto.getWithdrawStatus() != null, SysUserWalletWithdrawLogDO::getWithdrawStatus,
                    dto.getWithdrawStatus()) //

                .ne(SysUserWalletWithdrawTypeEnum.USER.equals(dto.getType()), SysUserWalletWithdrawLogDO::getUserId,
                    BaseConstant.TENANT_USER_ID) //

                .eq(SysUserWalletWithdrawTypeEnum.TENANT.equals(dto.getType()), SysUserWalletWithdrawLogDO::getUserId,
                    BaseConstant.TENANT_USER_ID) //

                .le(dto.getCtEndTime() != null, SysUserWalletWithdrawLogDO::getCreateTime, dto.getCtEndTime())
                .ge(dto.getCtBeginTime() != null, SysUserWalletWithdrawLogDO::getCreateTime, dto.getCtBeginTime())

                .le(dto.getEndWithdrawMoney() != null, SysUserWalletWithdrawLogDO::getWithdrawMoney,
                    dto.getEndWithdrawMoney()) //

                .ge(dto.getBeginWithdrawMoney() != null, SysUserWalletWithdrawLogDO::getWithdrawMoney,
                    dto.getBeginWithdrawMoney()) //

                .in(BaseEntityNoId::getTenantId, dto.getTenantIdSet()) //

                .orderByDesc(SysUserWalletWithdrawLogDO::getUpdateTime).page(dto.page(true));

        for (SysUserWalletWithdrawLogDO item : page.getRecords()) {

            // 脱敏：SysUserWalletWithdrawLogDO
            desensitizedSysUserWalletWithdrawLogDO(item);

        }

        return page;

    }

    /**
     * 脱敏：SysUserWalletWithdrawLogDO
     */
    private void desensitizedSysUserWalletWithdrawLogDO(SysUserWalletWithdrawLogDO sysUserWalletWithdrawLogDO) {

        if (sysUserWalletWithdrawLogDO == null) {
            return;
        }

        // 备注：需要和：银行卡的脱敏一致
        sysUserWalletWithdrawLogDO.setBankCardNo(
            StrUtil.cleanBlank(DesensitizedUtil.bankCard(sysUserWalletWithdrawLogDO.getBankCardNo()))); // 脱敏

        sysUserWalletWithdrawLogDO
            .setPayeeName(DesensitizedUtil.chineseName(sysUserWalletWithdrawLogDO.getPayeeName())); // 脱敏

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
     * 分页排序查询-租户
     */
    @Override
    public Page<SysUserWalletWithdrawLogDO> myPageTenant(SysUserWalletWithdrawLogPageUserSelfDTO dto) {

        SysUserWalletWithdrawLogPageDTO sysUserWalletWithdrawLogPageDTO =
            BeanUtil.copyProperties(dto, SysUserWalletWithdrawLogPageDTO.class);

        sysUserWalletWithdrawLogPageDTO.setUserId(BaseConstant.TENANT_USER_ID);

        // 执行
        return myPage(sysUserWalletWithdrawLogPageDTO);

    }

    /**
     * 新增/修改-租户
     */
    @Override
    @DSTransactional
    public String insertOrUpdateTenant(SysUserWalletWithdrawLogInsertOrUpdateTenantDTO dto) {

        SysTenantUtil.checkTenantId(dto.getTenantId());

        // 执行
        return doInsertOrUpdate(dto, dto.getTenantId(), true);

    }

    /**
     * 取消-租户
     */
    @Override
    @DSTransactional
    public String cancelTenant(NotNullId notNullId) {

        Set<Long> idSet = CollUtil.newHashSet(notNullId.getId());

        // 检查：是否非法操作
        SysTenantUtil.checkIllegal(idSet, getCheckIllegalFunc1(idSet));

        // 执行
        return doCancel(notNullId, true, false);

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

        // 执行
        return myPage(sysUserWalletWithdrawLogPageDTO);

    }

    /**
     * 新增/修改-用户
     */
    @Override
    @DSTransactional
    public String insertOrUpdateUserSelf(SysUserWalletWithdrawLogInsertOrUpdateUserSelfDTO dto) {

        Long currentUserId = UserUtil.getCurrentUserId();

        // 执行
        return doInsertOrUpdate(dto, currentUserId, false);

    }

    /**
     * 执行：新增/修改-用户
     *
     * @param id 用户 id 或者 租户主键 id
     */
    @NotNull
    private String doInsertOrUpdate(SysUserWalletWithdrawLogInsertOrUpdateUserSelfDTO dto, Long id,
        boolean tenantFlag) {

        Long currentUserId = UserUtil.getCurrentUserId();

        SysUserWalletWithdrawLogDO sysUserWalletWithdrawLogDO = new SysUserWalletWithdrawLogDO();

        if (tenantFlag) {

            sysUserWalletWithdrawLogDO.setUserId(BaseConstant.TENANT_USER_ID);

        } else {

            sysUserWalletWithdrawLogDO.setUserId(id);

        }

        sysUserWalletWithdrawLogDO.setWithdrawMoney(dto.getWithdrawMoney());

        // 查询：用户银行卡信息
        SysUserBankCardDO sysUserBankCardDO =
            ChainWrappers.lambdaQueryChain(sysUserBankCardMapper).eq(!tenantFlag, SysUserBankCardDO::getId, id)
                .eq(tenantFlag, SysUserBankCardDO::getId, BaseConstant.TENANT_USER_ID)
                .eq(tenantFlag, BaseEntityNoIdFather::getTenantId, id).one();

        if (sysUserBankCardDO == null) {
            ApiResultVO.errorMsg("操作失败：请先绑定银行卡");
        }

        sysUserWalletWithdrawLogDO.setOpenBankName(sysUserBankCardDO.getOpenBankName());
        sysUserWalletWithdrawLogDO.setPayeeName(sysUserBankCardDO.getPayeeName());
        sysUserWalletWithdrawLogDO.setBankCardNo(sysUserBankCardDO.getBankCardNo());
        sysUserWalletWithdrawLogDO.setBranchBankName(sysUserBankCardDO.getBranchBankName());

        sysUserWalletWithdrawLogDO.setWithdrawStatus(SysUserWalletWithdrawStatusEnum.COMMIT);

        sysUserWalletWithdrawLogDO.setRejectReason("");
        sysUserWalletWithdrawLogDO.setEnableFlag(true);
        sysUserWalletWithdrawLogDO.setDelFlag(false);
        sysUserWalletWithdrawLogDO.setRemark("");

        if (tenantFlag) {
            sysUserWalletWithdrawLogDO.setTenantId(id);
        }

        saveOrUpdate(sysUserWalletWithdrawLogDO); // 先操作数据库，原因：如果后面报错了，则会回滚该更新

        // 检查和增加：用户钱包的可提现余额
        sysUserWalletService.doAddWithdrawableMoney(currentUserId, new Date(), CollUtil.newHashSet(id),
            sysUserWalletWithdrawLogDO.getWithdrawMoney().negate(), SysUserWalletLogTypeEnum.REDUCE_WITHDRAW, true,
            true, tenantFlag, null, null);

        return BaseBizCodeEnum.OK;

    }

    /**
     * 取消-用户
     */
    @Override
    @DSTransactional
    public String cancelUserSelf(NotNullId notNullId) {

        // 执行
        return doCancel(notNullId, false, true);

    }

    /**
     * 执行：取消
     */
    private String doCancel(NotNullId notNullId, boolean tenantFlag, boolean userSelfFlag) {

        Long currentUserId = UserUtil.getCurrentUserId();

        return RedissonUtil.doLock(BaseRedisKeyEnum.PRE_USER_WALLET_WITHDRAW_LOG.name() + notNullId.getId(), () -> {

            SysUserWalletWithdrawLogDO sysUserWalletWithdrawLogDO =
                lambdaQuery().eq(BaseEntity::getId, notNullId.getId())
                    .eq(!tenantFlag && userSelfFlag, SysUserWalletWithdrawLogDO::getUserId, currentUserId)
                    .eq(tenantFlag && !userSelfFlag, SysUserWalletWithdrawLogDO::getUserId, BaseConstant.TENANT_USER_ID)
                    .select(BaseEntity::getId, SysUserWalletWithdrawLogDO::getWithdrawMoney,
                        SysUserWalletWithdrawLogDO::getWithdrawStatus, BaseEntityNoIdFather::getTenantId,
                        SysUserWalletWithdrawLogDO::getUserId).one();

            if (sysUserWalletWithdrawLogDO == null) {
                ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST, notNullId.getId());
            }

            // 只有待受理状态的提现记录，才可以取消
            if (!SysUserWalletWithdrawStatusEnum.COMMIT.equals(sysUserWalletWithdrawLogDO.getWithdrawStatus())) {
                ApiResultVO.error("操作失败：只能取消待受理状态的提现记录", notNullId.getId());
            }

            sysUserWalletWithdrawLogDO.setWithdrawStatus(SysUserWalletWithdrawStatusEnum.CANCEL);

            updateById(sysUserWalletWithdrawLogDO); // 先更新提现记录状态，原因：如果后面报错了，则会回滚该更新

            if (tenantFlag) {

                // 检查和增加：用户钱包的可提现余额
                sysUserWalletService.doAddWithdrawableMoney(currentUserId, new Date(),
                    CollUtil.newHashSet(sysUserWalletWithdrawLogDO.getTenantId()),
                    sysUserWalletWithdrawLogDO.getWithdrawMoney(), SysUserWalletLogTypeEnum.REDUCE_WITHDRAW, true, true,
                    true, null, null);

            } else {

                // 检查和增加：用户钱包的可提现余额
                sysUserWalletService.doAddWithdrawableMoney(currentUserId, new Date(),
                    CollUtil.newHashSet(sysUserWalletWithdrawLogDO.getUserId()),
                    sysUserWalletWithdrawLogDO.getWithdrawMoney(), SysUserWalletLogTypeEnum.REDUCE_WITHDRAW, true, true,
                    false, null, null);

            }

            return BaseBizCodeEnum.OK;

        });

    }

    /**
     * 受理-用户的提现记录
     */
    @Override
    public String accept(NotEmptyIdSet notEmptyIdSet) {

        // 检查：是否非法操作
        SysTenantUtil.checkIllegal(notEmptyIdSet.getIdSet(), getCheckIllegalFunc1(notEmptyIdSet.getIdSet()));

        return RedissonUtil
            .doMultiLock(BaseRedisKeyEnum.PRE_USER_WALLET_WITHDRAW_LOG.name(), notEmptyIdSet.getIdSet(), () -> {

                List<SysUserWalletWithdrawLogDO> sysUserWalletWithdrawLogDOList =
                    lambdaQuery().in(BaseEntity::getId, notEmptyIdSet.getIdSet())
                        .eq(SysUserWalletWithdrawLogDO::getWithdrawStatus, SysUserWalletWithdrawStatusEnum.COMMIT)
                        .list();

                if (CollUtil.isEmpty(sysUserWalletWithdrawLogDOList)) {
                    return BaseBizCodeEnum.OK;
                }

                Map<Long, List<SysUserWalletWithdrawLogDO>> groupMap = sysUserWalletWithdrawLogDOList.stream()
                    .collect(Collectors.groupingBy(SysUserWalletWithdrawLogDO::getUserId));

                // 检查：用户钱包是否被冻结
                String resStr =
                    checkUserWallet(sysUserWalletWithdrawLogDOList, groupMap, notEmptyIdSet.getIdSet().size() == 1);

                if (StrUtil.isNotBlank(resStr)) {
                    return resStr;
                }

                if (CollUtil.isEmpty(sysUserWalletWithdrawLogDOList)) {
                    return BaseBizCodeEnum.OK;
                }

                for (SysUserWalletWithdrawLogDO item : sysUserWalletWithdrawLogDOList) {
                    item.setWithdrawStatus(SysUserWalletWithdrawStatusEnum.ACCEPT);
                }

                updateBatchById(sysUserWalletWithdrawLogDOList);

                return BaseBizCodeEnum.OK;

            });

    }

    /**
     * 检查：用户钱包是否被冻结
     */
    @Nullable
    private String checkUserWallet(List<SysUserWalletWithdrawLogDO> sysUserWalletWithdrawLogDOList,
        Map<Long, List<SysUserWalletWithdrawLogDO>> groupMap, boolean errorFlag) {

        return RedissonUtil.doMultiLock(BaseRedisKeyEnum.PRE_USER_WALLET.name(), groupMap.keySet(), () -> {

            // 只要：没有被冻结的钱包
            List<SysUserWalletDO> sysUserWalletDOList =
                sysUserWalletService.lambdaQuery().in(SysUserWalletDO::getId, groupMap.keySet())
                    .eq(BaseEntityNoId::getEnableFlag, true).select(SysUserWalletDO::getId).list();

            if (CollUtil.isEmpty(sysUserWalletDOList)) {

                if (errorFlag) {
                    ApiResultVO.error("操作失败：钱包已被冻结，请联系管理员", sysUserWalletWithdrawLogDOList.get(0).getUserId());
                }

                return BaseBizCodeEnum.OK;

            }

            sysUserWalletWithdrawLogDOList.clear(); // 先移除原始数据

            for (SysUserWalletDO item : sysUserWalletDOList) {

                sysUserWalletWithdrawLogDOList.addAll(groupMap.get(item.getId())); // 再添加数据

            }

            return null;

        });

    }

    /**
     * 成功-用户的提现记录
     */
    @Override
    @DSTransactional
    public String success(NotNullId notNullId) {

        Set<Long> idSet = CollUtil.newHashSet(notNullId.getId());

        // 检查：是否非法操作
        SysTenantUtil.checkIllegal(idSet, getCheckIllegalFunc1(idSet));

        return RedissonUtil.doLock(BaseRedisKeyEnum.PRE_USER_WALLET_WITHDRAW_LOG.name() + notNullId.getId(), () -> {

            SysUserWalletWithdrawLogDO sysUserWalletWithdrawLogDO =
                lambdaQuery().eq(BaseEntity::getId, notNullId.getId())
                    .eq(SysUserWalletWithdrawLogDO::getWithdrawStatus, SysUserWalletWithdrawStatusEnum.ACCEPT)
                    .select(BaseEntity::getId, SysUserWalletWithdrawLogDO::getUserId).one();

            if (sysUserWalletWithdrawLogDO == null) {
                ApiResultVO.error("操作失败：只能成功受理中状态的提现记录", notNullId.getId());
            }

            // 只要：没有被冻结的钱包
            boolean userWalletEnableFlag =
                sysUserWalletService.lambdaQuery().eq(SysUserWalletDO::getId, sysUserWalletWithdrawLogDO.getUserId())
                    .eq(BaseEntityNoId::getEnableFlag, true).exists();

            if (!userWalletEnableFlag) {
                ApiResultVO.error("操作失败：钱包已被冻结，请联系管理员", sysUserWalletWithdrawLogDO.getUserId());
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
    @DSTransactional
    public String reject(NotNullIdAndStringValue notNullIdAndStringValue) {

        Long currentUserId = UserUtil.getCurrentUserId();

        Set<Long> idSet = CollUtil.newHashSet(notNullIdAndStringValue.getId());

        // 检查：是否非法操作
        SysTenantUtil.checkIllegal(idSet, getCheckIllegalFunc1(idSet));

        return RedissonUtil
            .doLock(BaseRedisKeyEnum.PRE_USER_WALLET_WITHDRAW_LOG.name() + notNullIdAndStringValue.getId(), () -> {

                SysUserWalletWithdrawLogDO sysUserWalletWithdrawLogDO =
                    lambdaQuery().eq(BaseEntity::getId, notNullIdAndStringValue.getId())
                        .eq(SysUserWalletWithdrawLogDO::getWithdrawStatus, SysUserWalletWithdrawStatusEnum.ACCEPT)
                        .select(BaseEntity::getId, SysUserWalletWithdrawLogDO::getWithdrawMoney,
                            SysUserWalletWithdrawLogDO::getUserId).one();

                if (sysUserWalletWithdrawLogDO == null) {
                    ApiResultVO.error("操作失败：只能拒绝受理中状态的提现记录", notNullIdAndStringValue.getId());
                }

                sysUserWalletWithdrawLogDO.setWithdrawStatus(SysUserWalletWithdrawStatusEnum.REJECT);
                sysUserWalletWithdrawLogDO.setRejectReason(notNullIdAndStringValue.getValue());

                updateById(sysUserWalletWithdrawLogDO); // 先更新提现记录状态，原因：如果后面报错了，则会回滚该更新

                // 检查和增加：用户钱包的可提现余额
                sysUserWalletService.doAddWithdrawableMoney(currentUserId, new Date(),
                    CollUtil.newHashSet(sysUserWalletWithdrawLogDO.getUserId()),
                    sysUserWalletWithdrawLogDO.getWithdrawMoney(), SysUserWalletLogTypeEnum.REDUCE_WITHDRAW, true, true,
                    false, null, null);

                return BaseBizCodeEnum.OK;

            });

    }

    /**
     * 获取：检查：是否非法操作的 getCheckIllegalFunc1
     */
    @NotNull
    private Func1<Set<Long>, Long> getCheckIllegalFunc1(Set<Long> idSet) {

        return tenantIdSet -> lambdaQuery().in(SysUserWalletWithdrawLogDO::getId, idSet)
            .in(BaseEntityNoId::getTenantId, tenantIdSet).count();

    }

}
