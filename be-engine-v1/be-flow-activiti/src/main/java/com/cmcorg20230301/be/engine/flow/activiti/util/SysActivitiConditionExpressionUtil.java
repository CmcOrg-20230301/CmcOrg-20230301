package com.cmcorg20230301.be.engine.flow.activiti.util;

import org.activiti.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

@Component
public class SysActivitiConditionExpressionUtil {

    public boolean check(DelegateExecution execution) {

        return true;

    }

}
