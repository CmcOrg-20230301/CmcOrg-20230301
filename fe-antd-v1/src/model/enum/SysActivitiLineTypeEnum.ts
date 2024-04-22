import {IEnum} from "@/model/enum/CommonEnum";
import {ProSchemaValueEnumType} from "@ant-design/pro-components";

export interface ISysActivitiLineTypeEnum {

    NORMAL: IEnum<number>,

    FUNCTION_CALL_SIMPLE: IEnum<number>,

    FUNCTION_CALL_CUSTOM: IEnum<number>,

}

// 流程图线类型，枚举类
export const SysActivitiLineTypeEnum: ISysActivitiLineTypeEnum = {

    NORMAL: {
        code: 101,
        name: '普通判断',
    },

    FUNCTION_CALL_SIMPLE: {
        code: 201,
        name: '函数调用',
    },

    FUNCTION_CALL_CUSTOM: {
        code: 301,
        name: '函数调用-高级',
    },

}

export const SysActivitiLineTypeEnumDict = new Map<number, ProSchemaValueEnumType>();

Object.keys(SysActivitiLineTypeEnum).forEach(key => {

    const item = SysActivitiLineTypeEnum[key] as IEnum<number>;

    SysActivitiLineTypeEnumDict.set(item.code!, {text: item.name})

})
