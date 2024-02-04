package com.cmcorg20230301.be.engine.other.app.model.dto;

import cn.hutool.json.JSONObject;
import lombok.Data;

@Data
public class SysOtherAppWxEventValueDTO {

    /**
     * 要执行的操作
     */
    private String key;

    /**
     * 数据
     */
    private JSONObject data;

}
