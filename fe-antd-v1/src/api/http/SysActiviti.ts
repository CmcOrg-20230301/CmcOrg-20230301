import {SortOrder} from "antd/es/table/interface";
import MyOrderDTO from "@/model/dto/MyOrderDTO";
import {$http, IHttpConfig} from "@/util/HttpUtil";

export interface NotEmptyStringSet {
    idSet?: string[] // 主键 idSet，required：true
}

// 流程实例-批量挂起
export function SysActivitiProcessInstanceSuspendByIdSet(form: NotEmptyStringSet, config?: IHttpConfig) {
    return $http.myPost<string>('/sys/activiti/processInstance/suspendByIdSet', form, config)
}

export interface NotEmptyStringAndVariableMapSet {
    idSet?: string[] // 主键 idSet，required：true
    variableMap?: object // 参数：map
}

// 任务-批量完成
export function SysActivitiTaskCompleteByIdSet(form: NotEmptyStringAndVariableMapSet, config?: IHttpConfig) {
    return $http.myPost<string>('/sys/activiti/task/completeByIdSet', form, config)
}

// 任务-批量归还
export function SysActivitiTaskReturnByIdSet(form: NotEmptyStringSet, config?: IHttpConfig) {
    return $http.myPost<string>('/sys/activiti/task/returnByIdSet', form, config)
}

export interface SysActivitiDeployInsertOrUpdateDTO {
    url?: string // 获取：部署文件的url，required：true
}

export interface SysActivitiDeployInsertOrUpdateVO {
    processDefinitionId?: string // 流程定义：id
    deploymentId?: string // 部署：id
    processDefinitionKey?: string // 流程定义：key
}

// 部署-新增/修改
export function SysActivitiDeployInsertOrUpdate(form: SysActivitiDeployInsertOrUpdateDTO, config?: IHttpConfig) {
    return $http.myPost<SysActivitiDeployInsertOrUpdateVO>('/sys/activiti/deploy/insertOrUpdate', form, config)
}

export interface NotBlankString {
    value?: string // 值，required：true
}

export interface SysActivitiProcessInstanceVO {
    processDefinitionId?: string // 流程定义：id
    description?: string // 流程实例：描述
    processDefinitionName?: string // 流程定义：名称
    suspended?: boolean // 流程实例：是否是暂停状态
    processDefinitionKey?: string // 流程定义：key
    startUserId?: string // 流程实例：开始用户主键 id
    deploymentId?: string // 部署：id
    businessKey?: string // 流程实例：业务key
    ended?: boolean // 流程实例：是否是结束状态
    tenantId?: string // 流程实例：租户 id
    name?: string // 流程实例：名称
    startTime?: string // 流程实例：开始时间，format：date-time
    id?: string // 流程实例：id
    processDefinitionVersion?: number // 流程定义：版本号，format：int32
    processVariableMap?: object // 流程实例：参数
}

// 流程实例-通过主键id，查看详情
export function SysActivitiProcessInstanceInfoById(form: NotBlankString, config?: IHttpConfig) {
    return $http.myProPost<SysActivitiProcessInstanceVO>('/sys/activiti/processInstance/infoById', form, config)
}

export interface SysActivitiProcessInstanceInsertOrUpdateDTO {
    processDefinitionId?: string // 流程定义：id，required：true
    businessKey?: string // 业务：key
    variableMap?: object // 参数：map
}

// 流程实例-新增/修改
export function SysActivitiProcessInstanceInsertOrUpdate(form: SysActivitiProcessInstanceInsertOrUpdateDTO, config?: IHttpConfig) {
    return $http.myPost<string>('/sys/activiti/processInstance/insertOrUpdate', form, config)
}

// 任务-批量接受
export function SysActivitiTaskClaimByIdSet(form: NotEmptyStringSet, config?: IHttpConfig) {
    return $http.myPost<string>('/sys/activiti/task/claimByIdSet', form, config)
}

