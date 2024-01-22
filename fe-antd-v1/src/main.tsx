import ReactDOM from 'react-dom/client'
import MyApp from './MyApp'
import {Provider} from "react-redux";
import store from "@/store";
import dayjs from 'dayjs';
import relativeTime from 'dayjs/plugin/relativeTime'
import 'dayjs/locale/zh-cn';
import {App, ConfigProvider, FloatButton} from 'antd';
import zhCN from 'antd/locale/zh_CN';
import 'antd/dist/reset.css';
// 引入：自定义样式
import './style/color.less'
import './style/layout.less'
import './style/style.less'
import './style/antd.less'
import './style/theme.less'

dayjs.extend(relativeTime)
dayjs.locale('zh-cn');

// 自定义 console.error ↓
const consoleErrorOld = console.error

console.error = (message?: any, ...optionalParams: any[]) => {

    if (message?.startsWith('Warning: [antd: Select] `bordered` is deprecated.')) {
        return
    }

    consoleErrorOld(message, ...optionalParams)

}
// 自定义 console.error ↑

ReactDOM.createRoot(document.getElementById('root')!).render(
    // <React.StrictMode>

    <Provider store={store}>

        <ConfigProvider locale={zhCN}>

            <App>

                <MyApp/>

                <div title={"返回顶部"}>

                    <FloatButton.BackTop/>

                </div>

            </App>

        </ConfigProvider>

    </Provider>

    // </React.StrictMode>
)
