import {getApp} from "@/MyApp";

export function ToastSuccess(msg: string, duration: number = 5) {

    getApp().message.success(msg, duration);

}

export function ToastInfo(msg: string, duration: number = 5) {

    getApp().message.info(msg, duration);

}

export function ToastWarning(msg: string, duration: number = 5) {

    getApp().message.warning(msg, duration);

}

export function ToastError(msg: string, duration: number = 5) {

    getApp().message.error(msg, duration);

}

// 注意：【confirmFun】和【cancelFun】，如果是 http请求，则需要 return http 请求，如果不是 Promise，则在方法前面加 async，即可
export function ExecConfirm(
    confirmFun: () => Promise<void>,
    cancelFun?: () => Promise<void>,
    msg?: string
) {

    getApp().modal.confirm({

        title: '提示',
        content: msg || '确定执行操作？',
        maskClosable: false,
        closable: true,

        onOk: () => {

            return new Promise<void>(async resolve => {

                if (confirmFun) {

                    return await confirmFun()
                        .then(() => resolve())
                        .catch(() => resolve())

                }

                return resolve() // 关闭 confirm弹窗

            })

        },

        onCancel() {

            return new Promise<void>(async resolve => {

                if (cancelFun) {

                    return await cancelFun()
                        .then(() => resolve())
                        .catch(() => resolve())

                }

                return resolve() // 关闭 confirm弹窗

            })

        },

    })

}
