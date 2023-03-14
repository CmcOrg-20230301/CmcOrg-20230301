package com.cmcorg20230301.engine.be.netty.boot.exception;

import com.cmcorg20230301.engine.be.model.exception.IBizCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BizCodeEnum implements IBizCode {

    PATH_NOT_FOUND(300011, "路径未找到：{}"), //

    ;

    private final int code;
    private final String msg;

}
