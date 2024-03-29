package com.cmcorg20230301.be.engine.flow.activiti.model.enums;

import java.util.concurrent.ConcurrentHashMap;

import com.cmcorg20230301.be.engine.flow.activiti.model.interfaces.ISysActivitiTaskCategory;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SysActivitiTaskCategoryEnum implements ISysActivitiTaskCategory {

    CHAT_GPT(101), //

    MIDJOURNEY(201), //

    ;

    private final int code;

    public static final ConcurrentHashMap<Integer, ISysActivitiTaskCategory> MAP =
        new ConcurrentHashMap<>(SysActivitiTaskCategoryEnum.values().length);

    static {

        for (SysActivitiTaskCategoryEnum item : SysActivitiTaskCategoryEnum.values()) {

            MAP.put(item.getCode(), item);

        }

    }

}
