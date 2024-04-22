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
import Fingerprint2 from 'fingerprintjs2';
// 引入：自定义样式
import './style/color.less'
import './style/layout.less'
import './style/style.less'
import './style/antd.less'
import './style/theme.less'
import LocalStorageKey from "@/model/constant/LocalStorageKey.ts";
import VConsole from "vconsole";
import {BrowserRouter} from "react-router-dom";
import {MyLocalStorage} from "@/util/StorageUtil.ts";

const consoleOpenFlag = MyLocalStorage.getItem(LocalStorageKey.CONSOLE_OPEN_FLAG);

let vConsole: VConsole | null = null

export function OpenVConsole() {

    DestroyVConsole() // 先销毁，再打开

    vConsole = new VConsole(); // 打开控制台

}

export function DestroyVConsole() {

    vConsole?.destroy() // 销毁控制台

}

if (consoleOpenFlag === '1') {

    OpenVConsole()

}

dayjs.extend(relativeTime)
dayjs.locale('zh-cn');

let browserId: string | null

export function GetBrowserId() {

    if (browserId) {
        return browserId
    }

    browserId = MyLocalStorage.getItem(LocalStorageKey.BROWSER_ID);

    if (browserId) {
        return browserId
    }

    // 浏览器指纹
    Fingerprint2.get((componentArr) => { // 参数只有回调函数时，默认浏览器指纹依据所有配置信息进行生成

        const values = componentArr.map(item => item.value); // 配置的值的数组

        browserId = Fingerprint2.x64hash128(values.join(''), 31); // 生成浏览器指纹

        MyLocalStorage.setItem(LocalStorageKey.BROWSER_ID, browserId!)

    });

    return browserId

}

// 自定义 console ↓
const consoleErrorOld = console.error

console.error = (message?: any, ...optionalParams: any[]) => {

    if (message && typeof (message) === "string" && message.startsWith('Warning: [antd:')) {
        return
    }

    consoleErrorOld(message, ...optionalParams)

}

// const consoleLogOld = console.log;
//
// console.log = (message?: any, ...optionalParams: any[]) => {
//
//     consoleLogOld(message, ...optionalParams)
//
//     $http.myPost('/sys/log/push', {log: (GetBrowserId() + " ") + (message instanceof Object ? JSON.stringify(message) : message) + (" " + JSON.stringify(optionalParams))}, {
//
//         headers: {
//
//             hiddenErrorMsg: true
//
//         } as any
//
//     })
//
// }
// 自定义 console ↑

ReactDOM.createRoot(document.getElementById('root')!).render(
    // <React.StrictMode>

    <BrowserRouter>

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

    </BrowserRouter>

    // </React.StrictMode>
)
