package com.cmcorg20230301.be.engine.generate.model.bo;

import lombok.Data;

import java.util.List;
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
     * 请求的所属的分组
     */
    private String group;

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
    private BeApiSchema requestBody;

    /**
     * 一般的 get或者 post 请求时，传递的参数，key是参数名
     */
    private Map<String, BeApiField> parameter;

    /**
     * 响应的对象
     */
    private BeApiField response;

    /**
     * 生成前端 api时用，api接口需要返回的类型
     */
    private String returnTypeStr;

    /**
     * 生成前端 api时用，api接口需要返回的类型，是否是数组类型
     */
    private Boolean returnTypeArrFlag;

    /**
     * 字段对象的接口类
     */
    public interface BeApiField {

    }

    /**
     * 请求的是对象时
     */
    @Data
    public static class BeApiSchema implements BeApiField {

        /**
         * 对象的 class名
         */
        private String className;

        /**
         * 对象传递时的名称，备注：只有此对象是一个对象的字段时，才有值
         */
        private String name;

        /**
         * 对象的类型
         */
        private String type;

        /**
         * 是否必须传递
         */
        private Boolean required;

        /**
         * 字段描述
         */
        private String description;

        /**
         * 对象的字段情况 map，key是参数名
         */
        private Map<String, BeApiField> fieldMap;

        /**
         * 必传的字段名集合
         */
        private List<String> requiredFieldName;

        /**
         * 是否是集合
         */
        private Boolean arrFlag;

    }

    /**
     * 请求的是一般类型时
     */
    @Data
    public static class BeApiParameter implements BeApiField {

        /**
         * 字段名称
         */
        private String name;

        /**
         * 对象的类型
         */
        private String type;

        /**
         * 是否必须传递
         */
        private Boolean required;

        /**
         * 字段描述
         */
        private String description;

        /**
         * 字段格式化
         */
        private String format;

        /**
         * 正则表达式
         */
        private String pattern;

        /**
         * 最大长度
         */
        private Integer maxLength;

        /**
         * 最小长度
         */
        private Integer minLength;

        /**
         * 是否是集合
         */
        private Boolean arrFlag;

    }

}
