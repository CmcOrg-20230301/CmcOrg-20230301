import React, {ReactNode, useRef, useState} from "react";
import {
    NotBlankCodeDTO,
    SignEmailBindAccount,
    SignEmailBindAccountDTO,
    SignEmailBindAccountSendCode,
    SignEmailSignDelete,
    SignEmailSignDeleteSendCode,
    SignEmailUpdateAccount,
    SignEmailUpdateAccountDTO,
    SignEmailUpdateAccountSendCode,
    SignEmailUpdatePassword,
    SignEmailUpdatePasswordDTO,
    SignEmailUpdatePasswordSendCode
} from "@/api//SignEmail";
import {useAppSelector} from "@/store";
import {List, Modal} from "antd";
import {ValidatorUtil} from "@/util/ValidatorUtil";
import {ToastSuccess} from "@/util/ToastUtil";
import {
    ModalForm,
    ProFormCaptcha,
    ProFormInstance,
    ProFormText,
    ProTable,
    RouteContext,
    RouteContextType
} from "@ant-design/pro-components";
import CommonConstant from "@/model/constant/CommonConstant";
import {SignOut} from "@/util/UserUtil";
import {USER_CENTER_KEY_TWO} from "@/page/user/Self/Self";
import Title from "antd/es/typography/Title";
import {
    SignSignInNameSignDelete,
    SignSignInNameSignDeleteDTO,
    SignSignInNameUpdateAccount,
    SignSignInNameUpdateAccountDTO,
    SignSignInNameUpdatePassword,
    SignSignInNameUpdatePasswordDTO
} from "@/api/SignSignInName";
import {PasswordRSAEncrypt, RSAEncryptPro} from "@/util/RsaUtil";
import {SysRequestDO, SysRequestPageDTO, SysRequestSelfLoginRecord} from "@/api/SysRequest";
import {HandlerRegion} from "@/util/StrUtil";
import {GetDictListByKey} from "@/util/DictUtil";

interface IUserSelfSetting {

    title: string
    description?: string
    actions: ReactNode[];

}

const RequestSelfLoginRecordModalTitle = "登录记录"
const SetSignNameAccountModalTitle = "设置登录名"
const UpdateSignNameAccountModalTitle = "修改登录名"
const SetEmailAccountModalTitle = "设置邮箱"
const UpdateEmailAccountModalTitle = "修改邮箱"
const UserSelfDeleteModalTitle = "账号注销"
const UserSelfDeleteModalTargetName = "立即注销"
const UserSelfUpdatePasswordTitle = "修改密码"

// 账号设置
export default function () {

    const userSelfInfo = useAppSelector((state) => state.user.userSelfInfo)

    return (

        <List<IUserSelfSetting>

            header={<Title level={5}>{USER_CENTER_KEY_TWO}</Title>}
            rowKey={"title"}

            dataSource={[

                {
                    title: '密码',
                    actions: [
                        userSelfInfo.email ? <UserSelfUpdatePasswordByCodeModalForm/> :
                            <UserSelfUpdatePasswordByPasswordModalForm/>
                    ]
                },

                {
                    title: '登录名',
                    description: userSelfInfo.signInName || '暂无',
                    actions: [
                        <UpdateSignNameAccountModalForm/>
                    ]
                },

                {
                    title: '邮箱',
                    description: userSelfInfo.email || '暂无',
                    actions: [
                        userSelfInfo.email ? <UpdateEmailAccountModalForm/> : <SetEmailAccountModalForm/>
                    ]
                },

                {
                    title: RequestSelfLoginRecordModalTitle,
                    actions: [
                        <RequestSelfLoginRecordModal key={"1"}/>
                    ]
                },

                {
                    title: UserSelfDeleteModalTitle,
                    description: userSelfInfo.createTime ? ('注册时间：' + userSelfInfo.createTime) : undefined,
                    actions: [
                        userSelfInfo.email ? <UserSelfDeleteByCodeModalForm/> : <UserSelfDeleteByPasswordModalForm/>
                    ]
                },

            ]}

            renderItem={item => (

                <List.Item actions={item.actions}>

                    <List.Item.Meta
                        title={item.title}
                        description={item.description}
                    />

                </List.Item>

            )}

        />

    )

}

