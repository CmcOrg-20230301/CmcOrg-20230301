package com.cmcorg20230301.engine.be.security.configuration.base;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ExecutorConfigurationSupport;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@ComponentScan(basePackages = "com.cmcorg20230301")
@MapperScan(basePackages = "com.cmcorg20230301.**.mapper")
@EnableAsync
@EnableScheduling
public class BaseConfiguration {

    public static String applicationName; // 服务名
    public static Integer port; // 启动的端口
    public static String profilesActive; // 启动的环境

    public BaseConfiguration(@Value("${spring.application.name:applicationName}") String applicationName,
        @Value("${server.port:8080}") int port, @Value("${spring.profiles.active:prod}") String profilesActive) {

        BaseConfiguration.applicationName = applicationName;
        BaseConfiguration.port = port;
        BaseConfiguration.profilesActive = profilesActive;

    }

    /**
     * 获取：是否是正式环境
     */
    public static boolean prodFlag() {

        return "prod".equals(BaseConfiguration.profilesActive);

    }

    /**
     * 获取：是否是开发环境
     */
    public static boolean devFlag() {

        return "dev".equals(BaseConfiguration.profilesActive);

    }

    /**
     * 设置：@Async的线程池
     */
    @Bean
    public TaskExecutor taskExecutor() {

        int availableProcessors = Runtime.getRuntime().availableProcessors();

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        executor.setCorePoolSize(availableProcessors);
        // 设置最大线程数
        executor.setMaxPoolSize(availableProcessors * 10);
        // 设置队列容量
        executor.setQueueCapacity(availableProcessors * 100);
        // 设置核心线程之外的线程，在空闲多久之后会被销毁的时间
        executor.setKeepAliveSeconds(60);
        // 设置：线程名前缀
        executor.setThreadNamePrefix("taskExecutor-");
        // 设置：线程池通用属性
        setCommonExecutor(executor);

        return executor;

    }

    /**
     * 设置：@Scheduled 的线程池，备注：额外加了 @Async注解，会在 @Async的线程池里面执行
     */
    @Bean
    public TaskScheduler taskScheduler() {

        int availableProcessors = Runtime.getRuntime().availableProcessors();

        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        // 设置核心线程数
        scheduler.setPoolSize(availableProcessors);
        // 设置：线程名前缀
        scheduler.setThreadNamePrefix("taskScheduler-");
        // 设置：线程池通用属性
        setCommonExecutor(scheduler);

        return scheduler;

    }

    /**
     * 设置：线程池通用属性
     */
    public void setCommonExecutor(ExecutorConfigurationSupport executorConfigurationSupport) {

        // 设置拒绝策略：由调用线程处理该任务
        executorConfigurationSupport.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executorConfigurationSupport.setWaitForTasksToCompleteOnShutdown(true);
        // 最多等待多少秒
        executorConfigurationSupport.setAwaitTerminationSeconds(60);
        // 执行初始化
        executorConfigurationSupport.initialize();

    }

}
