package com.cmcorg20230301.be.engine.pay.google.service;

import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.pay.google.model.dto.SysPayGooglePaySuccessDTO;

public interface PayGoogleService {

    boolean paySuccess(SysPayGooglePaySuccessDTO dto);

    boolean payConsume(NotNullId notNullId);

}
