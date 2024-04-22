import React, {useEffect, useRef, useState} from "react";
import LogicFlow, {NodeConfig} from "@logicflow/core";
import "@logicflow/core/dist/style/index.css";
import {
    BPMNAdapter,
    BPMNElements,
    DndPanel,
    InsertNodeInPolyline,
    Menu,
    MiniMap,
    SelectionSelect,
    Snapshot
} from "@logicflow/extension";
import "@logicflow/extension/lib/style/index.css";
import '@/style/logicflow.less'
import {DesktopOutlined} from "@ant-design/icons";
import {Collapse, Slider, Tooltip} from "antd";
import {MyUseState} from "@/util/HookUtil.ts";
import {NewNodeConfig} from "@logicflow/core/types/view/behavior/DnD";
import StartEventNone from '@/asset/image/bpmn/start-event-none.svg';
import EndEventNone from '@/asset/image/bpmn/end-event-none.svg';
import User from '@/asset/image/bpmn/user.svg';
import GatewayOr from '@/asset/image/bpmn/gateway-or.svg';
import {CheckBlobType, DownloadByString, FileDownload, FileUpload} from "@/util/FileUtil.ts";
import {ExecConfirm, ToastError, ToastSuccess} from "@/util/ToastUtil.ts";
import {FormInstance} from "@ant-design/pro-components";
import {GetServerTimestamp} from "@/util/DateUtil.ts";
import {lfXml2Json} from "@logicflow/extension/es/bpmn-elements-adapter/xml2json";
import {MyLocalStorage} from "@/util/StorageUtil.ts";
import LocalStorageKey from "@/model/constant/LocalStorageKey.ts";
import {SysActivitiDeployInsertOrUpdateVO, SysActivitiHistoryTaskPage} from "@/api/http/SysActiviti.ts";
import {useAppSelector} from "@/store";
import {SYS_ACTIVITI_PARAM_CHANGE} from "@/api/socket/WebSocket.ts";
import {HandleEdge, HandleNode} from "@/page/sys/flow/FlowDesign/FlowDesignUtil.tsx";
import {IFormShowItem, InitFormShowItem} from "@/page/sys/flow/FlowDesign/SchemaFormColumnList.tsx";
import CollapseItemList from "@/page/sys/flow/FlowDesign/CollapseItemList.tsx";
import SchemaForm from "@/page/sys/flow/MyFlowList/SchemaForm.tsx";

const ZoomInitValue = 100

const CopyOffset = 40;

const LeftWidth = 60;

interface ILeftItem {

    icon: string
    title: string
    nodeConfig: NewNodeConfig

}

interface IPropertiesDrawOpenItem {

    nodeConfig?: NodeConfig

}

export interface IRootProperties {

    id?: string
    name?: string
    remark?: string

}

const Id = 'p' + GetServerTimestamp();

export interface SysActivitiFunctionCallParamPropertiesBO {

    type?: string // 字段类型，例如：string

    description?: string // 字段描述

}

export interface SysActivitiFunctionCallParametersBO {

    type?: string // 一般为：object

    properties?: Record<string, SysActivitiFunctionCallParamPropertiesBO> // 入参的字段，备注：content字段为默认的入参字段

    required?: string[] // 必须的字段

}

export interface SysActivitiParamSubItemBO {

    type?: number
    value?: string

}

export interface SysActivitiParamItemBO {

    paramList?: SysActivitiParamSubItemBO[]

    execFlag?: boolean // 是否被执行过，默认：false

    startTs?: number // 执行的开始时间（时间戳）

    execErrorMessage?: boolean

    fromNodeId?: string

}

export interface IProcessInstanceVariable {

    // key，节点 id，value：该节点输入的值集合
    inMap?: Record<string, SysActivitiParamItemBO[]>

}

export interface IProcessVariable {

    processInstanceVariable?: IProcessInstanceVariable

    tenantId?: string
    userId?: string

}

/**
 * 通过：流程实例 id，显示日志和动画
 */
