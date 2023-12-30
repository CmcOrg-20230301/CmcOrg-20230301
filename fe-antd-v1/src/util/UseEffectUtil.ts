import {useEffect} from "react";
import LocalStorageKey, {LocalStorageKeyList} from "@/model/constant/LocalStorageKey";
import {ClearStorage, SignOut} from "@/util/UserUtil";
import {SysMenuDO, SysMenuUserSelfMenuList} from "@/api/http/SysMenu";
import {ToastError} from "@/util/ToastUtil";
import {setUserSelfMenuList} from "@/store/userSlice";
import {ConnectWebSocket} from "@/util/WebSocket/WebSocketUtil";
import {GetAppDispatch, GetAppNav, GetUserSelfMenuList} from "@/MyApp";
import {GetURLSearchParams} from "@/util/CommonUtil";
import {SessionStorageKeyList} from "@/model/constant/SessionStorageKey";
import VConsole from 'vconsole';

export interface IInit {

    localStorageData?: Record<string, string>

    sessionStorageData?: Record<string, string>

    redirect?: string

}

// 通过：url的参数，来初始化
export function UseEffectInit() {

    useEffect(() => {

        const dataStr = GetURLSearchParams().get("data");

        if (!dataStr) {
            return
        }

        ClearStorage()

        const data: IInit = JSON.parse(decodeURIComponent(dataStr));

        if (data.localStorageData) {

            Object.keys(data.localStorageData).forEach((key) => {

                if (LocalStorageKeyList.includes(key)) {

                    const value = data.localStorageData![key];

                    if (value) {

                        localStorage.setItem(key, value)

                    }

                }

            })

        }

        if (data.sessionStorageData) {

            Object.keys(data.sessionStorageData).forEach((key) => {

                if (SessionStorageKeyList.includes(key)) {

                    const value = data.sessionStorageData![key];

                    if (value) {

                        sessionStorage.setItem(key, value)

                    }

                }

            })

        }

        if (data.redirect) {

            const redirect = decodeURIComponent(data.redirect)

            if (redirect?.startsWith('http')) {

                window.location.href = redirect!

            } else {

                GetAppNav()(redirect)

            }

        }

    }, [])

}

// 监听是否：全屏
export function UseEffectFullScreenChange(setFullScreenFlag: (value: (((prevState: boolean) => boolean) | boolean)) => void) {

    useEffect(() => {

        const handleFullScreenChange = () => {
            setFullScreenFlag(document.fullscreenElement !== null)
        }

        document.addEventListener('fullscreenchange', handleFullScreenChange);

        return () => {

            document.removeEventListener('fullscreenchange', handleFullScreenChange);

        }

    }, [])

}

// 处理：用户菜单
function handleUserSelfMenuList(userSelfMenuList: SysMenuDO[], callBack: ((data: SysMenuDO[]) => void) | undefined, firstFlag: boolean) {

    if (!userSelfMenuList || !userSelfMenuList.length) {

        ToastError('暂未配置菜单，请联系管理员')
        SignOut()
        return

    }

    if (firstFlag) {

        GetAppDispatch()(setUserSelfMenuList(userSelfMenuList))

    }

    if (callBack) {

        callBack(userSelfMenuList)

    }

    ConnectWebSocket() // 连接 webSocket

}

// 加载菜单
export function UseEffectLoadSysMenuUserSelfMenuList(callBack?: (data: SysMenuDO[]) => void) {

    useEffect(() => {

        const jwt = localStorage.getItem(LocalStorageKey.JWT);

        if (!jwt) {

            SignOut()
            return

        }

        const userSelfMenuList = GetUserSelfMenuList();

        if (userSelfMenuList && userSelfMenuList.length) {  // 如果：已经加载过了菜单

            // 处理：用户菜单
            handleUserSelfMenuList(userSelfMenuList, callBack, false);

            return;

        }

        // 加载菜单
        SysMenuUserSelfMenuList().then(res => {

            // 处理：用户菜单
            handleUserSelfMenuList(res.data, callBack, true);

        })

    }, [])

}

// 控制台：按键触发
export function UseEffectConsoleOpenKeydownListener() {

    useEffect(() => {

        const consoleOpenFlag = localStorage.getItem(LocalStorageKey.CONSOLE_OPEN_FLAG);

        if (consoleOpenFlag === '1') {

            new VConsole(); // 打开控制台
            return

        }

        const consoleOpenKeydownArr = ['c', 'm', 'c', '+', '-', '*', '/', '1', '2', '3', '.']

        let keydownArr: string[] = []

        const handleKeydown = (event: KeyboardEvent) => {

            if (consoleOpenKeydownArr.includes(event.key)) {

                keydownArr.push(event.key)

                if (keydownArr.length > consoleOpenKeydownArr.length) {

                    keydownArr = []
                    document.removeEventListener('keydown', handleKeydown); // 只有一次机会
                    return

                }

                if (keydownArr.length < consoleOpenKeydownArr.length) {
                    return
                }

                if (keydownArr.length === consoleOpenKeydownArr.length) {

                    for (let i = 0; i < keydownArr.length; i++) {

                        if (keydownArr[i] !== consoleOpenKeydownArr[i]) {

                            document.removeEventListener('keydown', handleKeydown); // 只有一次机会
                            return;

                        }

                    }

                    document.removeEventListener('keydown', handleKeydown);

                    localStorage.setItem(LocalStorageKey.CONSOLE_OPEN_FLAG, '1');

                    new VConsole(); // 打开控制台

                }

            } else {

                document.removeEventListener('keydown', handleKeydown); // 只有一次机会
                keydownArr = []

            }

        }

        document.addEventListener('keydown', handleKeydown);

        setTimeout(() => {

            document.removeEventListener('keydown', handleKeydown);

        }, 15000) // 只有 15秒的机会

        return () => {

            document.removeEventListener('keydown', handleKeydown);

        }

    }, [])

}
