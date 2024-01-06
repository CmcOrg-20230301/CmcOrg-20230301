import {IEnum} from "@/model/enum/CommonEnum";
import {ProSchemaValueEnumType} from "@ant-design/pro-components";

export interface ISysFileStorageTypeEnum {

    EMPTY: IEnum<number>,

    ALI_YUN: IEnum<number>,

    MINIO: IEnum<number>,

}

// 存放文件的服务器类型，枚举类
export const SysFileStorageTypeEnum: ISysFileStorageTypeEnum = {

    EMPTY: {
        code: 0,
        name: '无',
    },

    ALI_YUN: {
        code: 101,
        name: 'aliyun',
    },

    MINIO: {
        code: 201,
        name: 'minio',
    },


}

export const SysFileStorageTypeEnumDict = new Map<number, ProSchemaValueEnumType>();

Object.keys(SysFileStorageTypeEnum).forEach(key => {

    const item = SysFileStorageTypeEnum[key];

    SysFileStorageTypeEnumDict.set(item.code as number, {text: item.name})

})