function HandleProcessInstanceId(processInstanceId: string | undefined | null, setProcessVariable: React.Dispatch<IProcessVariable>, logicFlow: LogicFlow, openEdgeAnimationEdgeIdSetRef: React.MutableRefObject<Set<string>>) {

    if (!processInstanceId) {
        return
    }

    SysActivitiHistoryTaskPage({processInstanceId: processInstanceId}).then(res => {

        openEdgeAnimationEdgeIdSetRef.current.clear()

        let processVariableTemp: IProcessVariable = {}

        if (res.data && res.data.length) {

            processVariableTemp = (res.data[0].processVariableMap || {}) as IProcessVariable;

            setProcessVariable(processVariableTemp)

        } else {

            setProcessVariable(processVariableTemp)

        }

        console.log('流程参数', processVariableTemp)

        const inMap = processVariableTemp.processInstanceVariable?.inMap;

        if (inMap) {

            const inMapKeyArr = Object.keys(inMap);

            inMapKeyArr.forEach(key => {

                const nodeIncomingEdgeArr = logicFlow.getNodeIncomingEdge(key);

                const inMapFromKeySet = new Set<string>();

                inMap[key].forEach(subItem => {

                    if (subItem.fromNodeId) {

                        inMapFromKeySet.add(subItem.fromNodeId)

                    }

                })

                nodeIncomingEdgeArr.forEach(item => {

                    if (inMapFromKeySet.has(item.sourceNodeId)) {

                        openEdgeAnimationEdgeIdSetRef.current.add(item.id)

                    }

                })

            })

        }

        openEdgeAnimationEdgeIdSetRef.current.forEach(value => {

            logicFlow.openEdgeAnimation(value)

        })

    })
}

/**
 * 上传设计
 */
async function UploadFlowDesign(logicFlowRef: React.MutableRefObject<LogicFlow | null>, rootProperties: React.MutableRefObject<IRootProperties>, setModalVisitId: React.Dispatch<React.SetStateAction<string | null | undefined>>, showToastFlag: boolean): Promise<SysActivitiDeployInsertOrUpdateVO | null> {

    const graphRawData = logicFlowRef.current?.adapterOut(logicFlowRef.current?.getGraphRawData()) as string;

    const fileName = rootProperties.current.id + "-" + GetServerTimestamp() + '.bpmn20.xml';

    const blob = new Blob([graphRawData], {
        type: 'text/plain'
    });

    const formData = new FormData();

    formData.append("file", blob, fileName);

    let sysActivitiDeployInsertOrUpdateVO: null | SysActivitiDeployInsertOrUpdateVO = null

    await FileUpload<SysActivitiDeployInsertOrUpdateVO>(formData, '/sys/activiti/deploy/insertOrUpdate/byFile').then(res => {

        if (showToastFlag) {

            ToastSuccess("上传成功，请在【机器自动化-我的自动化】中进行查看")

        }

        sysActivitiDeployInsertOrUpdateVO = res.data;

        const processDefinitionId = sysActivitiDeployInsertOrUpdateVO.processDefinitionId!

        MyLocalStorage.setItem(LocalStorageKey.FLOW_DESIGN_DEPLOYMENT_ID, sysActivitiDeployInsertOrUpdateVO.deploymentId!);

        MyLocalStorage.setItem(LocalStorageKey.FLOW_DESIGN_PROCESS_DEFINITION_ID, processDefinitionId);

        setModalVisitId(processDefinitionId)

    })

    return sysActivitiDeployInsertOrUpdateVO

}

