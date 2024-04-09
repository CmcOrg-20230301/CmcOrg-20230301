package com.cmcorg20230301.be.engine.flow.activiti.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.flow.activiti.mapper.SysActivitiProcessInstanceMapper;
import com.cmcorg20230301.be.engine.flow.activiti.model.bo.*;
import com.cmcorg20230301.be.engine.flow.activiti.model.entity.SysActivitiProcessInstanceDO;
import com.cmcorg20230301.be.engine.flow.activiti.model.enums.SysActivitiLineTypeEnum;
import com.cmcorg20230301.be.engine.flow.activiti.model.enums.SysActivitiParamItemTypeEnum;
import com.cmcorg20230301.be.engine.flow.activiti.model.interfaces.ISysActivitiLineType;
import com.cmcorg20230301.be.engine.flow.activiti.model.interfaces.ISysActivitiParamItemType;
import com.cmcorg20230301.be.engine.flow.activiti.model.interfaces.ISysActivitiTaskCategory;
import com.cmcorg20230301.be.engine.flow.activiti.model.vo.*;
import com.cmcorg20230301.be.engine.kafka.util.KafkaUtil;
import com.cmcorg20230301.be.engine.model.model.enums.BaseWebSocketUriEnum;
import com.cmcorg20230301.be.engine.security.model.bo.SysWebSocketEventBO;
import com.cmcorg20230301.be.engine.security.model.dto.WebSocketMessageDTO;
import com.cmcorg20230301.be.engine.util.util.SeparatorUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;

@Component
public class SysActivitiUtil {

    public static final String VARIABLE_NAME_USER_ID = "userId";

    public static final String VARIABLE_NAME_TENANT_ID = "tenantId";

    // 流程实例，全局参数
    public static final String VARIABLE_NAME_PROCESS_INSTANCE_VARIABLE = "processInstanceVariable";

    public static final ConcurrentHashMap<Integer, ISysActivitiTaskCategory> TASK_CATEGORY_MAP =
        new ConcurrentHashMap<>();

    public static final Map<Integer, ISysActivitiParamItemType> PARAM_ITEM_TYPE_MAP = new ConcurrentHashMap<>();

    public static final Map<Integer, ISysActivitiLineType> LINE_TYPE_MAP = new ConcurrentHashMap<>();

    static {

        for (SysActivitiLineTypeEnum item : SysActivitiLineTypeEnum.values()) {

            LINE_TYPE_MAP.put(item.getCode(), item);

        }

    }

    /**
     * 默认的入参字段
     */
    public static final String CONTENT = "content";

    /**
     * 图片的数据
     */
    public static final String IMAGE_DATA = "imageData";

    private static SysActivitiProcessInstanceMapper sysActivitiProcessInstanceMapper;

    @Resource
    public void setSysActivitiProcessInstanceMapper(SysActivitiProcessInstanceMapper sysActivitiProcessInstanceMapper) {
        SysActivitiUtil.sysActivitiProcessInstanceMapper = sysActivitiProcessInstanceMapper;
    }

    private static RuntimeService runtimeService;

    @Resource
    public void setRuntimeService(RuntimeService runtimeService) {
        SysActivitiUtil.runtimeService = runtimeService;
    }

    /**
     * 获取：流程实例全局参数
     */
    @Nullable
    public static SysActivitiParamBO getSysActivitiParamBO(String processInstanceId) {

        SysActivitiProcessInstanceDO sysActivitiProcessInstanceDO =
            ChainWrappers.lambdaQueryChain(sysActivitiProcessInstanceMapper)
                .eq(SysActivitiProcessInstanceDO::getProcessInstanceId, processInstanceId).one();

        if (sysActivitiProcessInstanceDO == null) {
            return null;
        }

        String processInstanceJsonStr = sysActivitiProcessInstanceDO.getProcessInstanceJsonStr();

        if (StrUtil.isBlank(processInstanceJsonStr)) {
            return null;
        }

        return JSONUtil.toBean(processInstanceJsonStr, SysActivitiParamBO.class);

    }

