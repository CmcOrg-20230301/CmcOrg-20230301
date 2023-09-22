package com.cmcorg20230301.be.engine.pay.google.service;

import com.cmcorg20230301.be.engine.pay.google.model.dto.SysPayGooglePayConsumeDTO;
import com.cmcorg20230301.be.engine.pay.google.model.dto.SysPayGooglePaySuccessDTO;

public interface PayGoogleService {

    boolean paySuccess(SysPayGooglePaySuccessDTO dto);

    boolean payConsume(SysPayGooglePayConsumeDTO dto);

}
