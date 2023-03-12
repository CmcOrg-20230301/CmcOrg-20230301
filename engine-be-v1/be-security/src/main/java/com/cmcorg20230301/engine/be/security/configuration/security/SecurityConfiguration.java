package com.cmcorg20230301.engine.be.security.configuration.security;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import com.cmcorg20230301.engine.be.model.model.configuration.ISecurityPermitAllConfiguration;
import com.cmcorg20230301.engine.be.security.filter.JwtAuthorizationFilter;
import com.cmcorg20230301.engine.be.security.properties.SecurityProperties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    public SecurityConfiguration(@Autowired(required = false) MethodInterceptor methodSecurityInterceptor) {

        if (methodSecurityInterceptor instanceof MethodSecurityInterceptor) {

            AffirmativeBased accessDecisionManager =
                (AffirmativeBased)((MethodSecurityInterceptor)methodSecurityInterceptor).getAccessDecisionManager();

            accessDecisionManager.getDecisionVoters().add(0, new MyAccessDecisionVoter()); // 添加：自定义投票者

        }

    }

    @SneakyThrows
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,
        @Value("${spring.profiles.active:prod}") String profiles,
        List<ISecurityPermitAllConfiguration> iSecurityPermitAllConfigurationList, RedissonClient redissonClient,
        SecurityProperties securityProperties, List<IJwtValidatorConfiguration> iJwtValidatorConfigurationList) {

        boolean prodFlag = "prod".equals(profiles);

        Set<String> permitAllSet = new HashSet<>();

        if (CollUtil.isNotEmpty(iSecurityPermitAllConfigurationList)) {
            for (ISecurityPermitAllConfiguration item : iSecurityPermitAllConfigurationList) {
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

        httpSecurity.addFilterBefore(
            new JwtAuthorizationFilter(redissonClient, securityProperties, iJwtValidatorConfigurationList),
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
