import {useEffect} from "react";
import LocalStorageKey from "@/model/constant/LocalStorageKey";
import {ClearStorage, SignOut} from "@/util/UserUtil";
import {SysMenuDO, SysMenuUserSelfMenuList} from "@/api/http/SysMenu";
import {ToastError} from "@/util/ToastUtil";
import {
    setUserSelfAvatarUrl,
    setUserSelfInfo,
    setUserSelfInfoLoadFlag,
    setUserSelfMenuList,
    setUserSelfMenuListLoadFlag
} from "@/store/userSlice";
import {ConnectWebSocket} from "@/util/WebSocket/WebSocketUtil";
import {GetAppDispatch, GetAppNav} from "@/MyApp";
import {GetURLSearchParams, SetTenantIdToStorage} from "@/util/CommonUtil";
import VConsole from 'vconsole';
import {UserSelfInfo, UserSelfInfoVO} from "@/api/http/UserSelf.ts";
import {SysFileGetPublicUrl} from "@/api/http/SysFile.ts";
import {useAppDispatch, useAppSelector} from "@/store";
import {SysUserManageSignInFlag} from "@/api/http/SysUser.ts";

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

                const value = data.localStorageData![key];

                if (value) {

                    localStorage.setItem(key, value)

                }

            })

        }

        if (data.sessionStorageData) {

            Object.keys(data.sessionStorageData).forEach((key) => {

                const value = data.sessionStorageData![key];

                if (value) {

                    sessionStorage.setItem(key, value)

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
function handleUserSelfMenuList(userSelfMenuList: SysMenuDO[], callBack: ((data: SysMenuDO[], firstFlag: boolean) => void) | undefined, firstFlag: boolean) {

    if (!userSelfMenuList || !userSelfMenuList.length) {

        ToastError('暂未配置菜单，请联系管理员')
        SignOut()
        return

    }

    if (firstFlag) {

        GetAppDispatch()(setUserSelfMenuList(userSelfMenuList))

        ConnectWebSocket() // 连接 webSocket

    }

    if (callBack) {

        callBack(userSelfMenuList, firstFlag)

    }

}

// 加载菜单
export function UseEffectLoadSysMenuUserSelfMenuList(callBack?: (data: SysMenuDO[], firstFlag: boolean) => void, manageFlag: boolean = false) {

    const userSelfMenuList = useAppSelector((state) => state.user.userSelfMenuList);

    const userSelfMenuListLoadFlag = useAppSelector((state) => state.user.userSelfMenuListLoadFlag);

    const appDispatch = useAppDispatch();

    useEffect(() => {

        // 执行
        LoadSysMenuUserSelfMenuList(callBack, userSelfMenuList, userSelfMenuListLoadFlag, appDispatch, manageFlag)();

    }, [])

}

const ManageSignInToastError = "您暂不允许登录后台，请联系管理员"

/**
 * 执行：加载菜单
 */
function LoadSysMenuUserSelfMenuList(callBack: ((data: SysMenuDO[], firstFlag: boolean) => void) | undefined, userSelfMenuList: SysMenuDO[], userSelfMenuListLoadFlag: boolean, appDispatch, manageFlag: boolean) {

    return async () => {

        const jwt = localStorage.getItem(LocalStorageKey.JWT);

        if (!jwt) {

            SignOut()
            return

        }

        let handleManageSignInFlag = false // 是否：处理过后台登录的限制

        if (manageFlag) {

            const manageSignInFlag = localStorage.getItem(LocalStorageKey.MANAGE_SIGN_IN_FLAG);

            if (manageSignInFlag !== '1') {

                const res = await SysUserManageSignInFlag();

                if (res.data) {

                    localStorage.setItem(LocalStorageKey.MANAGE_SIGN_IN_FLAG, "1")

                    handleManageSignInFlag = true

                } else {

                    ToastError(ManageSignInToastError)
                    SignOut()
                    return

                }

            }

        }

        if (callBack) {
            callBack(userSelfMenuList, false)
        }

        if (userSelfMenuListLoadFlag) {  // 如果：已经加载过了菜单
            return;
        }

        // 先设置为：true
        appDispatch(setUserSelfMenuListLoadFlag(true))

        if (manageFlag && !handleManageSignInFlag) {

            SysUserManageSignInFlag().then(res => {

                if (!res.data) {

                    ToastError(ManageSignInToastError)
                    SignOut()

                }

            })

        }

        // 加载菜单
        SysMenuUserSelfMenuList().then(res => {

            // 处理：用户菜单
            handleUserSelfMenuList(res.data, callBack, true);

        })

    }

}

// 加载：用户数据
export function UseEffectLoadUserSelfInfo(callBack?: (data: UserSelfInfoVO) => void) {

    const userSelfInfo = useAppSelector((state) => state.user.userSelfInfo)

    const userSelfInfoLoadFlag = useAppSelector((state) => state.user.userSelfInfoLoadFlag);

    const appDispatch = useAppDispatch();

    useEffect(() => {

        const jwt = localStorage.getItem(LocalStorageKey.JWT);

        if (!jwt) {

            SignOut()
            return

        }

        if (userSelfInfoLoadFlag) { // 如果：已经加载过了用户信息

            if (callBack) {
                callBack(userSelfInfo)
            }

            return

        }

        // 先设置为：true
        appDispatch(setUserSelfInfoLoadFlag(true))

        UserSelfInfo().then(res => {

            appDispatch(setUserSelfInfo(res.data))

            const avatarFileId = res.data.avatarFileId!;

            if (avatarFileId as any !== -1) {

                SysFileGetPublicUrl({idSet: [avatarFileId!]}).then(res => {

                    appDispatch(setUserSelfAvatarUrl(res.data.map![avatarFileId] || ''))

                })

            }

            SetTenantIdToStorage(res.data.tenantId!); // 更新为：最新的租户 id

            if (callBack) {

                callBack(res.data)

            }

        })

    }, [])

}

// 控制台：按键触发
export function UseEffectConsoleOpenKeydownListener() {

    useEffect(() => {

        const consoleOpenFlag = localStorage.getItem(LocalStorageKey.CONSOLE_OPEN_FLAG);

        if (consoleOpenFlag === '1') {
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
