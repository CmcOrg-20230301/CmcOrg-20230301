package com.cmcorg20230301.be.engine.flow.activiti.service.impl;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.activiti.engine.repository.*;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.be.engine.flow.activiti.model.bo.*;
import com.cmcorg20230301.be.engine.flow.activiti.model.dto.*;
import com.cmcorg20230301.be.engine.flow.activiti.model.enums.SysActivitiParamItemTypeEnum;
import com.cmcorg20230301.be.engine.flow.activiti.model.interfaces.ISysActivitiParamItemType;
import com.cmcorg20230301.be.engine.flow.activiti.model.interfaces.ISysActivitiTaskCategory;
import com.cmcorg20230301.be.engine.flow.activiti.model.vo.*;
import com.cmcorg20230301.be.engine.flow.activiti.service.SysActivitiService;
import com.cmcorg20230301.be.engine.flow.activiti.util.SysActivitiUtil;
import com.cmcorg20230301.be.engine.model.model.dto.NotBlankString;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyStringAndVariableMapSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyStringSet;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import com.cmcorg20230301.be.engine.redisson.util.RedissonUtil;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.model.enums.SysFileUploadTypeEnum;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.util.MyThreadUtil;
import com.cmcorg20230301.be.engine.security.util.ResponseUtil;
import com.cmcorg20230301.be.engine.security.util.UserUtil;
import com.cmcorg20230301.be.engine.util.util.CallBack;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import lombok.SneakyThrows;

@Service
public class SysActivitiServiceImpl implements SysActivitiService {

    private static RepositoryService repositoryService;

    @Resource
    public void setRepositoryService(RepositoryService repositoryService) {
        SysActivitiServiceImpl.repositoryService = repositoryService;
    }

    private static RuntimeService runtimeService;

    @Resource
    public void setRuntimeService(RuntimeService runtimeService) {
        SysActivitiServiceImpl.runtimeService = runtimeService;
    }

    private static TaskService taskService;

    @Resource
    public void setTaskService(TaskService taskService) {
        SysActivitiServiceImpl.taskService = taskService;
    }

    @Resource
    HistoryService historyService;

    /**
     * 部署-新增/修改
     */
    @Override
    public String deployInsertOrUpdate(SysActivitiDeployInsertOrUpdateDTO dto) {

        String url = dto.getUrl();

        if (!url.endsWith(".bpmn") && !url.endsWith(".bpmn20.xml")) {
            ApiResultVO.error("操作失败：文件名请以：.bpmn 或者 .bpmn20.xml 结尾", url);
        }

        String fileName = FileNameUtil.getName(url);

        byte[] downloadByteArr = HttpUtil.downloadBytes(url);

        // 执行：部署-新增/修改
        return doDeployInsertOrUpdate(fileName, downloadByteArr);

    }

    /**
     * 执行：部署-新增/修改
     */
    private String doDeployInsertOrUpdate(String fileName, byte[] downloadByteArr) {

        Long tenantId = UserUtil.getCurrentTenantIdDefault();

        Long userId = UserUtil.getCurrentUserId();

        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().addBytes(fileName, downloadByteArr);

        deploymentBuilder.name(fileName);

        deploymentBuilder.tenantId(tenantId.toString());

        deploymentBuilder.category(userId.toString());

        Deployment deployment = deploymentBuilder.deploy();

        String deploymentId = deployment.getId();

        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();

        processDefinitionQuery.deploymentId(deploymentId);

        ProcessDefinition processDefinition = processDefinitionQuery.singleResult();

        repositoryService.setProcessDefinitionCategory(processDefinition.getId(), userId.toString());

        repositoryService.setDeploymentKey(deploymentId, processDefinition.getKey());

        return deploymentId;

    }

    /**
     * 部署-新增/修改，通过文件上传
     */
    @SneakyThrows
    @Override
    public String deployInsertOrUpdateByFile(SysActivitiDeployInsertOrUpdateByFileDTO dto) {

        SysFileUploadTypeEnum sysFileUploadTypeEnum = SysFileUploadTypeEnum.BPMN;

        // 上传文件检查
        SysFileUploadTypeEnum.uploadCheckWillError(dto.getFile(), sysFileUploadTypeEnum);

        // 执行：部署-新增/修改
        return doDeployInsertOrUpdate(dto.getFile().getOriginalFilename(), dto.getFile().getBytes());

    }

    /**
     * 部署-下载文件
     */
    @Override
    public void deployDownloadResourceFile(NotBlankString notBlankString, HttpServletResponse response) {

        String tenantId = UserUtil.getCurrentTenantIdDefault().toString();

        String userId = UserUtil.getCurrentUserId().toString();

        Deployment deployment = repositoryService.createDeploymentQuery().deploymentTenantId(tenantId)
            .deploymentCategory(userId).deploymentId(notBlankString.getValue()).singleResult();

        InputStream inputStream = repositoryService.getResourceAsStream(deployment.getId(), deployment.getName());

        ResponseUtil.getOutputStream(response, deployment.getName());

        // 推送
        ResponseUtil.flush(response, inputStream);

    }

