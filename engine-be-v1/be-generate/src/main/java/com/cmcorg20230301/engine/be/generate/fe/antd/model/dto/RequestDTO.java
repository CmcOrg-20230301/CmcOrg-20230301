package com.cmcorg20230301.engine.be.generate.fe.antd.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.http.HttpMethod;

import java.util.Map;

@Data
public class RequestDTO {

    @Schema(description = "请求描述")
    private String description;

    @Schema(description = "请求路径")
    private String uri;

    @Schema(description = "完整的请求路径")
    private String fullUri;

    @Schema(description = "完整请求路径的驼峰")
    private String fullUriHump;

    @Schema(description = "请求方式")
    private HttpMethod method;

    @Schema(description = "参数的 class")
    private Class<?> paramClass;

    @Schema(description = "表单参数，备注：Content-Type 为 application/json时，会转换为 json，反之为 form，默认 json")
    private Map<String, RequestFieldDTO> formMap;

    @Schema(description = "返回值 实际的 class，备注：会最多取两层泛型，并且返回值必须是 ApiResultVO")
    private Class<?> returnRealClass;

    @Schema(description = "是否是 page接口，备注：请求返回值必须是 ApiResultVO<Page<T>>")
    private Boolean pageFlag;

    @Schema(description = "是否是 tree接口，备注：请求返回值必须是 ApiResultVO<List<T>>")
    private Boolean treeFlag;

    @Schema(description = "是否是 infoById接口，备注：只有入参是 NotNullId才行，并且请求返回值必须是 ApiResultVO")
    private Boolean infoByIdFlag;

}
