import {createSlice, PayloadAction} from '@reduxjs/toolkit'
import {IWebSocketMessage} from "@/util/webSocket/WebSocketHelper";

interface ICommonSlice {
    webSocketMessage: IWebSocketMessage<any> // webSocket消息
    webSocketStatus: boolean // webSocket连接状态
}

const initialState: ICommonSlice = {
    webSocketMessage: {} as IWebSocketMessage<any>,
    webSocketStatus: false,
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

    },

})

export const {setWebSocketMessage, setWebSocketStatus} = commonSlice.actions

export default commonSlice.reducer
