package com.cmcorg20230301.be.engine.netty.tcp.protobuf.configuration;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.StrUtil;
import com.cmcorg20230301.be.engine.netty.tcp.protobuf.annotation.NettyTcpProtobufController;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * 映射：有 @NettyTcpProtobufController 注解的 Bean
 */
@Component
public class NettyTcpProtobufBeanPostProcessor implements BeanPostProcessor {

    // 映射之后的 map
    private static final Map<String, MappingValue> MAPPING_MAP = MapUtil.newHashMap();

    public static MappingValue getMappingValueByKey(String key) {
        return MAPPING_MAP.get(key);
    }

    public static int getMappingMapSize() {
        return MAPPING_MAP.size();
    }

    @Data
    @AllArgsConstructor
    public static class MappingValue {

        private Object bean;

        private Method method;

    }

    @Override
    public Object postProcessAfterInitialization(@NotNull Object bean, @NotNull String beanName) throws BeansException {

        Class<?> beanClass = bean.getClass();

        NettyTcpProtobufController nettyTcpProtobufController =
                beanClass.getAnnotation(NettyTcpProtobufController.class);

        if (nettyTcpProtobufController == null) {
            return bean;
        }

        RequestMapping classRequestMappingAnnotation = beanClass.getAnnotation(RequestMapping.class);

        StrBuilder strBuilder = StrBuilder.create();

        String classRequestMappingValue = "";
        if (classRequestMappingAnnotation != null) {
            classRequestMappingValue = classRequestMappingAnnotation.value()[0]; // 只取第一个路径
        }

        Method[] declaredMethodArr = beanClass.getDeclaredMethods();

        // 添加：映射
        for (Method item : declaredMethodArr) {

            RequestMapping methodMappingAnnotation = item.getAnnotation(RequestMapping.class);

            if (methodMappingAnnotation == null) {
                continue;
            }

            String methodRequestMappingValue = methodMappingAnnotation.value()[0]; // 只取第一个路径

            // 组装并处理路径
            List<String> splitTrimList = StrUtil.splitTrim(
                    strBuilder.append(classRequestMappingValue).append("/").append(methodRequestMappingValue)
                            .toStringAndReset(), "/");

            String key = "/" + CollUtil.join(splitTrimList, "/");

            if (MAPPING_MAP.containsKey(key)) {
                throw new RuntimeException(StrUtil.format("NettyTcpProtobuf，映射重复：【{}】", key));
            }

            // 添加到映射 map里
            MAPPING_MAP.put(key, new MappingValue(bean, item));

        }

        return bean;

    }
}
