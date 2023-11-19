import {BetaSchemaForm, FormInstance, ProFormColumnsType} from "@ant-design/pro-components";
import {
    SysUserBankCardDictListOpenBankName,
    SysUserBankCardDO,
    SysUserBankCardInfoById,
    SysUserBankCardInfoByIdUserSelf,
    SysUserBankCardInsertOrUpdate,
    SysUserBankCardInsertOrUpdateUserSelf,
    SysUserBankCardInsertOrUpdateUserSelfDTO
} from "@/api/http/SysUserBankCard";
import {Validate} from "@/util/ValidatorUtil";
import {DoGetDictList} from "@/util/DictUtil";
import React, {useRef} from "react";
import {Button} from "antd";
import {ExecConfirm, ToastSuccess} from "@/util/ToastUtil";
import {SysTenantBankCardInfoById, SysTenantBankCardInsertOrUpdateTenant} from "@/api/http/SysTenantBankCard";
import {BindUserBankCardModalTitle, UpdateUserBankCardModalTitle} from "@/page/user/Wallet/UserWallet";

/**
 * 用户银行卡基础 form字段集合
 */
export const UserBankCardFormBaseColumnArr: ProFormColumnsType<SysUserBankCardInsertOrUpdateUserSelfDTO>[] = [

    {
        title: '银行卡号',
        dataIndex: 'bankCardNo',
        formItemProps: {
            rules: [
                {
                    required: true,
                    whitespace: true,
                    validator: Validate.bankDebitCard.validator
                },
            ],
        },
    },

    {
        title: '开户行',
        dataIndex: 'openBankName',
        fieldProps: {
            allowClear: true,
            showSearch: true,
        },
        formItemProps: {
            rules: [
                {
                    required: true,
                },
            ],
        },
        valueType: 'select',
        request: () => {
            return DoGetDictList(SysUserBankCardDictListOpenBankName())
        },
    },

    {
        title: '支行',
        dataIndex: 'branchBankName',
        formItemProps: {
            rules: [
                {
                    required: true,
                    whitespace: true,
                },
            ],
        },
        tooltip: '请正确填写，填写错误将导致打款失败'
    },

    {
        title: '收款人姓名',
        dataIndex: 'payeeName',
        formItemProps: {
            rules: [
                {
                    required: true,
                    whitespace: true,
                },
            ],
        },
    },

]

interface IUserBankCardModal {

    sysUserBankCardDO: SysUserBankCardDO

    UpdateSysUserBankCardDO: () => void

    tenantId?: string // 租户 id，备注：如果传递了，则表示是管理租户的钱包，备注：租户 id和用户 id只会传递一个

    userId?: string // 用户 id，备注：如果传递了，则表示是管理用户的钱包，备注：租户 id和用户 id只会传递一个

}

// 用户银行卡
export default function (props: IUserBankCardModal) {

    const formRef = useRef<FormInstance<SysUserBankCardInsertOrUpdateUserSelfDTO>>();

    const currentForm = useRef<SysUserBankCardInsertOrUpdateUserSelfDTO>({} as SysUserBankCardInsertOrUpdateUserSelfDTO)

    return (

        <BetaSchemaForm<SysUserBankCardInsertOrUpdateUserSelfDTO>

            trigger={
                <a>{props.sysUserBankCardDO.bankCardNo ? UpdateUserBankCardModalTitle : BindUserBankCardModalTitle}</a>}

            title={props.sysUserBankCardDO.bankCardNo ? UpdateUserBankCardModalTitle : BindUserBankCardModalTitle}
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
                destroyOnClose: true,
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

                if (props.tenantId) {

                    SysTenantBankCardInfoById({value: props.tenantId}).then(res => {

                        currentForm.current = res as SysUserBankCardInsertOrUpdateUserSelfDTO

                        formRef.current?.setFieldsValue(currentForm.current)

                    })

                } else if (props.userId) {

                    SysUserBankCardInfoById({value: props.userId}).then(res => {

                        currentForm.current = res as SysUserBankCardInsertOrUpdateUserSelfDTO

                        formRef.current?.setFieldsValue(currentForm.current)

                    })

                } else {

                    SysUserBankCardInfoByIdUserSelf().then(res => {

                        currentForm.current = res as SysUserBankCardInsertOrUpdateUserSelfDTO

                        formRef.current?.setFieldsValue(currentForm.current)

                    })

                }

                return {}

            }}

            columns={

                UserBankCardFormBaseColumnArr

            }

            onFinish={async (form) => {

                if (props.tenantId) {

                    await SysTenantBankCardInsertOrUpdateTenant({
                        ...currentForm.current, ...form,
                        tenantId: props.tenantId
                    }).then(res => {

                        ToastSuccess(res.msg)

                        props.UpdateSysUserBankCardDO() // 更新：银行卡信息

                    })

                } else if (props.userId) {

                    await SysUserBankCardInsertOrUpdate({
                        ...currentForm.current, ...form,
                        id: props.userId
                    }).then(res => {

                        ToastSuccess(res.msg)

                        props.UpdateSysUserBankCardDO() // 更新：银行卡信息

                    })

                } else {

                    await SysUserBankCardInsertOrUpdateUserSelf({...currentForm.current, ...form}).then(res => {

                        ToastSuccess(res.msg)

                        props.UpdateSysUserBankCardDO() // 更新：银行卡信息

                    })

                }

                return true

            }}

        />

    )

}
