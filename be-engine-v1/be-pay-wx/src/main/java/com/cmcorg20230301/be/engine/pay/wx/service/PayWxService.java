package com.cmcorg20230301.be.engine.pay.wx.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface PayWxService {

    void notifyCallBackNative(HttpServletRequest request, HttpServletResponse response, Long tenantId);

    void notifyCallBackJsApi(HttpServletRequest request, HttpServletResponse response, Long tenantId);

}
