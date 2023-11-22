package com.cmcorg20230301.be.engine.wallet.configuration;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.cmcorg20230301.be.engine.pay.base.model.configuration.ISysPayRefHandler;
import com.cmcorg20230301.be.engine.pay.base.model.entity.SysPayDO;
import com.cmcorg20230301.be.engine.pay.base.model.enums.SysPayRefTypeEnum;
import com.cmcorg20230301.be.engine.pay.base.model.enums.SysPayTradeStatusEnum;
import com.cmcorg20230301.be.engine.pay.base.model.interfaces.ISysPayRefType;
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
public class SysWalletTenantSysPayRefHandlerConfiguration implements ISysPayRefHandler {

    @Resource
    SysUserWalletService sysUserWalletService;

    /**
     * 关联的类型
     */
    @Override
    public ISysPayRefType getSysPayRefType() {
        return SysPayRefTypeEnum.WALLET_RECHARGE_TENANT;
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

        // 获取：订单 id
        Long refId = sysPayDO.getRefId();

        if (refId == null) {
            return;
        }

        // 异步执行
        MyThreadUtil.execute(() -> {

            String refData = sysPayDO.getRefData();

            Date date = new Date();

            // 增加租户的：可提现余额
            sysUserWalletService.doAddWithdrawableMoney(sysPayDO.getUserId(), date, CollUtil.newHashSet(refId),
                sysPayDO.getOriginalPrice(), SysUserWalletLogTypeEnum.ADD_PAY, false, false, true, sysPayDO.getRefId(),
                refData, true, null, null);

            if (StrUtil.isNotBlank(refData)) {

                // 减少租户的：可提现余额和冻结余额
                sysUserWalletService
                    .doAddWithdrawableMoney(sysPayDO.getUserId(), date, CollUtil.newHashSet(Convert.toLong(refData)),
                        sysPayDO.getOriginalPrice().negate(), SysUserWalletLogTypeEnum.REDUCE_USER_BUY, false, false,
                        true, sysPayDO.getRefId(), refData, false, null, null);

            }

        });

    }

}
