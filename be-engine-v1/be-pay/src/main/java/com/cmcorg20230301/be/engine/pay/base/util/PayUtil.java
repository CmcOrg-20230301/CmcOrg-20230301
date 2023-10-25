package com.cmcorg20230301.be.engine.pay.base.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.datasource.util.TransactionUtil;
import com.cmcorg20230301.be.engine.kafka.util.KafkaUtil;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.be.engine.pay.base.model.bo.SysPayReturnBO;
import com.cmcorg20230301.be.engine.pay.base.model.bo.SysPayTradeNotifyBO;
import com.cmcorg20230301.be.engine.pay.base.model.configuration.ISysPay;
import com.cmcorg20230301.be.engine.pay.base.model.dto.PayDTO;
import com.cmcorg20230301.be.engine.pay.base.model.entity.SysPayConfigurationDO;
import com.cmcorg20230301.be.engine.pay.base.model.entity.SysPayDO;
import com.cmcorg20230301.be.engine.pay.base.model.enums.SysPayRefTypeEnum;
import com.cmcorg20230301.be.engine.pay.base.model.enums.SysPayTradeStatusEnum;
import com.cmcorg20230301.be.engine.pay.base.model.enums.SysPayTypeEnum;
import com.cmcorg20230301.be.engine.pay.base.service.SysPayConfigurationService;
import com.cmcorg20230301.be.engine.pay.base.service.SysPayService;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import com.cmcorg20230301.be.engine.redisson.util.IdGeneratorUtil;
import com.cmcorg20230301.be.engine.redisson.util.RedissonUtil;
import com.cmcorg20230301.be.engine.security.mapper.SysTenantMapper;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoIdFather;
import com.cmcorg20230301.be.engine.security.model.entity.SysTenantDO;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.util.MyEntityUtil;
import com.cmcorg20230301.be.engine.security.util.MyThreadUtil;
import com.cmcorg20230301.be.engine.security.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@Component
@Slf4j(topic = LogTopicConstant.PAY)
public class PayUtil {

    private static final Map<SysPayTypeEnum, ISysPay> SYS_PAY_MAP = MapUtil.newHashMap();

    private static SysPayService sysPayService;

    private static SysPayConfigurationService sysPayConfigurationService;

    private static SysTenantMapper sysTenantMapper;

    public PayUtil(@Autowired(required = false) @Nullable List<ISysPay> iSysPayList, SysPayService sysPayService,
        SysPayConfigurationService sysPayConfigurationService, SysTenantMapper sysTenantMapper) {

        PayUtil.sysPayService = sysPayService;

        if (CollUtil.isNotEmpty(iSysPayList)) {

            for (ISysPay item : iSysPayList) {

                SYS_PAY_MAP.put(item.getSysPayType(), item);

            }

        }

        PayUtil.sysPayConfigurationService = sysPayConfigurationService;

        PayUtil.sysTenantMapper = sysTenantMapper;

    }

    private static CopyOnWriteArrayList<SysPayDO> SYS_PAY_DO_LIST = new CopyOnWriteArrayList<>();

    /**
     * 定时任务，保存数据
     */
    @PreDestroy
    @Scheduled(fixedDelay = 5000)
    public void scheduledSava() {

        CopyOnWriteArrayList<SysPayDO> tempSysPayDOList;

        synchronized (SYS_PAY_DO_LIST) {

            if (CollUtil.isEmpty(SYS_PAY_DO_LIST)) {
                return;
            }

            tempSysPayDOList = SYS_PAY_DO_LIST;
            SYS_PAY_DO_LIST = new CopyOnWriteArrayList<>();

        }

        // 目的：防止还有程序往：tempList，里面添加数据，所以这里等待一会
        MyThreadUtil.schedule(() -> {

            log.info("保存支付数据，长度：{}", tempSysPayDOList.size());

            // 批量保存数据
            sysPayService.updateBatchById(tempSysPayDOList);

        }, DateUtil.offsetSecond(new Date(), 2));

    }

