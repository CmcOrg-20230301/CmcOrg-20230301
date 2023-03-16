import {contextBridge, ipcRenderer, IpcRendererEvent} from "electron";

window.addEventListener("DOMContentLoaded", () => {
});

// 渲染进程（html）给主进程发送消息
contextBridge.exposeInMainWorld('ipcRenderer', {

    // 需要使用：ipcRenderer.on(channel, listener)，才可以拿到返回值
    send(channel: string, data?: any) {

        ipcRenderer.send(channel, data)

    },

    // 使用：.then，即可拿到返回值，备注：要使用：ipcMain.handle 才行
    invoke(channel: string, data?: any) {

        return ipcRenderer.invoke(channel, data)

    },

    on(channel: string, listener: (event: IpcRendererEvent, data?: any) => void) {

        ipcRenderer.on(channel, listener)

    },

    removeAllListeners(channel: string) {

        ipcRenderer.removeAllListeners(channel)

    }

})