// 流程实例-批量删除
export function SysActivitiProcessInstanceDeleteByIdSet(form: NotEmptyStringSet, config?: IHttpConfig) {
    return $http.myPost<string>('/sys/activiti/processInstance/deleteByIdSet', form, config)
}

export interface SysActivitiProcessDefinitionVO {
    deploymentId?: string // 部署：id
    name?: string // 流程定义：名称
    tenantId?: string // 流程定义：租户id
    description?: string // 流程定义：描述
    resourceName?: string // 部署文件：名称
    id?: string // 流程定义：id
    category?: string // 流程定义：分类
    version?: number // 流程定义：版本号，format：int32
    key?: string // 流程定义：key
    suspended?: boolean // 流程定义：是否是暂停状态
}

// 流程定义-通过主键id，查看详情
export function SysActivitiProcessDefinitionInfoById(form: NotBlankString, config?: IHttpConfig) {
    return $http.myProPost<SysActivitiProcessDefinitionVO>('/sys/activiti/processDefinition/infoById', form, config)
}

export interface SysActivitiProcessDefinitionPageDTO {
    current?: string // 第几页，format：int64
    onlyQueryCount?: boolean // 是否只查询：数量，默认：false
    deploymentId?: string // 部署：id
    name?: string // 流程定义：名称
    pageSize?: string // 每页显示条数，format：int64
    tenantIdSet?: string[] // 租户 idSet，format：int64
    resourceName?: string // 部署文件：名称
    id?: string // 流程定义：id
    key?: string // 流程定义：key
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
}

// 流程定义-分页排序查询
export function SysActivitiProcessDefinitionPage(form: SysActivitiProcessDefinitionPageDTO, config?: IHttpConfig) {
    return $http.myProPagePost<SysActivitiProcessDefinitionVO>('/sys/activiti/processDefinition/page', form, config)
}

export interface SysActivitiHistoryTaskPageDTO {
    processDefinitionId?: string // 流程定义：id
    processInstanceId?: string // 流程实例：id
    current?: string // 第几页，format：int64
    processInstanceBusinessKey?: string // 流程实例：业务key
    pageSize?: string // 每页显示条数，format：int64
    taskId?: string // 任务：id
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
    processDefinitionKey?: string // 流程定义：key
}

export interface SysActivitiHistoryTaskVO {
    owner?: string // 任务：拥有者
    processInstanceId?: string // 流程实例：id
    processDefinitionId?: string // 流程定义：id
    formKey?: string // 任务：表单key
    durationInMillis?: string // 任务：开始到结束的时间，单位：毫秒，format：int64
    parentTaskId?: string // 任务：父级任务 id
    dueDate?: string // 任务：到期时间，format：date-time
    description?: string // 任务：描述
    priority?: number // 任务：优先级，format：int32
    deleteReason?: string // 删除原因
    claimTime?: string // 任务：执行者接受任务的时间，format：date-time
    executionId?: string // 执行器：id
    taskDefinitionKey?: string // 任务定义：id
    workTimeInMillis?: string // 任务：接受到结束的时间，单位：毫秒，format：int64
    createTime?: string // 任务：创建时间，format：date-time
    name?: string // 任务：名称
    tenantId?: string // 任务：租户 id
    businessKey?: string // 任务：业务key
    startTime?: string // 任务：开始时间，format：date-time
    id?: string // 任务：id
    assignee?: string // 任务：执行者
    endTime?: string // 任务：结束时间，format：date-time
    category?: string // 任务：分类
    processVariableMap?: object // 任务：参数
}

// 历史任务-分页排序查询
export function SysActivitiHistoryTaskPage(form: SysActivitiHistoryTaskPageDTO, config?: IHttpConfig) {
    return $http.myProPagePost<SysActivitiHistoryTaskVO>('/sys/activiti/history/task/page', form, config)
}

