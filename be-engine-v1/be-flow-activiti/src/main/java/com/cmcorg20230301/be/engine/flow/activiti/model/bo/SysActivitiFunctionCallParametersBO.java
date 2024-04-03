package com.cmcorg20230301.be.engine.flow.activiti.model.bo;

import java.util.Map;
import java.util.Set;

import com.cmcorg20230301.be.engine.flow.activiti.util.SysActivitiUtil;

import lombok.Data;

@Data
public class SysActivitiFunctionCallParametersBO {

    /**
     * 一般为：object
     */
    private String type;

    /**
     * 入参的字段，备注：content字段为默认的入参字段 {@link SysActivitiUtil#CONTENT}
     */
    private Map<String, SysActivitiFunctionCallParamPropertiesBO> properties;

    @Data
    static class SysActivitiFunctionCallParamPropertiesBO {

        /**
         * 字段类型，例如：string
         */
        private String type;

        /**
         * 字段描述
         */
        private String description;

    }

    /**
     * 必须的字段
     */
    private Set<String> required;

}
