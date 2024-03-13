package com.cmcorg20230301.be.engine.flow.activiti.configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.sql.DataSource;

import org.activiti.api.runtime.shared.identity.UserGroupManager;
import org.activiti.core.common.spring.project.ProjectModelService;
import org.activiti.engine.cfg.ProcessEngineConfigurator;
import org.activiti.engine.impl.persistence.StrongUuidGenerator;
import org.activiti.spring.SpringAsyncExecutor;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.AbstractProcessEngineAutoConfiguration;
import org.activiti.spring.boot.ActivitiProperties;
import org.activiti.spring.boot.ProcessEngineConfigurationConfigurer;
import org.activiti.spring.boot.process.validation.AsyncPropertyValidator;
import org.activiti.spring.resources.ResourceFinder;
import org.activiti.spring.resources.ResourceFinderDescriptor;
import org.activiti.validation.ProcessValidatorImpl;
import org.activiti.validation.validator.ValidatorSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

import com.cmcorg20230301.be.engine.security.configuration.base.BaseConfiguration;

@Configuration
@AutoConfigureAfter(value = {BaseConfiguration.class}) // 目的：可以注入：springAsyncExecutor
public class ActivitiDataSourceConfiguration extends AbstractProcessEngineAutoConfiguration {

    private final UserGroupManager userGroupManager;

    public ActivitiDataSourceConfiguration(UserGroupManager userGroupManager) {
        this.userGroupManager = userGroupManager;
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.activiti.datasource")
    public DataSource activitiDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public SpringProcessEngineConfiguration springProcessEngineConfiguration(
        @Qualifier(value = "activitiDataSource") DataSource dataSource, PlatformTransactionManager transactionManager,
        SpringAsyncExecutor springAsyncExecutor, ActivitiProperties activitiProperties, ResourceFinder resourceFinder,
        List<ResourceFinderDescriptor> resourceFinderDescriptors, ProjectModelService projectModelService,
        @Autowired(required = false) List<ProcessEngineConfigurationConfigurer> processEngineConfigurationConfigurers,
        @Autowired(required = false) List<ProcessEngineConfigurator> processEngineConfigurators) throws IOException {

        SpringProcessEngineConfiguration conf = new SpringProcessEngineConfiguration(projectModelService);

        conf.setConfigurators(processEngineConfigurators);

        configureResources(resourceFinder, resourceFinderDescriptors, conf);

        conf.setDataSource(dataSource);
        conf.setTransactionManager(transactionManager);

        conf.setAsyncExecutor(springAsyncExecutor);
        conf.setDeploymentName(activitiProperties.getDeploymentName());
        conf.setDatabaseSchema(activitiProperties.getDatabaseSchema());
        conf.setDatabaseSchemaUpdate(activitiProperties.getDatabaseSchemaUpdate());
        conf.setDbHistoryUsed(activitiProperties.isDbHistoryUsed());
        conf.setAsyncExecutorActivate(activitiProperties.isAsyncExecutorActivate());
        addAsyncPropertyValidator(activitiProperties, conf);
        conf.setMailServerHost(activitiProperties.getMailServerHost());
        conf.setMailServerPort(activitiProperties.getMailServerPort());
        conf.setMailServerUsername(activitiProperties.getMailServerUserName());
        conf.setMailServerPassword(activitiProperties.getMailServerPassword());
        conf.setMailServerDefaultFrom(activitiProperties.getMailServerDefaultFrom());
        conf.setMailServerUseSSL(activitiProperties.isMailServerUseSsl());
        conf.setMailServerUseTLS(activitiProperties.isMailServerUseTls());

        if (userGroupManager != null) {
            conf.setUserGroupManager(userGroupManager);
        }

        conf.setHistoryLevel(activitiProperties.getHistoryLevel());
        conf.setCopyVariablesToLocalForTasks(activitiProperties.isCopyVariablesToLocalForTasks());
        conf.setSerializePOJOsInVariablesToJson(activitiProperties.isSerializePOJOsInVariablesToJson());
        conf.setJavaClassFieldForJackson(activitiProperties.getJavaClassFieldForJackson());

        if (activitiProperties.getCustomMybatisMappers() != null) {
            conf.setCustomMybatisMappers(getCustomMybatisMapperClasses(activitiProperties.getCustomMybatisMappers()));
        }

        if (activitiProperties.getCustomMybatisXMLMappers() != null) {
            conf.setCustomMybatisXMLMappers(new HashSet<>(activitiProperties.getCustomMybatisXMLMappers()));
        }

        if (activitiProperties.getCustomMybatisXMLMappers() != null) {
            conf.setCustomMybatisXMLMappers(new HashSet<>(activitiProperties.getCustomMybatisXMLMappers()));
        }

        if (activitiProperties.isUseStrongUuids()) {
            conf.setIdGenerator(new StrongUuidGenerator());
        }

        if (activitiProperties.getDeploymentMode() != null) {
            conf.setDeploymentMode(activitiProperties.getDeploymentMode());
        }

        if (processEngineConfigurationConfigurers != null) {
            for (ProcessEngineConfigurationConfigurer processEngineConfigurationConfigurer : processEngineConfigurationConfigurers) {
                processEngineConfigurationConfigurer.configure(conf);
            }
        }

        springAsyncExecutor.applyConfig(conf);

        return conf;

    }

    private void configureResources(ResourceFinder resourceFinder,
        List<ResourceFinderDescriptor> resourceFinderDescriptors, SpringProcessEngineConfiguration conf)
        throws IOException {

        List<Resource> resources = new ArrayList<>();
        for (ResourceFinderDescriptor resourceFinderDescriptor : resourceFinderDescriptors) {
            resources.addAll(resourceFinder.discoverResources(resourceFinderDescriptor));
        }

        conf.setDeploymentResources(resources.toArray(new Resource[0]));
    }

    protected void addAsyncPropertyValidator(ActivitiProperties activitiProperties,
        SpringProcessEngineConfiguration conf) {
        if (!activitiProperties.isAsyncExecutorActivate()) {
            ValidatorSet springBootStarterValidatorSet = new ValidatorSet("activiti-spring-boot-starter");
            springBootStarterValidatorSet.addValidator(new AsyncPropertyValidator());
            if (conf.getProcessValidator() == null) {
                ProcessValidatorImpl processValidator = new ProcessValidatorImpl();
                processValidator.addValidatorSet(springBootStarterValidatorSet);
                conf.setProcessValidator(processValidator);
            } else {
                conf.getProcessValidator().getValidatorSets().add(springBootStarterValidatorSet);
            }
        }
    }

}
