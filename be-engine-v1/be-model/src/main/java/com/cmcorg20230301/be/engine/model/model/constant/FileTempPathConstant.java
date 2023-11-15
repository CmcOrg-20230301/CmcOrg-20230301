package com.cmcorg20230301.be.engine.model.model.constant;

/**
 * 文件临时路径枚举类
 */
public interface FileTempPathConstant {

    String PRE_TEMP_PATH = "/home/myTempFile";

    // 微信上传文件时的，临时路径
    String WX_MEDIA_UPLOAD_TEMP_PATH = PRE_TEMP_PATH + "/wxMediaUpload/";

}
