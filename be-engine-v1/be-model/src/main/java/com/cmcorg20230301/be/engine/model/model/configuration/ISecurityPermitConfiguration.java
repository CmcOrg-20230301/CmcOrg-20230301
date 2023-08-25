package com.cmcorg20230301.be.engine.model.model.configuration;

import java.util.Set;

/**
 * Security 不用权限就可以访问的 url配置类
 */
public interface ISecurityPermitConfiguration {

    /**
     * 开发环境 不用权限就可以访问的 url
     */
    Set<String> devPermitAllSet();

    /**
     * 生产环境 不用权限就可以访问的 url
     */
    Set<String> prodPermitAllSet();

    /**
     * 所有环境 不用权限就可以访问的 url
     */
    Set<String> anyPermitAllSet();

}
