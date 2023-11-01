package com.cmcorg20230301.be.engine.sign.wx.service;

import com.cmcorg20230301.be.engine.sign.wx.model.dto.SignInBrowserCodeDTO;
import com.cmcorg20230301.be.engine.sign.wx.model.dto.SignInMiniProgramCodeDTO;
import com.cmcorg20230301.be.engine.sign.wx.model.dto.SignInMiniProgramPhoneCodeDTO;

public interface SignWxService {

    String signInMiniProgramPhoneCode(SignInMiniProgramPhoneCodeDTO dto);

    String signInMiniProgramCode(SignInMiniProgramCodeDTO dto);

    String signInBrowserCode(SignInBrowserCodeDTO dto);

    String signInBrowserCodeUserInfo(SignInBrowserCodeDTO dto);

}
