import React, {useRef} from "react";
import CommonConstant from "@/model/constant/CommonConstant.ts";
import {ExecConfirm} from "@/util/ToastUtil.ts";
import {NotNullIdAndStringValue} from "@/api/http/SysUserWalletWithdrawLog.ts";
import {BetaSchemaForm, FormInstance} from "@ant-design/pro-components";
import {Button} from "antd";

interface IRejectModal {

    trigger?: JSX.Element

    onFinish?: (form: NotNullIdAndStringValue) => Promise<any>

}

// 拒绝弹窗
export default function (props: IRejectModal) {

    const formRef = useRef<FormInstance<NotNullIdAndStringValue>>();

    return (

        <BetaSchemaForm<NotNullIdAndStringValue>

            trigger={props.trigger || <a className={"red3"}>拒绝</a>}

            title={'拒绝原因'}
            layoutType={"ModalForm"}

            modalProps={{
                maskClosable: false,
                destroyOnClose: true,
            }}

            formRef={formRef}

            isKeyPressSubmit

            width={CommonConstant.MODAL_FORM_WIDTH}

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

                return {}

            }}

            columns={[

                {
                    title: '拒绝原因',
                    dataIndex: 'value',
                    valueType: 'textarea',
                    formItemProps: {
                        rules: [
                            {
                                whitespace: true,
                                max: 300,
                                required: true,
                            },
                        ],
                    },
                    fieldProps: {
                        showCount: true,
                        maxLength: 300,
                        allowClear: true,
                    }
                },

            ]}

            onFinish={async (form) => {

                if (props.onFinish) {

                    await props.onFinish(form)

                }

                return true

            }}

        />

    )

}