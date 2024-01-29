package com.cmcorg20230301.be.engine.other.app.wx.service;

import com.cmcorg20230301.be.engine.other.app.model.dto.SysOtherAppWxOfficialAccountVerifyDTO;

import javax.servlet.http.HttpServletRequest;

public interface SysOtherAppWxOfficialAccountService {

    String verify(SysOtherAppWxOfficialAccountVerifyDTO dto);

    String receiveMessage(HttpServletRequest request);

}
