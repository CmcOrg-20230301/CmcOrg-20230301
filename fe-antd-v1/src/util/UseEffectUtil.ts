import {useEffect} from "react";
import LocalStorageKey from "@/model/constant/LocalStorageKey";
import {SignOut} from "@/util/UserUtil";
import {SysMenuDO, SysMenuUserSelfMenuList} from "@/api/http/SysMenu";
import {ToastError} from "@/util/ToastUtil";
import {setUserSelfMenuList} from "@/store/userSlice";
import {ConnectWebSocket} from "@/util/webSocket/WebSocketUtil";
import {getAppDispatch, getUserSelfMenuList} from "@/MyApp";

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

        getAppDispatch()(setUserSelfMenuList(userSelfMenuList))

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

        const userSelfMenuList = getUserSelfMenuList();

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

            const vConsole = new window.VConsole(); // 打开控制台

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

                    const vConsole = new window.VConsole(); // 打开控制台

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
