package com.cmcorg20230301.engine.be.sign.wx.service;

import com.cmcorg20230301.engine.be.sign.wx.model.dto.SignInMiniProgramCodeDTO;
import com.cmcorg20230301.engine.be.sign.wx.model.dto.SignInMiniProgramPhoneCodeDTO;

public interface SignWxService {

    String signInMiniProgramPhoneCode(SignInMiniProgramPhoneCodeDTO dto);

    String signInMiniProgramCode(SignInMiniProgramCodeDTO dto);

}
