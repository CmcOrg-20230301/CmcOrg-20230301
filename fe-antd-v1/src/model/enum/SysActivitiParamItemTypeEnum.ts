import {IEnum} from "@/model/enum/CommonEnum";
import {ProSchemaValueEnumType} from "@ant-design/pro-components";

export interface ISysActivitiParamItemTypeEnum {

    TEXT: IEnum<number>,

    IMAGE: IEnum<number>,

    URL: IEnum<number>,

}

// 流程图参数类型，枚举类
export const SysActivitiParamItemTypeEnum: ISysActivitiParamItemTypeEnum = {

    TEXT: {
        code: 101,
        name: '文字',
    },

    IMAGE: {
        code: 201,
        name: '图片路径',
    },

    URL: {
        code: 501,
        name: '网络链接',
    },

}

export const SysActivitiParamItemTypeEnumDict = new Map<number, ProSchemaValueEnumType>();

Object.keys(SysActivitiParamItemTypeEnum).forEach(key => {

    const item = SysActivitiParamItemTypeEnum[key] as IEnum<number>;

    SysActivitiParamItemTypeEnumDict.set(item.code!, {text: item.name})

})