    /**
     * 设置：流程实例全局参数
     */
    public static void setSysActivitiParamBO(String processInstanceId, SysActivitiParamBO sysActivitiParamBO,
        boolean insertFlag, boolean notifyFlag, Long userId) {

        String processInstanceJsonStr = JSONUtil.toJsonStr(sysActivitiParamBO);

        if (insertFlag) {

            SysActivitiProcessInstanceDO sysActivitiProcessInstanceDO = new SysActivitiProcessInstanceDO();

            sysActivitiProcessInstanceDO.setProcessInstanceId(processInstanceId);
            sysActivitiProcessInstanceDO.setProcessInstanceJsonStr(processInstanceJsonStr);

            sysActivitiProcessInstanceMapper.insert(sysActivitiProcessInstanceDO);

        } else {

            ChainWrappers.lambdaUpdateChain(sysActivitiProcessInstanceMapper)
                .eq(SysActivitiProcessInstanceDO::getProcessInstanceId, processInstanceId)
                .set(SysActivitiProcessInstanceDO::getProcessInstanceJsonStr, processInstanceJsonStr).update();

        }

        if (notifyFlag) {

            SysWebSocketEventBO<String> sysWebSocketEventBO = new SysWebSocketEventBO<>();

            sysWebSocketEventBO.setUserIdSet(CollUtil.newHashSet(userId));

            WebSocketMessageDTO<String> webSocketMessageDTO =
                WebSocketMessageDTO.okData(BaseWebSocketUriEnum.SYS_ACTIVITI_PARAM_CHANGE, processInstanceId);

            sysWebSocketEventBO.setWebSocketMessageDTO(webSocketMessageDTO);

            KafkaUtil.sendSysWebSocketEventTopic(sysWebSocketEventBO);

        }

    }

    /**
     * 删除：流程实例全局参数
     */
    public static void deleteSysActivitiParamBO(Set<String> processInstanceIdSet) {

        ChainWrappers.lambdaUpdateChain(sysActivitiProcessInstanceMapper)
            .in(SysActivitiProcessInstanceDO::getProcessInstanceId, processInstanceIdSet).remove();

    }

    /**
     * 获取：直接结束整个流程实例
     */
    public static SysActivitiTaskHandlerVO getSysActivitiTaskHandlerVoOnlyEndAll(String processInstanceId) {

        // 删除：流程实例，即可结束整个流程实例
        runtimeService.deleteProcessInstance(processInstanceId, null);

        return new SysActivitiTaskHandlerVO(false, true);

    }

    /**
     * 获取：只结束不完成
     */
    public static SysActivitiTaskHandlerVO getSysActivitiTaskHandlerVoOnlyEndAuto() {
        return new SysActivitiTaskHandlerVO(false, true);
    }

    /**
     * 获取：只完成不结束
     */
    public static SysActivitiTaskHandlerVO getSysActivitiTaskHandlerVoOnlyComplete() {
        return new SysActivitiTaskHandlerVO(true, false);
    }

    public static SysActivitiDeploymentVO getSysActivitiDeploymentVO(Deployment item) {

        String key = item.getKey();

        SysActivitiDeploymentVO sysActivitiDeploymentVO = new SysActivitiDeploymentVO();

        sysActivitiDeploymentVO.setId(item.getId());
        sysActivitiDeploymentVO.setName(item.getName());
        sysActivitiDeploymentVO.setCategory(item.getCategory());
        sysActivitiDeploymentVO.setKey(key);
        sysActivitiDeploymentVO.setTenantId(item.getTenantId());
        sysActivitiDeploymentVO.setDeploymentTime(item.getDeploymentTime());
        sysActivitiDeploymentVO.setVersion(item.getVersion());

        if (StrUtil.isNotBlank(key)) {

            List<String> splitList = StrUtil.splitTrim(key, SeparatorUtil.POUND_SIGN_SEPARATOR);

            if (splitList.size() == 2) {

                sysActivitiDeploymentVO.setProcessDefinitionId(splitList.get(0));

                sysActivitiDeploymentVO.setProcessDefinitionKey(splitList.get(1));

            }

        }

        return sysActivitiDeploymentVO;

    }

