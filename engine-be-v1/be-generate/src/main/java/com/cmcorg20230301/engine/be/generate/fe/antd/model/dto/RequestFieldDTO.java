package com.cmcorg20230301.engine.be.generate.fe.antd.model.dto;

import com.cmcorg20230301.engine.be.generate.model.enums.FormInputTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RequestFieldDTO {

    @Schema(description = "表单页面，是否是删除时的提示字段，备注：只会存在一个，会被后面的覆盖")
    private Boolean formDeleteNameFlag;

    @Schema(description = "表单页面，字段显示用，默认为：tableTitle/description")
    private String formTitle;

    @Schema(description = "table页面，字段显示用，默认为：formTitle/description")
    private String tableTitle;

    @Schema(description = "表单是否忽略")
    private Boolean formIgnoreFlag;

    @Schema(description = "table是否忽略")
    private Boolean tableIgnoreFlag;

    @Schema(description = "不在 search里面显示")
    private Boolean hideInSearchFlag;

    @Schema(description = "表单页面，字段额外说明")
    private String formTooltip;

    @Schema(description = "table页面，字段排序，越大越前面，默认为：Integer.MIN_VALUE")
    private Integer tableOrderNo;

    @Schema(description = "表单输入类型")
    private FormInputTypeEnum formInputType;

    @Schema(description = "表单选择时，是否支持多选，备注：此值只针对：下拉选和树形下拉选")
    private Boolean formSelectMultipleFlag;

    @Schema(description = "表单是下拉选时，options的值，备注：此值只针对：下拉选和树形下拉选")
    private String formSelectOptionsStr;

    @Schema(description = "表单是下拉选时，request的值，备注：此值只针对：下拉选和树形下拉选")
    private String formSelectRequestStr;

    @Schema(description = "表单是下拉选时，request是否按照 tree格式来获取，备注：此值只针对：下拉选和树形下拉选")
    private Boolean formSelectRequestTreeFlag;

    @Schema(description = "表单是下拉选时，options或者request需要导入的包，备注：此值只针对：下拉选和树形下拉选")
    private String formSelectOptionsOrRequestImportStr;

    @Schema(description = "字段描述")
    private String description;

    @Schema(description = "ts 数据类型，备注：不是基础类型时，会为 null")
    private String tsType;

    @Schema(description = "字段的 class，备注：只获取第一层泛型里面的类型，或者类型的 class，不会为 null")
    private Class<?> fieldClass;

    @Schema(description = "是否是集合")
    private Boolean collectFlag;

    @Schema(description = "不为 null")
    private Boolean notNull;

    @Schema(description = "字符串去掉前后空格之后，大小不为 0")
    private Boolean notBlank;

    @Schema(description = "字符串/数组/map 大小不为 0")
    private Boolean notEmpty;

    @Schema(description = "字符串/数组/map 的最小大小，包含")
    private Integer sizeMax;

    @Schema(description = "字符串/数组/map 的最大大小，包含")
    private Integer sizeMin;

    @Schema(description = "数字最大值，包含")
    private Long max;

    @Schema(description = "数字最小值，包含")
    private Long min;

    @Schema(description = "正则表达式")
    private String regexp;

}