export interface SysActivitiDeployPageDTO {
    current?: string // 第几页，format：int64
    name?: string // 部署：名称
    pageSize?: string // 每页显示条数，format：int64
    tenantIdSet?: string[] // 租户 idSet，format：int64
    id?: string // 部署：id
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
}

export interface SysActivitiDeploymentVO {
    processDefinitionId?: string // 流程定义：id
    name?: string // 部署：名称
    tenantId?: string // 部署：租户id
    id?: string // 部署：id
    category?: string // 部署：分类
    version?: number // 部署：版本号，format：int32
    key?: string // 部署：key，格式：流程定义 id + # + 流程定义 key
    deploymentTime?: string // 部署：创建时间，format：date-time
    processDefinitionKey?: string // 流程定义：key
}

// 部署-分页排序查询
export function SysActivitiDeployPage(form: SysActivitiDeployPageDTO, config?: IHttpConfig) {
    return $http.myProPagePost<SysActivitiDeploymentVO>('/sys/activiti/deploy/page', form, config)
}

// 部署-批量删除
export function SysActivitiDeployDeleteByIdSet(form: NotEmptyStringSet, config?: IHttpConfig) {
    return $http.myPost<string>('/sys/activiti/deploy/deleteByIdSet', form, config)
}

export interface SysActivitiProcessInstancePageDTO {
    processDefinitionId?: string // 流程定义：id
    processInstanceId?: string // 流程实例：id
    current?: string // 第几页，format：int64
    processInstanceBusinessKey?: string // 流程实例：业务key
    pageSize?: string // 每页显示条数，format：int64
    tenantIdSet?: string[] // 租户 idSet，format：int64
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
    processDefinitionKey?: string // 流程定义：key
}

// 流程实例-分页排序查询
export function SysActivitiProcessInstancePage(form: SysActivitiProcessInstancePageDTO, config?: IHttpConfig) {
    return $http.myProPagePost<SysActivitiProcessInstanceVO>('/sys/activiti/processInstance/page', form, config)
}

export interface SysActivitiProcessInstanceInsertOrUpdateByKeyDTO {
    businessKey?: string // 业务：key，required：true
    variableMap?: object // 参数：map
    processDefinitionKey?: string // 流程定义：key，required：true
}

// 流程实例-新增/修改，通过key
export function SysActivitiProcessInstanceInsertOrUpdateByKey(form: SysActivitiProcessInstanceInsertOrUpdateByKeyDTO, config?: IHttpConfig) {
    return $http.myPost<string>('/sys/activiti/processInstance/insertOrUpdate/byKey', form, config)
}

export interface SysActivitiDeployInsertOrUpdateByFileDTO {
    file?: string // null，required：true，format：binary
}

// 部署-新增/修改，通过文件上传
export function SysActivitiDeployInsertOrUpdateByFile(form: SysActivitiDeployInsertOrUpdateByFileDTO, config?: IHttpConfig) {
    return $http.myPost<SysActivitiDeployInsertOrUpdateVO>('/sys/activiti/deploy/insertOrUpdate/byFile', form, config)
}

// 部署-下载文件
export function SysActivitiDeployDownloadResourceFile(form: NotBlankString, config?: IHttpConfig) {
    return $http.myPost<void>('/sys/activiti/deploy/downloadResourceFile', form, config)
}

// 流程实例-批量激活
export function SysActivitiProcessInstanceActiveByIdSet(form: NotEmptyStringSet, config?: IHttpConfig) {
    return $http.myPost<string>('/sys/activiti/processInstance/activeByIdSet', form, config)
}

export interface SysActivitiHistoryProcessInstancePageDTO {
    processDefinitionId?: string // 流程定义：id
    current?: string // 第几页，format：int64
    businessKey?: string // 流程实例：业务key
    ended?: boolean // 流程实例：是否是结束状态
    pageSize?: string // 每页显示条数，format：int64
    tenantIdSet?: string[] // 租户 idSet，format：int64
    processDefinitionName?: string // 流程定义：名称
    id?: string // 流程实例：id
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
    processDefinitionKey?: string // 流程定义：key
}

