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

    String SMS = PRE_BE + "sms"; // 短信服务相关

    String SMS_TENCENT = SMS + ".tencent"; // 腾讯短信相关

    String SMS_ALI_YUN = SMS + ".aliyun"; // 阿里云短信相关

    String FILE = PRE_BE + "file"; // 文件相关

    String MINIO = FILE + ".minio"; // minio文件相关

}