    public static SysActivitiProcessDefinitionVO getSysActivitiProcessDefinitionVO(ProcessDefinition item) {

        SysActivitiProcessDefinitionVO sysActivitiProcessDefinitionVO = new SysActivitiProcessDefinitionVO();

        sysActivitiProcessDefinitionVO.setId(item.getId());
        sysActivitiProcessDefinitionVO.setName(item.getName());
        sysActivitiProcessDefinitionVO.setDescription(item.getDescription());
        sysActivitiProcessDefinitionVO.setKey(item.getKey());
        sysActivitiProcessDefinitionVO.setVersion(item.getVersion());
        sysActivitiProcessDefinitionVO.setCategory(item.getCategory());
        sysActivitiProcessDefinitionVO.setDeploymentId(item.getDeploymentId());
        sysActivitiProcessDefinitionVO.setResourceName(item.getResourceName());
        sysActivitiProcessDefinitionVO.setTenantId(item.getTenantId());
        sysActivitiProcessDefinitionVO.setSuspended(item.isSuspended());

        return sysActivitiProcessDefinitionVO;

    }

    public static SysActivitiProcessInstanceVO getSysActivitiProcessInstanceVO(ProcessInstance item) {

        SysActivitiProcessInstanceVO sysActivitiProcessInstanceVO = new SysActivitiProcessInstanceVO();

        sysActivitiProcessInstanceVO.setProcessDefinitionId(item.getProcessDefinitionId());
        sysActivitiProcessInstanceVO.setProcessDefinitionName(item.getProcessDefinitionName());
        sysActivitiProcessInstanceVO.setProcessDefinitionKey(item.getProcessDefinitionKey());
        sysActivitiProcessInstanceVO.setProcessDefinitionVersion(item.getProcessDefinitionVersion());
        sysActivitiProcessInstanceVO.setDeploymentId(item.getDeploymentId());
        sysActivitiProcessInstanceVO.setBusinessKey(item.getBusinessKey());
        sysActivitiProcessInstanceVO.setSuspended(item.isSuspended());
        sysActivitiProcessInstanceVO.setProcessVariableMap(item.getProcessVariables());
        sysActivitiProcessInstanceVO.setTenantId(item.getTenantId());
        sysActivitiProcessInstanceVO.setName(item.getName());
        sysActivitiProcessInstanceVO.setDescription(item.getDescription());
        sysActivitiProcessInstanceVO.setStartTime(item.getStartTime());
        sysActivitiProcessInstanceVO.setStartUserId(item.getStartUserId());

        sysActivitiProcessInstanceVO.setId(item.getId());
        sysActivitiProcessInstanceVO.setEnded(item.isEnded());

        return sysActivitiProcessInstanceVO;

    }

    public static SysActivitiTaskVO getSysActivitiTaskVO(Task item) {

        SysActivitiTaskVO sysActivitiTaskVO = new SysActivitiTaskVO();

        sysActivitiTaskVO.setId(item.getId());
        sysActivitiTaskVO.setName(item.getName());
        sysActivitiTaskVO.setDescription(item.getDescription());
        sysActivitiTaskVO.setPriority(item.getPriority());
        sysActivitiTaskVO.setOwner(item.getOwner());
        sysActivitiTaskVO.setAssignee(item.getAssignee());
        sysActivitiTaskVO.setProcessInstanceId(item.getProcessInstanceId());
        sysActivitiTaskVO.setExecutionId(item.getExecutionId());
        sysActivitiTaskVO.setProcessDefinitionId(item.getProcessDefinitionId());
        sysActivitiTaskVO.setCreateTime(item.getCreateTime());
        sysActivitiTaskVO.setTaskDefinitionKey(item.getTaskDefinitionKey());
        sysActivitiTaskVO.setDueDate(item.getDueDate());
        sysActivitiTaskVO.setCategory(item.getCategory());
        sysActivitiTaskVO.setParentTaskId(item.getParentTaskId());
        sysActivitiTaskVO.setTenantId(item.getTenantId());
        sysActivitiTaskVO.setFormKey(item.getFormKey());
        sysActivitiTaskVO.setProcessVariableMap(item.getProcessVariables());
        sysActivitiTaskVO.setClaimTime(item.getClaimTime());
        sysActivitiTaskVO.setBusinessKey(item.getBusinessKey());

        sysActivitiTaskVO.setSuspended(item.isSuspended());

        return sysActivitiTaskVO;

    }

