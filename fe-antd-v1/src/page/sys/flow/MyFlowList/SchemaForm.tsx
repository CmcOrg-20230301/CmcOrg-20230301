import CommonConstant from "@/model/constant/CommonConstant.ts";
import {
    SysActivitiParamItemTypeEnum,
    SysActivitiParamItemTypeEnumDict
} from "@/model/enum/SysActivitiParamItemTypeEnum.ts";
import {SysActivitiDeployInsertOrUpdateVO, SysActivitiProcessInstanceInsertOrUpdate} from "@/api/http/SysActiviti.ts";
import {ToastSuccess} from "@/util/ToastUtil.ts";
import {BetaSchemaForm, FormInstance} from "@ant-design/pro-components";
import React, {useRef} from "react";

export interface ISchemaForm {

    modalVisit?: boolean
    setModalVisit?: React.Dispatch<React.SetStateAction<boolean>>

    processDefinitionId?: string | null // 流程定义 id

    preFun?: () => Promise<SysActivitiDeployInsertOrUpdateVO | null>

    callBack?: (processInstanceId?: string) => void

}

export interface IVariableMap {

    inputType?: number

    inputValue?: string

}

// 执行流程
export default function (props: ISchemaForm) {

    const formRef = useRef<FormInstance<IVariableMap>>();

    return <>

        <BetaSchemaForm<IVariableMap>

            open={props.modalVisit}

            onOpenChange={props.setModalVisit}

            title={'启动参数'}

            layoutType={"ModalForm"}

            modalProps={{
                maskClosable: false,
                destroyOnClose: true,
            }}

            formRef={formRef}

            isKeyPressSubmit

            width={CommonConstant.MODAL_FORM_WIDTH}

            params={new Date()} // 目的：为了打开页面时，执行 request方法

            request={async () => {

                return {}

            }}

            columns={[

                {
                    title: '类型',
                    dataIndex: 'inputType',
                    valueEnum: SysActivitiParamItemTypeEnumDict,
                    formItemProps: {
                        rules: [
                            {
                                required: true,
                            },
                        ],
                    },
                    fieldProps: {
                        allowClear: true,
                        showSearch: true,
                    },
                    initialValue: SysActivitiParamItemTypeEnum.TEXT.code
                },

                {
                    title: '参数',
                    dataIndex: 'inputValue',
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

            onFinish={async (form: IVariableMap) => {

                let sysActivitiDeployInsertOrUpdateVO: SysActivitiDeployInsertOrUpdateVO | null = null

                if (props.preFun) {

                    sysActivitiDeployInsertOrUpdateVO = await props.preFun();

                }

                await SysActivitiProcessInstanceInsertOrUpdate({

                    processDefinitionId: sysActivitiDeployInsertOrUpdateVO?.processDefinitionId || props.processDefinitionId!,
                    variableMap: form

                }).then(res => {

                    ToastSuccess('执行成功')

                    if (props.callBack) {

                        props.callBack(res.data)

                    }

                })

                return true

            }}

        />

    </>

}