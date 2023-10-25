package com.cmcorg20230301.be.engine.wallet.model.interfaces;

public interface ISysUserWalletLogType {

    int getCode(); // 建议从：10001和20001（包含）开始：1开头 增加 2开头 减少

    String getName();

}
