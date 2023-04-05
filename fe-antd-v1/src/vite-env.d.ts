/// <reference types="vite/client" />

interface ImportMetaEnv {

    readonly NODE_ENV: string
    readonly VITE_API_BASE_URL: string

}

interface ImportMeta {

    readonly env: ImportMetaEnv

}
