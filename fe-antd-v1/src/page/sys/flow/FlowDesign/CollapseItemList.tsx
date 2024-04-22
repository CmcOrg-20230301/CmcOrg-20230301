import {ItemType} from "rc-collapse/es/interface";
import {BetaSchemaForm, FormInstance} from "@ant-design/pro-components";

import React from "react";
import LogicFlow from "@logicflow/core";

import {SysActivitiParamItemTypeEnum} from "@/model/enum/SysActivitiParamItemTypeEnum.ts";
import {Image} from "antd";
import {FormatDateTimeForCurrentDay, GetServerTimestamp} from "@/util/DateUtil.ts";
import {IProcessVariable, IRootProperties, SysActivitiParamItemBO} from "@/page/sys/flow/FlowDesign/FlowDesign.tsx";
import SchemaFormColumnList, {
    DeleteDataIndexArr,
    IFormShowItem
} from "@/page/sys/flow/FlowDesign/SchemaFormColumnList.tsx";

const CollapseItemList = (formRef: React.MutableRefObject<FormInstance | undefined>, logicFlowRef: React.MutableRefObject<LogicFlow | null>, rootProperties: React.MutableRefObject<IRootProperties>, SetProcessId: (updateFormFlag: boolean) => void, formShowItem: IFormShowItem, processVariable: IProcessVariable) => {

    const res: ItemType[] = []

    res.push({

        key: '1',

        label: '属性',

        children: <>

            <BetaSchemaForm

                autoFocusFirstInput={false}

                formRef={formRef}

                layout={"horizontal"}

                submitter={false}

                onValuesChange={(changedValue, allValue) => {

                    const id = formRef.current?.getFieldValue('id');

                    const model = logicFlowRef.current?.getModelById(id);

                    if (model) {

                        const updateProperties = {...allValue};

                        if ('name' in changedValue) {

                            logicFlowRef.current?.updateText(id, changedValue.name);

                        } else if ('condition' in changedValue) {

                            logicFlowRef.current?.updateText(id, changedValue.condition);

                            updateProperties.name = changedValue.condition

                        } else if (changedValue.functionCall) {

                            if ('name' in changedValue.functionCall) {

                                logicFlowRef.current?.updateText(id, changedValue.functionCall.name);

                            }

                        }

                        DeleteDataIndexArr.forEach(item => {

                            delete updateProperties[item] // 不输出到流程文件里面

                        })

                        logicFlowRef.current?.setProperties(id, updateProperties);

                    } else {

                        rootProperties.current = {...rootProperties.current, ...changedValue,}

                        SetProcessId(false);

                    }

                }}

                columns={SchemaFormColumnList(formShowItem, processVariable, formRef, logicFlowRef)}

            />

        </>

    })

    if (processVariable) {

        const processInstanceVariable = processVariable.processInstanceVariable;

        if (processInstanceVariable && processInstanceVariable.inMap) {

            const inMap = processInstanceVariable.inMap;

            const arr: SysActivitiParamItemBO[] = []

            const serverTimestamp = GetServerTimestamp();

            Object.keys(inMap).forEach(key => {

                const sysActivitiParamItemBOArr = inMap[key];

                sysActivitiParamItemBOArr.forEach(item => {

                    if (item.fromNodeId) {

                        arr.push(item)

                    }

                })

            })

            // 排序
            arr.sort((a, b) => {

                const ts1 = Number(a.startTs) || serverTimestamp;

                const ts2 = Number(b.startTs) || serverTimestamp;

                return ts1 > ts2 ? 1 : -1

            })

            res.push({

                key: '2',

                label: '日志',

                children: <div className={"flex-c overflow-y"} style={{maxHeight: '600px'}}>

                    {arr.map((item, index) => {

                        let execTime = ''

                        let name = ''

                        let contentEle: React.ReactNode = null

                        if (item.paramList && item.paramList.length) {

                            const fromNodeId = item.fromNodeId!;

                            const nodeModelById = logicFlowRef.current?.getNodeModelById(fromNodeId);

                            if (nodeModelById) {

                                name = nodeModelById.text.value || fromNodeId

                                const sysActivitiParamSubItemBO = item.paramList[0];

                                if (sysActivitiParamSubItemBO.type === SysActivitiParamItemTypeEnum.IMAGE.code) {

                                    contentEle = <Image src={sysActivitiParamSubItemBO.value} height={120}/>

                                } else {

                                    contentEle = <span className={"black3"}>

                                        {sysActivitiParamSubItemBO.value}

                                    </span>

                                }

                            }

                        }

                        if (item.startTs) {

                            execTime = FormatDateTimeForCurrentDay(new Date(Number(item.startTs)))

                        }

                        return <div className={"space-t-10 overflow-y f-12"} key={index} style={{maxHeight: '200px'}}>

                            <span className={"black3"}>{execTime}</span>

                            <span className={"m-l-r-10 green2"}>{name}</span>

                            {contentEle}

                        </div>


                    })}

                </div>

            })

        }

    }

    return res

}

export default CollapseItemList