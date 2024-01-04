package com.cmcorg20230301.be.engine.security.configuration.security;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import com.cmcorg20230301.be.engine.model.model.configuration.ISecurityPermitConfiguration;
import com.cmcorg20230301.be.engine.security.configuration.base.BaseConfiguration;
import com.cmcorg20230301.be.engine.security.filter.JwtAuthorizationFilter;
import com.cmcorg20230301.be.engine.security.model.configuration.IJwtValidatorConfiguration;
import com.cmcorg20230301.be.engine.security.properties.SecurityProperties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.intercept.aopalliance.MethodSecurityInterceptor;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EnableGlobalMethodSecurity(prePostEnabled = true) // 开启 @PreAuthorize 权限注解
@EnableWebSecurity
@Slf4j
public class SecurityConfiguration {

    /**
     * @param methodSecurityInterceptor 注意：这个名字不要改
     */
    public SecurityConfiguration(@Autowired(required = false) @Nullable MethodInterceptor methodSecurityInterceptor) {

        if (methodSecurityInterceptor instanceof MethodSecurityInterceptor) {

            AffirmativeBased accessDecisionManager =
                    (AffirmativeBased) ((MethodSecurityInterceptor) methodSecurityInterceptor).getAccessDecisionManager();

            accessDecisionManager.getDecisionVoters().add(0, new MyAccessDecisionVoter()); // 添加：自定义投票者

        }

    }

    /**
     * @param baseConfiguration 不要删除，目的：让 springboot实例化该对象
     */
    @SneakyThrows
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, BaseConfiguration baseConfiguration,
                                            List<ISecurityPermitConfiguration> iSecurityPermitConfigurationList, SecurityProperties securityProperties,
                                            List<IJwtValidatorConfiguration> iJwtValidatorConfigurationList) {

        boolean prodFlag = BaseConfiguration.prodFlag();

        Set<String> permitAllSet = new HashSet<>();

        if (CollUtil.isNotEmpty(iSecurityPermitConfigurationList)) {

            for (ISecurityPermitConfiguration item : iSecurityPermitConfigurationList) {

                if (prodFlag) {

                    CollUtil.addAll(permitAllSet, item.prodPermitAllSet());

                } else {

                    CollUtil.addAll(permitAllSet, item.devPermitAllSet());

                }

                CollUtil.addAll(permitAllSet, item.anyPermitAllSet());

            }

        }

        log.info("permitAllSet：{}", permitAllSet);

        httpSecurity.authorizeRequests().antMatchers(ArrayUtil.toArray(permitAllSet, String.class))
                .permitAll() // 可以匿名访问的请求
                .anyRequest().authenticated(); // 拦截所有请求

        httpSecurity.addFilterBefore(new JwtAuthorizationFilter(securityProperties, iJwtValidatorConfigurationList),
                UsernamePasswordAuthenticationFilter.class);

        httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // 不需要session

        // 用户没有登录，但是访问需要权限的资源时，而报出的错误
        httpSecurity.exceptionHandling().authenticationEntryPoint(new MyAuthenticationEntryPoint());

        httpSecurity.csrf().disable(); // 关闭CSRF保护

        httpSecurity.logout().disable(); // 禁用 logout

        httpSecurity.formLogin().disable(); // 禁用 login

        return httpSecurity.build();

    }

}
