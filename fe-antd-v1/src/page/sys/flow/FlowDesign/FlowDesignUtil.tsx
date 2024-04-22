import {ProFormColumnsType} from "@ant-design/pro-components";
import {MyLocalStorage} from "@/util/StorageUtil.ts";
import LocalStorageKey from "@/model/constant/LocalStorageKey.ts";

export interface IFlowDesignEleMap {

    form: ProFormColumnsType[] // 表单

}

export const FlowDesignMap: Map<number, IFlowDesignEleMap> = new Map()

// FlowDesignMap.set(SysAssistantFlowDesignCategoryEnum.CHAT_GPT.code!, {
//
//     form: ChatGptSchemaFormColumnList()
//
// })
//
// FlowDesignMap.set(SysAssistantFlowDesignCategoryEnum.MIDJOURNEY.code!, {
//
//     form: MidjourneySchemaFormColumnList()
//
// })
//
// FlowDesignMap.set(SysAssistantFlowDesignCategoryEnum.CRAWLER.code!, {
//
//     form: CrawlerSchemaFormColumnList()
//
// })

// 复制的：logic-flow.js，ctrl + v的代码
export function HandleNode(t, e) {
    return t.x += e, t.y += e, t.text && (t.text.x += e, t.text.y += e), t
}

// 复制的：logic-flow.js，ctrl + v的代码
export function HandleEdge(t, e) {
    return t.startPoint && (t.startPoint.x += e, t.startPoint.y += e), t.endPoint && (t.endPoint.x += e, t.endPoint.y += e), t.pointsList && t.pointsList.length > 0 && t.pointsList.forEach((function (t) {
        t.x += e, t.y += e
    })), t.text && (t.text.x += e, t.text.y += e), t
}

// 打开：流程设计
export function OpenFlowDesign(deploymentId?: string, processInstanceId?: string, processDefinitionId?: string) {

    if (deploymentId) {

        MyLocalStorage.setItem(LocalStorageKey.FLOW_DESIGN_DEPLOYMENT_ID, deploymentId)

    } else {

        MyLocalStorage.removeItem(LocalStorageKey.FLOW_DESIGN_DEPLOYMENT_ID)

    }

    if (processDefinitionId) {

        MyLocalStorage.setItem(LocalStorageKey.FLOW_DESIGN_PROCESS_DEFINITION_ID, processDefinitionId)

    } else {

        MyLocalStorage.removeItem(LocalStorageKey.FLOW_DESIGN_PROCESS_DEFINITION_ID)

    }

    if (processInstanceId) {

        MyLocalStorage.setItem(LocalStorageKey.FLOW_DESIGN_PROCESS_INSTANCE_ID, processInstanceId)

    } else {

        MyLocalStorage.removeItem(LocalStorageKey.FLOW_DESIGN_PROCESS_INSTANCE_ID)

    }

    // window.open(SysAssistantPathConstant.LX_SAAS_ASSISTANT_FLOW_FLOW_DESIGN_PATH, '_blank')

}