// 设置邮箱：通过：邮箱验证码
export function SetEmailAccountModalForm() {

    const formRef = useRef<ProFormInstance<SignEmailBindAccountDTO>>();

    return <ModalForm<SignEmailBindAccountDTO>

        formRef={formRef}
        modalProps={{
            maskClosable: false
        }}

        isKeyPressSubmit
        width={CommonConstant.MODAL_FORM_WIDTH}
        title={SetEmailAccountModalTitle}
        trigger={<a>{SetEmailAccountModalTitle}</a>}
        onFinish={async (form) => {

            await SignEmailBindAccount(form).then(res => {

                SignOut()
                ToastSuccess(res.msg)

            })

            return true

        }}
    >

        <ProFormText
            name="email"
            fieldProps={{
                allowClear: true,
            }}
            required
            label="邮箱"
            placeholder={'请输入邮箱'}
            rules={[
                {
                    validator: ValidatorUtil.emailValidate
                }
            ]}
        />

        <ProFormCaptcha
            fieldProps={{
                maxLength: 6,
                allowClear: true,
            }}
            required
            label="验证码"
            name="code"
            placeholder={"请输入验证码"}
            rules={[{validator: ValidatorUtil.codeValidate}]}
            onGetCaptcha={async () => {

                await formRef.current?.validateFields(['email']).then(async res => {

                    await SignEmailBindAccountSendCode({email: res.email}).then(res => {

                        ToastSuccess(res.msg)

                    })

                })

            }}
        />

    </ModalForm>

}

// 修改邮箱：通过：邮箱验证码
export function UpdateEmailAccountModalForm() {

    const formRef = useRef<ProFormInstance<SignEmailUpdateAccountDTO>>();

    return <ModalForm<SignEmailUpdateAccountDTO>

        formRef={formRef}
        modalProps={{
            maskClosable: false
        }}

        isKeyPressSubmit
        width={CommonConstant.MODAL_FORM_WIDTH}
        title={UpdateEmailAccountModalTitle}
        trigger={<a>{UpdateEmailAccountModalTitle}</a>}
        onFinish={async (form) => {

            await SignEmailUpdateAccount(form).then(res => {

                SignOut()
                ToastSuccess(res.msg)

            })

            return true

        }}

    >

        <ProFormText

            name="newEmail"
            fieldProps={{
                allowClear: true,
            }}
            required
            label="新邮箱"
            placeholder={'请输入新邮箱'}

            rules={[
                {
                    validator: ValidatorUtil.emailValidate
                }
            ]}

        />

        <ProFormCaptcha

            fieldProps={{
                maxLength: 6,
                allowClear: true,
            }}

            required
            label="新邮箱验证码"
            name="newEmailCode"
            placeholder={"请输入新邮箱验证码"}
            rules={[{validator: ValidatorUtil.codeValidate}]}

            onGetCaptcha={async () => {

                await formRef.current?.validateFields(['newEmail']).then(async res => {

                    await SignEmailBindAccountSendCode({email: res.newEmail}).then(res => {

                        ToastSuccess(res.msg)

                    })

                })

            }}

        />

        <ProFormCaptcha

            fieldProps={{
                maxLength: 6,
                allowClear: true,
            }}

            required
            label="旧邮箱验证码"
            name="oldEmailCode"
            placeholder={"请输入旧邮箱验证码"}
            rules={[{validator: ValidatorUtil.codeValidate}]}

            onGetCaptcha={async () => {

                await SignEmailUpdateAccountSendCode().then(res => {

                    ToastSuccess(res.msg)

                })

            }}

        />

    </ModalForm>

}