    /**
     * 支付
     *
     * @param consumer 注意：SysPayDO对象，只建议修改：refType 和 refId这两个属性，其他属性不建议修改
     */
    public static SysPayDO pay(PayDTO dto, @Nullable Consumer<SysPayDO> consumer) {

        if (dto.getSysPayConfigurationDoTemp() != null) {
            dto.setPayType(dto.getSysPayConfigurationDoTemp().getType()); // 保证一致性
        }

        // 检查：dto对象
        checkPayDTO(dto);

        if (SysPayTypeEnum.DEFAULT.equals(dto.getPayType())) { // 如果是：默认支付
            handleDefaultPayType(dto); // 处理：默认支付
        }

        ISysPay iSysPay = SYS_PAY_MAP.get(dto.getPayType());

        if (iSysPay == null) {
            ApiResultVO.errorMsg("操作失败：支付方式未找到：{}", dto.getPayType().getCode());
        }

        if (StrUtil.isBlank(dto.getOpenId())) {
            setOpenId(dto); // 设置：openId
        }

        Long payId = IdGeneratorUtil.nextId();

        dto.setOutTradeNo(payId.toString()); // 设置：支付的订单号

        Long tenantId = dto.getTenantId(); // 租户主键 id，备注：因为 iSysPay.pay方法会修改 dto的 tenantId的值

        // 调用：第三方支付
        SysPayReturnBO sysPayReturnBO = iSysPay.pay(dto);

        // 获取：SysPayDO对象
        SysPayDO sysPayDO = getSysPayDO(dto, iSysPay, payId, sysPayReturnBO, tenantId);

        TransactionUtil.exec(() -> {

            if (consumer != null) {

                consumer.accept(sysPayDO);

            }

            sysPayService.save(sysPayDO);

        });

        return sysPayDO;

    }

    /**
     * 处理：默认支付
     */
    private static void handleDefaultPayType(PayDTO dto) {

        Long tenantIdOriginal = dto.getTenantId();

        SysPayConfigurationDO sysPayConfigurationDO =
            sysPayConfigurationService.lambdaQuery().eq(BaseEntityNoIdFather::getTenantId, dto.getTenantId())
                .eq(SysPayConfigurationDO::getDefaultFlag, true).eq(BaseEntityNoId::getEnableFlag, true).one();

        if (sysPayConfigurationDO == null) {

            if (BooleanUtil.isTrue(dto.getUseParentTenantPayFlag())) {

                // 递归：获取上级租户的支付方式
                sysPayConfigurationDO = handleUseParentTenantPayFlag(dto, tenantIdOriginal, lambdaQueryChainWrapper -> {

                    lambdaQueryChainWrapper.eq(SysPayConfigurationDO::getDefaultFlag, true);

                });

            }

        }

        if (sysPayConfigurationDO == null) {

            ApiResultVO.error("操作失败：未配置默认支付方式，请联系管理员",
                StrUtil.format("tenantIdOriginal：{}，currentTenantId：{}", tenantIdOriginal, dto.getTenantId()));

        }

        dto.setPayType(sysPayConfigurationDO.getType());
        dto.setSysPayConfigurationDoTemp(sysPayConfigurationDO);

    }

