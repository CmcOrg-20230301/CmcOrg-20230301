import {GetTenantIdFromStorage, SetTenantIdToStorage} from "@/util/CommonUtil.ts";
import LocalStorageKey from "@/model/constant/LocalStorageKey.ts";

export interface IStorageForeverValue {

    MAIN_URI: string

    MAIN_REDIRECT_URI: string

    NO_JWT_URI: string

    CONSOLE_OPEN_FLAG: string

    SIGN_IN_TYPE: string

    SIGN_IN_TYPE_SINGLE: string

    SYS_SIGN_CONFIGURATION_VO: string

    SYS_SIGN_CONFIGURATION_VO_SINGLE: string

    TENANT_MANAGE_NAME: string

    OTHER_APP_ID: string

    BROWSER_ID: string

    JWT_REFRESH_TOKEN: string

}

const StorageForeverValue: IStorageForeverValue = {

    MAIN_URI: LocalStorageKey.MAIN_URI,

    MAIN_REDIRECT_URI: LocalStorageKey.MAIN_REDIRECT_URI,

    NO_JWT_URI: LocalStorageKey.NO_JWT_URI,

    CONSOLE_OPEN_FLAG: LocalStorageKey.CONSOLE_OPEN_FLAG,

    SIGN_IN_TYPE: LocalStorageKey.SIGN_IN_TYPE,

    SIGN_IN_TYPE_SINGLE: LocalStorageKey.SIGN_IN_TYPE_SINGLE,

    SYS_SIGN_CONFIGURATION_VO: LocalStorageKey.SYS_SIGN_CONFIGURATION_VO,

    SYS_SIGN_CONFIGURATION_VO_SINGLE: LocalStorageKey.SYS_SIGN_CONFIGURATION_VO_SINGLE,

    TENANT_MANAGE_NAME: LocalStorageKey.TENANT_MANAGE_NAME,

    OTHER_APP_ID: LocalStorageKey.OTHER_APP_ID,

    BROWSER_ID: LocalStorageKey.BROWSER_ID,

    JWT_REFRESH_TOKEN: LocalStorageKey.JWT_REFRESH_TOKEN,

}

/**
 * 获取：退出登录时，需要保存的参数
 */
export function GetStorageForeverValue() {

    const resObj: Record<string, string> = {}

    resObj[LocalStorageKey.TENANT_ID] = GetTenantIdFromStorage();

    Object.keys(StorageForeverValue).forEach(key => {

        const storageKey = StorageForeverValue[key] as string;

        const storageValue = MyLocalStorage.getItem(storageKey);

        if (storageValue) {

            resObj[storageKey] = storageValue

        }

    })

    return resObj;

}

/**
 * 设置：退出登录时，需要保存的参数
 */
export function SetStorageForeverValue(resObj: Record<string, string>) {

    SetTenantIdToStorage(resObj[LocalStorageKey.TENANT_ID])

    Object.keys(resObj).forEach(key => {

        MyLocalStorage.setItem(key, resObj[key])

    })

}

interface IStorage {

    length(): number

    clear(): void

    getItem(key: string): string | null

    key(index: number): string | null

    removeItem(key: string): void

    setItem(key: string, value: string): void

}

export const MyLocalStorage: IStorage = {

    length(): number {

        return localStorage.length

    },

    clear(): void {

        localStorage.clear()

    },

    getItem(key: string): string | null {

        return localStorage.getItem(key)

    },

    key(index: number): string | null {

        return localStorage.key(index)

    },

    removeItem(key: string): void {

        localStorage.removeItem(key)

    },

    setItem(key: string, value: string): void {

        localStorage.setItem(key, value)

    }

}

export const MySessionStorage: IStorage = {

    length(): number {

        return sessionStorage.length

    },

    clear(): void {

        sessionStorage.clear()

    },

    getItem(key: string): string | null {

        return sessionStorage.getItem(key)

    },

    key(index: number): string | null {

        return sessionStorage.key(index)

    },

    removeItem(key: string): void {

        sessionStorage.removeItem(key)

    },

    setItem(key: string, value: string): void {

        sessionStorage.setItem(key, value)

    }

}