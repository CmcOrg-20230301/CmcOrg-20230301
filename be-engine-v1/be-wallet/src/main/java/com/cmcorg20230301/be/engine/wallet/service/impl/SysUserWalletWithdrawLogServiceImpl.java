package com.cmcorg20230301.be.engine.wallet.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
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
import com.cmcorg20230301.be.engine.wallet.mapper.SysUserBankCardMapper;
import com.cmcorg20230301.be.engine.wallet.mapper.SysUserWalletWithdrawLogMapper;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserWalletWithdrawLogInsertOrUpdateUserSelfDTO;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserWalletWithdrawLogPageDTO;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserWalletWithdrawLogPageUserSelfDTO;
import com.cmcorg20230301.be.engine.wallet.model.entity.SysUserBankCardDO;
import com.cmcorg20230301.be.engine.wallet.model.entity.SysUserWalletWithdrawLogDO;
import com.cmcorg20230301.be.engine.wallet.model.enums.SysUserWalletLogTypeEnum;
import com.cmcorg20230301.be.engine.wallet.model.enums.SysUserWalletWithdrawStatusEnum;
import com.cmcorg20230301.be.engine.wallet.service.SysUserWalletService;
import com.cmcorg20230301.be.engine.wallet.service.SysUserWalletWithdrawLogService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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

    @Resource
    SysUserBankCardMapper sysUserBankCardMapper;

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

                .like(StrUtil.isNotBlank(dto.getRejectReason()), SysUserWalletWithdrawLogDO::getRejectReason,
                    dto.getRejectReason()) //

                .eq(dto.getWithdrawStatus() != null, SysUserWalletWithdrawLogDO::getWithdrawStatus,
                    dto.getWithdrawStatus()) //

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
    public String insertOrUpdateUserSelf(SysUserWalletWithdrawLogInsertOrUpdateUserSelfDTO dto) {

        Long currentUserId = UserUtil.getCurrentUserId();

        SysUserWalletWithdrawLogDO sysUserWalletWithdrawLogDO = new SysUserWalletWithdrawLogDO();

        sysUserWalletWithdrawLogDO.setUserId(currentUserId);
        sysUserWalletWithdrawLogDO.setWithdrawMoney(dto.getWithdrawMoney());

        // 查询：用户银行卡信息
        SysUserBankCardDO sysUserBankCardDO =
            ChainWrappers.lambdaQueryChain(sysUserBankCardMapper).eq(SysUserBankCardDO::getId, currentUserId).one();

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

        saveOrUpdate(sysUserWalletWithdrawLogDO); // 先操作数据库，原因：如果后面报错了，则会回滚该更新

        // 检查和增加：用户钱包的可提现余额
        sysUserWalletService.doAddWithdrawableMoney(currentUserId, new Date(), CollUtil.newHashSet(currentUserId),
            sysUserWalletWithdrawLogDO.getWithdrawMoney().negate(), SysUserWalletLogTypeEnum.REDUCE_WITHDRAW, true);

        return BaseBizCodeEnum.OK;

    }

    /**
     * 取消-用户
     */
    @Override
    public String cancelUserSelf(NotNullId notNullId) {

        Long currentUserId = UserUtil.getCurrentUserId();

        return RedissonUtil.doLock(BaseRedisKeyEnum.PRE_USER_WALLET_WITHDRAW_LOG.name() + notNullId.getId(), () -> {

            SysUserWalletWithdrawLogDO sysUserWalletWithdrawLogDO =
                lambdaQuery().eq(BaseEntity::getId, notNullId.getId())
                    .eq(SysUserWalletWithdrawLogDO::getUserId, currentUserId).one();

            if (sysUserWalletWithdrawLogDO == null) {
                ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST, notNullId.getId());
            }

            // 只有待受理状态的提现记录，才可以取消
            if (!SysUserWalletWithdrawStatusEnum.COMMIT.equals(sysUserWalletWithdrawLogDO.getWithdrawStatus())) {
                ApiResultVO.error("操作失败：只能取消待受理状态的提现记录", notNullId.getId());
            }

            sysUserWalletWithdrawLogDO.setWithdrawStatus(SysUserWalletWithdrawStatusEnum.CANCEL);

            updateById(sysUserWalletWithdrawLogDO); // 先更新提现记录状态，原因：如果后面报错了，则会回滚该更新

            // 检查和增加：用户钱包的可提现余额
            sysUserWalletService.doAddWithdrawableMoney(currentUserId, new Date(), CollUtil.newHashSet(currentUserId),
                sysUserWalletWithdrawLogDO.getWithdrawMoney(), SysUserWalletLogTypeEnum.REDUCE_WITHDRAW, true);

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
                    lambdaQuery().eq(BaseEntity::getId, notEmptyIdSet.getIdSet())
                        .eq(SysUserWalletWithdrawLogDO::getWithdrawStatus, SysUserWalletWithdrawStatusEnum.COMMIT)
                        .list();

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
     * 成功-用户的提现记录
     */
    @Override
    public String success(NotNullId notNullId) {

        Set<Long> idSet = CollUtil.newHashSet(notNullId.getId());

        // 检查：是否非法操作
        SysTenantUtil.checkIllegal(idSet, getCheckIllegalFunc1(idSet));

        return RedissonUtil.doLock(BaseRedisKeyEnum.PRE_USER_WALLET_WITHDRAW_LOG.name() + notNullId.getId(), () -> {

            SysUserWalletWithdrawLogDO sysUserWalletWithdrawLogDO =
                lambdaQuery().eq(BaseEntity::getId, notNullId.getId())
                    .eq(SysUserWalletWithdrawLogDO::getWithdrawStatus, SysUserWalletWithdrawStatusEnum.ACCEPT).one();

            if (sysUserWalletWithdrawLogDO == null) {
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

        Set<Long> idSet = CollUtil.newHashSet(notNullIdAndStringValue.getId());

        // 检查：是否非法操作
        SysTenantUtil.checkIllegal(idSet, getCheckIllegalFunc1(idSet));

        return RedissonUtil
            .doLock(BaseRedisKeyEnum.PRE_USER_WALLET_WITHDRAW_LOG.name() + notNullIdAndStringValue.getId(), () -> {

                SysUserWalletWithdrawLogDO sysUserWalletWithdrawLogDO =
                    lambdaQuery().eq(BaseEntity::getId, notNullIdAndStringValue.getId())
                        .eq(SysUserWalletWithdrawLogDO::getWithdrawStatus, SysUserWalletWithdrawStatusEnum.ACCEPT)
                        .one();

                if (sysUserWalletWithdrawLogDO == null) {
                    ApiResultVO.error("操作失败：只能拒绝受理中状态的提现记录", notNullIdAndStringValue.getId());
                }

                sysUserWalletWithdrawLogDO.setWithdrawStatus(SysUserWalletWithdrawStatusEnum.REJECT);
                sysUserWalletWithdrawLogDO.setRejectReason(notNullIdAndStringValue.getValue());

                updateById(sysUserWalletWithdrawLogDO); // 先更新提现记录状态，原因：如果后面报错了，则会回滚该更新

                // 检查和增加：用户钱包的可提现余额
                sysUserWalletService.doAddWithdrawableMoney(currentUserId, new Date(),
                    CollUtil.newHashSet(sysUserWalletWithdrawLogDO.getUserId()),
                    sysUserWalletWithdrawLogDO.getWithdrawMoney(), SysUserWalletLogTypeEnum.REDUCE_WITHDRAW, true);

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

    /**
     * 获取：检查：是否非法操作的 getTenantIdBaseEntityFunc1
     */
    @NotNull
    private Func1<Long, BaseEntityNoIdFather> getTenantIdBaseEntityFunc1() {

        return id -> lambdaQuery().eq(SysUserWalletWithdrawLogDO::getId, id)
            .select(SysUserWalletWithdrawLogDO::getTenantId).one();

    }

}
