package com.cmcorg20230301.engine.be.pay.ali.service;

import javax.servlet.http.HttpServletRequest;

public interface PayAliService {

    String notifyCallBack(HttpServletRequest request);

}
