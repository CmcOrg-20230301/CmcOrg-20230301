import React, {useRef} from "react";
import {BetaSchemaForm, FormInstance, ProFormColumnsType} from "@ant-design/pro-components";
import {ExecConfirm, ToastSuccess} from "@/util/ToastUtil";
import {
    SysEmailConfigurationDO,
    SysEmailConfigurationInfoById,
    SysEmailConfigurationInsertOrUpdate,
    SysEmailConfigurationInsertOrUpdateDTO
} from "@/api/http/SysEmailConfiguration";
import {YesNoDict} from "@/util/DictUtil";
import {validate} from "@/util/ValidatorUtil";

// 邮箱配置
export default function () {

    const formRef = useRef<FormInstance<SysEmailConfigurationInsertOrUpdateDTO>>();

    return (

        <>

            <BetaSchemaForm<SysEmailConfigurationInsertOrUpdateDTO>

                formRef={formRef}

                isKeyPressSubmit

                layout={"horizontal"}

                request={async () => {

                    formRef.current?.resetFields()

                    SysEmailConfigurationInfoById().then(res => {

                        formRef.current?.setFieldsValue(res as SysEmailConfigurationInsertOrUpdateDTO)

                    });

                    return {}

                }}


                onFinish={(form) => {

                    return new Promise<boolean>(resolve => {

                        ExecConfirm(() => {

                            return SysEmailConfigurationInsertOrUpdate(
                                form).then(res => {

                                ToastSuccess(res.msg)

                                resolve(true)

                            }).catch(() => {

                                resolve(true)

                            })

                        }, async () => {

                            resolve(true)

                        }, "确定提交邮箱配置吗？")

                    })

                }}

                columns={[

                    {
                        title: '正文前缀',
                        dataIndex: 'contentPre',
                        formItemProps: {
                            rules: [
                                {
                                    required: true,
                                    whitespace: true,
                                },
                            ],
                        },
                    },

                    {

                        title: '端口',
                        dataIndex: 'port',
                        valueType: 'digit',

                        formItemProps: {
                            rules: [
                                {
                                    required: true
                                }
                            ]
                        },

                        fieldProps: {
                            className: "w100"
                        }

                    },

                    {
                        title: '发送人邮箱',
                        dataIndex: 'fromEmail',
                        formItemProps: {
                            rules: [
                                {
                                    required: true,
                                    min: 0,
                                    max: 100,
                                    pattern: validate.email.regex,
                                    message: validate.email.errorMsg,
                                },
                            ],
                        },
                    },

                    {

                        valueType: 'dependency',

                        name: ['id'],

                        columns: ({id}: SysEmailConfigurationDO): ProFormColumnsType<SysEmailConfigurationInsertOrUpdateDTO>[] => {

                            return id ?

                                [

                                    {
                                        title: '发送人密码',
                                        dataIndex: 'pass',
                                        tooltip: '不设置则不会修改密码，设置则会修改密码',
                                    },

                                ]

                                :

                                [

                                    {
                                        title: '发送人密码',
                                        dataIndex: 'pass',
                                        formItemProps: {
                                            rules: [
                                                {
                                                    required: true
                                                }
                                            ]
                                        },
                                    },

                                ]

                        }

                    },

                    {
                        title: '使用SSL',
                        dataIndex: 'sslFlag',
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