    /**
     * 部署-分页排序查询
     */
    @Override
    public Page<SysActivitiDeploymentVO> deployPage(SysActivitiDeployPageDTO dto) {

        Long tenantId = UserUtil.getCurrentTenantIdDefault();

        Long userId = UserUtil.getCurrentUserId();

        DeploymentQuery deploymentQuery = repositoryService.createDeploymentQuery();

        deploymentQuery.deploymentTenantId(tenantId.toString());

        deploymentQuery.deploymentCategory(userId.toString());

        if (StrUtil.isNotBlank(dto.getId())) {
            deploymentQuery.deploymentId(dto.getId());
        }

        if (StrUtil.isNotBlank(dto.getName())) {
            deploymentQuery.deploymentNameLike(dto.getName());
        }

        long count = deploymentQuery.count();

        if (count == 0) {
            return new Page<>();
        }

        Page<Deployment> page = dto.page(true);

        long firstResult = (page.getCurrent() - 1) * page.getSize();

        deploymentQuery.orderByDeploymenTime().desc();

        List<Deployment> deploymentList = deploymentQuery.listPage((int)firstResult, (int)page.getSize());

        List<SysActivitiDeploymentVO> list = new ArrayList<>(deploymentList.size());

        for (Deployment item : deploymentList) {

            list.add(SysActivitiUtil.getSysActivitiDeploymentVO(item));

        }

        return new Page<SysActivitiDeploymentVO>().setTotal(count).setRecords(list);

    }

    /**
     * 部署-批量删除
     */
    @Override
    public String deployDeleteByIdSet(NotEmptyStringSet notEmptyStringSet) {

        String tenantId = UserUtil.getCurrentTenantIdDefault().toString();

        String userId = UserUtil.getCurrentUserId().toString();

        List<Deployment> deploymentList =
            repositoryService.createDeploymentQuery().deploymentTenantId(tenantId).deploymentCategory(userId).list();

        if (CollUtil.isEmpty(deploymentList)) {
            return BaseBizCodeEnum.OK;
        }

        Set<String> dbDeploymentIdSet = deploymentList.stream().map(Deployment::getId).collect(Collectors.toSet());

        Set<String> deleteDeploymentIdSet = new HashSet<>();

        for (String deploymentId : notEmptyStringSet.getIdSet()) {

            if (dbDeploymentIdSet.contains(deploymentId)) {

                deleteDeploymentIdSet.add(deploymentId);

            }

        }

        if (CollUtil.isEmpty(deleteDeploymentIdSet)) {
            return BaseBizCodeEnum.OK;
        }

        // 执行：移除
        doDeleteDeploymentIdSet(userId, tenantId, deleteDeploymentIdSet);

        return BaseBizCodeEnum.OK;

    }

    /**
     * 执行：移除
     */
    private void doDeleteDeploymentIdSet(String userId, String tenantId, Set<String> deleteDeploymentIdSet) {

        // 先：移除流程实例
        List<ProcessInstance> processInstanceList = runtimeService.createProcessInstanceQuery().startedBy(userId)
            .processInstanceTenantId(tenantId).deploymentIdIn(new ArrayList<>(deleteDeploymentIdSet)).list();

        if (CollUtil.isNotEmpty(processInstanceList)) {

            Set<String> processInstanceIdSet =
                processInstanceList.stream().map(Execution::getProcessInstanceId).collect(Collectors.toSet());

            for (String processInstanceId : processInstanceIdSet) {

                runtimeService.deleteProcessInstance(processInstanceId, null);

            }

        }

        for (String deploymentId : deleteDeploymentIdSet) {

            repositoryService.deleteDeployment(deploymentId);

        }

    }

    /**
     * 部署-批量删除，通过流程定义主键 id
     */
    @Override
    @DSTransactional
    public String deployDeleteByProcessDefinitionIdSet(NotEmptyStringSet notEmptyStringSet) {

        String tenantId = UserUtil.getCurrentTenantIdDefault().toString();

        String userId = UserUtil.getCurrentUserId().toString();

        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();

        processDefinitionQuery.processDefinitionTenantId(tenantId);

        processDefinitionQuery.processDefinitionCategory(userId);

        processDefinitionQuery.processDefinitionIds(notEmptyStringSet.getIdSet());

        List<ProcessDefinition> processDefinitionList = processDefinitionQuery.list();

        if (CollUtil.isEmpty(processDefinitionList)) {
            return BaseBizCodeEnum.OK;
        }

        Set<String> deploymentIdSet =
            processDefinitionList.stream().map(ProcessDefinition::getDeploymentId).collect(Collectors.toSet());

        // 执行：移除
        doDeleteDeploymentIdSet(userId, tenantId, deploymentIdSet);

        return BaseBizCodeEnum.OK;

    }

