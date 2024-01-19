import {IEnum} from "@/model/enum/CommonEnum";
import {ProSchemaValueEnumType} from "@ant-design/pro-components";

export interface ISysUserTenantEnum {

    USER: IEnum<number>,
    TENANT: IEnum<number>,

}

// 用户/租户
export const SysUserTenantEnum: ISysUserTenantEnum = {

    USER: {
        code: 1,
        name: '用户',
    },

    TENANT: {
        code: 2,
        name: '租户',
    },

}

export const SysUserTenantEnumDict = new Map<number, ProSchemaValueEnumType>();

Object.keys(SysUserTenantEnum).forEach(key => {

    const item = SysUserTenantEnum[key] as IEnum<number>;

    SysUserTenantEnumDict.set(item.code!, {text: item.name})

})
