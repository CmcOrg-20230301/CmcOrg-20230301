package com.cmcorg20230301.be.engine.wallet.configuration;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.datasource.util.TransactionUtil;
import com.cmcorg20230301.be.engine.pay.base.mapper.SysPayMapper;
import com.cmcorg20230301.be.engine.pay.base.model.configuration.ISysPayRefHandler;
import com.cmcorg20230301.be.engine.pay.base.model.entity.SysPayDO;
import com.cmcorg20230301.be.engine.pay.base.model.enums.SysPayRefStatusEnum;
import com.cmcorg20230301.be.engine.pay.base.model.enums.SysPayRefTypeEnum;
import com.cmcorg20230301.be.engine.pay.base.model.enums.SysPayTradeStatusEnum;
import com.cmcorg20230301.be.engine.pay.base.model.interfaces.ISysPayRefType;
import com.cmcorg20230301.be.engine.pay.base.util.PayHelper;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import com.cmcorg20230301.be.engine.redisson.util.RedissonUtil;
import com.cmcorg20230301.be.engine.security.util.MyThreadUtil;
import com.cmcorg20230301.be.engine.wallet.model.enums.SysUserWalletLogTypeEnum;
import com.cmcorg20230301.be.engine.wallet.service.SysUserWalletService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 购买算力的订单支付回调处理
 */
@Component
public class SysWalletUserSysPayRefHandlerConfiguration implements ISysPayRefHandler {

    @Resource
    SysUserWalletService sysUserWalletService;

    @Resource
    SysPayMapper sysPayMapper;

    /**
     * 关联的类型
     */
    @Override
    public ISysPayRefType getSysPayRefType() {
        return SysPayRefTypeEnum.WALLET_RECHARGE_USER;
    }

    /**
     * 执行处理
     */
    @Override
    public void handle(SysPayDO sysPayDO) {

        if (!SysPayTradeStatusEnum.TRADE_SUCCESS.equals(sysPayDO.getStatus())) {
            return;
        }

        if (sysPayDO.getRefType() != getSysPayRefType().getCode()) {
            return;
        }

        // 获取：订单关联的 id
        Long refId = sysPayDO.getRefId();

        if (refId == null) {
            return;
        }

        if (SysPayRefStatusEnum.FINISHED.getCode() == sysPayDO.getRefStatus()) {
            return;
        }

        // 异步执行
        MyThreadUtil.execute(() -> {

            RedissonUtil.doLock(BaseRedisKeyEnum.PRE_PAY.name() + sysPayDO.getId(), () -> {

                // 再查询一次：是否已经处理过该支付
                boolean exists = ChainWrappers.lambdaQueryChain(sysPayMapper)
                    .eq(SysPayDO::getId, sysPayDO.getId())
                    .eq(SysPayDO::getRefStatus, SysPayRefStatusEnum.WAIT_PAY.getCode()).exists();

                if (!exists) { // 如果：已经处理过了，则不再处理
                    return;
                }

                String refData = sysPayDO.getRefData();

                Long userId = sysPayDO.getUserId();

                Date date = new Date();

                TransactionUtil.exec(() -> {

                    // 增加用户的：可提现余额
                    sysUserWalletService.doAddWithdrawableMoney(userId, date,
                        CollUtil.newHashSet(refId),
                        sysPayDO.getOriginalPrice(), SysUserWalletLogTypeEnum.ADD_PAY, false, false,
                        false, refId,
                        refData, true, null, sysPayDO.getTenantId());

                    if (StrUtil.isNotBlank(refData)) {

                        // 减少租户的：可提现余额和可提现预使用余额
                        sysUserWalletService.doAddWithdrawableMoney(userId, date,
                            CollUtil.newHashSet(Convert.toLong(refData)),
                            sysPayDO.getOriginalPrice().negate(),
                            SysUserWalletLogTypeEnum.REDUCE_USER_BUY, false, false, true, refId,
                            refData, false, null,
                            null);

                    }

                    sysPayDO.setRefStatus(SysPayRefStatusEnum.FINISHED.getCode());

                    sysPayMapper.updateById(sysPayDO); // 更新：支付的关联状态

                });

                // 关闭：前端支付弹窗
                PayHelper.sendSysPayCloseModalTopic(sysPayDO);

            });

        });

    }

}
