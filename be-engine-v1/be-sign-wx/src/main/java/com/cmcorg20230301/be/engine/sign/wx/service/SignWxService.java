package com.cmcorg20230301.be.engine.sign.wx.service;

import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.model.model.vo.GetQrCodeVO;
import com.cmcorg20230301.be.engine.model.model.vo.SignInVO;
import com.cmcorg20230301.be.engine.sign.helper.model.dto.UserSignBaseDTO;
import com.cmcorg20230301.be.engine.sign.wx.model.dto.SignInBrowserCodeDTO;
import com.cmcorg20230301.be.engine.sign.wx.model.dto.SignInMiniProgramCodeDTO;
import com.cmcorg20230301.be.engine.sign.wx.model.dto.SignInMiniProgramPhoneCodeDTO;

public interface SignWxService {

    SignInVO signInMiniProgramPhoneCode(SignInMiniProgramPhoneCodeDTO dto);

    SignInVO signInMiniProgramCode(SignInMiniProgramCodeDTO dto);

    SignInVO signInBrowserCode(SignInBrowserCodeDTO dto);

    SignInVO signInBrowserCodeUserInfo(SignInBrowserCodeDTO dto);

    GetQrCodeVO getQrCodeUrl(UserSignBaseDTO dto);

    SignInVO queryQrCodeById(NotNullId notNullId);

}