export interface SysActivitiHistoryProcessInstanceVO {
    processDefinitionId?: string // 流程定义：id
    durationInMillis?: string // 任务：开始到结束的时间，单位：毫秒，format：int64
    description?: string // 流程实例：描述
    processDefinitionName?: string // 流程定义：名称
    endActivityId?: string // 结束活动：id
    deleteReason?: string // 删除原因
    suspended?: boolean // 流程实例：是否是暂停状态，默认：false
    processDefinitionKey?: string // 流程定义：key
    startUserId?: string // 流程实例：开始用户主键 id
    startActivityId?: string // 开始活动：id
    deploymentId?: string // 部署：id
    businessKey?: string // 流程实例：业务key
    ended?: boolean // 流程实例：是否是结束状态
    tenantId?: string // 流程实例：租户 id
    name?: string // 流程实例：名称
    startTime?: string // 流程实例：开始时间，format：date-time
    id?: string // 流程实例：id
    endTime?: string // 流程实例：结束时间，format：date-time
    processDefinitionVersion?: number // 流程定义：版本号，format：int32
    processVariableMap?: object // 流程实例：参数
    superProcessInstanceId?: string // 超级流程实例：id
}

// 历史流程实例-分页排序查询
export function SysActivitiHistoryProcessInstancePage(form: SysActivitiHistoryProcessInstancePageDTO, config?: IHttpConfig) {
    return $http.myProPagePost<SysActivitiHistoryProcessInstanceVO>('/sys/activiti/history/processInstance/page', form, config)
}

// 部署-批量删除，通过流程定义主键 id
export function SysActivitiDeployDeleteByProcessDefinitionIdSet(form: NotEmptyStringSet, config?: IHttpConfig) {
    return $http.myPost<string>('/sys/activiti/deploy/deleteByProcessDefinitionIdSet', form, config)
}

export interface SysActivitiTaskPageDTO {
    processDefinitionId?: string // 流程定义：id
    processInstanceId?: string // 流程实例：id
    current?: string // 第几页，format：int64
    processInstanceBusinessKey?: string // 流程实例：业务key
    pageSize?: string // 每页显示条数，format：int64
    tenantIdSet?: string[] // 租户 idSet，format：int64
    taskId?: string // 任务：id
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
    processDefinitionKey?: string // 流程定义：key
}

export interface SysActivitiTaskVO {
    owner?: string // 任务：拥有者
    processInstanceId?: string // 流程实例：id
    processDefinitionId?: string // 流程定义：id
    formKey?: string // 任务：表单key
    parentTaskId?: string // 任务：父级任务 id
    dueDate?: string // 任务：到期时间，format：date-time
    description?: string // 任务：描述
    priority?: number // 任务：优先级，format：int32
    suspended?: boolean // 任务：是否暂停
    claimTime?: string // 任务：执行者接受任务的时间，format：date-time
    executionId?: string // 执行器：id
    taskDefinitionKey?: string // 任务定义：id
    createTime?: string // 任务：创建时间，format：date-time
    name?: string // 任务：名称
    tenantId?: string // 任务：租户 id
    businessKey?: string // 任务：业务key
    id?: string // 任务：id
    assignee?: string // 任务：执行者
    category?: string // 任务：分类
    processVariableMap?: object // 任务：参数
}

// 任务-分页排序查询
export function SysActivitiTaskPage(form: SysActivitiTaskPageDTO, config?: IHttpConfig) {
    return $http.myProPagePost<SysActivitiTaskVO>('/sys/activiti/task/page', form, config)
}

// 历史流程实例-批量删除
export function SysActivitiHistoryProcessInstanceDeleteByIdSet(form: NotEmptyStringSet, config?: IHttpConfig) {
    return $http.myPost<string>('/sys/activiti/history/processInstance/deleteByIdSet', form, config)
}
