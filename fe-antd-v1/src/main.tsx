import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App'
import {Provider} from "react-redux";
import store from "@/store";
import dayjs from 'dayjs';
import 'dayjs/locale/zh-cn';
import {ConfigProvider, FloatButton} from 'antd';
import zhCN from 'antd/locale/zh_CN';
import 'antd/dist/reset.css';
// 引入：自定义样式
import './style/color.less'
import './style/layout.less'
import './style/style.less'
import './style/antd.less'
import './style/theme.less'

dayjs.locale('zh-cn');

// 自定义 console.error ↓
const consoleOldError = console.error

console.error = (message?: any, ...optionalParams: any[]) => {

    consoleOldError(message, ...optionalParams)

}
// 自定义 console.error ↑

ReactDOM.createRoot(document.getElementById('root') as HTMLElement).render(
    <React.StrictMode>

        <Provider store={store}>
            <ConfigProvider locale={zhCN}>

                <App/>

                <div title={"返回顶部"}>
                    <FloatButton.BackTop/>
                </div>

            </ConfigProvider>

        </Provider>

    </React.StrictMode>
)
