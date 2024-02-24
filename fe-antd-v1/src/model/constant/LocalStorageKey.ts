const LocalStorageKey = {

    JWT: "JWT",

    MANAGE_SIGN_IN_FLAG: "MANAGE_SIGN_IN_FLAG", // 是否：允许登录后台管理系统 0 不允许 1 允许

    JWT_EXPIRE_TS: "JWT_EXPIRE_TS", // jwt过期时间戳

    USER_SELF_INFO: 'USER_SELF_INFO',

    USER_SELF_MENU_LIST: 'USER_SELF_MENU_LIST', // 用户菜单

    USER_SELF_AVATAR_URL: 'USER_SELF_AVATAR_URL',

    TENANT_ID: 'TENANT_ID', // 租户 id

    OTHER_APP_ID: 'OTHER_APP_ID', // 第三方应用 id

    SYS_SIGN_CONFIGURATION_VO: 'SYS_SIGN_CONFIGURATION_VO', // 登录注册相关的配置

    SYS_SIGN_CONFIGURATION_VO_SINGLE: 'SYS_SIGN_CONFIGURATION_VO_SINGLE', // 登录注册相关的配置：统一登录

    TENANT_MANAGE_NAME: 'TENANT_MANAGE_NAME', // 管理系统名称

    USER_WALLET_OWNER_INFO: "USER_WALLET_OWNER_INFO", // 用户钱包：归属者信息：租户或者用户

    SYS_OTHER_APP_OFFICIAL_ACCOUNT_MENU_OTHER_APP_ID: "SYS_OTHER_APP_OFFICIAL_ACCOUNT_MENU_OTHER_APP_ID", // 第三方应用-公众号-菜单-管理：otherApp主键 id

    MAIN_URI: 'MAIN_URI', // 主页地址，例如：/admin

    MAIN_REDIRECT_URI: 'MAIN_REDIRECT_URI', // 主页跳转地址，例如：/admin/sys/dict，主要用在：BlankLayout里

    NO_JWT_URI: 'NO_JWT_URI', // 没有 jwt时的地址，例如：/sign/in

    CONSOLE_OPEN_FLAG: 'CONSOLE_OPEN_FLAG', // 是否打开控制台，默认 '0'，打开 '1'

    SIGN_IN_TYPE: 'SIGN_IN_TYPE', // 登录方式

    SIGN_IN_TYPE_SINGLE: 'SIGN_IN_TYPE_SINGLE', // 登录方式：统一登录

}

export default LocalStorageKey
