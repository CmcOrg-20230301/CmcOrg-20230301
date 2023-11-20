package com.cmcorg20230301.be.engine.wallet.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.util.BooleanUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.model.model.dto.ChangeBigDecimalNumberDTO;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullLong;
import com.cmcorg20230301.be.engine.pay.base.model.dto.PayDTO;
import com.cmcorg20230301.be.engine.pay.base.model.entity.SysPayDO;
import com.cmcorg20230301.be.engine.pay.base.model.enums.SysPayRefTypeEnum;
import com.cmcorg20230301.be.engine.pay.base.model.vo.BuyVO;
import com.cmcorg20230301.be.engine.pay.base.util.PayUtil;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import com.cmcorg20230301.be.engine.redisson.util.IdGeneratorUtil;
import com.cmcorg20230301.be.engine.redisson.util.RedissonUtil;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoIdFather;
import com.cmcorg20230301.be.engine.security.model.entity.SysTenantDO;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.util.MyEntityUtil;
import com.cmcorg20230301.be.engine.security.util.SysTenantUtil;
import com.cmcorg20230301.be.engine.security.util.UserUtil;
import com.cmcorg20230301.be.engine.wallet.configuration.SysUserWalletUserSignConfiguration;
import com.cmcorg20230301.be.engine.wallet.mapper.SysUserWalletMapper;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserWalletPageDTO;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserWalletRechargeTenantDTO;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserWalletRechargeUserSelfDTO;
import com.cmcorg20230301.be.engine.wallet.model.entity.SysUserWalletDO;
import com.cmcorg20230301.be.engine.wallet.model.entity.SysUserWalletLogDO;
import com.cmcorg20230301.be.engine.wallet.model.enums.SysUserWalletLogRefTypeEnum;
import com.cmcorg20230301.be.engine.wallet.model.enums.SysUserWalletLogTypeEnum;
import com.cmcorg20230301.be.engine.wallet.model.interfaces.ISysUserWalletLogRefType;
import com.cmcorg20230301.be.engine.wallet.model.interfaces.ISysUserWalletLogType;
import com.cmcorg20230301.be.engine.wallet.service.SysUserWalletService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@Service
public class SysUserWalletServiceImpl extends ServiceImpl<SysUserWalletMapper, SysUserWalletDO>
    implements SysUserWalletService {

    @Resource
    SysUserWalletUserSignConfiguration sysUserWalletUserSignConfiguration;

    /**
     * 批量冻结
     */
    @Override
    public String frozenByIdSet(NotEmptyIdSet notEmptyIdSet) {

        notEmptyIdSet.getIdSet().remove(BaseConstant.TENANT_USER_ID);

        // 改变：钱包冻结状态
        return changeEnableFlag(notEmptyIdSet, false, false);

    }

    /**
     * 改变：钱包冻结状态
     */
    @Override
    public String changeEnableFlag(NotEmptyIdSet notEmptyIdSet, boolean enableFlag, boolean tenantFlag) {

        Set<Long> idSet = notEmptyIdSet.getIdSet();

        if (CollUtil.isEmpty(idSet)) {
            return BaseBizCodeEnum.OK;
        }

        if (tenantFlag) {

            // 检查：是否是属于自己的租户
            SysTenantUtil.handleDtoTenantIdSet(false, idSet);

            // 并且不能操作自身租户
            if (!SysTenantUtil.adminOrDefaultTenantFlag()) {

                // 检查：不能是自身租户，并且必须是自己租户
                SysTenantUtil.checkOnlyChildrenTenantIdSet(idSet);

            }

        } else {

            // 检查：是否非法操作
            SysTenantUtil.checkIllegal(idSet, getCheckIllegalFunc1(idSet));

        }

        return RedissonUtil.doMultiLock(BaseRedisKeyEnum.PRE_USER_WALLET.name(), idSet, () -> {

            lambdaUpdate().in(!tenantFlag, SysUserWalletDO::getId, notEmptyIdSet.getIdSet())
                .eq(tenantFlag, SysUserWalletDO::getId, BaseConstant.TENANT_USER_ID)
                .in(tenantFlag, SysUserWalletDO::getTenantId, idSet).set(BaseEntityNoId::getEnableFlag, enableFlag)
                .update();

            return BaseBizCodeEnum.OK;

        });

    }

    /**
     * 批量解冻
     */
    @Override
    public String thawByIdSet(NotEmptyIdSet notEmptyIdSet) {

        notEmptyIdSet.getIdSet().remove(BaseConstant.TENANT_USER_ID);

        // 改变：钱包冻结状态
        return changeEnableFlag(notEmptyIdSet, true, false);

    }

    /**
     * 分页排序查询
     */
    @Override
    public Page<SysUserWalletDO> myPage(SysUserWalletPageDTO dto) {

        // 执行
        return doMyPage(dto, false);

    }

    /**
     * 执行：分页排序查询
     */
    @Override
    public Page<SysUserWalletDO> doMyPage(SysUserWalletPageDTO dto, boolean tenantFlag) {

        // 处理：MyTenantPageDTO
        SysTenantUtil.handleMyTenantPageDTO(dto, !tenantFlag);

        if (tenantFlag) {

            dto.setId(BaseConstant.TENANT_USER_ID);

        } else {

            if (BaseConstant.TENANT_USER_ID.equals(dto.getId())) {

                dto.setId(null);

            }

        }

        return lambdaQuery().eq(dto.getId() != null, SysUserWalletDO::getId, dto.getId())
            .eq(dto.getEnableFlag() != null, BaseEntityNoId::getEnableFlag, dto.getEnableFlag())
            .ne(!tenantFlag, SysUserWalletDO::getId, BaseConstant.TENANT_USER_ID) //

            .le(dto.getEndWithdrawableMoney() != null, SysUserWalletDO::getWithdrawableMoney,
                dto.getEndWithdrawableMoney()) //

            .ge(dto.getBeginWithdrawableMoney() != null, SysUserWalletDO::getWithdrawableMoney,
                dto.getBeginWithdrawableMoney()) //

            .le(dto.getUtEndTime() != null, SysUserWalletDO::getUpdateTime, dto.getUtEndTime()) //

            .ge(dto.getUtBeginTime() != null, SysUserWalletDO::getUpdateTime, dto.getUtBeginTime()) //

            .in(BaseEntityNoId::getTenantId, dto.getTenantIdSet()) //
            .groupBy(!tenantFlag, SysUserWalletDO::getId) // 备注：因为 totalMoney是聚合函数算出来的，所以这里需要分组
            .groupBy(tenantFlag, SysUserWalletDO::getTenantId) // 备注：因为 totalMoney是聚合函数算出来的，所以这里需要分组
            .orderByDesc(SysUserWalletDO::getUpdateTime).page(dto.page(true));

    }

    /**
     * 通过主键id，查看详情
     */
    @Override
    public SysUserWalletDO infoById(NotNullLong notNullLong) {

        if (notNullLong.getValue().equals(BaseConstant.TENANT_USER_ID)) {
            return null;
        }

        // 获取：用户关联的租户
        Set<Long> queryTenantIdSet = SysTenantUtil.getUserRefTenantIdSet();

        return lambdaQuery().eq(SysUserWalletDO::getId, notNullLong.getValue())
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
            false, false, false, null, null, true);

    }

    /**
     * 执行：通过主键 idSet，加减可提现的钱
     *
     * @param idSet                 用户主键 idSet，或者：租户主键 idSet
     * @param withdrawableMoneyFlag true 操作可提现的钱 false 操作冻结的钱
     */
    @Override
    @NotNull
    @DSTransactional
    public String doAddWithdrawableMoney(Long currentUserId, Date date, Set<Long> idSet, BigDecimal addNumber,
        ISysUserWalletLogType iSysUserWalletLogType, boolean lowErrorFlag, boolean checkWalletEnableFlag,
        boolean tenantFlag, @Nullable ISysUserWalletLogRefType refType, @Nullable Long refId,
        boolean withdrawableMoneyFlag) {

        if (addNumber.equals(BigDecimal.ZERO)) {
            return BaseBizCodeEnum.OK;
        }

        // 日志集合
        List<SysUserWalletLogDO> sysUserWalletLogDoList = new ArrayList<>();

        RedissonUtil.doMultiLock(BaseRedisKeyEnum.PRE_USER_WALLET.name(), idSet, () -> {

            List<SysUserWalletDO> sysUserWalletDOList = lambdaQuery().in(!tenantFlag, SysUserWalletDO::getId, idSet)
                .eq(tenantFlag, SysUserWalletDO::getId, BaseConstant.TENANT_USER_ID)
                .in(tenantFlag, BaseEntityNoIdFather::getTenantId, idSet)
                .select(SysUserWalletDO::getId, SysUserWalletDO::getWithdrawableMoney, SysUserWalletDO::getFrozenMoney,
                    BaseEntityNoId::getVersion, BaseEntityNoIdFather::getTenantId, SysUserWalletDO::getTotalMoney,
                    BaseEntityNoId::getEnableFlag)
                .groupBy(!tenantFlag, SysUserWalletDO::getId) // 备注：因为 totalMoney是聚合函数算出来的，所以这里需要分组
                .groupBy(tenantFlag, SysUserWalletDO::getTenantId) // 备注：因为 totalMoney是聚合函数算出来的，所以这里需要分组
                .list();

            // 处理：sysUserWalletDOList
            handleSysUserWalletDOList(currentUserId, date, addNumber, iSysUserWalletLogType, lowErrorFlag,
                checkWalletEnableFlag, sysUserWalletLogDoList, sysUserWalletDOList, refType, refId,
                withdrawableMoneyFlag);

            if (tenantFlag) {

                // 操作数据库
                String sqlStatement = getSqlStatement(SqlMethod.UPDATE);

                executeBatch(sysUserWalletDOList, DEFAULT_BATCH_SIZE, (sqlSession, entity) -> {

                    Map<String, Object> map = CollectionUtils.newHashMapWithExpectedSize(2);

                    map.put(Constants.ENTITY, entity);

                    map.put(Constants.WRAPPER,
                        ChainWrappers.lambdaUpdateChain(baseMapper).eq(SysUserWalletDO::getId, entity.getId())
                            .eq(BaseEntityNoIdFather::getTenantId, entity.getTenantId()).getWrapper());

                    sqlSession.update(sqlStatement, map);

                });

            } else {

                // 操作数据库
                updateBatchById(sysUserWalletDOList);

            }

        });

        for (SysUserWalletLogDO item : sysUserWalletLogDoList) {

            SysUserWalletLogServiceImpl.add(item); // 保存日志

        }

        return BaseBizCodeEnum.OK;

    }

    /**
     * 充值-用户自我
     */
    @Override
    @DSTransactional
    public BuyVO rechargeUserSelf(SysUserWalletRechargeUserSelfDTO dto) {

        if (dto.getValue().compareTo(BigDecimal.ZERO) <= 0) {
            ApiResultVO.errorMsg("操作失败：充值金额必须大于 0");
        }

        Long currentUserId = UserUtil.getCurrentUserId();
        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        // 获取：支付对象
        PayDTO payDTO = getPayDTO(dto, currentUserId, currentTenantIdDefault, "钱包充值", false);

        // 调用支付
        SysPayDO sysPayDO = PayUtil.pay(payDTO, tempSysPayDO -> {

            tempSysPayDO.setRefType(SysPayRefTypeEnum.WALLET_RECHARGE_USER.getCode());
            tempSysPayDO.setRefId(currentUserId);

        });

        // 返回：调用支付之后，返回的参数
        return new BuyVO(sysPayDO.getPayType(), sysPayDO.getPayReturnValue(), sysPayDO.getId().toString(),
            sysPayDO.getSysPayConfigurationId());

    }

    @NotNull
    private PayDTO getPayDTO(SysUserWalletRechargeUserSelfDTO dto, Long currentUserId, Long currentTenantIdDefault,
        String subject, boolean tenantFlag) {

        PayDTO payDTO = new PayDTO();

        payDTO.setUseParentTenantPayFlag(true);

        payDTO.setPayType(dto.getSysPayType());
        payDTO.setTenantId(currentTenantIdDefault);
        payDTO.setUserId(currentUserId);

        payDTO.setTotalAmount(dto.getValue());
        payDTO.setSubject(subject);
        payDTO.setExpireTime(DateUtil.offsetMinute(new Date(), 30));

        payDTO.setCheckSysPayConfigurationDoConsumer(sysPayConfigurationDO -> {

            Long tenantId = currentTenantIdDefault;

            if (tenantFlag) { // 如果是：租户进行充值

                if (BaseConstant.TOP_TENANT_ID.equals(tenantId)) {
                    return;
                }

                SysTenantDO sysTenantDO = SysTenantUtil.getSysTenantCacheMap(false).get(tenantId);

                if (sysTenantDO == null) {
                    ApiResultVO.errorMsg("操作失败：租户不存在");
                }

                if (sysTenantDO.getEnableFlag().equals(false)) {
                    ApiResultVO.errorMsg("操作失败：租户已被禁用");
                }

                tenantId = sysTenantDO.getParentId(); // 设置：租户 id为：上级租户 id

            }

            // 如果：商品归属租户，配置了支付，则不进行任何操作
            if (sysPayConfigurationDO.getTenantId().equals(tenantId)) {
                return;
            }

            // 检查：租户钱包的可提现余额，增加租户的：冻结的钱
            doAddWithdrawableMoney(currentUserId, new Date(), CollUtil.newHashSet(tenantId), dto.getValue().negate(),
                SysUserWalletLogTypeEnum.ADD_PAY, true, true, true, null, null, false);

        });

        return payDTO;

    }

    /**
     * 充值-租户
     */
    @Override
    public BuyVO rechargeTenant(SysUserWalletRechargeTenantDTO dto) {

        if (dto.getValue().compareTo(BigDecimal.ZERO) <= 0) {
            ApiResultVO.errorMsg("操作失败：充值金额必须大于 0");
        }

        Long currentUserId = UserUtil.getCurrentUserId();
        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        // 获取：支付对象
        PayDTO payDTO = getPayDTO(dto, currentUserId, currentTenantIdDefault, "钱包充值", false);

        // 调用支付
        SysPayDO sysPayDO = PayUtil.pay(payDTO, tempSysPayDO -> {

            tempSysPayDO.setRefType(SysPayRefTypeEnum.WALLET_RECHARGE_TENANT.getCode());
            tempSysPayDO.setRefId(currentTenantIdDefault);

        });

        // 返回：调用支付之后，返回的参数
        return new BuyVO(sysPayDO.getPayType(), sysPayDO.getPayReturnValue(), sysPayDO.getId().toString(),
            sysPayDO.getSysPayConfigurationId());

    }

    /**
     * 处理：sysUserWalletDOList
     *
     * @param withdrawableMoneyFlag true 操作可提现的钱 false 操作冻结的钱
     */
    private void handleSysUserWalletDOList(Long currentUserId, Date date, BigDecimal changeNumber,
        ISysUserWalletLogType iSysUserWalletLogType, boolean lowErrorFlag, boolean checkWalletEnableFlag,
        List<SysUserWalletLogDO> sysUserWalletLogDoList, List<SysUserWalletDO> sysUserWalletDOList,
        @Nullable ISysUserWalletLogRefType refType, @Nullable Long refId, boolean withdrawableMoneyFlag) {

        for (SysUserWalletDO item : sysUserWalletDOList) {

            if (checkWalletEnableFlag) {
                if (BooleanUtil.isFalse(item.getEnableFlag())) {
                    ApiResultVO.error("操作失败：钱包已被冻结，请联系管理员", item.getId());
                }
            }

            BigDecimal preTotalMoney = item.getTotalMoney();
            BigDecimal preWithdrawableMoney = item.getWithdrawableMoney();
            BigDecimal preFrozenMoney = item.getFrozenMoney();

            if (withdrawableMoneyFlag) {

                item.setTotalMoney(item.getTotalMoney().add(changeNumber)); // 修改：数字
                item.setWithdrawableMoney(item.getWithdrawableMoney().add(changeNumber)); // 修改：数字

            } else {

                item.setWithdrawableMoney(item.getWithdrawableMoney().add(changeNumber.negate())); // 修改：数字
                item.setFrozenMoney(item.getWithdrawableMoney().add(changeNumber)); // 修改：数字

            }

            if (item.getWithdrawableMoney().compareTo(BigDecimal.ZERO) < 0) {
                if (lowErrorFlag) {
                    ApiResultVO.error("操作失败：可提现余额不足", item.getId());
                } else {
                    item.setWithdrawableMoney(BigDecimal.ZERO);
                }
            }

            SysUserWalletLogDO sysUserWalletLogDO = new SysUserWalletLogDO();

            sysUserWalletLogDO.setUserId(item.getId());
            sysUserWalletLogDO.setName(iSysUserWalletLogType.getName());
            sysUserWalletLogDO.setType(iSysUserWalletLogType.getCode());

            ISysUserWalletLogRefType iSysUserWalletLogRefType =
                MyEntityUtil.getNotNullObject(refType, SysUserWalletLogRefTypeEnum.NONE);

            sysUserWalletLogDO.setRefType(iSysUserWalletLogRefType.getCode());

            sysUserWalletLogDO.setRefId(MyEntityUtil.getNotNullLong(refId));

            sysUserWalletLogDO.setTotalMoneyPre(preTotalMoney);
            sysUserWalletLogDO.setTotalMoneySuf(item.getTotalMoney());

            sysUserWalletLogDO.setWithdrawableMoneyPre(preWithdrawableMoney);
            sysUserWalletLogDO.setWithdrawableMoneySuf(item.getWithdrawableMoney());

            sysUserWalletLogDO.setFrozenMoneyPre(preFrozenMoney);
            sysUserWalletLogDO.setFrozenMoneySuf(item.getFrozenMoney());

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

    }

    /**
     * 通用：处理：SysUserWalletLogDO
     */
    private void commonHandleSysUserWalletLogDO(SysUserWalletLogDO sysUserWalletLogDO) {

        sysUserWalletLogDO
            .setTotalMoneyChange(sysUserWalletLogDO.getTotalMoneySuf().subtract(sysUserWalletLogDO.getTotalMoneyPre()));

        sysUserWalletLogDO.setWithdrawableMoneyChange(
            sysUserWalletLogDO.getWithdrawableMoneySuf().subtract(sysUserWalletLogDO.getWithdrawableMoneyPre()));

        sysUserWalletLogDO.setFrozenMoneyChange(
            sysUserWalletLogDO.getFrozenMoneySuf().subtract(sysUserWalletLogDO.getFrozenMoneyPre()));

    }

    /**
     * 获取：检查：是否非法操作的 getCheckIllegalFunc1
     */
    @NotNull
    private Func1<Set<Long>, Long> getCheckIllegalFunc1(Set<Long> idSet) {

        return tenantIdSet -> lambdaQuery().in(SysUserWalletDO::getId, idSet)
            .in(BaseEntityNoId::getTenantId, tenantIdSet).count();

    }

}
