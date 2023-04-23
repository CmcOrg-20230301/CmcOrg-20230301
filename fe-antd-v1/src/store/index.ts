import {configureStore} from '@reduxjs/toolkit'
import {TypedUseSelectorHook, useDispatch, useSelector} from 'react-redux'
import commonReducer from './commonSlice'
import userReducer from './userSlice'

const store = configureStore({

    reducer: {

        common: commonReducer,
        user: userReducer,

    },

    devTools: import.meta.env.DEV,

})

export type RootState = ReturnType<typeof store.getState>
export type AppDispatch = typeof store.dispatch

export const useAppDispatch: () => AppDispatch = useDispatch
export const useAppSelector: TypedUseSelectorHook<RootState> = useSelector

export default store
