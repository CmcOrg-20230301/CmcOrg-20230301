import React, {useRef} from "react";
import {BetaSchemaForm, FormInstance} from "@ant-design/pro-components";
import {ExecConfirm, ToastSuccess} from "@/util/ToastUtil";
import {SysUserConfigurationInsertOrUpdateDTO} from "@/api/http/UserConfiguration";
import {YesNoDict} from "@/util/DictUtil";
import {SysUserConfigurationInfoById, SysUserConfigurationInsertOrUpdate} from "@/api/http/SysUserConfiguration";

// 用户配置
export default function () {

    const formRef = useRef<FormInstance<SysUserConfigurationInsertOrUpdateDTO>>();

    return (

        <>

            <BetaSchemaForm<SysUserConfigurationInsertOrUpdateDTO>

                formRef={formRef}

                isKeyPressSubmit

                layout={"horizontal"}

                request={async () => {

                    formRef.current?.resetFields()

                    SysUserConfigurationInfoById().then(res => {

                        formRef.current?.setFieldsValue(res as SysUserConfigurationInsertOrUpdateDTO)

                    });

                    return {}

                }}


                onFinish={(form) => {

                    return new Promise<boolean>(resolve => {

                        ExecConfirm(() => {

                            return SysUserConfigurationInsertOrUpdate(
                                form).then(res => {

                                ToastSuccess(res.msg)

                                resolve(true)

                            }).catch(() => {

                                resolve(true)

                            })

                        }, async () => {

                            resolve(true)

                        }, "确定提交用户配置吗？")

                    })

                }}

                columns={[

                    {
                        title: '用户名注册',
                        dataIndex: 'signInNameSignUpEnable',
                        valueEnum: YesNoDict,
                        valueType: 'switch',
                        formItemProps: {
                            rules: [
                                {
                                    required: true
                                }
                            ]
                        },
                    },

                    {
                        title: '邮箱注册',
                        dataIndex: 'emailSignUpEnable',
                        valueEnum: YesNoDict,
                        valueType: 'switch',
                        formItemProps: {
                            rules: [
                                {
                                    required: true
                                }
                            ]
                        },
                    },

                    {
                        title: '手机号注册',
                        dataIndex: 'phoneSignUpEnable',
                        valueEnum: YesNoDict,
                        valueType: 'switch',
                        formItemProps: {
                            rules: [
                                {
                                    required: true
                                }
                            ]
                        },
                    },

                ]}

            />

        </>

    )

}
