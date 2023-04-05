// import {SysMenuDO} from "@/api/admin/SysMenuController";
// import {UserSelfInfoVO} from "@/api/none/UserSelfController";
import LocalStorageKey from "@/model/constant/LocalStorageKey";

interface IUserSlice {

    // userSelfMenuList: SysMenuDO[] // 用户菜单
    // userSelfInfo: UserSelfInfoVO // 当前用户，基本信息

}

const initialState: IUserSlice = {

    userSelfMenuList: [],
    userSelfInfo: JSON.parse(
        localStorage.getItem(LocalStorageKey.USER_SELF_INFO) || '{}'
    ),

}

// function setLocalStorageUserSelfInfo(userSelfInfo: UserSelfInfoVO) {
//
//     localStorage.setItem(
//         LocalStorageKey.USER_SELF_INFO,
//         JSON.stringify(userSelfInfo)
//     )
//
// }

// export const userSlice = createSlice({

// name: 'userSlice',
// initialState,
// reducers: {

// setUserSelfMenuList: (state, action: PayloadAction<SysMenuDO[]>) => {
//     state.userSelfMenuList = action.payload
// },
//
// setUserSelfInfo: (state, action: PayloadAction<UserSelfInfoVO>) => {
//     state.userSelfInfo = action.payload
//     setLocalStorageUserSelfInfo(action.payload)
// },
//
// // 退出登录
// signOut: (state) => {
//     state.userSelfMenuList = []
//     state.userSelfInfo = {}
//     setLocalStorageUserSelfInfo({})
// }

// },

// })

// export const {setUserSelfMenuList, setUserSelfInfo, signOut} = userSlice.actions
// export const {} = userSlice.actions

// export default userSlice.reducer
