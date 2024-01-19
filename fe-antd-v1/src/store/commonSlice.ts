import {createSlice, PayloadAction} from '@reduxjs/toolkit'
import {IWebSocketMessage} from "@/util/WebSocket/WebSocketHelper";
import LocalStorageKey from "@/model/constant/LocalStorageKey.ts";

interface ICommonSlice {

    webSocketMessage: IWebSocketMessage<any> // webSocket消息

    webSocketStatus: boolean // webSocket连接状态

    tenantManageName: string // 管理系统名称

}

const initialState: ICommonSlice = {

    webSocketMessage: {} as IWebSocketMessage<any>,

    webSocketStatus: false,

    tenantManageName: localStorage.getItem(LocalStorageKey.TENANT_MANAGE_NAME) || "",

}

export const commonSlice = createSlice({

    name: 'commonSlice',

    initialState,

    reducers: {

        setWebSocketMessage(state, action: PayloadAction<IWebSocketMessage<any>>) {
            state.webSocketMessage = action.payload
        },

        setWebSocketStatus(state, action: PayloadAction<boolean>) {
            state.webSocketStatus = action.payload
        },

        setTenantManageName(state, action: PayloadAction<string>) {

            state.tenantManageName = action.payload

            localStorage.setItem(LocalStorageKey.TENANT_MANAGE_NAME, action.payload)

        },

    },

})

export const {
    setWebSocketMessage,
    setWebSocketStatus,
    setTenantManageName,
} = commonSlice.actions

export default commonSlice.reducer
