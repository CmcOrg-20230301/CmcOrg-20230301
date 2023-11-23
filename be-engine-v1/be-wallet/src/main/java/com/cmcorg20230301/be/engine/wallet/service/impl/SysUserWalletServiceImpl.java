package com.cmcorg20230301.be.engine.wallet.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
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
import com.cmcorg20230301.be.engine.pay.base.model.enums.SysPayRefStatusEnum;
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
import com.cmcorg20230301.be.engine.security.util.MyExceptionUtil;
import com.cmcorg20230301.be.engine.security.util.SysTenantUtil;
import com.cmcorg20230301.be.engine.security.util.UserUtil;
import com.cmcorg20230301.be.engine.util.util.CallBack;
import com.cmcorg20230301.be.engine.wallet.configuration.SysUserWalletUserSignConfiguration;
import com.cmcorg20230301.be.engine.wallet.mapper.SysUserWalletMapper;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserWalletPageDTO;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserWalletRechargeTenantDTO;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserWalletRechargeUserSelfDTO;
import com.cmcorg20230301.be.engine.wallet.model.entity.SysUserWalletDO;
import com.cmcorg20230301.be.engine.wallet.model.entity.SysUserWalletLogDO;
import com.cmcorg20230301.be.engine.wallet.model.enums.SysUserWalletLogTypeEnum;
import com.cmcorg20230301.be.engine.wallet.model.interfaces.ISysUserWalletLogType;
import com.cmcorg20230301.be.engine.wallet.service.SysUserWalletService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@Service
public class SysUserWalletServiceImpl extends ServiceImpl<SysUserWalletMapper, SysUserWalletDO>
    implements SysUserWalletService {

    @Resource
    SysUserWalletUserSignConfiguration sysUserWalletUserSignConfiguration;

    /**
     * 定时任务，可提现余额，预使用，检查
     */
    @PreDestroy
    @Scheduled(fixedDelay = BaseConstant.MINUTE_5_EXPIRE_TIME)
    public void scheduledCheckWithdrawablePreUseMoney() {

        Date date = new Date();

        DateTime checkDateTime = DateUtil.offsetMinute(date, -30);

        List<SysUserWalletDO> sysUserWalletDOList = lambdaQuery().gt(SysUserWalletDO::getWithdrawablePreUseMoney, 0)
            .le(BaseEntityNoIdFather::getUpdateTime, checkDateTime)
            .select(SysUserWalletDO::getId, SysUserWalletDO::getTenantId).list();

        if (CollUtil.isEmpty(sysUserWalletDOList)) {
            return;
        }

        for (SysUserWalletDO item : sysUserWalletDOList) {

            try {

                Long id = item.getId();

                boolean tenantFlag = id.equals(BaseConstant.TENANT_USER_ID); // 是否是：租户

                if (tenantFlag) {

                    id = item.getTenantId();

                }

                Long finalId = id;

                RedissonUtil.doLock(BaseRedisKeyEnum.PRE_USER_WALLET.name() + id, () -> {

                    // 再次查询，目的：防止出现并发问题
                    SysUserWalletDO sysUserWalletDO = lambdaQuery().eq(!tenantFlag, SysUserWalletDO::getId, finalId)
                        .eq(tenantFlag, SysUserWalletDO::getId, BaseConstant.TENANT_USER_ID)
                        .eq(SysUserWalletDO::getTenantId, item.getTenantId()).one();

                    // 如果：预使用可提现的钱，已经小于等于 0了，则不进行处理
                    if (sysUserWalletDO.getWithdrawablePreUseMoney().compareTo(BigDecimal.ZERO) <= 0) {
                        return;
                    }

                    // 如果：已经被更新了，则不进行处理
                    if (sysUserWalletDO.getUpdateTime().compareTo(checkDateTime) > 0) {
                        return;
                    }

                    BigDecimal preWithdrawableMoney = sysUserWalletDO.getWithdrawableMoney();
                    BigDecimal preWithdrawablePreUseMoney = sysUserWalletDO.getWithdrawablePreUseMoney();

                    sysUserWalletDO
                        .setWithdrawableMoney(sysUserWalletDO.getWithdrawableMoney().add(preWithdrawablePreUseMoney));

                    sysUserWalletDO.setWithdrawablePreUseMoney(BigDecimal.ZERO);

                    sysUserWalletDO.setUpdateId(null);
                    sysUserWalletDO.setUpdateTime(null);

                    updateById(sysUserWalletDO); // 操作数据库

                    // 新增日志
                    SysUserWalletLogServiceImpl.add(
                        addSysUserWalletLogDO(BaseConstant.SYS_ID, date, SysUserWalletLogTypeEnum.ADD_TIME_CHECK, null,
                            null, sysUserWalletDO, preWithdrawableMoney, preWithdrawablePreUseMoney));

                });

            } catch (Exception e) {

                MyExceptionUtil.printError(e);

            }

        }

    }

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

        SysUserWalletDO sysUserWalletDO = lambdaQuery().eq(SysUserWalletDO::getId, currentUserId)
            .eq(BaseEntityNoIdFather::getTenantId, currentTenantIdDefault).one();

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

        BigDecimal addNumber = dto.getNumber();

        // 检查：是否非法操作
        SysTenantUtil.checkIllegal(dto.getIdSet(), getCheckIllegalFunc1(dto.getIdSet()));

        SysUserWalletLogTypeEnum sysUserWalletLogTypeEnum =
            addNumber.compareTo(BigDecimal.ZERO) > 0 ? SysUserWalletLogTypeEnum.ADD_BACKGROUND :
                SysUserWalletLogTypeEnum.REDUCE_BACKGROUND;

        // 执行
        return doAddWithdrawableMoney(currentUserId, new Date(), dto.getIdSet(), addNumber, sysUserWalletLogTypeEnum,
            false, false, false, null, null, true, null, null);

    }

    /**
     * 执行：通过主键 idSet，加减可提现的钱
     *
     * @param idSet                 用户主键 idSet，或者：租户主键 idSet
     * @param withdrawableMoneyFlag true 操作可提现的钱 false 操作预使用可提现的钱
     * @param reduceFrozenMoneyType 如果 withdrawableMoneyFlag == false 时，并且是减少时：1 （默认）扣除预使用可提现的钱，并减少可提现的钱 2 扣除预使用可提现的钱
     * @param tenantId              当 tenantFlag == false时，才生效，用于：例如 admin账号，在各个租户下的 id相同，但是 tenantId不同，所导致的问题
     */
    @Override
    @NotNull
    @DSTransactional
    public String doAddWithdrawableMoney(Long currentUserId, Date date, Set<Long> idSet, BigDecimal addNumber,
        ISysUserWalletLogType iSysUserWalletLogType, boolean lowErrorFlag, boolean checkWalletEnableFlag,
        boolean tenantFlag, @Nullable Long refId, @Nullable String refData, boolean withdrawableMoneyFlag,
        @Nullable Integer reduceFrozenMoneyType, @Nullable Long tenantId) {

        if (addNumber.equals(BigDecimal.ZERO)) {
            return BaseBizCodeEnum.OK;
        }

        // 日志集合
        List<SysUserWalletLogDO> sysUserWalletLogDoList = new ArrayList<>();

        RedissonUtil.doMultiLock(BaseRedisKeyEnum.PRE_USER_WALLET.name(), idSet, () -> {

            List<SysUserWalletDO> sysUserWalletDOList = lambdaQuery().in(!tenantFlag, SysUserWalletDO::getId, idSet)
                .eq(!tenantFlag && tenantId != null, BaseEntityNoIdFather::getTenantId, tenantId)
                .eq(tenantFlag, SysUserWalletDO::getId, BaseConstant.TENANT_USER_ID)
                .in(tenantFlag, BaseEntityNoIdFather::getTenantId, idSet)
                .select(SysUserWalletDO::getId, SysUserWalletDO::getWithdrawableMoney,
                    SysUserWalletDO::getWithdrawablePreUseMoney, BaseEntityNoId::getVersion,
                    BaseEntityNoIdFather::getTenantId, BaseEntityNoId::getEnableFlag).list();

            // 处理：sysUserWalletDOList
            handleSysUserWalletDOList(currentUserId, date, addNumber, iSysUserWalletLogType, lowErrorFlag,
                checkWalletEnableFlag, sysUserWalletLogDoList, sysUserWalletDOList, refId, refData,
                withdrawableMoneyFlag, reduceFrozenMoneyType);

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

        // 扣除可提现余额的，上级租户的主键 id，如果扣除了，则不为 -1，如果没有扣除，则为 -1，目的：支付成功之后，扣除总的钱
        CallBack<Long> deductTenantIdCallBack = new CallBack<>(BaseConstant.NEGATIVE_ONE);

        // 获取：支付对象
        PayDTO payDTO = getPayDTO(dto, currentUserId, currentTenantIdDefault, false, deductTenantIdCallBack);

        // 调用支付
        SysPayDO sysPayDO = PayUtil.pay(payDTO, tempSysPayDO -> {

            tempSysPayDO.setRefType(SysPayRefTypeEnum.WALLET_RECHARGE_USER.getCode());
            tempSysPayDO.setRefId(currentUserId);

            tempSysPayDO.setRefData(deductTenantIdCallBack.getValue().toString());

            tempSysPayDO.setRefStatus(SysPayRefStatusEnum.WAIT_PAY.getCode());

        });

        // 返回：调用支付之后，返回的参数
        return new BuyVO(sysPayDO.getPayType(), sysPayDO.getPayReturnValue(), sysPayDO.getId().toString(),
            sysPayDO.getSysPayConfigurationId());

    }

    /**
     * 获取：PayDTO对象
     */
    @NotNull
    private PayDTO getPayDTO(SysUserWalletRechargeUserSelfDTO dto, Long userId, Long tenantId, boolean tenantFlag,
        CallBack<Long> deductTenantIdCallBack) {

        PayDTO payDTO = new PayDTO();

        payDTO.setUseParentTenantPayFlag(true);

        payDTO.setPayType(dto.getSysPayType());
        payDTO.setTenantId(tenantId);
        payDTO.setUserId(userId);

        payDTO.setTotalAmount(dto.getValue());
        payDTO.setSubject("钱包充值");
        payDTO.setExpireTime(DateUtil.offsetMinute(new Date(), 30));

        payDTO.setCheckSysPayConfigurationDoConsumer(sysPayConfigurationDO -> {

            Long tenantIdTemp = tenantId;

            if (tenantFlag) { // 如果是：租户进行充值

                if (BaseConstant.TOP_TENANT_ID.equals(tenantIdTemp)) {
                    return;
                }

                SysTenantDO sysTenantDO = SysTenantUtil.getSysTenantCacheMap(false).get(tenantIdTemp);

                if (sysTenantDO == null) {
                    ApiResultVO.errorMsg("操作失败：租户不存在");
                }

                if (sysTenantDO.getEnableFlag().equals(false)) {
                    ApiResultVO.errorMsg("操作失败：租户已被禁用");
                }

                tenantIdTemp = sysTenantDO.getParentId(); // 设置：租户 id为：上级租户 id

            }

            // 如果：商品归属租户，配置了支付，则不进行任何操作
            if (sysPayConfigurationDO.getTenantId().equals(tenantIdTemp)) {
                return;
            }

            // 检查：租户钱包的可用可提现余额，然后增加租户的：预使用可提现的钱
            doAddWithdrawableMoney(userId, new Date(), CollUtil.newHashSet(tenantIdTemp), dto.getValue(),
                tenantFlag ? SysUserWalletLogTypeEnum.REDUCE_TENANT_BUY : SysUserWalletLogTypeEnum.REDUCE_USER_BUY,
                true, true, true, null, null, false, null, null);

            deductTenantIdCallBack.setValue(tenantIdTemp); // 设置：扣除可提现余额的租户 id

        });

        return payDTO;

    }

    /**
     * 充值-租户
     */
    @Override
    public BuyVO rechargeTenant(SysUserWalletRechargeTenantDTO dto) {

        Long tenantId = dto.getTenantId();

        SysTenantUtil.checkTenantId(tenantId);

        if (dto.getValue().compareTo(BigDecimal.ZERO) <= 0) {
            ApiResultVO.errorMsg("操作失败：充值金额必须大于 0");
        }

        Long currentUserId = UserUtil.getCurrentUserId();

        // 扣除可提现余额的，上级租户的主键 id，如果扣除了，则不为 -1，如果没有扣除，则为 -1，目的：支付成功之后，扣除总的钱
        CallBack<Long> deductTenantIdCallBack = new CallBack<>(BaseConstant.NEGATIVE_ONE);

        // 获取：支付对象
        PayDTO payDTO = getPayDTO(dto, currentUserId, tenantId, true, deductTenantIdCallBack);

        // 调用支付
        SysPayDO sysPayDO = PayUtil.pay(payDTO, tempSysPayDO -> {

            tempSysPayDO.setRefType(SysPayRefTypeEnum.WALLET_RECHARGE_TENANT.getCode());
            tempSysPayDO.setRefId(tenantId);

            tempSysPayDO.setRefData(deductTenantIdCallBack.getValue().toString());

            tempSysPayDO.setRefStatus(SysPayRefStatusEnum.WAIT_PAY.getCode());

        });

        // 返回：调用支付之后，返回的参数
        return new BuyVO(sysPayDO.getPayType(), sysPayDO.getPayReturnValue(), sysPayDO.getId().toString(),
            sysPayDO.getSysPayConfigurationId());

    }

    /**
     * 处理：sysUserWalletDOList
     *
     * @param withdrawableMoneyFlag true 操作可提现的钱 false 操作预使用可提现的钱
     * @param reduceFrozenMoneyType 如果 withdrawableMoneyFlag == false 时，并且是减少时：1 （默认）扣除预使用可提现的钱，并减少可提现的钱 2 扣除预使用可提现的钱
     */
    private void handleSysUserWalletDOList(Long currentUserId, Date date, BigDecimal addNumber,
        ISysUserWalletLogType iSysUserWalletLogType, boolean lowErrorFlag, boolean checkWalletEnableFlag,
        List<SysUserWalletLogDO> sysUserWalletLogDoList, List<SysUserWalletDO> sysUserWalletDOList,
        @Nullable Long refId, @Nullable String refData, boolean withdrawableMoneyFlag,
        @Nullable Integer reduceFrozenMoneyType) {

        for (SysUserWalletDO item : sysUserWalletDOList) {

            if (checkWalletEnableFlag) {
                if (BooleanUtil.isFalse(item.getEnableFlag())) {
                    ApiResultVO.error("操作失败：钱包已被冻结，请联系管理员", item.getId());
                }
            }

            BigDecimal preWithdrawableMoney = item.getWithdrawableMoney();
            BigDecimal preWithdrawablePreUseMoney = item.getWithdrawablePreUseMoney();

            // 处理：需要增加的钱
            handleAddNumber(addNumber, withdrawableMoneyFlag, reduceFrozenMoneyType, item);

            item.setUpdateId(null);
            item.setUpdateTime(null);

            if (item.getWithdrawableRealMoney().compareTo(BigDecimal.ZERO) < 0) {

                if (lowErrorFlag) {

                    ApiResultVO.error("操作失败：可提现余额不足", StrUtil
                        .format("id：{}，tenantId：{}，withdrawableRealMoney：{}", item.getId(), item.getTenantId(),
                            item.getWithdrawableRealMoney()));

                } else {

                    item.setWithdrawableMoney(BigDecimal.ZERO);

                }

            }

            // 新增日志
            sysUserWalletLogDoList.add(
                addSysUserWalletLogDO(currentUserId, date, iSysUserWalletLogType, refId, refData, item,
                    preWithdrawableMoney, preWithdrawablePreUseMoney));

        }

    }

    /**
     * 新增日志
     */
    public static SysUserWalletLogDO addSysUserWalletLogDO(Long currentUserId, Date date,
        ISysUserWalletLogType iSysUserWalletLogType, @Nullable Long refId, @Nullable String refData,
        SysUserWalletDO item, BigDecimal preWithdrawableMoney, BigDecimal preWithdrawablePreUseMoney) {

        SysUserWalletLogDO sysUserWalletLogDO = new SysUserWalletLogDO();

        sysUserWalletLogDO.setUserId(item.getId());
        sysUserWalletLogDO.setName(iSysUserWalletLogType.getName());

        sysUserWalletLogDO.setType(iSysUserWalletLogType.getCode());

        sysUserWalletLogDO.setRefId(MyEntityUtil.getNotNullLong(refId));

        sysUserWalletLogDO.setRefData(MyEntityUtil.getNotNullStr(refData));

        sysUserWalletLogDO.setWithdrawableMoneyPre(preWithdrawableMoney);
        sysUserWalletLogDO.setWithdrawableMoneySuf(item.getWithdrawableMoney());

        sysUserWalletLogDO.setWithdrawablePreUseMoneyPre(preWithdrawablePreUseMoney);
        sysUserWalletLogDO.setWithdrawablePreUseMoneySuf(item.getWithdrawablePreUseMoney());

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

        return sysUserWalletLogDO;

    }

    /**
     * 处理：需要增加的钱
     */
    private void handleAddNumber(BigDecimal addNumber, boolean withdrawableMoneyFlag,
        @Nullable Integer reduceFrozenMoneyType, SysUserWalletDO item) {

        if (withdrawableMoneyFlag) {

            item.setWithdrawableMoney(item.getWithdrawableMoney().add(addNumber));

        } else {

            if (addNumber.compareTo(BigDecimal.ZERO) < 0) {

                if (reduceFrozenMoneyType != null && reduceFrozenMoneyType == 2) { // 2 扣除预使用可提现的钱

                    item.setWithdrawablePreUseMoney(item.getWithdrawablePreUseMoney().add(addNumber));

                } else { // 1 （默认）扣除预使用可提现的钱，并减少可提现的钱

                    item.setWithdrawableMoney(item.getWithdrawableMoney().add(addNumber));
                    item.setWithdrawablePreUseMoney(item.getWithdrawablePreUseMoney().add(addNumber));

                }

            } else {

                item.setWithdrawablePreUseMoney(item.getWithdrawablePreUseMoney().add(addNumber));

            }

        }

    }

    /**
     * 通用：处理：SysUserWalletLogDO
     */
    public static void commonHandleSysUserWalletLogDO(SysUserWalletLogDO sysUserWalletLogDO) {

        sysUserWalletLogDO.setWithdrawableMoneyChange(
            sysUserWalletLogDO.getWithdrawableMoneySuf().subtract(sysUserWalletLogDO.getWithdrawableMoneyPre()));

        sysUserWalletLogDO.setWithdrawablePreUseMoneyChange(sysUserWalletLogDO.getWithdrawablePreUseMoneySuf()
            .subtract(sysUserWalletLogDO.getWithdrawablePreUseMoneyPre()));

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
