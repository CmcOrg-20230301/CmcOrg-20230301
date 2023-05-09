package com.cmcorg20230301.engine.be.pay.wx.service.impl;

import com.cmcorg20230301.engine.be.pay.wx.properties.PayWxProperties;
import com.cmcorg20230301.engine.be.pay.wx.service.PayWxService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Service
@Slf4j
public class PayWxServiceImpl implements PayWxService {

    @Resource
    PayWxProperties payWxProperties;

    /**
     * 服务器异步通知，备注：第三方应用调用
     */
    @SneakyThrows
    @Override
    public String notifyCallBack(HttpServletRequest request) {

        return null;

    }

}