// 工作流设计
export default function () {

    const rootRef = useRef<HTMLDivElement>(null);

    const containerParentRef = useRef<HTMLDivElement>(null);

    const containerRef = useRef<HTMLDivElement>(null);

    const leftRef = useRef<HTMLDivElement>(null);

    const [miniMapOpenFlag, setMiniMapOpenFlag, miniMapOpenFlagRef] = MyUseState(useState<boolean>(false));

    const logicFlowRef = useRef<LogicFlow | null>(null);

    const [moveFlag, setMoveFlag, moveFlagRef] = MyUseState(useState<boolean>(false));

    const moveXYRef = useRef<{ x: number, y: number }>({x: 0, y: 0})

    const [zoomValue, setZoomValue] = useState<number>(ZoomInitValue);

    const [logicFlowDragFlag, setLogicFlowDragFlag] = useState<boolean>(false);

    const [leftItemDragFlag, setLeftItemDragFlag] = useState<boolean>(false);

    const formRef = useRef<FormInstance>();

    const rootProperties = useRef<IRootProperties>({id: Id, name: Id});

    const openEdgeAnimationEdgeIdSetRef = useRef<Set<string>>(new Set<string>());

    /**
     * 设置：流程 id
     */
    function SetProcessId(updateFormFlag: boolean) {

        logicFlowRef.current!.extension.BPMNAdapter.processAttributes['-id'] = rootProperties.current.id || ''

        logicFlowRef.current!.extension.BPMNAdapter.processAttributes['-name'] = rootProperties.current.name || ''

        const rootPropertiesTemp = {...rootProperties.current}

        delete rootPropertiesTemp.id
        delete rootPropertiesTemp.name

        logicFlowRef.current!.extension.BPMNAdapter.processAttributes['bpmn:documentation'] = JSON.stringify(rootPropertiesTemp)

        if (updateFormFlag) {

            formRef.current?.setFieldsValue(rootProperties.current)

        }

    }

    useEffect(() => {

        document.title = "设计自动化";

        // 计算：初始大小
        const containerParent = containerParentRef.current!;

        const clientWidth = containerParent.clientWidth;

        const clientHeight = containerParent.clientHeight;

        const logicFlow = new LogicFlow({
            plugins: [Menu, DndPanel, MiniMap, SelectionSelect, InsertNodeInPolyline, BPMNElements, BPMNAdapter, Snapshot],
            width: clientWidth,
            height: clientHeight,
            container: containerRef.current!,
            keyboard: {
                enabled: true,
                shortcuts: [

                    {
                        keys: ["delete"],

                        callback: () => {

                            deleteAllSelect()

                        },

                    },

                ],
            },
            background: {
                backgroundImage: "url(/grid.svg)",
                backgroundRepeat: "repeat",
            },
            edgeTextDraggable: true,
            adjustEdgeStartAndEnd: true,
            nodeTextDraggable: true,
            multipleSelectKey: 'shift',
            nodeSelectedOutline: false,
            edgeSelectedOutline: false,
        });

        /**
         * 删除：所有选中的元素
         */
        function deleteAllSelect() {

            const selectElement = logicFlow.getSelectElements(true);

            logicFlow.clearSelectElements();

            selectElement.edges.forEach((edge) => logicFlow.deleteEdge(edge.id!));
            selectElement.nodes.forEach((node) => logicFlow.deleteNode(node.id!));

        }

        logicFlowRef.current = logicFlow

        logicFlow.render();

        logicFlow.translate(LeftWidth, 0)

        const adapterInOld = logicFlow.adapterIn;

        logicFlow.adapterIn = function (bpmnData) {

            const json = lfXml2Json(bpmnData);

            const process = json['bpmn:definitions']['bpmn:process'];

            const processPropertiesJsonStr = process['bpmn:documentation'];

            delete process['bpmn:documentation']

            if (processPropertiesJsonStr) {

                rootProperties.current = JSON.parse(processPropertiesJsonStr)

                const regExp = new RegExp(`<bpmn:documentation>${processPropertiesJsonStr}</bpmn:documentation>`);

                bpmnData = (bpmnData as string).replace(regExp, "");

            }

            rootProperties.current.id = process['-id']

            rootProperties.current.name = process['-name']

            const id = formRef.current?.getFieldValue('id');

            const model = logicFlowRef.current?.getModelById(id);

            SetProcessId(!model);

            return adapterInOld(bpmnData)

        }

        const adapterOutOld = logicFlow.adapterOut;

        logicFlow.adapterOut = function (bpmnData) {

            return adapterOutOld(bpmnData)

        }

        SetProcessId(true);

        logicFlow.extension.BPMNAdapter.props = {

            transformer: {

                'bpmn:userTask': {

                    out(data: any) {

                        if (Object.keys(data.properties).length) {

                            const jsonStr = JSON.stringify(data.properties);

                            data.properties = {} // 重置：所有属性

                            return {
                                json: `  <bpmn:documentation>${jsonStr}</bpmn:documentation>`,
                            };

                        }

                        return {
                            json: '',
                        };

                    },

                },

                'bpmn:documentation': {

                    in(_key: string, jsonStr: any) {

                        const json = JSON.parse(jsonStr);

                        const res = {}

                        Object.keys(json).forEach(key => {

                            if (key === 'functionCall') {

                                if (json[key].parameters) {

                                    json[key].parameters = JSON.stringify(json[key].parameters)

                                }

                            }

                            res['-' + key] = json[key]

                        })

                        return res;

                    },

                },

                'bpmn:sequenceFlow': {

                    out(data: any) {

                        delete data.properties.isDefaultFlow

                        delete data.properties.condition

                        let documentationStr = ''

                        if (Object.keys(data.properties).length) {

                            const properties = {...data.properties}

                            properties.functionCall.name = data.id

                            if (properties.functionCall.parameters) {

                                properties.functionCall.parameters = JSON.parse(properties.functionCall.parameters)

                            }

                            const jsonStr = JSON.stringify(properties);

                            data.properties = {} // 重置：所有属性

                            documentationStr = `<bpmn:documentation>${jsonStr}</bpmn:documentation>`

                        }

                        if (documentationStr) {

                            return {
                                json: `${documentationStr}
        <bpmn:conditionExpression xsi:type="bpmn:tFormalExpression"><![CDATA[\${sysActivitiConditionExpressionUtil.check(execution)}]]></bpmn:conditionExpression>`
                            };

                        } else {

                            return {
                                json: `<bpmn:conditionExpression xsi:type="bpmn:tFormalExpression"><![CDATA[\${sysActivitiConditionExpressionUtil.check(execution)}]]></bpmn:conditionExpression>`
                            };

                        }

                    },

                },

                'bpmn:conditionExpression': {

                    in(_key: string, data: any) {

                        const condition = /^\$\{(.*)\}$/g.exec(data['#cdata-section'])?.[1] || '';

                        return {
                            '-condition': condition,
                        };

                    },

                },

            }

        }

        logicFlow.extension.miniMap.rightPosition = 35
        logicFlow.extension.miniMap.bottomPosition = 35
        logicFlow.extension.miniMap.isShowHeader = false
        logicFlow.extension.miniMap.isShowCloseIcon = false

        logicFlow.extension.selectionSelect.openSelectionSelect();

        logicFlow.extension.menu.setMenuConfig({

            nodeMenu: [

                {

                    text: "复制",

                    callback() {

                        const selectElement = logicFlow.getSelectElements(true);

                        logicFlow.clearSelectElements();

                        selectElement.nodes.forEach(item => HandleNode(item, CopyOffset))
                        selectElement.edges.forEach(item => HandleEdge(item, CopyOffset))

                        const graphConfigModel = logicFlow.addElements({
                            nodes: selectElement.nodes,
                            edges: selectElement.edges
                        });

                        graphConfigModel.nodes.forEach(item => logicFlow.selectElementById(item.id!, true))
                        graphConfigModel.edges.forEach(item => logicFlow.selectElementById(item.id!, true))

                    },

                },

                {

                    text: "编辑文本",

                    callback(node) {

                        logicFlow.editText(node.id)

                    },

                },

                {

                    text: "删除",

                    callback() {

                        deleteAllSelect()

                    },

                },

            ], // 覆盖默认的节点右键菜单

            edgeMenu: [

                {

                    text: "编辑文本",

                    callback(edge) {

                        logicFlow.editText(edge.id)

                    },

                },

                {

                    text: "删除",

                    callback() {

                        deleteAllSelect()

                    },

                },

            ], // 删除默认的边右键菜单

            graphMenu: [], // 覆盖默认的画布右键菜单

        });

        containerParent.addEventListener('mousedown', (e) => {

            if (e.button !== 2) { // 2：鼠标右键
                return
            }

            logicFlow.extension.selectionSelect.closeSelectionSelect();

            setMoveFlag(true)

            moveXYRef.current = {x: e.x, y: e.y}

        });

        /**
         * 处理：结束时的事件
         */
        function handleEndEvent(e: MouseEvent) {

            if (!moveFlagRef.current) {
                return;
            }

            logicFlow.extension.selectionSelect.openSelectionSelect();

            setMoveFlag(false)

            setLogicFlowDragFlag(false)

        }

        containerParent.addEventListener('mouseup', (e) => {

            if (e.button !== 2) { // 2：鼠标右键
                return
            }

            handleEndEvent(e);

        });

        containerParent.addEventListener('mouseleave', (e) => {

            handleEndEvent(e);

        });

        containerParent.oncontextmenu = () => {

            return false; // 阻止浏览器的默认弹窗行为

        }

        containerParent.addEventListener('mousemove', (e) => {

            if (!moveFlagRef.current) {
                return
            }

            setLogicFlowDragFlag(true)

            const preX = moveXYRef.current.x;

            const preY = moveXYRef.current.y;

            const endX = e.x;

            const endY = e.y;

            logicFlow.translate((endX - preX), (endY - preY))

            if (miniMapOpenFlagRef.current) {

                // 告诉小地图移动
                logicFlow.graphModel.eventCenter.emit('graph:transform', {})

            }

            moveXYRef.current = {x: endX, y: endY}

        });

        function HandleAdd(e) {

            if (e.data.text) {
                logicFlowRef.current?.setProperties(e.data.id, {name: e.data.text.value})
            }

        }

        logicFlow.on('node:dnd-add', e => {

            setLeftItemDragFlag(false)

            HandleAdd(e)

        })

        logicFlow.on('node:add', e => {

            HandleAdd(e)

        })

        logicFlow.on('graph:transform', e => {

            if (e.type === 'zoom') {

                const transform = e.transform;

                const zoom = transform.SCALE_X;

                const zoomValueStr = Number(zoom * ZoomInitValue).toFixed(0);

                setZoomValue(Number(zoomValueStr))

            }

        })

        logicFlow.on('element:click', e => {

            console.log('点击元素', e.data.properties)

            if (e.data.type === 'bpmn:userTask') {

                setFormShowItem({showCategory: true, idEditFlag: false,})

            } else if (e.data.type === 'bpmn:sequenceFlow') {

                setFormShowItem({hiddenName: true, idEditFlag: false, showFunctionCall: true})

            } else if (e.data.type === 'bpmn:endEvent') {

                setFormShowItem({})

            } else if (e.data.type === 'bpmn:startEvent') {

                setFormShowItem({})

            } else {

                setFormShowItem({})

            }

            formRef.current?.resetFields()

            let name = ''

            if (e.data.text) {
                name = e.data.text.value
            }

            formRef.current?.setFieldsValue({id: e.data.id, ...e.data.properties, name})

        })

        logicFlow.on('blank:click', e => {

            console.log('点击画布-1', {
                ...rootProperties.current,
                processAttributes: {...logicFlow.extension.BPMNAdapter.processAttributes}
            })

            setFormShowItem(InitFormShowItem)

            formRef.current?.resetFields()

            formRef.current?.setFieldsValue(rootProperties.current)

        })

        logicFlow.on('text:update', e => {

            const properties = logicFlowRef.current?.getProperties(e.id)!;

            const updateProperties = {
                ...properties,
                name: e.text
            }

            const model = logicFlow.getModelById(e.id);

            const conditionFlag = model.BaseType === 'edge'

            if (conditionFlag) {

                updateProperties['condition'] = e.text

            }

            logicFlowRef.current?.setProperties(e.id, updateProperties);

            const id = formRef.current?.getFieldValue('id');

            if (id) {

                formRef.current?.setFieldsValue(updateProperties)

            }

        })

        const deploymentId = MyLocalStorage.getItem(LocalStorageKey.FLOW_DESIGN_DEPLOYMENT_ID);

        if (deploymentId) {

            // 加载数据
            FileDownload('/sys/activiti/deploy/downloadResourceFile', ((blob, fileName) => {

                if (!CheckBlobType(blob)) {
                    return
                }

                // 将Blob 对象转换成字符串
                const fileReader = new FileReader();

                fileReader.readAsText(blob, 'utf-8');

                fileReader.onload = (e) => {

                    logicFlowRef.current?.render(fileReader.result as any);

                    setModalVisitId(MyLocalStorage.getItem(LocalStorageKey.FLOW_DESIGN_PROCESS_DEFINITION_ID))

                    const processInstanceId = MyLocalStorage.getItem(LocalStorageKey.FLOW_DESIGN_PROCESS_INSTANCE_ID);

                    setProcessInstanceId(processInstanceId)

                }

            }), {value: deploymentId})

        }

    }, [])

    // 本次流程执行的参数结果
    const [processVariable, setProcessVariable, processVariableRef] = MyUseState(useState<IProcessVariable>({}));

    const [formShowItem, setFormShowItem] = useState<IFormShowItem>(InitFormShowItem);

    const [leftItemArr, setLeftItemArr] = useState<ILeftItem[]>([

        {
            icon: StartEventNone,
            title: '开始',
            nodeConfig: {
                type: 'bpmn:startEvent',
                text: '开始'
            }
        },

        {
            icon: EndEventNone,
            title: '结束',
            nodeConfig: {
                type: 'bpmn:endEvent',
                text: '结束'
            }
        },

        {
            icon: User,
            title: '用户任务',
            nodeConfig: {
                type: 'bpmn:userTask',
            }
        },

        {
            icon: GatewayOr,
            title: '包含网关',
            nodeConfig: {
                type: 'bpmn:inclusiveGateway',
            }
        },

    ]);

    const [modalVisit, setModalVisit] = useState<boolean>(false);

    const [modalVisitId, setModalVisitId] = useState<string | undefined | null>();

    const [processInstanceId, setProcessInstanceId] = useState<string | undefined | null>();

    const webSocketMessage = useAppSelector((state) => state.common.webSocketMessage);

    useEffect(() => {

        if (webSocketMessage.uri === SYS_ACTIVITI_PARAM_CHANGE) {

            if (webSocketMessage.data === processInstanceId) {

                // 通过：流程实例 id，显示日志和动画
                HandleProcessInstanceId(processInstanceId, setProcessVariable, logicFlowRef.current!, openEdgeAnimationEdgeIdSetRef);

            }

        }

    }, [webSocketMessage])

    useEffect(() => {

        if (!processInstanceId) {
            return
        }

        // 通过：流程实例 id，显示日志和动画
        HandleProcessInstanceId(processInstanceId, setProcessVariable, logicFlowRef.current!, openEdgeAnimationEdgeIdSetRef);

    }, [processInstanceId])

    return <>

        <div className={"vwh100 flex-c"} ref={rootRef}>

            <div className={"flex-s h-30 flex ai-c p-l-r-20 us-no"}>

                <div className={"hand space-l-15"} onClick={() => {

                    if (leftItemDragFlag) {

                        setLeftItemDragFlag(false)

                    } else {

                        setLeftItemDragFlag(true)

                    }

                }}>
                    图形库
                </div>

                <div className={"hand space-l-15"} onClick={() => {

                    ExecConfirm(async () => {

                        logicFlowRef.current?.clearData()

                    }, undefined, '确定清空画布吗？')

                }}>
                    清空画布
                </div>

                <div className={"hand space-l-15"} onClick={() => {

                    logicFlowRef.current?.getSnapshot()

                }}>
                    下载图片
                </div>

                <div className={"hand space-l-15"} onClick={() => {

                    const graphRawData = logicFlowRef.current?.adapterOut(logicFlowRef.current?.getGraphRawData()) as string;

                    const fileName = rootProperties.current.id + "-" + GetServerTimestamp() + '.bpmn20.xml';

                    DownloadByString(graphRawData, fileName)

                }}>
                    导出
                </div>

                <div className={"space-l-15 rel overflow-h"}>

                    <input type="file" className="abs wh100 hand p-l-40"
                           style={{zIndex: 1, left: 0, top: 0, opacity: 0}}
                           onChange={(e) => {

                               // @ts-ignore
                               const file = (e.target as FileEventTarget).files[0];

                               const reader = new FileReader()

                               reader.onload = (event: ProgressEvent<FileReader>) => {

                                   if (event.target) {

                                       e.target.value = '' // 解决：同一个文件不能二次上传的问题

                                       const result = event.target.result as string;

                                       const adapterIn = logicFlowRef.current?.adapterIn(result);

                                       if (adapterIn) {

                                           logicFlowRef.current?.clearSelectElements();

                                           adapterIn.nodes.forEach(nodeData => {
                                               logicFlowRef.current?.addNode(nodeData);
                                               logicFlowRef.current?.selectElementById(nodeData.id!, true)
                                           });

                                           adapterIn.edges.forEach(edgeData => {
                                               logicFlowRef.current?.addEdge(edgeData);
                                               logicFlowRef.current?.selectElementById(edgeData.id!, true)
                                           });

                                           ToastSuccess('导入成功')

                                       } else {

                                           ToastError('导入失败')

                                       }

                                   }

                               }

                               reader.readAsText(file);

                           }}/>
                    导入
                </div>

                <div className={"hand space-l-15"} onClick={() => {

                    ExecConfirm(async () => {

                        await UploadFlowDesign(logicFlowRef, rootProperties, setModalVisitId, true);

                    }, undefined, '确定上传该自动化设计吗？')

                }}>
                    上传
                </div>

                <div className={"hand space-l-15 green2"} onClick={() => {

                    setModalVisit(true)

                }}>
                    执行
                </div>

            </div>

            <div className={"flex1 flex rel"}>

                <div className={"black9-bc overflow-y abs us-no"}
                     style={{
                         left: 0,
                         top: 0,
                         zIndex: 1,
                         display: leftItemDragFlag ? 'none' : 'flex',
                         flexDirection: 'column',
                     }}
                     ref={leftRef}>

                    <>

                        {

                            leftItemArr.map((item, index) => {

                                return <div key={index} className={"m-5 w-50 h-50 us-no"} title={item.title}>

                                    <img src={item.icon} alt="" draggable={false} onMouseDown={() => {

                                        logicFlowRef.current?.dnd.startDrag(item.nodeConfig);

                                    }}/>

                                </div>

                            })

                        }

                    </>

                </div>

                <div className={"flex1 flex-c black8-bc" + (logicFlowDragFlag ? ' logicFlow-drag' : '')}
                     ref={containerParentRef}>

                    <div ref={containerRef}></div>

                </div>

                <div className={"flex-s black9-bc overflow-y w-350 h100 p-l-r-10 flex-c"}>

                    <Collapse defaultActiveKey={["1", "2"]} ghost
                              items={CollapseItemList(formRef, logicFlowRef, rootProperties, SetProcessId, formShowItem, processVariable)}/>

                </div>

            </div>

            <div className={"flex-s"}>

                <div className={"flex"}>

                    <div className={"flex1"}></div>

                    <div className={"flex-s p-l-r-20 flex ai-c"}>

                        <Tooltip title={(miniMapOpenFlag ? '关闭' : '打开') + "视图导航"}>

                            <DesktopOutlined onClick={() => {

                                if (miniMapOpenFlag) {

                                    setMiniMapOpenFlag(false)

                                    logicFlowRef.current?.extension.miniMap.hide();

                                } else {

                                    setMiniMapOpenFlag(true)

                                    logicFlowRef.current?.extension.miniMap.show();

                                }

                            }}/>

                        </Tooltip>

                        <div className={"m-l-20 w-300 flex-s"}>

                            <Slider

                                min={20}
                                max={200}
                                onChange={(newValue) => {

                                    logicFlowRef.current?.zoom(newValue / 100)

                                    setZoomValue(newValue)

                                }}
                                value={zoomValue}
                            />

                        </div>

                        <Tooltip title={"重置缩放"}>

                            <div className={"m-l-20 hand w-40 flex-s"} onClick={() => {

                                logicFlowRef.current?.zoom(ZoomInitValue / 100)

                                setZoomValue(ZoomInitValue)

                            }}>{zoomValue + '%'}</div>

                        </Tooltip>

                    </div>

                </div>

            </div>

        </div>

        <SchemaForm modalVisit={modalVisit} processDefinitionId={modalVisitId} setModalVisit={setModalVisit}
                    preFun={async () => {

                        openEdgeAnimationEdgeIdSetRef.current.forEach(item => {

                            try {

                                logicFlowRef.current?.closeEdgeAnimation(item) // 备注：不存在的线 id，则会报错

                            } catch (e) {

                            }

                        })

                        openEdgeAnimationEdgeIdSetRef.current.clear()

                        let processDefinitionId = MyLocalStorage.getItem(LocalStorageKey.FLOW_DESIGN_PROCESS_DEFINITION_ID);

                        let sysActivitiDeployInsertOrUpdateVO: SysActivitiDeployInsertOrUpdateVO | null = null

                        if (!processDefinitionId) {

                            sysActivitiDeployInsertOrUpdateVO = await UploadFlowDesign(logicFlowRef, rootProperties, setModalVisitId, false);

                        }

                        return sysActivitiDeployInsertOrUpdateVO

                    }}

                    callBack={processInstanceId => {

                        setProcessInstanceId(processInstanceId)

                        MyLocalStorage.setItem(LocalStorageKey.FLOW_DESIGN_PROCESS_INSTANCE_ID, processInstanceId!);

                    }}></SchemaForm>

    </>

}