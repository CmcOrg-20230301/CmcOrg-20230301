package com.cmcorg20230301.be.engine.security.configuration.security;

import cn.hutool.jwt.JWT;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.exception.BaseException;
import com.cmcorg20230301.be.engine.security.model.configuration.IJwtValidatorConfiguration;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.util.MyJwtUtil;
import com.cmcorg20230301.be.engine.security.util.ResponseUtil;
import com.cmcorg20230301.be.engine.security.util.SysTenantUtil;
import com.cmcorg20230301.be.engine.security.util.UserUtil;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;

@Component
public class SecurityJwtValidatorConfiguration implements IJwtValidatorConfiguration {

    /**
     * 判断：用户是否被封禁，或者用户所属的租户是否被封禁
     */
    @Override
    public boolean validator(JWT jwt, String requestUri, HttpServletResponse response) {

        // 获取：userId的值
        Long userId = MyJwtUtil.getPayloadMapUserIdValue(jwt.getPayload().getClaimsJson());

        if (userId == null) {

            return true;

        }

        // 检查：用户：是否被冻结
        if (UserUtil.getDisable(userId)) {

            try {

                ApiResultVO.error(BaseBizCodeEnum.ACCOUNT_IS_DISABLED); // 这里肯定会抛出 BaseException异常

            } catch (BaseException e) {

                ResponseUtil.out(response, e.getMessage(), false);

            }

            return false;

        }

        // 获取：tenantId的值
        Long tenantId = MyJwtUtil.getPayloadMapTenantIdValue(jwt.getPayload().getClaimsJson());

        try {

            // 这里会校验：租户是否被禁用
            SysTenantUtil.getTenantId(tenantId);

        } catch (BaseException e) {

            ResponseUtil.out(response, e.getMessage(), false);

        }

        return true;

    }

}
