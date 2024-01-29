package com.cmcorg20230301.be.engine.other.app.wx.work.service;

import cn.hutool.json.JSONObject;

import javax.servlet.http.HttpServletRequest;

public interface SysOtherAppWxWordService {

    String verify(JSONObject dto);

    String receiveMessage(HttpServletRequest request);

}
