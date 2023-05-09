package com.cmcorg20230301.engine.be.pay.wx.service;

import javax.servlet.http.HttpServletRequest;

public interface PayWxService {

    String notifyCallBack(HttpServletRequest request);

}