    /**
     * 流程定义-分页排序查询
     */
    @Override
    public Page<SysActivitiProcessDefinitionVO> processDefinitionPage(SysActivitiProcessDefinitionPageDTO dto) {

        Long tenantId = UserUtil.getCurrentTenantIdDefault();

        Long userId = UserUtil.getCurrentUserId();

        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();

        processDefinitionQuery.processDefinitionTenantId(tenantId.toString());

        processDefinitionQuery.processDefinitionCategory(userId.toString());

        if (StrUtil.isNotBlank(dto.getDeploymentId())) {
            processDefinitionQuery.deploymentId(dto.getDeploymentId());
        }

        if (StrUtil.isNotBlank(dto.getId())) {
            processDefinitionQuery.processDefinitionId(dto.getId());
        }

        if (StrUtil.isNotBlank(dto.getKey())) {
            processDefinitionQuery.processDefinitionKeyLike(dto.getKey());
        }

        if (StrUtil.isNotBlank(dto.getName())) {
            processDefinitionQuery.processDefinitionNameLike(dto.getName());
        }

        if (StrUtil.isNotBlank(dto.getResourceName())) {
            processDefinitionQuery.processDefinitionResourceName(dto.getResourceName());
        }

        long count = processDefinitionQuery.count();

        if (count == 0 || BooleanUtil.isTrue(dto.getOnlyQueryCount())) {
            return new Page<SysActivitiProcessDefinitionVO>().setTotal(count);
        }

        Page<Deployment> page = dto.page(true);

        long firstResult = (page.getCurrent() - 1) * page.getSize();

        processDefinitionQuery.orderByProcessDefinitionId().desc();

        List<ProcessDefinition> processDefinitionList =
            processDefinitionQuery.listPage((int)firstResult, (int)page.getSize());

        List<SysActivitiProcessDefinitionVO> list = new ArrayList<>(processDefinitionList.size());

        for (ProcessDefinition item : processDefinitionList) {

            list.add(SysActivitiUtil.getSysActivitiProcessDefinitionVO(item));

        }

        return new Page<SysActivitiProcessDefinitionVO>().setTotal(count).setRecords(list);

    }

    /**
     * 流程定义-通过主键id，查看详情
     */
    @Override
    public SysActivitiProcessDefinitionVO processDefinitionInfoById(NotBlankString notBlankString) {

        Long tenantId = UserUtil.getCurrentTenantIdDefault();

        String userId = UserUtil.getCurrentUserId().toString();

        ProcessDefinition processDefinition =
            repositoryService.createProcessDefinitionQuery().processDefinitionId(notBlankString.getValue())
                .processDefinitionTenantId(tenantId.toString()).processDefinitionCategory(userId).singleResult();

        return SysActivitiUtil.getSysActivitiProcessDefinitionVO(processDefinition);

    }

    /**
     * 流程实例-新增/修改
     */
    @Override
    public String processInstanceInsertOrUpdate(SysActivitiProcessInstanceInsertOrUpdateDTO dto) {

        String userId = UserUtil.getCurrentUserId().toString();

        String tenantId = UserUtil.getCurrentTenantIdDefault().toString();

        Authentication.setAuthenticatedUserId(userId); // 设置：启动流程实例的 userId

        CallBack<BpmnModel> bpmnModelCallBack = new CallBack<>();

        CallBack<SysActivitiParamBO> sysActivitiParamBoCallBack = new CallBack<>();

        // 获取：参数 map
        Map<String, Object> variableMap = getVariableMap(userId, tenantId, dto.getVariableMap(),
            dto.getProcessDefinitionId(), bpmnModelCallBack, sysActivitiParamBoCallBack);

        ExecutionEntityImpl processInstance = (ExecutionEntityImpl)runtimeService.createProcessInstanceBuilder()
            .tenantId(tenantId).processDefinitionId(dto.getProcessDefinitionId()).businessKey(dto.getBusinessKey())
            .variables(variableMap).start();

        String processInstanceId = processInstance.getProcessInstanceId();

        SysActivitiUtil.setSysActivitiParamBO(processInstanceId, sysActivitiParamBoCallBack.getValue(), true);

        MyThreadUtil.execute(() -> {

            // 通过：流程实例，执行任务
            doTaskByProcessInstance(processInstance, bpmnModelCallBack);

        });

        return processInstanceId;

    }

    /**
     * 通过：流程实例，执行任务
     */
    public static void doTaskByProcessInstance(ProcessInstance processInstance,
        @Nullable CallBack<BpmnModel> bpmnModelCallBack) {

        BpmnModel bpmnModel = null;

        if (bpmnModelCallBack != null) {

            bpmnModel = bpmnModelCallBack.getValue();

        }

        if (bpmnModel == null) {

            bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());

        }

        // 获取：nodeBoMap
        Map<String, SysActivitiNodeBO> nodeBoMap = getNodeBoMap(bpmnModel);

