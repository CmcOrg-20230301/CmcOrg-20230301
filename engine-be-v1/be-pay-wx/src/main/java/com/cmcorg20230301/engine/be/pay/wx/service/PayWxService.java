package com.cmcorg20230301.engine.be.pay.wx.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface PayWxService {

    void notifyCallBackNative(HttpServletRequest request, HttpServletResponse response);

    void notifyCallBackJsApi(HttpServletRequest request, HttpServletResponse response);

}
