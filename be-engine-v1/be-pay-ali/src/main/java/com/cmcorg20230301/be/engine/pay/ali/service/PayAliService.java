package com.cmcorg20230301.be.engine.pay.ali.service;

import javax.servlet.http.HttpServletRequest;

public interface PayAliService {

    String notifyCallBack(HttpServletRequest request, Long tenantId);

}
