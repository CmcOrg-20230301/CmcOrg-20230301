package com.cmcorg20230301.be.engine.sign.single.service;

import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.model.model.vo.GetQrCodeVO;
import com.cmcorg20230301.be.engine.model.model.vo.SignInVO;
import com.cmcorg20230301.be.engine.model.model.vo.SysSignConfigurationVO;
import com.cmcorg20230301.be.engine.sign.single.model.dto.SignSingleSignInCodePhoneDTO;
import com.cmcorg20230301.be.engine.sign.single.model.dto.SignSingleSignInSendCodePhoneDTO;

public interface SignSingleService {

    SysSignConfigurationVO getSignInConfiguration();

    GetQrCodeVO signInGetQrCodeUrlWx(boolean b);

    SignInVO signInByQrCodeIdWx(NotNullId notNullId);

    String signInSendCodePhone(SignSingleSignInSendCodePhoneDTO dto);

    SignInVO signInCodePhone(SignSingleSignInCodePhoneDTO dto);

}
