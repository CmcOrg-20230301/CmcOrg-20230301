/// <reference types="vite/client" />

interface ImportMetaEnv {

    readonly NODE_ENV: string

}

interface ImportMeta {

    readonly env: ImportMetaEnv

}

interface Window {

    apiUrl: string // api的请求地址，例如：https://cmcopen.top/prod-api/be

}
