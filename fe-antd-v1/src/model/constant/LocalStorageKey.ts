const LocalStorageKey = {

    JWT: "JWT",

    JWT_EXPIRE_TIME: "JWT_EXPIRE_TIME", // jwt过期时间

    USER_SELF_INFO: 'USER_SELF_INFO',

    USER_SELF_AVATAR_URL: 'USER_SELF_AVATAR_URL',

    TENANT_ID: 'TENANT_ID', // 租户 id

    USER_WALLET_OWNER_INFO: "USER_WALLET_OWNER_INFO", // 用户钱包：归属者信息：租户或者用户

    SYS_OTHER_APP_OFFICIAL_ACCOUNT_MENU_OTHER_APP_ID: "SYS_OTHER_APP_OFFICIAL_ACCOUNT_MENU_OTHER_APP_ID", // 第三方应用-公众号-菜单-管理：otherApp主键 id

    MAIN_URI: 'MAIN_URI', // 主页地址，例如：/admin

    MAIN_REDIRECT_URI: 'MAIN_REDIRECT_URI', // 主页跳转地址，例如：/admin/sys/dict，主要用在：BlankLayout里

    NO_JWT_URI: 'NO_JWT_URI', // 没有 jwt时的地址，例如：/sign/in

    CONSOLE_OPEN_FLAG: 'CONSOLE_OPEN_FLAG', // 是否打开控制台，默认 '0'，打开 '1'

}

export default LocalStorageKey

export const LocalStorageKeyList = Object.keys(LocalStorageKey)
