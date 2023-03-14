package com.cmcorg20230301.engine.be.generate.fe.antd.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharPool;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.cmcorg20230301.engine.be.generate.fe.antd.model.dto.PageDTO;
import com.cmcorg20230301.engine.be.generate.fe.antd.model.dto.RequestDTO;
import com.cmcorg20230301.engine.be.generate.fe.antd.model.dto.RequestFieldDTO;
import com.cmcorg20230301.engine.be.generate.fe.antd.model.dto.WebDTO;
import com.cmcorg20230301.engine.be.generate.fe.antd.model.enums.ColumnTypeRefEnum;
import com.cmcorg20230301.engine.be.generate.model.annotation.Request;
import com.cmcorg20230301.engine.be.generate.model.annotation.WebPage;
import com.cmcorg20230301.engine.be.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.engine.be.model.model.dto.NotNullId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

@Slf4j(topic = LogTopicConstant.JAVA_TO_WEB)
public class JavaToWebJsonUtil {

    private static final String PACKAGE_NAME = "com.cmcorg";
    private static final Class<WebPage> WEB_PAGE_ANNOTATION = WebPage.class;
    private static final Class<RequestMapping> REQUEST_MAPPING_ANNOTATION = RequestMapping.class;
    private static final Class<Tag> TAG_ANNOTATION = Tag.class;

    private static final Class<Request> REQUEST_ANNOTATION = Request.class;
    private static final Class<PostMapping> POST_MAPPING_ANNOTATION = PostMapping.class;
    private static final Class<GetMapping> GET_MAPPING_ANNOTATION = GetMapping.class;
    private static final Class<Operation> OPERATION_ANNOTATION = Operation.class;

    // 其他常量
    private static final String API_RESULT_VO_CLASS_NAME = "ApiResultVO";
    private static final String PAGE_CLASS_FULL_NAME = "com.baomidou.mybatisplus.extension.plugins.pagination.Page";
    private static final String LIST_CLASS_FULL_NAME = List.class.getName();

    /**
     * 把 java的接口，转换为 json格式
     */
    @Nullable
    public static WebDTO doJavaToWebJson() {

        log.info("JavaToWebJson：执行开始 =====================>");
        TimeInterval timer = DateUtil.timer();

        WebDTO webDTO = JavaToWebJsonUtil.execJavaToWebJson();

        log.info(JSONUtil.toJsonStr(webDTO));

        long interval = timer.interval();
        log.info("JavaToWebJson：执行结束 =====================> 耗时：{}毫秒", interval);

        return webDTO;
    }

    /**
     * 通过 java，直接生成用于生成前端页面的 json
     */
    @SneakyThrows
    @Nullable
    private static WebDTO execJavaToWebJson() {

        Set<Class<?>> webPageClassSet =
            ClassUtil.scanPackageByAnnotation(JavaToWebJsonUtil.PACKAGE_NAME, JavaToWebJsonUtil.WEB_PAGE_ANNOTATION);

        if (CollUtil.isEmpty(webPageClassSet)) {
            log.info("【{}】下的 page为空", JavaToWebJsonUtil.PACKAGE_NAME);
            return null;
        }

        WebDTO webDTO = new WebDTO();
        webDTO.setPageList(new ArrayList<>());
        webDTO.setPageTypeMap(MapUtil.newHashMap(webPageClassSet.size()));

        for (Class<?> clazz : webPageClassSet) {

            WebPage webPageAnnotation = clazz.getAnnotation(JavaToWebJsonUtil.WEB_PAGE_ANNOTATION);

            PageDTO pageDTO = new PageDTO();
            pageDTO.setFileName(clazz.getSimpleName());

            if (StrUtil.isBlank(webPageAnnotation.path())) { // 不存在则取：RestController的值
                RequestMapping restControllerAnnotation =
                    clazz.getAnnotation(JavaToWebJsonUtil.REQUEST_MAPPING_ANNOTATION);
                pageDTO.setPath(restControllerAnnotation.value()[0]); // 这里只取一个 path
            } else {
                pageDTO.setPath(webPageAnnotation.path());
            }

            pageDTO.setType(webPageAnnotation.type());

            if (ArrayUtil.isEmpty(webPageAnnotation.title())) {
                Tag tagAnnotation = clazz.getAnnotation(JavaToWebJsonUtil.TAG_ANNOTATION);
                pageDTO.setTitle(tagAnnotation.name()); // 不存在则取：Tag的值
            } else {
                pageDTO.setTitle(webPageAnnotation.title());
            }

            pageDTO.setIcon(webPageAnnotation.icon());
            pageDTO.setRequestList(new ArrayList<>());

            webDTO.getPageList().add(pageDTO); // 添加页面

            // 添加：页面类型和数组下标
            Set<Integer> set = webDTO.getPageTypeMap().computeIfAbsent(pageDTO.getType(), k -> new HashSet<>());

            set.add(webDTO.getPageList().size() - 1);

            // 添加请求
            JavaToWebJsonUtil.addRequest(clazz, pageDTO);

        }

        return webDTO;
    }

