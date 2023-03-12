import {defineConfig, loadEnv} from 'vite'
import react from '@vitejs/plugin-react'
import {resolve} from 'path'
import {version} from "antd";

// https://vitejs.dev/config/
export default ({mode}) =>
    defineConfig({
        define: {
            'process.env.ANTD_VERSION': `"${version}"`,
        },
        plugins: [react()],
        resolve: {
            alias: [
                {
                    find: /^~/,
                    replacement: '',
                },
                {
                    find: '@',
                    replacement: resolve(__dirname, './src')
                }
            ],
        },
        server: {
            proxy: {
                '/api': {
                    target: loadEnv(mode, process.cwd()).VITE_API_BASE_URL,
                    changeOrigin: true,
                    rewrite: (path) => path.replace(/^\/api/, ''),
                },
            },
            port: 5001,
            host: '0.0.0.0',
        },
        css: {
            preprocessorOptions: {
                less: {
                    javascriptEnabled: true,
                },
            },
        },
    })

