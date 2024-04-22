import {FormInstance, ProFormColumnsType} from "@ant-design/pro-components";
import {SysActivitiLineTypeEnum, SysActivitiLineTypeEnumDict} from "@/model/enum/SysActivitiLineTypeEnum.ts";
import {OptionProps} from "antd/es/mentions";
import LogicFlow from "@logicflow/core";
import {IProcessVariable, SysActivitiFunctionCallParametersBO} from "@/page/sys/flow/FlowDesign/FlowDesign.tsx";
import {FlowDesignMap} from "@/page/sys/flow/FlowDesign/FlowDesignUtil.tsx";

export interface IFormShowItem {

    hiddenId?: boolean
    showCategory?: boolean
    showCondition?: boolean
    hiddenName?: boolean
    idEditFlag?: boolean
    showRemark?: boolean
    showFunctionCall?: boolean

}

export const FunctionCallSimpleDescriptionDataIndex = "functionCallSimpleDescription"

export const DeleteDataIndexArr = [FunctionCallSimpleDescriptionDataIndex]

export const FunctionCallDataIndex = "functionCall"

export const FunctionCallParametersInitialValueDescription = "输入的内容"

export const InitFormShowItem: IFormShowItem = {

    hiddenId: true,
    showCategory: false,
    showCondition: false,
    hiddenName: false,
    idEditFlag: false,
    showRemark: true,
    showFunctionCall: false,

}

const SchemaFormColumnList = (formShowItem: IFormShowItem, processVariable: IProcessVariable, formRef: React.MutableRefObject<FormInstance | undefined>, logicFlowRef: React.MutableRefObject<LogicFlow | null>): ProFormColumnsType[] => {

    const res: ProFormColumnsType[] = []

    if (!formShowItem.hiddenId) {

        res.push({

            title: 'id',
            dataIndex: 'id',
            formItemProps: {
                rules: [
                    {
                        required: true,
                        whitespace: true,
                    },
                ],
            },
            readonly: !formShowItem.idEditFlag,

        })

    }

    if (!formShowItem.hiddenName) {

        res.push({
            title: '名称',
            dataIndex: 'name',
        })

    }

    if (formShowItem.showCategory) {

        res.push({

            title: '类型',
            dataIndex: 'category',
            valueEnum: undefined,
            fieldProps: {

                allowClear: true,
                showSearch: true,

                onSelect: (value: string, option: OptionProps) => {

                    if (!formShowItem.hiddenName && !formRef.current?.getFieldValue("name")) {

                        const label = option.label;

                        formRef.current?.setFieldValue("name", label);

                        const id = formRef.current?.getFieldValue('id');

                        logicFlowRef.current?.updateText(id, label);

                        logicFlowRef.current?.setProperties(id, {name: label});

                    }

                },

            },

        })

        res.push({

            valueType: 'dependency',

            name: ['category'],

            columns: ({category}: { category: number }): ProFormColumnsType[] => {

                const res = FlowDesignMap.get(category);

                if (res) {

                    return res.form

                }

                return []

            }

        })

    }

    if (formShowItem.showCondition) {

        res.push({
            title: '条件',
            dataIndex: 'condition',
        })

    }

    if (formShowItem.showRemark) {

        res.push({

            title: '描述',
            dataIndex: 'remark',
            valueType: 'textarea',

            formItemProps: {
                rules: [
                    {
                        whitespace: true,
                        max: 300,
                    },
                ],
            },
            fieldProps: {
                showCount: true,
                maxLength: 300,
                allowClear: true,
            }

        })

    }

    if (formShowItem.showFunctionCall) {

        res.push({

            title: '类型',
            dataIndex: 'type',
            valueEnum: SysActivitiLineTypeEnumDict,

            fieldProps: {
                allowClear: true,
                showSearch: true,
            },
            initialValue: SysActivitiLineTypeEnum.NORMAL.code

        })

        res.push({

            valueType: 'dependency',

            name: ['type'],

            columns: ({type}: { type: number }): ProFormColumnsType[] => {

                const functionCallCustomFlag = type === SysActivitiLineTypeEnum.FUNCTION_CALL_CUSTOM.code;

                const functionCallSimpleFlag = type === SysActivitiLineTypeEnum.FUNCTION_CALL_SIMPLE.code;

                const resArr: ProFormColumnsType[] = []

                if (functionCallCustomFlag || functionCallSimpleFlag) {

                    resArr.push({

                        title: '描述',
                        dataIndex: [FunctionCallDataIndex, 'description'],
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

                    })


                    resArr.push({

                        title: '字段',
                        dataIndex: [FunctionCallDataIndex, "parameters"],
                        valueType: 'textarea',

                        formItemProps: {
                            rules: [
                                {
                                    whitespace: true,
                                    max: 300,
                                },
                            ],
                        },
                        fieldProps: {
                            showCount: true,
                            maxLength: 300,
                            allowClear: true,
                            autoSize: {
                                minRows: 10, maxRows: 20,
                            }
                        },

                        readonly: functionCallSimpleFlag,

                        initialValue: `{"type":"object","properties":{"content":{"type":"string","description":"${FunctionCallParametersInitialValueDescription}"}},"required":["content"]}`

                    })

                    if (functionCallSimpleFlag) {

                        let initialValue = FunctionCallParametersInitialValueDescription

                        const functionCallValue = formRef.current?.getFieldValue(FunctionCallDataIndex);

                        if (functionCallValue) {

                            const parametersJsonStr = functionCallValue.parameters;

                            if (parametersJsonStr) {

                                const parameters = JSON.parse(parametersJsonStr) as SysActivitiFunctionCallParametersBO;

                                if (parameters.properties) {

                                    const content = parameters.properties.content;

                                    if (content) {

                                        if (content.description) {

                                            initialValue = content.description

                                        }

                                    }

                                }

                            }

                        }

                        resArr.push({

                            title: '字段描述',
                            dataIndex: FunctionCallSimpleDescriptionDataIndex,
                            valueType: 'textarea',

                            formItemProps: {
                                rules: [
                                    {
                                        whitespace: true,
                                        max: 300,
                                    },
                                ],
                            },

                            initialValue: initialValue,

                            fieldProps: {
                                showCount: true,
                                maxLength: 300,
                                allowClear: true,
                                onChange: (e) => {

                                    const functionCallValue = formRef.current?.getFieldValue(FunctionCallDataIndex);

                                    const parametersJsonStr = functionCallValue.parameters;

                                    if (!parametersJsonStr) {
                                        return
                                    }

                                    const parameters = JSON.parse(parametersJsonStr) as SysActivitiFunctionCallParametersBO;

                                    if (!parameters.properties) {
                                        return;
                                    }

                                    const content = parameters.properties.content;

                                    if (!content) {
                                        return;
                                    }

                                    content.description = e.target.value

                                    functionCallValue.parameters = JSON.stringify(parameters)

                                    formRef.current?.setFieldValue(FunctionCallDataIndex, functionCallValue)

                                }
                            },

                        })

                    }

                }

                return resArr

            }

        })

    }

    return res

}

export default SchemaFormColumnList