    public static SysActivitiHistoryTaskVO getSysActivitiHistoryTaskVO(HistoricTaskInstance item) {

        SysActivitiHistoryTaskVO sysActivitiHistoryTaskVO = new SysActivitiHistoryTaskVO();

        sysActivitiHistoryTaskVO.setId(item.getId());
        sysActivitiHistoryTaskVO.setName(item.getName());
        sysActivitiHistoryTaskVO.setDescription(item.getDescription());
        sysActivitiHistoryTaskVO.setPriority(item.getPriority());
        sysActivitiHistoryTaskVO.setOwner(item.getOwner());
        sysActivitiHistoryTaskVO.setAssignee(item.getAssignee());
        sysActivitiHistoryTaskVO.setProcessInstanceId(item.getProcessInstanceId());
        sysActivitiHistoryTaskVO.setExecutionId(item.getExecutionId());
        sysActivitiHistoryTaskVO.setProcessDefinitionId(item.getProcessDefinitionId());
        sysActivitiHistoryTaskVO.setCreateTime(item.getCreateTime());
        sysActivitiHistoryTaskVO.setTaskDefinitionKey(item.getTaskDefinitionKey());
        sysActivitiHistoryTaskVO.setDueDate(item.getDueDate());
        sysActivitiHistoryTaskVO.setCategory(item.getCategory());
        sysActivitiHistoryTaskVO.setParentTaskId(item.getParentTaskId());
        sysActivitiHistoryTaskVO.setTenantId(item.getTenantId());
        sysActivitiHistoryTaskVO.setFormKey(item.getFormKey());
        sysActivitiHistoryTaskVO.setProcessVariableMap(item.getProcessVariables());
        sysActivitiHistoryTaskVO.setClaimTime(item.getClaimTime());
        sysActivitiHistoryTaskVO.setBusinessKey(item.getBusinessKey());

        sysActivitiHistoryTaskVO.setStartTime(item.getStartTime());
        sysActivitiHistoryTaskVO.setEndTime(item.getEndTime());
        sysActivitiHistoryTaskVO.setDurationInMillis(item.getDurationInMillis());
        sysActivitiHistoryTaskVO.setWorkTimeInMillis(item.getWorkTimeInMillis());

        sysActivitiHistoryTaskVO.setDeleteReason(item.getDeleteReason());

        return sysActivitiHistoryTaskVO;

    }

    public static SysActivitiHistoryProcessInstanceVO
        getSysActivitiHistoryProcessInstanceVO(HistoricProcessInstance item) {

        SysActivitiHistoryProcessInstanceVO sysActivitiHistoryProcessInstanceVO =
            new SysActivitiHistoryProcessInstanceVO();

        sysActivitiHistoryProcessInstanceVO.setProcessDefinitionId(item.getProcessDefinitionId());
        sysActivitiHistoryProcessInstanceVO.setProcessDefinitionName(item.getProcessDefinitionName());
        sysActivitiHistoryProcessInstanceVO.setProcessDefinitionKey(item.getProcessDefinitionKey());
        sysActivitiHistoryProcessInstanceVO.setProcessDefinitionVersion(item.getProcessDefinitionVersion());
        sysActivitiHistoryProcessInstanceVO.setDeploymentId(item.getDeploymentId());
        sysActivitiHistoryProcessInstanceVO.setBusinessKey(item.getBusinessKey());
        sysActivitiHistoryProcessInstanceVO.setProcessVariableMap(item.getProcessVariables());
        sysActivitiHistoryProcessInstanceVO.setTenantId(item.getTenantId());
        sysActivitiHistoryProcessInstanceVO.setName(item.getName());
        sysActivitiHistoryProcessInstanceVO.setDescription(item.getDescription());
        sysActivitiHistoryProcessInstanceVO.setStartTime(item.getStartTime());
        sysActivitiHistoryProcessInstanceVO.setStartUserId(item.getStartUserId());

        sysActivitiHistoryProcessInstanceVO.setId(item.getId());

        sysActivitiHistoryProcessInstanceVO.setEndTime(item.getEndTime());
        sysActivitiHistoryProcessInstanceVO.setDurationInMillis(item.getDurationInMillis());
        sysActivitiHistoryProcessInstanceVO.setEndActivityId(item.getEndActivityId());
        sysActivitiHistoryProcessInstanceVO.setStartActivityId(item.getStartActivityId());
        sysActivitiHistoryProcessInstanceVO.setDeleteReason(item.getDeleteReason());
        sysActivitiHistoryProcessInstanceVO.setSuperProcessInstanceId(item.getSuperProcessInstanceId());

        return sysActivitiHistoryProcessInstanceVO;

    }