// 设置登录名：通过：密码
export function UpdateSignNameAccountModalForm() {

    const userSelfInfo = useAppSelector((state) => state.user.userSelfInfo)

    return <ModalForm<SignSignInNameUpdateAccountDTO>

        modalProps={{
            maskClosable: false
        }}

        isKeyPressSubmit

        width={CommonConstant.MODAL_FORM_WIDTH}

        title={userSelfInfo.signInName ? UpdateSignNameAccountModalTitle : SetSignNameAccountModalTitle}

        trigger={<a>{userSelfInfo.signInName ? UpdateSignNameAccountModalTitle : SetSignNameAccountModalTitle}</a>}

        onFinish={async (form) => {

            form.currentPassword = PasswordRSAEncrypt(form.currentPassword!)

            await SignSignInNameUpdateAccount(form).then(res => {

                SignOut()
                ToastSuccess(res.msg)

            })

            return true

        }}

    >

        <ProFormText

            name="newSignInName"
            fieldProps={{
                allowClear: true,
            }}
            required
            label="新登录名"
            placeholder={'请输入新登录名'}
            rules={[
                {
                    validator: ValidatorUtil.signInNameValidate
                }
            ]}

        />

        <ProFormText.Password

            fieldProps={{
                allowClear: true,
            }}
            label="当前密码"
            name="currentPassword"
            rules={[{
                required: true,
            }]}

        />

    </ModalForm>

}

// 用户修改密码：通过：旧密码
export function UserSelfUpdatePasswordByPasswordModalForm() {

    return <ModalForm<SignSignInNameUpdatePasswordDTO>

        modalProps={{
            maskClosable: false
        }}

        isKeyPressSubmit

        width={CommonConstant.MODAL_FORM_WIDTH}
        title={UserSelfUpdatePasswordTitle}

        trigger={<a>{UserSelfUpdatePasswordTitle}</a>}
        onFinish={async (form) => {

            form.oldPassword = PasswordRSAEncrypt(form.oldPassword!)
            form.originNewPassword = RSAEncryptPro(form.newPassword!)
            form.newPassword = PasswordRSAEncrypt(form.newPassword!)

            await SignSignInNameUpdatePassword(form).then(res => {

                SignOut()
                ToastSuccess(res.msg)

            })

            return true

        }}

    >

        <ProFormText

            label="旧密码"
            placeholder={'请输入旧密码'}
            name="oldPassword"
            fieldProps={{
                allowClear: true,
            }}
            rules={[{required: true,},]}

        />

        <ProFormText

            label="新密码"
            placeholder={'请输入新密码'}
            name="newPassword"
            required
            fieldProps={{
                allowClear: true,
            }}
            rules={[{validator: ValidatorUtil.passwordValidate}]}

        />

    </ModalForm>

}

// 用户修改密码：通过：发送验证码
export function UserSelfUpdatePasswordByCodeModalForm() {

    return <ModalForm<SignEmailUpdatePasswordDTO>

        modalProps={{
            maskClosable: false
        }}

        isKeyPressSubmit

        width={CommonConstant.MODAL_FORM_WIDTH}
        title={UserSelfUpdatePasswordTitle}
        trigger={<a>{UserSelfUpdatePasswordTitle}</a>}

        onFinish={async (form) => {

            form.originNewPassword = RSAEncryptPro(form.newPassword!)
            form.newPassword = PasswordRSAEncrypt(form.newPassword!)

            await SignEmailUpdatePassword(form).then(res => {

                SignOut()
                ToastSuccess(res.msg)

            })

            return true

        }}

    >

        <ProFormCaptcha

            fieldProps={{
                maxLength: 6,
                allowClear: true,
            }}
            required
            label="验证码"
            placeholder={'请输入验证码'}
            name="code"
            rules={[{validator: ValidatorUtil.codeValidate}]}

            onGetCaptcha={async () => {

                await SignEmailUpdatePasswordSendCode().then(res => {
                    ToastSuccess(res.msg)
                })

            }}

        />

        <ProFormText

            label="新密码"
            placeholder={'请输入新密码'}
            name="newPassword"
            required
            fieldProps={{
                allowClear: true,
            }}
            rules={[{validator: ValidatorUtil.passwordValidate}]}

        />

    </ModalForm>

}

