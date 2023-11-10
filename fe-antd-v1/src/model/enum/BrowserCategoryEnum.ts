import {IEnum} from "@/model/enum/CommonEnum";
import {ProSchemaValueEnumType} from "@ant-design/pro-components";

export interface IBrowserCategoryEnum {

    WINDOWS_BROWSER: IEnum<number>,
    MAC_BROWSER: IEnum<number>,
    LINUX_BROWSER: IEnum<number>,

    WINDOWS_BROWSER_WX: IEnum<number>,
    MAC_BROWSER_WX: IEnum<number>,
    LINUX_BROWSER_WX: IEnum<number>,

    ANDROID_BROWSER: IEnum<number>,
    ANDROID_BROWSER_WX: IEnum<number>,

    APPLE_BROWSER: IEnum<number>,
    APPLE_BROWSER_WX: IEnum<number>,

}

// 浏览器类别，枚举类
export const BrowserCategoryEnum: IBrowserCategoryEnum = {

    WINDOWS_BROWSER: {
        code: 101,
        name: 'windows-浏览器',
    },
    MAC_BROWSER: {
        code: 102,
        name: 'mac-浏览器',
    },
    LINUX_BROWSER: {
        code: 103,
        name: 'linux-浏览器',
    },

    WINDOWS_BROWSER_WX: {
        code: 104,
        name: 'windows-浏览器-微信',
    },
    MAC_BROWSER_WX: {
        code: 105,
        name: 'mac-浏览器-微信',
    },
    LINUX_BROWSER_WX: {
        code: 106,
        name: 'linux-浏览器-微信',
    },

    ANDROID_BROWSER: {
        code: 302,
        name: '安卓-浏览器',
    },
    ANDROID_BROWSER_WX: {
        code: 303,
        name: '安卓-浏览器-微信',
    },

    APPLE_BROWSER: {
        code: 402,
        name: '苹果-浏览器',
    },
    APPLE_BROWSER_WX: {
        code: 403,
        name: '苹果-浏览器-微信',
    },

}

export const BrowserCategoryEnumDict = new Map<number, ProSchemaValueEnumType>();

Object.keys(BrowserCategoryEnum).forEach(key => {

    const item = BrowserCategoryEnum[key];

    BrowserCategoryEnumDict.set(item.code as number, {text: item.name})

})
