export default {

    ADMIN_REDIRECT_PATH: 'ADMIN_REDIRECT_PATH', // 页面打开时，进行路由跳转的地址，为空则不跳转

    WEB_SOCKET_ID: 'WEB_SOCKET_ID',

    TENANT_ID: 'TENANT_ID', // 租户 id，用于：手动清除 LocalStorage之后，有一个备份，如果把 SessionStorage，也清除的话，则就没有办法复原了

    OAUTH2_REDIRECT_URI: 'OAUTH2_REDIRECT_URI', // oauth2-需要跳转的地址

}