// 账号注销：通过：密码
export function UserSelfDeleteByPasswordModalForm() {

    return (

        <ModalForm<SignSignInNameSignDeleteDTO>

            modalProps={{
                maskClosable: false
            }}
            isKeyPressSubmit

            width={CommonConstant.MODAL_FORM_WIDTH}
            title={UserSelfDeleteModalTitle}
            trigger={<a className={"red3"}>{UserSelfDeleteModalTargetName}</a>}

            onFinish={async (form) => {

                const currentPassword = PasswordRSAEncrypt(form.currentPassword!)

                await SignSignInNameSignDelete({currentPassword}).then(res => {

                    SignOut()
                    ToastSuccess(res.msg)

                })

                return true

            }}

        >

            <ProFormText.Password
                fieldProps={{
                    allowClear: true,
                }}
                placeholder={'请输入当前密码'}
                label="当前密码"
                name="currentPassword"
                rules={[{
                    required: true,
                }]}
            />

        </ModalForm>

    )

}

// 账号注销：通过：发送验证码
export function UserSelfDeleteByCodeModalForm() {

    return (

        <ModalForm<NotBlankCodeDTO>

            modalProps={{
                maskClosable: false
            }}
            isKeyPressSubmit

            width={CommonConstant.MODAL_FORM_WIDTH}
            title={UserSelfDeleteModalTitle}
            trigger={<a className={"red3"}>{UserSelfDeleteModalTargetName}</a>}

            onFinish={async (form) => {

                await SignEmailSignDelete({code: form.code}).then(res => {

                    SignOut()
                    ToastSuccess(res.msg)

                })

                return true

            }}

        >

            <ProFormCaptcha

                fieldProps={{
                    maxLength: 6,
                    allowClear: true,
                }}

                required
                label="验证码"
                name="code"
                placeholder={"请输入验证码"}
                rules={[{validator: ValidatorUtil.codeValidate}]}

                onGetCaptcha={async () => {

                    await SignEmailSignDeleteSendCode().then(res => {

                        ToastSuccess(res.msg)

                    })

                }}

            />

        </ModalForm>

    )

}

// 登录记录
function RequestSelfLoginRecordModal() {

    const [open, setOpen] = useState(false);

    return (

        <RouteContext.Consumer>

            {(routeContextType: RouteContextType) => {

                return <>

                    <a onClick={() => {
                        setOpen(true)
                    }}>查看记录</a>

                    <Modal

                        width={1200}

                        title={RequestSelfLoginRecordModalTitle}

                        onCancel={() => setOpen(false)}

                        open={open}

                        maskClosable={false}

                        footer={false}

                        className={"noFooterModal"}

                    >

                        <ProTable<SysRequestDO, SysRequestPageDTO>

                            rowKey={"id"}

                            columnEmptyText={false}

                            revalidateOnFocus={false}

                            scroll={{y: 440}}

                            search={{
                                filterType: 'light',
                            }}

                            columns={[

                                {
                                    title: '序号',
                                    dataIndex: 'index',
                                    valueType: 'index',
                                    width: 50,
                                },

                                {
                                    title: '创建时间',
                                    dataIndex: 'createTime',
                                    sorter: true,
                                    valueType: 'fromNow',
                                    ellipsis: true,
                                    hideInSearch: true,
                                    width: 90,
                                },

                                {title: 'ip', dataIndex: 'ip', width: 120, ellipsis: true,},

                                {
                                    title: 'ip位置',
                                    dataIndex: 'region',
                                    ellipsis: true,
                                    copyable: true,
                                    width: 180,
                                    renderText: (text) => {
                                        return HandlerRegion(text)
                                    }
                                },

                                {
                                    title: '来源',
                                    dataIndex: 'category',
                                    valueType: 'select',
                                    width: 120,
                                    ellipsis: true,
                                    fieldProps: {
                                        showSearch: true,
                                    },
                                    request: () => {
                                        return GetDictListByKey('sys_request_category')
                                    }
                                },

                            ]}

                            pagination={{
                                showQuickJumper: true,
                                showSizeChanger: true,
                            }}

                            options={{
                                fullScreen: true,
                            }}

                            request={(params, sort, filter) => {
                                return SysRequestSelfLoginRecord({...params, sort})
                            }}

                        >

                        </ProTable>

                    </Modal>

                </>

            }}

        </RouteContext.Consumer>

    )

}
