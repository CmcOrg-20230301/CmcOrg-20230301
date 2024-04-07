package com.cmcorg20230301.be.engine.flow.activiti.util;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.jetbrains.annotations.Nullable;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import com.cmcorg20230301.be.engine.flow.activiti.model.bo.*;
import com.cmcorg20230301.be.engine.flow.activiti.model.enums.SysActivitiLineTypeEnum;
import com.cmcorg20230301.be.engine.flow.activiti.model.interfaces.ISysActivitiLineType;
import com.cmcorg20230301.be.engine.flow.activiti.model.interfaces.ISysActivitiParamItemType;
import com.cmcorg20230301.be.engine.flow.activiti.model.interfaces.ISysActivitiTaskCategory;
import com.cmcorg20230301.be.engine.flow.activiti.model.vo.*;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;

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

    private static RedissonClient redissonClient;

    @Resource
    public void setRedissonClient(RedissonClient redissonClient) {
        SysActivitiUtil.redissonClient = redissonClient;
    }

    @Nullable
    public static SysActivitiParamBO getSysActivitiParamBO(String processInstanceId) {

        String processInstanceJsonStr = redissonClient
            .<String>getBucket(BaseRedisKeyEnum.PRE_SYS_ACTIVITI_PROCESS_INSTANCE_JSON_STR + processInstanceId)
            .getAndExpire(Duration.ofMillis(BaseConstant.HOUR_1_EXPIRE_TIME));

        if (StrUtil.isBlank(processInstanceJsonStr)) {
            return null;
        }

        return JSONUtil.toBean(processInstanceJsonStr, SysActivitiParamBO.class);

    }

    public static void setSysActivitiParamBO(String processInstanceId, SysActivitiParamBO sysActivitiParamBO) {

        redissonClient
            .<String>getBucket(BaseRedisKeyEnum.PRE_SYS_ACTIVITI_PROCESS_INSTANCE_JSON_STR + processInstanceId)
            .set(JSONUtil.toJsonStr(sysActivitiParamBO), Duration.ofMillis(BaseConstant.HOUR_1_EXPIRE_TIME));

    }

    public static void deleteSysActivitiParamBO(String processInstanceId) {

        redissonClient
            .<String>getBucket(BaseRedisKeyEnum.PRE_SYS_ACTIVITI_PROCESS_INSTANCE_JSON_STR + processInstanceId)
            .delete();

    }

    /**
     * 获取：只结束不完成的返回值
     */
    public static SysActivitiTaskHandlerVO getSysActivitiTaskHandlerVoOnlyEndAuto() {
        return new SysActivitiTaskHandlerVO(false, true);
    }

    /**
     * 获取：只完成不结束的返回值
     */
    public static SysActivitiTaskHandlerVO getSysActivitiTaskHandlerVoOnlyComplete() {
        return new SysActivitiTaskHandlerVO(true, false);
    }

    public static SysActivitiDeploymentVO getSysActivitiDeploymentVO(Deployment item) {

        SysActivitiDeploymentVO sysActivitiDeploymentVO = new SysActivitiDeploymentVO();

        sysActivitiDeploymentVO.setId(item.getId());
        sysActivitiDeploymentVO.setName(item.getName());
        sysActivitiDeploymentVO.setCategory(item.getCategory());
        sysActivitiDeploymentVO.setKey(item.getKey());
        sysActivitiDeploymentVO.setTenantId(item.getTenantId());
        sysActivitiDeploymentVO.setDeploymentTime(item.getDeploymentTime());
        sysActivitiDeploymentVO.setVersion(item.getVersion());

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
     * 设置：下一个节点的入参
     */
    public static void setNextNodeInParam(SysActivitiParamBO sysActivitiParamBO, SequenceFlow item, String content,
        Integer paramSubItemType) {

        String targetRef = item.getTargetRef();

        List<SysActivitiParamItemBO> sysActivitiParamItemBOList =
            sysActivitiParamBO.getInMap().computeIfAbsent(targetRef, k -> new ArrayList<>());

        SysActivitiParamItemBO sysActivitiParamItemBO = new SysActivitiParamItemBO();

        SysActivitiParamSubItemBO sysActivitiParamSubItemBO = new SysActivitiParamSubItemBO();

        sysActivitiParamSubItemBO.setType(paramSubItemType);

        sysActivitiParamSubItemBO.setValue(content);

        sysActivitiParamItemBO.setParamList(CollUtil.newArrayList(sysActivitiParamSubItemBO));

        sysActivitiParamItemBO.setFromNodeId(item.getSourceRef());

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