    /**
     * 递归：获取上级租户的支付方式
     *
     * @param dto 备注：只使用 dto的 tenantId属性
     */
    @NotNull
    public static SysPayConfigurationDO handleUseParentTenantPayFlag(PayDTO dto, Long tenantIdOriginal,
        @Nullable Consumer<LambdaQueryChainWrapper<SysPayConfigurationDO>> lambdaQueryChainWrapperConsumer) {

        if (BaseConstant.TENANT_ID.equals(dto.getTenantId())) {

            ApiResultVO.error("操作失败：未配置支付，请联系管理员",
                StrUtil.format("tenantIdOriginal：{}，currentTenantId：{}", tenantIdOriginal, dto.getTenantId()));

        }

        SysTenantDO sysTenantDO =
            ChainWrappers.lambdaQueryChain(sysTenantMapper).eq(BaseEntity::getId, dto.getTenantId())
                .select(SysTenantDO::getParentId, BaseEntityNoId::getEnableFlag).one();

        if (sysTenantDO == null) {

            ApiResultVO.error("操作失败：租户不存在",
                StrUtil.format("tenantIdOriginal：{}，currentTenantId：{}", tenantIdOriginal, dto.getTenantId()));

        }

        if (!sysTenantDO.getEnableFlag()) {

            ApiResultVO.error("操作失败：租户已被禁用，无法调用支付",
                StrUtil.format("tenantIdOriginal：{}，currentTenantId：{}", tenantIdOriginal, dto.getTenantId()));

        }

        dto.setTenantId(sysTenantDO.getParentId()); // 设置为：上级租户 id

        LambdaQueryChainWrapper<SysPayConfigurationDO> lambdaQueryChainWrapper =
            sysPayConfigurationService.lambdaQuery().eq(BaseEntityNoIdFather::getTenantId, dto.getTenantId())
                .eq(BaseEntityNoId::getEnableFlag, true);

        if (lambdaQueryChainWrapperConsumer != null) {
            lambdaQueryChainWrapperConsumer.accept(lambdaQueryChainWrapper);
        }

        List<SysPayConfigurationDO> sysPayConfigurationDOList = lambdaQueryChainWrapper.list();

        SysPayConfigurationDO sysPayConfigurationDO = null;

        if (CollUtil.isNotEmpty(sysPayConfigurationDOList)) {

            // 随机取一个
            sysPayConfigurationDO = RandomUtil.randomEle(sysPayConfigurationDOList);

        }

        if (sysPayConfigurationDO == null) {

            // 递归：获取上级租户的支付方式
            return handleUseParentTenantPayFlag(dto, tenantIdOriginal, lambdaQueryChainWrapperConsumer);

        }

        return sysPayConfigurationDO;

    }

    /**
     * 设置：openId
     */
    private static void setOpenId(PayDTO dto) {

        String openId;

        if (dto.getPayType().getCode() >= SysPayTypeEnum.WX_NATIVE.getCode()
            && dto.getPayType().getCode() < SysPayTypeEnum.UNION.getCode()) {

            openId = UserUtil.getCurrentUserWxOpenIdDefault();

        } else {

            openId = "";

        }

        dto.setOpenId(openId);

    }

    /**
     * 检查：dto对象
     */
    private static void checkPayDTO(PayDTO dto) {

        Assert.notNull(dto.getPayType());
        Assert.notNull(dto.getTenantId());
        Assert.notNull(dto.getUserId());

        Assert.notNull(dto.getTotalAmount());
        Assert.notBlank(dto.getSubject());

        int compare = DateUtil.compare(dto.getExpireTime(), new Date());

        if (compare <= 0) {
            ApiResultVO.errorMsg("操作失败：支付过期时间晚于当前时间");
        }

    }

