package com.cmcorg20230301.engine.be.generate.model.bo;

import lombok.Data;

import java.util.Map;

/**
 * 一个接口的信息描述
 */
@Data
public class BeApi {

    /**
     * 请求路径
     */
    private String path;

    /**
     * 请求的方法
     */
    private String method;

    /**
     * 请求的所属的标签
     */
    private String tag;

    /**
     * 请求的描述
     */
    private String summary;

    /**
     * contentType
     */
    private String contentType;

    /**
     * post，json请求时，需要传递的对象
     */
    private BeApiRequestBody requestBody;

    /**
     * 一般的 get或者 post 请求时，传递的参数，key是参数名
     */
    private Map<String, BeApiParameter> parameter;

    /**
     * 请求的是对象时
     */
    @Data
    public static class BeApiRequestBody {

        /**
         * 对象的类型
         */
        private String type;

        /**
         * 对象的字段情况 map，key是参数名
         */
        private Map<String, BeApiParameter> propertiesMap;

        /**
         * 是否必须传递
         */
        private Boolean required;

    }

    /**
     * 请求的是一般类型时
     */
    @Data
    public static class BeApiParameter {

        /**
         * 字段名称
         */
        private String name;

        /**
         * 字段类型
         */
        private String type;

        /**
         * 字段格式化
         */
        private String format;

        /**
         * 是否必须传递
         */
        private Boolean required;

        /**
         * 正则表达式
         */
        private String pattern;

        /**
         * 字段描述
         */
        private String description;

    }

}
