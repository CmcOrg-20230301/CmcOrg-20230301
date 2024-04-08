package com.cmcorg20230301.be.engine.flow.activiti.util;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.el.VariableScopeElResolver;
import org.springframework.stereotype.Component;

@Component
public class SysActivitiConditionExpressionUtil {

    /**
     * 备注：execution不要修改，原因：{@link VariableScopeElResolver#getValue}，是 equals进行比较的
     */
    public boolean check(DelegateExecution execution) {

        return true;

    }

}