        // 执行任务：通过，流程实例 id
        execTaskByProcessInstanceId(processInstance.getProcessInstanceId(), nodeBoMap);

    }

    /**
     * 获取：nodeBoMap
     */
    public static Map<String, SysActivitiNodeBO> getNodeBoMap(BpmnModel bpmnModel) {

        Map<String, SysActivitiNodeBO> nodeBoMap = new HashMap<>();

        for (Map.Entry<String, FlowElement> item : bpmnModel.getMainProcess().getFlowElementMap().entrySet()) {

            FlowElement value = item.getValue();

            if (value instanceof SequenceFlow) { // 如果是：线

                SequenceFlow sequenceFlow = (SequenceFlow)value;

                String sourceRef = sequenceFlow.getSourceRef();

                String targetRef = sequenceFlow.getTargetRef();

                SysActivitiNodeBO sourceSysActivitiNodeBO =
                    nodeBoMap.computeIfAbsent(sourceRef, k -> new SysActivitiNodeBO());

                SysActivitiNodeBO targetSysActivitiNodeBO =
                    nodeBoMap.computeIfAbsent(targetRef, k -> new SysActivitiNodeBO());

                sourceSysActivitiNodeBO.getSufLineSet().add(sequenceFlow);

                sourceSysActivitiNodeBO.getSufNodeSet().add(sequenceFlow.getTargetFlowElement());

                targetSysActivitiNodeBO.getPreLineSet().add(sequenceFlow);

                targetSysActivitiNodeBO.getPreNodeSet().add(sequenceFlow.getSourceFlowElement());

            }

        }

        return nodeBoMap;

    }

    /**
     * 执行任务：通过，流程实例 id
     */
    @SneakyThrows
    public static void execTaskByProcessInstanceId(String processInstanceId, Map<String, SysActivitiNodeBO> nodeBoMap) {

        TaskQuery taskQuery = taskService.createTaskQuery();

        taskQuery.processInstanceId(processInstanceId);

        List<Task> list = taskQuery.list();

        if (CollUtil.isEmpty(list)) {
            return;
        }

        // 执行：任务
        for (Task item : list) {

            if (item.isSuspended()) {
                return; // 如果：任务暂停了，则停止
            }

            String description = item.getDescription();

            if (StrUtil.isBlank(description)) {

                taskService.complete(item.getId());

                continue;

            }

            SysActivitiTaskBO sysActivitiTaskBO = JSONUtil.toBean(description, SysActivitiTaskBO.class);

            if (sysActivitiTaskBO.getCategory() == null) {

                taskService.complete(item.getId());

                continue;

            }

            ISysActivitiTaskCategory iSysActivitiTaskCategory =
                SysActivitiUtil.TASK_CATEGORY_MAP.get(sysActivitiTaskBO.getCategory());

            if (iSysActivitiTaskCategory == null) {

                taskService.complete(item.getId());

                continue;

            }

            boolean endAutoFlag = RedissonUtil
                .doLock(BaseRedisKeyEnum.PRE_SYS_ACTIVITI_PROCESS_INSTANCE_ID + item.getProcessInstanceId(), () -> {

                    // 执行任务
                    return execTaskHandler(nodeBoMap, item, iSysActivitiTaskCategory, sysActivitiTaskBO);

                });

            if (endAutoFlag) {

                return;

            }

        }

        // 继续执行下一个任务
        execTaskByProcessInstanceId(processInstanceId, nodeBoMap);

    }

    /**
     * 执行任务
     * 
     * @return true 结束自动执行任务 false 继续执行下一个任务
     */
    @SneakyThrows
    @NotNull
    private static Boolean execTaskHandler(Map<String, SysActivitiNodeBO> nodeBoMap, Task item,
        ISysActivitiTaskCategory iSysActivitiTaskCategory, SysActivitiTaskBO sysActivitiTaskBO) {

        Func1<SysActivitiTaskHandlerBO, SysActivitiTaskHandlerVO> handler = iSysActivitiTaskCategory.getHandler();

        SysActivitiTaskHandlerVO sysActivitiTaskHandlerVO =
            handler.call(new SysActivitiTaskHandlerBO(nodeBoMap, item, sysActivitiTaskBO));

        if (BooleanUtil.isTrue(sysActivitiTaskHandlerVO.getCompleteFlag())) {

            taskService.complete(item.getId());

        }

        if (BooleanUtil.isTrue(sysActivitiTaskHandlerVO.getEndAutoFlag())) {

            return true;

        }

        return false;

    }

    /**
     * 获取：参数 map
     */
    @NotNull
    private static Map<String, Object> getVariableMap(String userId, String tenantId,
        Map<String, Object> variableMapTemp, String processDefinitionId, CallBack<BpmnModel> bpmnModelCallBack,
        CallBack<SysActivitiParamBO> sysActivitiParamBoCallBack) {

        Map<String, Object> variableMap = MapUtil.newHashMap();

        variableMap.put(SysActivitiUtil.VARIABLE_NAME_USER_ID, userId); // 设置：启动参数

        variableMap.put(SysActivitiUtil.VARIABLE_NAME_TENANT_ID, tenantId); // 设置：启动参数

        if (CollUtil.isEmpty(variableMapTemp)) {

            return variableMap;

        }

        String inputValue = (String)variableMapTemp.get("inputValue");

        if (StrUtil.isBlank(inputValue)) {

            return variableMap;

        }

        Integer inputType = (Integer)variableMapTemp.get("inputType");

        ISysActivitiParamItemType iSysActivitiParamItemType = SysActivitiUtil.PARAM_ITEM_TYPE_MAP.get(inputType);

        if (iSysActivitiParamItemType == null) {
            iSysActivitiParamItemType = SysActivitiParamItemTypeEnum.TEXT;
        }

        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);

        bpmnModelCallBack.setValue(bpmnModel);

        for (Map.Entry<String, FlowElement> item : bpmnModel.getMainProcess().getFlowElementMap().entrySet()) {

            FlowElement value = item.getValue();

            if (value instanceof StartEvent) {

                SysActivitiParamBO sysActivitiParamBO = new SysActivitiParamBO();

                // 设置：回调值
                sysActivitiParamBoCallBack.setValue(sysActivitiParamBO);

                Map<String, List<SysActivitiParamItemBO>> inMap = MapUtil.newHashMap();

                sysActivitiParamBO.setInMap(inMap);

                StartEvent startEvent = (StartEvent)value;

                // 往：inMap里面添加数据
                putInMap(iSysActivitiParamItemType, inputValue, inMap, startEvent.getId(), null);

                List<SequenceFlow> outgoingFlowList = startEvent.getOutgoingFlows();

                for (SequenceFlow subItem : outgoingFlowList) {

                    // 往：inMap里面添加数据
                    putInMap(iSysActivitiParamItemType, inputValue, inMap, subItem.getTargetRef(), startEvent.getId());

                }

                break;

            }

        }

        return variableMap;

    }

    /**
     * 往：inMap里面添加数据
     */
    private static void putInMap(ISysActivitiParamItemType iSysActivitiParamItemType, String inputValue,
        Map<String, List<SysActivitiParamItemBO>> inMap, String id, @Nullable String fromNodeId) {

        SysActivitiParamItemBO sysActivitiParamItemBO = new SysActivitiParamItemBO();

        SysActivitiParamSubItemBO sysActivitiParamSubItemBO = new SysActivitiParamSubItemBO();

        sysActivitiParamSubItemBO.setType(iSysActivitiParamItemType.getCode());

        sysActivitiParamSubItemBO.setValue(inputValue);

        sysActivitiParamItemBO.setParamList(CollUtil.newArrayList(sysActivitiParamSubItemBO));

        sysActivitiParamItemBO.setFromNodeId(fromNodeId);

        inMap.put(id, CollUtil.newArrayList(sysActivitiParamItemBO));

    }

    /**
     * 流程实例-新增/修改，通过key
     */
    @Override
    public String processInstanceInsertOrUpdateByKey(SysActivitiProcessInstanceInsertOrUpdateByKeyDTO dto) {

        String userId = UserUtil.getCurrentUserId().toString();

        String tenantId = UserUtil.getCurrentTenantIdDefault().toString();

        Authentication.setAuthenticatedUserId(userId); // 设置：启动流程实例的 userId

        CallBack<BpmnModel> bpmnModelCallBack = new CallBack<>();

        CallBack<SysActivitiParamBO> sysActivitiParamBoCallBack = new CallBack<>();

        ProcessDefinition processDefinition =
            repositoryService.createProcessDefinitionQuery().processDefinitionKey(dto.getProcessDefinitionKey())
                .processDefinitionTenantId(tenantId).processDefinitionCategory(userId).singleResult();

        // 获取：参数 map
        Map<String, Object> variableMap = getVariableMap(userId, tenantId, dto.getVariableMap(),
            processDefinition.getId(), bpmnModelCallBack, sysActivitiParamBoCallBack);

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKeyAndTenantId(
            dto.getProcessDefinitionKey(), dto.getBusinessKey(), variableMap, tenantId);

        String processInstanceId = processInstance.getProcessInstanceId();

        SysActivitiUtil.setSysActivitiParamBO(processInstanceId, sysActivitiParamBoCallBack.getValue(), true);

        return processInstanceId;

    }

    /**
     * 流程实例-通过主键id，查看详情
     */
    @Override
    public SysActivitiProcessInstanceVO processInstanceInfoById(NotBlankString notBlankString) {

        Long tenantId = UserUtil.getCurrentTenantIdDefault();

        String userId = UserUtil.getCurrentUserId().toString();

        ProcessInstance processInstance =
            runtimeService.createProcessInstanceQuery().processInstanceId(notBlankString.getValue())
                .processInstanceTenantId(tenantId.toString()).startedBy(userId).singleResult();

        return SysActivitiUtil.getSysActivitiProcessInstanceVO(processInstance);

    }

    /**
     * 流程实例-分页排序查询
     */
    @Override
    public Page<SysActivitiProcessInstanceVO> processInstancePage(SysActivitiProcessInstancePageDTO dto) {

        Long tenantId = UserUtil.getCurrentTenantIdDefault();

        String userId = UserUtil.getCurrentUserId().toString();

        ProcessInstanceQuery processInstanceQuery = runtimeService.createProcessInstanceQuery();

        processInstanceQuery.processInstanceTenantId(tenantId.toString());

        processInstanceQuery.startedBy(userId);

        if (StrUtil.isNotBlank(dto.getProcessDefinitionId())) {
            processInstanceQuery.processDefinitionId(dto.getProcessDefinitionId());
        }

        if (StrUtil.isNotBlank(dto.getProcessDefinitionKey())) {
            processInstanceQuery.processDefinitionKey(dto.getProcessDefinitionKey());
        }

        if (StrUtil.isNotBlank(dto.getProcessInstanceId())) {
            processInstanceQuery.processInstanceId(dto.getProcessInstanceId());
        }

        if (StrUtil.isNotBlank(dto.getProcessInstanceBusinessKey())) {
            processInstanceQuery.processInstanceBusinessKey(dto.getProcessInstanceBusinessKey());
        }

        long count = processInstanceQuery.count();

        if (count == 0) {
            return new Page<>();
        }

        Page<Deployment> page = dto.page(true);

        long firstResult = (page.getCurrent() - 1) * page.getSize();

        processInstanceQuery.orderByProcessInstanceId().desc();

        List<ProcessInstance> processInstanceList =
            processInstanceQuery.listPage((int)firstResult, (int)page.getSize());

        List<SysActivitiProcessInstanceVO> list = new ArrayList<>(processInstanceList.size());

        for (ProcessInstance item : processInstanceList) {

            list.add(SysActivitiUtil.getSysActivitiProcessInstanceVO(item));

        }

        return new Page<SysActivitiProcessInstanceVO>().setTotal(count).setRecords(list);

    }

    /**
     * 流程实例-批量挂起
     */
    @Override
    public String processInstanceSuspendByIdSet(NotEmptyStringSet notEmptyStringSet) {

        String tenantId = UserUtil.getCurrentTenantIdDefault().toString();

        String userId = UserUtil.getCurrentUserId().toString();

        for (String processInstanceId : notEmptyStringSet.getIdSet()) {

            // 避免：出现挂起不属于自己租户的流程实例
            runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId)
                .processInstanceTenantId(tenantId).startedBy(userId).singleResult();

            runtimeService.suspendProcessInstanceById(processInstanceId);

        }

        return BaseBizCodeEnum.OK;

    }

    /**
     * 流程实例-批量激活
     */
    @Override
    public String processInstanceActiveByIdSet(NotEmptyStringSet notEmptyStringSet) {

        String tenantId = UserUtil.getCurrentTenantIdDefault().toString();

        String userId = UserUtil.getCurrentUserId().toString();

        for (String processInstanceId : notEmptyStringSet.getIdSet()) {

            // 避免：出现激活不属于自己租户的流程实例
            ProcessInstance processInstance =
                runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId)
                    .processInstanceTenantId(tenantId).startedBy(userId).singleResult();

            runtimeService.activateProcessInstanceById(processInstanceId);

            MyThreadUtil.execute(() -> {

                // 通过：流程实例，执行任务
                doTaskByProcessInstance(processInstance, null);

            });

        }

        return BaseBizCodeEnum.OK;

    }

    /**
     * 流程实例-批量删除
     */
    @Override
    public String processInstanceDeleteByIdSet(NotEmptyStringSet notEmptyStringSet) {

        String tenantId = UserUtil.getCurrentTenantIdDefault().toString();

        String userId = UserUtil.getCurrentUserId().toString();

        for (String processInstanceId : notEmptyStringSet.getIdSet()) {

            // 避免：出现删除不属于自己租户的流程实例
            runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId)
                .processInstanceTenantId(tenantId).startedBy(userId).singleResult();

            runtimeService.deleteProcessInstance(processInstanceId, null);

        }

        return BaseBizCodeEnum.OK;

    }

    /**
     * 任务-分页排序查询
     */
    @Override
    public Page<SysActivitiTaskVO> taskPage(SysActivitiTaskPageDTO dto) {

        Long tenantId = UserUtil.getCurrentTenantIdDefault();

        String userId = UserUtil.getCurrentUserId().toString();

        TaskQuery taskQuery = taskService.createTaskQuery();

        taskQuery.taskTenantId(tenantId.toString());

        taskQuery.processVariableValueEquals(SysActivitiUtil.VARIABLE_NAME_USER_ID, userId);

        if (StrUtil.isNotBlank(dto.getProcessDefinitionId())) {
            taskQuery.processDefinitionId(dto.getProcessDefinitionId());
        }

        if (StrUtil.isNotBlank(dto.getProcessDefinitionKey())) {
            taskQuery.processDefinitionKey(dto.getProcessDefinitionKey());
        }

        if (StrUtil.isNotBlank(dto.getProcessInstanceId())) {
            taskQuery.processInstanceId(dto.getProcessInstanceId());
        }

        if (StrUtil.isNotBlank(dto.getProcessInstanceBusinessKey())) {
            taskQuery.processInstanceBusinessKey(dto.getProcessInstanceBusinessKey());
        }

        if (StrUtil.isNotBlank(dto.getTaskId())) {
            taskQuery.taskId(dto.getTaskId());
        }

        long count = taskQuery.count();

        if (count == 0) {
            return new Page<>();
        }

        Page<Deployment> page = dto.page(true);

        long firstResult = (page.getCurrent() - 1) * page.getSize();

        taskQuery.orderByTaskCreateTime().desc();

        List<Task> taskList = taskQuery.listPage((int)firstResult, (int)page.getSize());

        List<SysActivitiTaskVO> list = new ArrayList<>(taskList.size());

        for (Task item : taskList) {

            list.add(SysActivitiUtil.getSysActivitiTaskVO(item));

        }

        return new Page<SysActivitiTaskVO>().setTotal(count).setRecords(list);

    }

    /**
     * 任务-批量接受
     */
    @Override
    public String taskClaimByIdSet(NotEmptyStringSet notEmptyStringSet) {

        String userId = UserUtil.getCurrentUserId().toString();

        String tenantId = UserUtil.getCurrentTenantIdDefault().toString();

        for (String taskId : notEmptyStringSet.getIdSet()) {

            // 避免：出现接受不属于自己租户的任务
            taskService.createTaskQuery().taskId(taskId).taskTenantId(tenantId)
                .processVariableValueEquals(SysActivitiUtil.VARIABLE_NAME_USER_ID, userId).singleResult();

            taskService.claim(taskId, userId);

        }

        return BaseBizCodeEnum.OK;

    }

    /**
     * 任务-批量归还
     */
    @Override
    public String taskReturnByIdSet(NotEmptyStringSet notEmptyStringSet) {

        String tenantId = UserUtil.getCurrentTenantIdDefault().toString();

        String userId = UserUtil.getCurrentUserId().toString();

        for (String taskId : notEmptyStringSet.getIdSet()) {

            // 避免：出现归还不属于自己租户的任务
            taskService.createTaskQuery().taskId(taskId).taskTenantId(tenantId)
                .processVariableValueEquals(SysActivitiUtil.VARIABLE_NAME_USER_ID, userId).singleResult();

            taskService.unclaim(taskId);

        }

        return BaseBizCodeEnum.OK;

    }

    /**
     * 任务-批量完成
     */
    @Override
    public String taskCompleteByIdSet(NotEmptyStringAndVariableMapSet notEmptyStringAndVariableMapSet) {

        String tenantId = UserUtil.getCurrentTenantIdDefault().toString();

        String userId = UserUtil.getCurrentUserId().toString();

        for (String taskId : notEmptyStringAndVariableMapSet.getIdSet()) {

            // 避免：出现完成不属于自己租户的任务
            taskService.createTaskQuery().taskId(taskId).taskTenantId(tenantId)
                .processVariableValueEquals(SysActivitiUtil.VARIABLE_NAME_USER_ID, userId).singleResult();

            taskService.complete(taskId, notEmptyStringAndVariableMapSet.getVariableMap());

        }

        return BaseBizCodeEnum.OK;

    }

    /**
     * 历史任务-分页排序查询
     */
    @Override
    public Page<SysActivitiHistoryTaskVO> historyTaskPage(SysActivitiHistoryTaskPageDTO dto) {

        Long tenantId = UserUtil.getCurrentTenantIdDefault();

        String userId = UserUtil.getCurrentUserId().toString();

        HistoricTaskInstanceQuery historicTaskInstanceQuery = historyService.createHistoricTaskInstanceQuery();

        historicTaskInstanceQuery.taskTenantId(tenantId.toString());

        historicTaskInstanceQuery.processVariableValueEquals(SysActivitiUtil.VARIABLE_NAME_USER_ID, userId);

        if (StrUtil.isNotBlank(dto.getProcessDefinitionId())) {
            historicTaskInstanceQuery.processDefinitionId(dto.getProcessDefinitionId());
        }

        if (StrUtil.isNotBlank(dto.getProcessDefinitionKey())) {
            historicTaskInstanceQuery.processDefinitionKey(dto.getProcessDefinitionKey());
        }

        if (StrUtil.isNotBlank(dto.getProcessInstanceId())) {
            historicTaskInstanceQuery.processInstanceId(dto.getProcessInstanceId());
        }

        if (StrUtil.isNotBlank(dto.getProcessInstanceBusinessKey())) {
            historicTaskInstanceQuery.processInstanceBusinessKey(dto.getProcessInstanceBusinessKey());
        }

        if (StrUtil.isNotBlank(dto.getTaskId())) {
            historicTaskInstanceQuery.taskId(dto.getTaskId());
        }

        long count = historicTaskInstanceQuery.count();

        if (count == 0) {
            return new Page<>();
        }

        Page<Deployment> page = dto.page(true);

        long firstResult = (page.getCurrent() - 1) * page.getSize();

        historicTaskInstanceQuery.orderByHistoricTaskInstanceEndTime().desc();

        List<HistoricTaskInstance> historicTaskInstanceList =
            historicTaskInstanceQuery.listPage((int)firstResult, (int)page.getSize());

        List<SysActivitiHistoryTaskVO> list = new ArrayList<>(historicTaskInstanceList.size());

        for (HistoricTaskInstance item : historicTaskInstanceList) {

            list.add(SysActivitiUtil.getSysActivitiHistoryTaskVO(item));

        }

        if (CollUtil.isNotEmpty(list)) {

            SysActivitiHistoryTaskVO sysActivitiHistoryTaskVO = list.get(0);

            SysActivitiParamBO sysActivitiParamBO =
                SysActivitiUtil.getSysActivitiParamBO(sysActivitiHistoryTaskVO.getProcessInstanceId());

            if (sysActivitiParamBO != null) {

                Map<String, Object> processVariableMap = sysActivitiHistoryTaskVO.getProcessVariableMap();

                if (processVariableMap == null) {

                    processVariableMap = MapUtil.newHashMap();

                    sysActivitiHistoryTaskVO.setProcessVariableMap(processVariableMap);

                }

                processVariableMap.put(SysActivitiUtil.VARIABLE_NAME_PROCESS_INSTANCE_VARIABLE, sysActivitiParamBO);

            }

        }

        return new Page<SysActivitiHistoryTaskVO>().setTotal(count).setRecords(list);

    }

    /**
     * 历史流程实例-分页排序查询
     */
    @SneakyThrows
    @Override
    public Page<SysActivitiHistoryProcessInstanceVO>
        historyProcessInstancePage(SysActivitiHistoryProcessInstancePageDTO dto) {

        String tenantId = UserUtil.getCurrentTenantIdDefault().toString();

        String userId = UserUtil.getCurrentUserId().toString();

        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery();

        historicProcessInstanceQuery.processInstanceTenantId(tenantId);

        historicProcessInstanceQuery.startedBy(userId);

        if (StrUtil.isNotBlank(dto.getProcessDefinitionId())) {
            historicProcessInstanceQuery.processDefinitionId(dto.getProcessDefinitionId());
        }

        if (StrUtil.isNotBlank(dto.getProcessDefinitionKey())) {
            historicProcessInstanceQuery.processDefinitionKey(dto.getProcessDefinitionKey());
        }

        if (StrUtil.isNotBlank(dto.getProcessDefinitionName())) {
            historicProcessInstanceQuery.processDefinitionName(dto.getProcessDefinitionName());
        }

        if (StrUtil.isNotBlank(dto.getId())) {
            historicProcessInstanceQuery.processInstanceId(dto.getId());
        }

        if (StrUtil.isNotBlank(dto.getBusinessKey())) {
            historicProcessInstanceQuery.processInstanceBusinessKey(dto.getBusinessKey());
        }

        if (dto.getEnded() != null) {

            if (BooleanUtil.isTrue(dto.getEnded())) {

                historicProcessInstanceQuery.finished();

            } else {

                historicProcessInstanceQuery.unfinished();

            }

        }

        long count = historicProcessInstanceQuery.count();

        if (count == 0) {
            return new Page<>();
        }

        Page<Deployment> page = dto.page(true);

        long firstResult = (page.getCurrent() - 1) * page.getSize();

        historicProcessInstanceQuery.orderByProcessInstanceEndTime().desc();

        List<HistoricProcessInstance> historicProcessInstanceList =
            historicProcessInstanceQuery.listPage((int)firstResult, (int)page.getSize());

        List<SysActivitiHistoryProcessInstanceVO> list = new ArrayList<>(historicProcessInstanceList.size());

        Set<String> processInstanceIdSet = new HashSet<>();

        for (HistoricProcessInstance item : historicProcessInstanceList) {

            list.add(SysActivitiUtil.getSysActivitiHistoryProcessInstanceVO(item));

            if (item.getEndTime() == null) {

                processInstanceIdSet.add(item.getId());

            }

        }

        if (CollUtil.isNotEmpty(processInstanceIdSet)) {

            ProcessInstanceQuery processInstanceQuery = runtimeService.createProcessInstanceQuery();

            processInstanceQuery.processInstanceIds(processInstanceIdSet);

            List<ProcessInstance> processInstanceList = processInstanceQuery.list();

            if (CollUtil.isNotEmpty(processInstanceList)) {

                Map<String, Boolean> isSuspendedMap = processInstanceList.stream()
                    .collect(Collectors.toMap(Execution::getId, ProcessInstance::isSuspended));

                for (SysActivitiHistoryProcessInstanceVO item : list) {

                    item.setSuspended(isSuspendedMap.get(item.getId())); // 设置：是否是暂停状态

                }

            }

        }

        return new Page<SysActivitiHistoryProcessInstanceVO>().setTotal(count).setRecords(list);

    }

    /**
     * 历史流程实例-批量删除
     */
    @Override
    public String historyProcessInstanceDeleteByIdSet(NotEmptyStringSet notEmptyStringSet) {

        String tenantId = UserUtil.getCurrentTenantIdDefault().toString();

        String userId = UserUtil.getCurrentUserId().toString();

        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery();

        historicProcessInstanceQuery.processInstanceTenantId(tenantId);

        historicProcessInstanceQuery.startedBy(userId);

        historicProcessInstanceQuery.processInstanceIds(notEmptyStringSet.getIdSet());

        List<HistoricProcessInstance> historicProcessInstanceList = historicProcessInstanceQuery.list();

        if (CollUtil.isEmpty(historicProcessInstanceList)) {
            return BaseBizCodeEnum.OK;
        }

        Set<String> processInstanceIdSet =
            historicProcessInstanceList.stream().map(HistoricProcessInstance::getId).collect(Collectors.toSet());

        for (String processInstanceId : processInstanceIdSet) {

            historyService.deleteHistoricProcessInstance(processInstanceId);

        }

        SysActivitiUtil.deleteSysActivitiParamBO(processInstanceIdSet);

        return BaseBizCodeEnum.OK;

    }

}
