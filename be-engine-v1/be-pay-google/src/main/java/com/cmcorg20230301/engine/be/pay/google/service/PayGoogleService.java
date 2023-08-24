package com.cmcorg20230301.engine.be.pay.google.service;

import com.cmcorg20230301.engine.be.model.model.dto.NotNullId;
import com.cmcorg20230301.engine.be.pay.google.model.dto.SysPayGooglePaySuccessDTO;

public interface PayGoogleService {

    boolean paySuccess(SysPayGooglePaySuccessDTO dto);

    boolean payConsume(NotNullId notNullId);

}
