import {BrowserWindow, ipcMain, IpcMainEvent} from "electron"
import {TElectronChannel} from "./channel"

// electron
export default (mainWindow: BrowserWindow) => {

    // 发送：是否是最大化
    mainWindow.on('resize', () => {
        mainWindowWebContentsSend(mainWindow, 'electron:isMaximized', mainWindow.isMaximized())
    })

    // 发送：是否是最大化
    ipcMainOn('electron:isMaximized', (event, data) => {
        eventReply(event, 'electron:isMaximized', mainWindow.isMaximized())
    })

    // 最小化
    ipcMainOn('electron:minimize', (event, data) => {
        mainWindow.minimize()
    })

    // 最大化
    ipcMainOn('electron:maximize', (event, data) => {
        mainWindow.maximize()
    })

    // 取消最大化
    ipcMainOn('electron:unmaximize', (event, data) => {
        mainWindow.unmaximize()
    })

    // 关闭
    ipcMainOn('electron:close', (event, data) => {
        mainWindow.close()
    })

}

// 给 ipcMain 添加 listener
export function ipcMainOn(channel: TElectronChannel, listener: (event: IpcMainEvent, data?: any) => void) {

    ipcMain.on(channel, listener)

}

// mainWindow.webContents.send，作用类似于：event.reply
export function mainWindowWebContentsSend(mainWindow: BrowserWindow, channel: TElectronChannel, data?: any) {

    mainWindow.webContents.send(channel, data)

}

// event.reply
export function eventReply(event: IpcMainEvent, channel: TElectronChannel, data: any) {

    event.reply(channel, data)

}
