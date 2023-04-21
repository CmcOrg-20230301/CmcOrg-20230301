package com.cmcorg20230301.engine.be.model.model.constant;

/**
 * Properties配置的前缀常量类
 */
public interface PropertiesPrefixConstant {

    String PRE_BE = "be.";

    String SECURITY = PRE_BE + "security"; // 权限相关

    String COMMON = PRE_BE + "common"; // 通用相关

    String LOG = PRE_BE + "log"; // 日志相关

    String CACHE = PRE_BE + "cache"; // 缓存相关

    String WX = PRE_BE + "wx"; // 微信相关

    String TENCENT = PRE_BE + "tencent"; // 腾讯相关

    String ALI_YUN = PRE_BE + "aliyun"; // 阿里云相关

    String MINIO = "minio"; // minio文件系统相关

    String SMS = "sms"; // 短信服务相关

}
