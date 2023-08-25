package com.cmcorg20230301.be.engine.security.exception;

import cn.hutool.json.JSONUtil;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BaseException extends RuntimeException {

    private ApiResultVO<?> apiResultVO;

    public BaseException(ApiResultVO<?> apiResult) {

        super(JSONUtil.toJsonStr(apiResult)); // 把信息封装成json格式

        setApiResultVO(apiResult);

    }

}
