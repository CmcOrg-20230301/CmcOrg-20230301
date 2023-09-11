package com.cmcorg20230301.be.engine.pay.base.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.cmcorg20230301.be.engine.datasource.util.TransactionUtil;
import com.cmcorg20230301.be.engine.kafka.util.KafkaUtil;
import com.cmcorg20230301.be.engine.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.be.engine.model.model.dto.PayDTO;
import com.cmcorg20230301.be.engine.pay.base.model.bo.SysPayTradeNotifyBO;
import com.cmcorg20230301.be.engine.pay.base.model.configuration.ISysPay;
import com.cmcorg20230301.be.engine.pay.base.model.entity.SysPayDO;
import com.cmcorg20230301.be.engine.pay.base.model.enums.SysPayRefTypeEnum;
import com.cmcorg20230301.be.engine.pay.base.model.enums.SysPayTradeStatusEnum;
import com.cmcorg20230301.be.engine.pay.base.properties.SysPayProperties;
import com.cmcorg20230301.be.engine.pay.base.service.SysPayService;
import com.cmcorg20230301.be.engine.redisson.model.enums.RedisKeyEnum;
import com.cmcorg20230301.be.engine.redisson.util.IdGeneratorUtil;
import com.cmcorg20230301.be.engine.redisson.util.RedissonUtil;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.util.MyEntityUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@Component
@Slf4j(topic = LogTopicConstant.PAY)
public class PayUtil {

    private static SysPayProperties sysPayProperties;

    private static final Map<Integer, ISysPay> SYS_PAY_MAP = MapUtil.newHashMap();

    private static SysPayService sysPayService;

    public PayUtil(SysPayProperties sysPayProperties, @Autowired(required = false) @Nullable List<ISysPay> iSysPayList,
        SysPayService sysPayService) {

        PayUtil.sysPayProperties = sysPayProperties;
        PayUtil.sysPayService = sysPayService;

        if (CollUtil.isNotEmpty(iSysPayList)) {

            for (ISysPay item : iSysPayList) {

                SYS_PAY_MAP.put(item.getSysPayType().getCode(), item);

            }

        }

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

        log.info("保存支付数据，长度：{}", tempSysPayDOList.size());

        // 批量保存数据
        sysPayService.updateBatchById(tempSysPayDOList);

    }

    /**
     * 支付
     *
     * @param consumer 注意：SysPayDO对象，只建议修改：refType 和 refId这两个属性，其他属性不建议修改
     */
    public static SysPayDO pay(PayDTO dto, @Nullable Consumer<SysPayDO> consumer) {

        ISysPay iSysPay = SYS_PAY_MAP.get(sysPayProperties.getBasePayType());

        if (iSysPay == null) {

            ApiResultVO.errorMsg("操作失败：支付方式未找到：{}", sysPayProperties.getBasePayType());

        }

        Long payId = IdGeneratorUtil.nextId();

        dto.setOutTradeNo(payId.toString()); // 设置：支付的订单号

        String payReturnValue = iSysPay.pay(dto);

        SysPayDO sysPayDO = new SysPayDO();

        sysPayDO.setId(payId);

        sysPayDO.setPayType(iSysPay.getSysPayType());

        sysPayDO.setTenantId(dto.getTenantId());

        sysPayDO.setUserId(dto.getUserId());

        sysPayDO.setSubject(dto.getSubject());
        sysPayDO.setBody(dto.getBody());
        sysPayDO.setOriginPrice(dto.getTotalAmount());
        sysPayDO.setPayPrice(BigDecimal.ZERO);
        sysPayDO.setPayCurrency("");

        sysPayDO.setExpireTime(dto.getTimeExpire());

        sysPayDO.setOpenId(MyEntityUtil.getNotNullAndTrimStr(dto.getOpenId()));

        sysPayDO.setStatus(SysPayTradeStatusEnum.WAIT_BUYER_PAY);

        sysPayDO.setTradeNo("");

        sysPayDO.setPayReturnValue(MyEntityUtil.getNotNullStr(payReturnValue));

        sysPayDO.setRefType(SysPayRefTypeEnum.NONE);
        sysPayDO.setRefId(-1L);

        sysPayDO.setPackageName(MyEntityUtil.getNotNullAndTrimStr(dto.getPackageName()));
        sysPayDO.setProductId(MyEntityUtil.getNotNullAndTrimStr(dto.getProductId()));
        sysPayDO.setToken(MyEntityUtil.getNotNullAndTrimStr(dto.getToken()));

        sysPayDO.setEnableFlag(true);
        sysPayDO.setDelFlag(false);
        sysPayDO.setRemark("");

        TransactionUtil.exec(() -> {

            if (consumer != null) {

                consumer.accept(sysPayDO);

            }

            sysPayService.save(sysPayDO);

        });

        return sysPayDO;

    }

    /**
     * 查询订单状态
     *
     * @param outTradeNo 商户订单号，商户网站订单系统中唯一订单号，必填
     */
    public static SysPayTradeStatusEnum query(String outTradeNo) {

        ISysPay iSysPay = SYS_PAY_MAP.get(sysPayProperties.getBasePayType());

        if (iSysPay == null) {

            ApiResultVO.errorMsg("操作失败：支付方式未找到：{}", sysPayProperties.getBasePayType());

        }

        return iSysPay.query(outTradeNo);

    }

    /**
     * 处理：订单回调
     */
    public static boolean handleTradeNotify(@Nullable SysPayTradeNotifyBO sysPayTradeNotifyBO,
        @Nullable Consumer<SysPayDO> consumer) {

        if (sysPayTradeNotifyBO == null) {
            return false;
        }

        SysPayTradeStatusEnum sysPayTradeStatusEnum =
            SysPayTradeStatusEnum.getByStatus(sysPayTradeNotifyBO.getTradeStatus());

        if (sysPayTradeStatusEnum == null) {
            return false;
        }

        return RedissonUtil.doLock(RedisKeyEnum.PRE_PAY.name() + sysPayTradeNotifyBO.getOutTradeNo(), () -> {

            // 查询：订单状态不同的数据
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

            // 支付成功，处理业务
            KafkaUtil.sendPayStatusChangeTopic(sysPayDO);

            return true;

        });

    }

}
