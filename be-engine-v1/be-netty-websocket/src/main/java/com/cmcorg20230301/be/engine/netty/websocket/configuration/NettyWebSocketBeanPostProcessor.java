package com.cmcorg20230301.be.engine.netty.websocket.configuration;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.cmcorg20230301.be.engine.netty.websocket.annotation.NettyWebSocketController;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 映射：有 @NettyWebSocketController 注解的 Bean
 */
@Component
public class NettyWebSocketBeanPostProcessor implements BeanPostProcessor {

    // 映射之后的 map，key：uri，value：对象
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

    @SneakyThrows
    @Override
    public Object postProcessAfterInitialization(@NotNull Object bean, @NotNull String beanName)
        throws BeansException {

        Class<?> beanClass = bean.getClass();

        NettyWebSocketController nettyWebSocketControllerAnnotation =
            beanClass.getAnnotation(NettyWebSocketController.class);

        if (nettyWebSocketControllerAnnotation == null) {
            return bean;
        }

        String beanClassName = beanClass.getName();

        int indexOf = beanClassName.indexOf("$");

        if (indexOf != -1) { // 如果是被代理的类，则需要额外进行处理

            beanClassName = StrUtil.sub(beanClassName, 0, indexOf);

            beanClass = Class.forName(beanClassName);

        }

        RequestMapping classRequestMappingAnnotation = beanClass.getAnnotation(
            RequestMapping.class);

        StrBuilder strBuilder = StrBuilder.create();

        String classRequestMappingValue = "";
        if (classRequestMappingAnnotation != null) {
            classRequestMappingValue = classRequestMappingAnnotation.value()[0]; // 只取第一个路径
        }

        Method[] declaredMethodArr = beanClass.getDeclaredMethods();

        // 添加：映射
        for (Method item : declaredMethodArr) {

            PostMapping methodMappingAnnotation = item.getAnnotation(PostMapping.class);

            if (methodMappingAnnotation == null) {
                continue;
            }

            String methodRequestMappingValue;

            if (ArrayUtil.isEmpty(methodMappingAnnotation.value())) {

                methodRequestMappingValue = "";

            } else {

                methodRequestMappingValue = methodMappingAnnotation.value()[0]; // 只取第一个路径

            }

            // 组装并处理路径
            List<String> splitTrimList = StrUtil.splitTrim(
                strBuilder.append(classRequestMappingValue).append("/")
                    .append(methodRequestMappingValue)
                    .toStringAndReset(), "/");

            String key = "/" + CollUtil.join(splitTrimList, "/");

            if (MAPPING_MAP.containsKey(key)) {
                throw new RuntimeException(StrUtil.format("NettyWebSocket，映射重复：【{}】", key));
            }

            // 添加到映射 map里
            MAPPING_MAP.put(key, new MappingValue(bean, item));

        }

        return bean;

    }
}
