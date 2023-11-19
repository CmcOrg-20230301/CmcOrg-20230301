import {SysUserBankCardDO} from "@/api/http/SysUserBankCard";
import {SysUserWalletDO} from "@/api/http/SysUserWallet";
import React, {useRef, useState} from "react";
import {BetaSchemaForm, FormInstance} from "@ant-design/pro-components";
import {
    SysUserWalletWithdrawLogInsertOrUpdate,
    SysUserWalletWithdrawLogInsertOrUpdateTenant,
    SysUserWalletWithdrawLogInsertOrUpdateUserSelf,
    SysUserWalletWithdrawLogInsertOrUpdateUserSelfDTO
} from "@/api/http/SysUserWalletWithdrawLog";
import {Button} from "antd";
import {CheckCircleOutlined} from "@ant-design/icons";
import {ExecConfirm, ToastError, ToastSuccess} from "@/util/ToastUtil";
import CommonConstant from "@/model/constant/CommonConstant";
import {UserWalletWithdrawModalTitle} from "@/page/user/Wallet/UserWallet";

interface IUserWalletWithdrawModal {

    sysUserBankCardDO: SysUserBankCardDO

    sysUserWalletDO: SysUserWalletDO

    UpdateSysUserWalletDO: () => void

    tenantId?: string // 租户 id，备注：如果传递了，则表示是管理租户的钱包，备注：租户 id和用户 id只会传递一个

    userId?: string // 用户 id，备注：如果传递了，则表示是管理用户的钱包，备注：租户 id和用户 id只会传递一个

}

// 提现
export default function (props: IUserWalletWithdrawModal) {

    const formRef = useRef<FormInstance<SysUserWalletWithdrawLogInsertOrUpdateUserSelfDTO>>();

    const [formOpen, setFormOpen] = useState<boolean>(false);

    const currentForm = useRef<SysUserWalletWithdrawLogInsertOrUpdateUserSelfDTO>({} as SysUserWalletWithdrawLogInsertOrUpdateUserSelfDTO)

    return (

        <>

            <Button

                type="primary"
                icon={<CheckCircleOutlined/>}
                className={"m-l-20"}
                onClick={() => {

                    if (!props.sysUserWalletDO.withdrawableMoney) {

                        ToastError('操作失败：可提现余额不足')
                        return

                    }

                    if (!props.sysUserBankCardDO.bankCardNo) {

                        ToastError('操作失败：请先绑定银行卡')
                        return

                    }

                    setFormOpen(true)

                }}

            >{UserWalletWithdrawModalTitle}</Button>

            <BetaSchemaForm<SysUserWalletWithdrawLogInsertOrUpdateUserSelfDTO>

                title={UserWalletWithdrawModalTitle}
                layoutType={"ModalForm"}
                grid

                rowProps={{
                    gutter: 16
                }}

                colProps={{
                    span: 8
                }}

                modalProps={{
                    maskClosable: false,
                }}

                formRef={formRef}

                isKeyPressSubmit

                submitter={{

                    render: (props, dom) => {

                        return [

                            ...dom,

                            <Button

                                key="1"

                                onClick={() => {

                                    ExecConfirm(async () => {

                                        props.reset();

                                    }, undefined, "确定重置表单吗？")

                                }}

                            >

                                重置

                            </Button>,

                        ]

                    },

                }}

                params={new Date()} // 目的：为了打开页面时，执行 request方法

                request={async () => {

                    formRef.current?.resetFields()

                    setTimeout(() => {

                        // @ts-ignore
                        currentForm.current.bankCardNo = props.sysUserBankCardDO.bankCardNo
                        // @ts-ignore
                        currentForm.current.openBankName = props.sysUserBankCardDO.openBankName
                        // @ts-ignore
                        currentForm.current.branchBankName = props.sysUserBankCardDO.branchBankName
                        // @ts-ignore
                        currentForm.current.payeeName = props.sysUserBankCardDO.payeeName

                        formRef.current?.setFieldsValue(currentForm.current)

                    }, CommonConstant.SHORT_DELAY)

                    return {}

                }}

                open={formOpen}
                onOpenChange={setFormOpen}

                columns={[

                    ...GetUserWalletWithdrawFormColumnArr(),

                    {
                        title: '提现金额',
                        dataIndex: 'withdrawMoney',
                        valueType: 'digit',

                        fieldProps: {
                            precision: 2, // 小数点精度
                            className: "w100",
                            addonAfter: <div>{props.sysUserWalletDO.withdrawableMoney}</div>,
                            autoFocus: true,
                        },

                        formItemProps: {
                            rules: [
                                {
                                    required: true,
                                    min: 1,
                                    max: props.sysUserWalletDO.withdrawableMoney,
                                    type: "number",
                                }
                            ]
                        }

                    },

                ]}

                onFinish={async (form) => {

                    if (props.tenantId) {

                        await SysUserWalletWithdrawLogInsertOrUpdateTenant({
                            ...currentForm.current, ...form,
                            tenantId: props.tenantId
                        }).then(res => {

                            ToastSuccess(res.msg)

                            props.UpdateSysUserWalletDO() // 更新钱包的钱

                        })

                    } else if (props.userId) {

                        await SysUserWalletWithdrawLogInsertOrUpdate({
                            ...currentForm.current, ...form,
                            userId: props.userId
                        }).then(res => {

                            ToastSuccess(res.msg)

                            props.UpdateSysUserWalletDO() // 更新钱包的钱

                        })

                    } else {

                        await SysUserWalletWithdrawLogInsertOrUpdateUserSelf({...currentForm.current, ...form,}).then(res => {

                            ToastSuccess(res.msg)

                            props.UpdateSysUserWalletDO() // 更新钱包的钱

                        })

                    }

                    return true

                }}

            />

        </>

    )

}

/**
 * 获取：用户提现记录的 form基础字段集合
 */
export function GetUserWalletWithdrawFormColumnArr() {

    return [

        {
            title: '银行卡号',
            dataIndex: 'bankCardNo',
            readonly: true,
        },

        {
            title: '开户行',
            dataIndex: 'openBankName',
            readonly: true,
        },

        {
            title: '支行',
            dataIndex: 'branchBankName',
            readonly: true,
        },

        {
            title: '收款人姓名',
            dataIndex: 'payeeName',
            readonly: true,
        },

    ]

}
