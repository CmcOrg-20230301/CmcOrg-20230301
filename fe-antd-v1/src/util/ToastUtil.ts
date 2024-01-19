import {GetApp} from "@/MyApp";
import React from "react";

export function ToastSuccess(msg: React.ReactNode, duration: number = 5) {

    GetApp().message.success(msg, duration);

}

export function ToastInfo(msg: React.ReactNode, duration: number = 5) {

    if (!msg) {
        return
    }

    GetApp().message.info(msg, duration);

}

export function ToastWarning(msg: React.ReactNode, duration: number = 5) {

    if (!msg) {
        return
    }

    GetApp().message.warning(msg, duration);

}

export function ToastError(msg: React.ReactNode, duration: number = 5) {

    if (!msg) {
        return
    }

    GetApp().message.error(msg, duration);

}

// 注意：【confirmFun】和【cancelFun】，如果是 http请求，则需要 return http 请求，如果不是 Promise，则在方法前面加 async，即可
export function ExecConfirm(
    confirmFun: () => Promise<any>,
    cancelFun?: () => Promise<any>,
    msg?: React.ReactNode,
    res?: (value?: (PromiseLike<any> | any)) => void,
    rej?: (reason?: any) => void
) {

    GetApp().modal.confirm({

        title: '提示',
        content: msg || '确定执行操作？',
        maskClosable: false,
        closable: true,

        onOk: () => {

            return new Promise<void>(async resolve => {

                if (confirmFun) {

                    return await confirmFun()
                        .then(() => {
                            resolve() // 关闭
                            if (res) {
                                res()
                            }
                        })
                        .catch(() => {
                            resolve() // 关闭
                            if (res) {
                                res()
                            }
                        })

                }

                resolve() // 关闭
                if (res) {
                    res()
                }

            })

        },

        onCancel() {

            return new Promise<void>(async resolve => {

                if (cancelFun) {

                    return await cancelFun()
                        .then(() => {
                            resolve() // 关闭
                            if (rej) {
                                rej()
                            }
                        })
                        .catch(() => {
                            resolve() // 关闭
                            if (rej) {
                                rej()
                            }
                        })

                }

                resolve() // 关闭
                if (rej) {
                    rej()
                }

            })

        },

    })

}

// 注意：【confirmFun】和【cancelFun】，如果是 http请求，则需要 return http 请求，如果不是 Promise，则在方法前面加 async，即可
export function ExecConfirmPromise(
    confirmFun: () => Promise<void>,
    cancelFun?: () => Promise<void>,
    msg?: React.ReactNode
) {

    return new Promise<any>((res, rej) => {

        ExecConfirm(confirmFun, cancelFun, msg, res, rej)

    })

}
