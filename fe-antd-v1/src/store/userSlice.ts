import {SysMenuDO} from "@/api/http/SysMenu";
import {UserSelfInfoVO} from "@/api/http/UserSelf";
import LocalStorageKey from "@/model/constant/LocalStorageKey";
import {createSlice, PayloadAction} from '@reduxjs/toolkit'
import {MyLocalStorage} from "@/util/StorageUtil.ts";

interface IUserSlice {

    userSelfMenuList: SysMenuDO[] // 用户菜单

    userSelfMenuListLoadFlag: boolean // 是否：加载过菜单

    userSelfInfo: UserSelfInfoVO // 当前用户，基本信息

    userSelfInfoLoadFlag: boolean // 是否：当前用户基本信息

    userSelfAvatarUrl: string // 当前用户，头像链接

}

const initialState: IUserSlice = {

    userSelfMenuList: JSON.parse(MyLocalStorage.getItem(LocalStorageKey.USER_SELF_MENU_LIST) || "[]"),

    userSelfMenuListLoadFlag: false,

    userSelfInfo: JSON.parse(MyLocalStorage.getItem(LocalStorageKey.USER_SELF_INFO) || "{}"),

    userSelfInfoLoadFlag: false,

    userSelfAvatarUrl: MyLocalStorage.getItem(LocalStorageKey.USER_SELF_AVATAR_URL) || ''

}

export const userSlice = createSlice({

    name: 'userSlice',

    initialState,

    reducers: {

        setUserSelfMenuList: (state, action: PayloadAction<SysMenuDO[]>) => {

            state.userSelfMenuList = action.payload

            state.userSelfMenuListLoadFlag = true

            MyLocalStorage.setItem(LocalStorageKey.USER_SELF_MENU_LIST, JSON.stringify(action.payload))

        },

        setUserSelfMenuListLoadFlag: (state, action: PayloadAction<boolean>) => {

            state.userSelfMenuListLoadFlag = action.payload

        },

        setUserSelfInfo: (state, action: PayloadAction<UserSelfInfoVO>) => {

            state.userSelfInfo = action.payload

            MyLocalStorage.setItem(LocalStorageKey.USER_SELF_INFO, JSON.stringify(action.payload))

        },

        setUserSelfInfoLoadFlag: (state, action: PayloadAction<boolean>) => {

            state.userSelfInfoLoadFlag = action.payload

        },

        setUserSelfAvatarUrl: (state, action: PayloadAction<string>) => {

            state.userSelfAvatarUrl = action.payload

            MyLocalStorage.setItem(
                LocalStorageKey.USER_SELF_AVATAR_URL,
                action.payload
            )

        },

        signOut: (state) => {

            // 退出登录
            state.userSelfMenuList = []
            state.userSelfMenuListLoadFlag = false
            state.userSelfInfo = {}
            state.userSelfInfoLoadFlag = false
            state.userSelfAvatarUrl = ""

        }

    },

})

export const {
    setUserSelfMenuList,
    setUserSelfInfo,
    setUserSelfAvatarUrl,
    signOut,
    setUserSelfMenuListLoadFlag,
    setUserSelfInfoLoadFlag
} = userSlice.actions
export const {} = userSlice.actions

export default userSlice.reducer