    /**
     * 设置：下一个节点的参数：正常判断的线
     */
    public static void setNextNodeInParamForNormal(SysActivitiParamBO sysActivitiParamBO, SequenceFlow item,
        String content, @Nullable Integer paramSubItemType, @Nullable Long currentTimeMillis) {

        if (paramSubItemType == null) {

            paramSubItemType = SysActivitiParamItemTypeEnum.TEXT.getCode();

        }

        String documentation = item.getDocumentation();

        if (StrUtil.isBlank(documentation)) {

            // 设置：下一个节点的入参
            SysActivitiUtil.setNextNodeInParam(sysActivitiParamBO, item, content, paramSubItemType, currentTimeMillis);

            return;

        }

        SysActivitiLineBO sysActivitiLineBO = JSONUtil.toBean(documentation, SysActivitiLineBO.class);

        if (sysActivitiLineBO.getType() == null) {

            // 设置：下一个节点的入参
            SysActivitiUtil.setNextNodeInParam(sysActivitiParamBO, item, content, paramSubItemType, currentTimeMillis);

            return;

        }

        // 如果是：普通判断
        if (SysActivitiLineTypeEnum.NORMAL.getCode() == sysActivitiLineBO.getType()) {

            // 设置：下一个节点的入参
            SysActivitiUtil.setNextNodeInParam(sysActivitiParamBO, item, content, paramSubItemType, currentTimeMillis);

        }

    }

    /**
     * 设置：下一个节点的入参
     */
    public static void setNextNodeInParam(SysActivitiParamBO sysActivitiParamBO, SequenceFlow item, String content,
        @Nullable Integer paramSubItemType, @Nullable Long currentTimeMillis) {

        String targetRef = item.getTargetRef();

        List<SysActivitiParamItemBO> sysActivitiParamItemBOList =
            sysActivitiParamBO.getInMap().computeIfAbsent(targetRef, k -> new ArrayList<>());

        SysActivitiParamItemBO sysActivitiParamItemBO = new SysActivitiParamItemBO();

        SysActivitiParamSubItemBO sysActivitiParamSubItemBO = new SysActivitiParamSubItemBO();

        sysActivitiParamSubItemBO.setType(paramSubItemType);

        sysActivitiParamSubItemBO.setValue(content);

        sysActivitiParamItemBO.setParamList(CollUtil.newArrayList(sysActivitiParamSubItemBO));

        sysActivitiParamItemBO.setFromNodeId(item.getSourceRef());

        sysActivitiParamItemBO.setStartTs(currentTimeMillis);

        sysActivitiParamItemBOList.add(sysActivitiParamItemBO);

    }

    /**
     * 获取：默认的 SysActivitiFunctionCallParametersBO对象
     */
    public static SysActivitiFunctionCallParametersBO getDefaultSysActivitiFunctionCallParametersBO() {

        SysActivitiFunctionCallParametersBO sysActivitiFunctionCallParametersBO =
            new SysActivitiFunctionCallParametersBO();

        sysActivitiFunctionCallParametersBO.setType("object");

        sysActivitiFunctionCallParametersBO.setProperties(MapUtil.newHashMap());

        return sysActivitiFunctionCallParametersBO;

    }

    /**
     * 处理：SysActivitiFunctionCallItemBO
     */
    public static void
        handleSysActivitiFunctionCallItemParameters(SysActivitiFunctionCallItemBO sysActivitiFunctionCallItemBO) {

        if (sysActivitiFunctionCallItemBO.getParameters() == null) {

            sysActivitiFunctionCallItemBO.setParameters(getDefaultSysActivitiFunctionCallParametersBO());

        } else {

            if (StrUtil.isBlank(sysActivitiFunctionCallItemBO.getParameters().getType())) {

                sysActivitiFunctionCallItemBO.getParameters().setType("object");

            }

            if (sysActivitiFunctionCallItemBO.getParameters().getProperties() == null) {

                sysActivitiFunctionCallItemBO.getParameters().setProperties(MapUtil.newHashMap());

            }

        }

    }

}