    /**
     * 获取：SysPayDO对象
     */
    @NotNull
    private static SysPayDO getSysPayDO(PayDTO dto, ISysPay iSysPay, Long payId, SysPayReturnBO sysPayReturnBO,
        Long tenantId) {

        SysPayDO sysPayDO = new SysPayDO();

        sysPayDO.setId(payId);

        sysPayDO.setPayType(iSysPay.getSysPayType());

        sysPayDO.setTenantId(tenantId);

        sysPayDO.setUserId(dto.getUserId());

        sysPayDO.setSubject(dto.getSubject());
        sysPayDO.setBody(MyEntityUtil.getNotNullStr(dto.getBody()));
        sysPayDO.setOriginalPrice(dto.getTotalAmount());
        sysPayDO.setPayPrice(BigDecimal.ZERO);
        sysPayDO.setPayCurrency("");

        sysPayDO.setExpireTime(dto.getExpireTime());

        sysPayDO.setSysPayConfigurationId(dto.getSysPayConfigurationDoTemp().getId());
        sysPayDO.setSysPayConfigurationTenantId(dto.getSysPayConfigurationDoTemp().getTenantId());

        sysPayDO.setOpenId(MyEntityUtil.getNotNullAndTrimStr(dto.getOpenId()));

        sysPayDO.setPayAppId(MyEntityUtil.getNotNullStr(sysPayReturnBO.getPayAppId()));

        sysPayDO.setPayReturnValue(MyEntityUtil.getNotNullStr(sysPayReturnBO.getPayReturnValue()));

        sysPayDO.setStatus(SysPayTradeStatusEnum.WAIT_BUYER_PAY);

        sysPayDO.setTradeNo("");

        sysPayDO.setRefType(SysPayRefTypeEnum.NONE);
        sysPayDO.setRefId(-1L);

        sysPayDO.setPackageName(MyEntityUtil.getNotNullAndTrimStr(dto.getPackageName()));
        sysPayDO.setProductId(MyEntityUtil.getNotNullAndTrimStr(dto.getProductId()));
        sysPayDO.setToken(MyEntityUtil.getNotNullAndTrimStr(dto.getToken()));

        sysPayDO.setEnableFlag(true);
        sysPayDO.setDelFlag(false);
        sysPayDO.setRemark(MyEntityUtil.getNotNullStr(dto.getRemark()));

        return sysPayDO;

    }

    /**
     * 查询订单状态
     *
     * @param outTradeNo 商户订单号，商户网站订单系统中唯一订单号，必填
     */
    public static SysPayTradeStatusEnum query(SysPayTypeEnum sysPayTypeEnum, String outTradeNo, @Nullable Long tenantId,
        @Nullable SysPayConfigurationDO sysPayConfigurationDoTemp) {

        ISysPay iSysPay = SYS_PAY_MAP.get(sysPayTypeEnum);

        if (iSysPay == null) {
            ApiResultVO.errorMsg("操作失败：支付方式未找到：{}", sysPayTypeEnum.getCode());
        }

        if (tenantId == null) {
            tenantId = BaseConstant.TENANT_ID;
        }

        return iSysPay.query(outTradeNo, tenantId, sysPayConfigurationDoTemp);

    }

    /**
     * 处理：订单回调
     */
    public static boolean handleTradeNotify(@Nullable SysPayTradeNotifyBO sysPayTradeNotifyBO,
        @Nullable Consumer<SysPayDO> consumer) {

        if (sysPayTradeNotifyBO == null) {
            return false;
        }

        // 获取：支付状态
        SysPayTradeStatusEnum sysPayTradeStatusEnum =
            SysPayTradeStatusEnum.getByStatus(sysPayTradeNotifyBO.getTradeStatus());

        if (SysPayTradeStatusEnum.NOT_EXIST.equals(sysPayTradeStatusEnum)) {
            return false;
        }

        return RedissonUtil.doLock(BaseRedisKeyEnum.PRE_PAY.name() + sysPayTradeNotifyBO.getOutTradeNo(), () -> {

            // 查询：支付状态不同的数据
            SysPayDO sysPayDO = sysPayService.lambdaQuery().eq(SysPayDO::getId, sysPayTradeNotifyBO.getOutTradeNo())
                .ne(SysPayDO::getStatus, sysPayTradeStatusEnum).one();

            if (sysPayDO == null) {
                return false;
            }

            sysPayDO.setPayPrice(new BigDecimal(sysPayTradeNotifyBO.getTotalAmount()));
            sysPayDO.setStatus(sysPayTradeStatusEnum);
            sysPayDO.setTradeNo(sysPayTradeNotifyBO.getTradeNo());
            sysPayDO.setPayCurrency(MyEntityUtil.getNotNullStr(sysPayTradeNotifyBO.getPayCurrency()));

            if (consumer != null) {

                consumer.accept(sysPayDO); // 进行额外的处理

            }

            SYS_PAY_DO_LIST.add(sysPayDO);

            if (!SysPayRefTypeEnum.NONE.equals(sysPayDO.getRefType())) {

                // 支付成功，处理业务
                KafkaUtil.sendPayStatusChangeTopic(sysPayDO);

            }

            return true;

        });

    }

}
