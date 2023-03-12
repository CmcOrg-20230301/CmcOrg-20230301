import {createSlice} from '@reduxjs/toolkit'

interface ICommonSlice {
}

const initialState: ICommonSlice = {}

export const commonSlice = createSlice({

    name: 'commonSlice',
    initialState,
    reducers: {},

})

export const {} = commonSlice.actions

export default commonSlice.reducer