    /**
     * 添加请求
     */
    private static void addRequest(Class<?> clazz, PageDTO pageDTO) {

        Method[] declaredMethodArr = clazz.getDeclaredMethods();

        for (Method method : declaredMethodArr) {

            Request requestAnnotation = method.getAnnotation(JavaToWebJsonUtil.REQUEST_ANNOTATION);
            if (requestAnnotation != null && requestAnnotation.ignoreFlag()) {
                continue; // 忽略的接口
            }

            RequestDTO requestDTO = new RequestDTO();

            String[] uriArr; // uri 数组

            if (requestAnnotation == null || ArrayUtil.isEmpty(requestAnnotation.uriArr())) { // 不存在则取：PostMapping的值
                PostMapping postMappingAnnotation = method.getAnnotation(JavaToWebJsonUtil.POST_MAPPING_ANNOTATION);
                if (postMappingAnnotation == null) {
                    GetMapping getMappingAnnotation =
                        method.getAnnotation(JavaToWebJsonUtil.GET_MAPPING_ANNOTATION); // 不存在则取：GetMapping的值

                    uriArr = getMappingAnnotation.value();
                    requestDTO.setMethod(HttpMethod.GET);
                } else {
                    uriArr = postMappingAnnotation.value();
                    requestDTO.setMethod(HttpMethod.POST);
                }
            } else {
                uriArr = requestAnnotation.uriArr();
                requestDTO.setMethod(requestAnnotation.method());
            }

            if (uriArr.length != 0) { // 如果不存在：则使用类的值
                requestDTO.setUri(uriArr[0]);
                requestDTO.setFullUri(JavaToWebJsonUtil.getFullUriByValueArr(uriArr, pageDTO.getPath()));
            } else {
                requestDTO.setUri(pageDTO.getPath());
                requestDTO.setFullUri(pageDTO.getPath());
            }

            // 先驼峰转 斜杠，再斜杠转驼峰
            requestDTO.setFullUriHump(
                StrUtil.toCamelCase(StrUtil.toSymbolCase(requestDTO.getFullUri(), CharPool.SLASH), CharPool.SLASH));

            Operation operationAnnotation = method.getAnnotation(JavaToWebJsonUtil.OPERATION_ANNOTATION);
            if (operationAnnotation != null) {
                requestDTO.setDescription(operationAnnotation.summary()); // 设置：请求名
            }

            Parameter[] parameterArr = method.getParameters();

            if (ArrayUtil.isNotEmpty(parameterArr)) {

                Parameter parameter = parameterArr[0]; // 暂时只获取第一个参数

                // 设置：参数 class名
                requestDTO.setParamClass((parameter.getType()));

                Map<String, RequestFieldDTO> fieldMap = ColumnTypeRefEnum.getFieldMapByClazz(parameter.getType());

                requestDTO.setFormMap(fieldMap);
            }

            setAboutReturn(requestDTO, method);

            pageDTO.getRequestList().add(requestDTO); // 添加请求
        }

        // 这里需要排序一下，不然每次都是顺序都是乱的
        pageDTO.getRequestList().sort(Comparator.comparing(RequestDTO::getFullUriHump));
    }

    private static String getFullUriByValueArr(String[] valueArr, String pre) {
        return pre + valueArr[0];
    }

    /**
     * 设置：返回相关
     */
    @SneakyThrows
    private static void setAboutReturn(RequestDTO requestDTO, Method method) {

        requestDTO.setInfoByIdFlag(false);
        requestDTO.setPageFlag(false);
        requestDTO.setTreeFlag(false);

        if (!API_RESULT_VO_CLASS_NAME.equals(method.getReturnType().getSimpleName())) {
            return;
        }

        if (requestDTO.getParamClass() != null && requestDTO.getParamClass().equals(NotNullId.class)) {
            requestDTO.setInfoByIdFlag(true);
        }

        ParameterizedType parameterizedTypeOne = (ParameterizedType)method.getGenericReturnType();

        for (Type item : parameterizedTypeOne.getActualTypeArguments()) { // 遍历：第一层泛型
            if (item instanceof ParameterizedType) {
                ParameterizedType parameterizedTypeTwo = (ParameterizedType)item;
                for (Type subItem : parameterizedTypeTwo.getActualTypeArguments()) { // 遍历：第二层泛型
                    if (!(subItem instanceof ParameterizedType)) {
                        if (PAGE_CLASS_FULL_NAME.equals(parameterizedTypeTwo.getRawType().getTypeName())) {
                            requestDTO.setPageFlag(true);
                        } else if (LIST_CLASS_FULL_NAME.equals(parameterizedTypeTwo.getRawType().getTypeName())) {
                            requestDTO.setTreeFlag(true);
                        }
                        requestDTO.setReturnRealClass(ClassUtil.loadClass(
                            parameterizedTypeTwo.getActualTypeArguments()[0].getTypeName())); // 第二层，例如 Page<VO>，里面的 vo
                    }
                }
            } else {
                requestDTO.setReturnRealClass(ClassUtil.loadClass(item.getTypeName())); // 第一层：例如 String
            }
        }

    }

}
