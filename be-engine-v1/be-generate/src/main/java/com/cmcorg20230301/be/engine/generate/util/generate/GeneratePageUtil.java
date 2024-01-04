package com.cmcorg20230301.be.engine.generate.util.generate;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.func.VoidFunc0;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.be.engine.generate.model.bo.BeApi;
import com.cmcorg20230301.be.engine.generate.util.apitest.ApiTestHelper;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.util.util.CallBack;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 生成页面的工具类
 */
@Slf4j
@Data
public class GeneratePageUtil {

    // 读取：接口的地址
    //    private String SPRING_DOC_ENDPOINT = "http://43.154.37.130:10001/v3/api-docs/be";
    private String springDocEndpoint = "http://127.0.0.1:10001/v3/api-docs/be";

    private String systemUserDir = System.getProperty("user.dir"); // 例如：D:\GitHub\CmcOrg-20230301

    private String pagePath = getSystemUserDir() + "/fe-antd-v1/src/page/sys";

    // 其他常量
    private String tsx = ".tsx"; // 页面文件后缀
    private String tabIndent = "    "; // 缩进

    // Admin Table Page 页面模板
    private String adminPageTemp = FileUtil.readUtf8String("template/admin/AdminPageTableTemp.txt");

    // Admin Tree Page 页面模板
    private String adminTreeTemp = FileUtil.readUtf8String("template/admin/AdminPageTreeTemp.txt");

    // Admin 表单 页面模板
    private String adminFormFileName = "SchemaFormColumnList" + tsx;
    private String adminFormTemp = FileUtil.readUtf8String("template/admin/SchemaFormColumnList.txt");

    // Admin Table 页面模板
    private String adminTableFileName = "TableColumnList" + tsx;
    private String adminTableTemp = FileUtil.readUtf8String("template/admin/TableColumnList.txt");

    // 要替换的字符
    private String adminDeleteByIdSetApi = "AdminDeleteByIdSetApi";
    private String adminPageVO = "AdminPageVO";
    private String adminInfoByIdApi = "AdminInfoByIdApi";

    private String adminInsertOrUpdateApi = "AdminInsertOrUpdateApi";
    private String adminInsertOrUpdateDTO = "AdminInsertOrUpdateDTO";

    private String adminPageApi = "AdminPageApi";
    private String adminPageDTO = "AdminPageDTO";

    private String adminController = "AdminController";
    private String adminTsxTitle = "AdminTsxTitle";

    private String adminModalFormTitle = "AdminModalFormTitle";
    private String adminAddOrderNoApi = "AdminAddOrderNoApi";

    private String adminTreeApi = "AdminTreeApi";

    private String adminDeleteName = "AdminDeleteName";

    private String adminDefaultDeleteName = "name";

    private String adminFormJson = "AdminFormJson";

    private String adminTableJson = "AdminTableJson";

    // 一般的 form json字段模板
    private String adminFormJsonItemNormal =
            "\n        {\n            title: '{}',\n            dataIndex: '{}',\n{}        },\n";

    // 一般的 table json字段模板
    private String adminTableJsonItemNormal = "\n    {title: '{}', dataIndex: '{}', ellipsis: true, width: 90,{}},\n";

    // tooltip
    private String adminJsonItemTooltip = "            tooltip: '{}',\n";

    // formItemProps
    private String adminJsonItemFormItemProps =
            "            formItemProps: {\n" + "                rules: [\n" + "                    {" + "{}"
                    + "\n                    },\n" + "                ],\n" + "            },\n";

    // formItemProps required: true
    private String adminFormJsonItemFormItemPropsRequired = "\n                        required: true,";

    // formItemProps whitespace: true
    private String adminFormJsonItemFormItemPropsWhitespace = "\n                        whitespace: true,";

    // formItemProps max: {}
    private String adminFormJsonItemFormItemPropsMax = "\n                        max: {},";

    // formItemProps min: {}
    private String adminFormJsonItemFormItemPropsMin = "\n                        min: {},";

    // formItemProps pattern: {}
    private String adminFormJsonItemFormItemPropsPattern = "\n                        pattern: /{}/,";

    // formItemProps type: number
    private String adminFormJsonItemFormItemPropsTypeNumber = "\n                        type: 'number',";

    private String yesNoDict = "YesNoDict";

    private String importYesNoDict = "import {YesNoDict} from \"@/util/DictUtil\";\n";

    private String adminTableJsonItemYesNoDictSelect =
            "\n    {}{\n" + "        title: '{}',\n" + "        dataIndex: '{}',\n" + "        valueEnum: YesNoDict,\n"
                    + "        width: 90,\n" + "    },\n";

    // YesNoDict 开关
    private String adminFormJsonItemYesNoDictSwitch =
            "\n    {}{\n" + "            title: '{}',\n" + "            dataIndex: '{}',\n"
                    + "            valueEnum: YesNoDict,\n" + "            valueType: 'switch',\n" + "{}        },\n";

    // fromNow 并且 hideInSearch
    private String adminTableJsonItemFromNowAndHideInSearch =
            "\n    {}{\n" + "        title: '{}',\n" + "        dataIndex: '{}',\n" + "        hideInSearch: true,\n"
                    + "        valueType: 'fromNow',\n" + "        width: 90,\n" + "    },\n";

    // 最大长度为 300的文字域输入框，remark
    private String remark = "remark";

    private String adminFormJsonItemTextarea300Remark =
            "\n        {\n" + "            title: '{}',\n" + "            dataIndex: 'remark',\n"
                    + "            valueType: 'textarea',\n" + "            formItemProps: {\n" + "                rules: [\n"
                    + "                    {\n" + "                        whitespace: true,\n"
                    + "                        max: 300,\n" + "                    },\n" + "                ],\n"
                    + "            },\n" + "            fieldProps: {\n" + "                showCount: true,\n"
                    + "                maxLength: 300,\n" + "                allowClear: true,\n" + "            }\n"
                    + "        },\n";

    // fieldProps：下拉选，多选
    private String adminJsonFieldPropsSelectMultiple =
            "            valueType: 'select',\n" + "            fieldProps: {\n" + "                showSearch: true,\n"
                    + "                allowClear: true,\n" + "                mode: 'multiple',\n"
                    + "                maxTagCount: 'responsive',\n" + "            },\n{}\n";

    public String getSystemUserDir() {

        if (!systemUserDir.contains("CmcOrg-20230301")) {
            return systemUserDir + "/CmcOrg-20230301";
        }

        return systemUserDir;

    }

    public static void main(String[] args) {

        GeneratePageUtil generatePageUtil = new GeneratePageUtil();

        // 执行
        generatePageUtil.exec(generatePageUtil.getSpringDocEndpoint(), generatePageUtil.getPagePath());

    }

    /**
     * 执行
     */
    public void exec(String springDocEndpoint, String pagePath) {

        HashMap<String, HashMap<String, BeApi>> apiMap = SpringDocUtil.get(springDocEndpoint);

        System.out.println("所有的 group ↓");
        System.out.println(JSONUtil.toJsonStr(apiMap.keySet()));
        System.out.println("所有的 group ↑");

        String sysGroupStr =
                apiMap.keySet().stream().filter(it -> it.startsWith("Sys")).collect(Collectors.joining(" "));

        String group = ApiTestHelper.getStrFromScanner("请输入要生成页面的 group，多个用空格隔开，例如：" + sysGroupStr + "，为【all】则生成全部");

        Collection<String> groupSet;

        if ("all".equals(group)) {

            groupSet = apiMap.keySet();

        } else {

            groupSet = StrUtil.splitTrim(group, " ");

        }

        for (String item : groupSet) {

            doExec(pagePath, apiMap, item);

        }

    }

    /**
     * 开始执行
     */
    public void doExec(String pagePath, HashMap<String, HashMap<String, BeApi>> apiMap, String group) {

        HashMap<String, BeApi> pathBeApiMap = apiMap.get(group);

        if (CollUtil.isEmpty(pathBeApiMap)) {

            log.info("操作失败：group不存在：{}", group);
            return;

        }

        // 例如：Menu
        String fileNamePre = group.contains("Sys") ? group.replace("Sys", "") : group;

        // 例如：/fe-antd-v1/src/page/sys/Menu/
        pagePath = pagePath + "/" + fileNamePre + "/";

        CallBack<String> adminPageVoCallBack = new CallBack<>();
        CallBack<String> adminDeleteByIdSetApiCallBack = new CallBack<>();
        CallBack<String> adminInsertOrUpdateApiCallBack = new CallBack<>();
        CallBack<String> adminInsertOrUpdateDtoCallBack = new CallBack<>();
        CallBack<String> adminControllerCallBack = new CallBack<>();
        CallBack<String> adminDeleteNameCallBack = new CallBack<>();

        // 生成 table页面
        generateTableColumnList(pathBeApiMap, pagePath, group, adminPageVoCallBack, adminDeleteByIdSetApiCallBack,
                adminInsertOrUpdateApiCallBack, adminControllerCallBack, adminDeleteNameCallBack,
                adminInsertOrUpdateDtoCallBack);

        // 生成 page页面
        generatePage(pathBeApiMap, pagePath, group, fileNamePre, adminPageVoCallBack, adminDeleteByIdSetApiCallBack,
                adminInsertOrUpdateApiCallBack, adminControllerCallBack, adminDeleteNameCallBack,
                adminInsertOrUpdateDtoCallBack);

        // 生成 表单页面
        generateSchemaFormColumnList(pathBeApiMap, pagePath, group, fileNamePre, adminPageVoCallBack,
                adminDeleteByIdSetApiCallBack, adminInsertOrUpdateApiCallBack, adminControllerCallBack,
                adminDeleteNameCallBack, adminInsertOrUpdateDtoCallBack);

    }

    /**
     * 生成 表单页面
     */
    public void generateSchemaFormColumnList(HashMap<String, BeApi> pathBeApiMap, String pagePath, String group,
                                             String fileNamePre, CallBack<String> adminPageVoCallBack, CallBack<String> adminDeleteByIdSetApiCallBack,
                                             CallBack<String> adminInsertOrUpdateApiCallBack, CallBack<String> adminControllerCallBack,
                                             CallBack<String> adminDeleteNameCallBack, CallBack<String> adminInsertOrUpdateDtoCallBack) {

        String pageFilePath = pagePath + getAdminFormFileName();

        log.info("开始生成 Admin Form页面文件：{}", pageFilePath);
        FileUtil.del(pageFilePath); // 先移除文件
        File touchFile = FileUtil.touch(pageFilePath); // 再创建文件

        StrBuilder tempStrBuilder = StrBuilder.create(getAdminFormTemp());

        StrBuilder formJsonStrBuilder = StrBuilder.create();
        String adminInsertOrUpdateApi = getAdminInsertOrUpdateApi();
        String adminInsertOrUpdateDTO = getAdminInsertOrUpdateDTO();
        String adminController = getAdminController();

        adminInsertOrUpdateApi = adminInsertOrUpdateApiCallBack.getValue();
        adminInsertOrUpdateDTO = adminInsertOrUpdateDtoCallBack.getValue();
        adminController = adminControllerCallBack.getValue();

        // 执行替换
        tempStrBuilder =
                StrBuilder.create(equalsAndReplace(tempStrBuilder.toString(), adminController, getAdminController()));

        Set<String> importClassNameSet = new HashSet<>(); // 防止重复写入
        StrBuilder otherStrBuilder = StrBuilder.create(); // 对象复用
        StrBuilder formItemPropsStrBuilder = StrBuilder.create(); // 对象复用

        for (Map.Entry<String, BeApi> item : pathBeApiMap.entrySet()) {

            BeApi beApi = item.getValue();

            String summary = beApi.getSummary();

            if ("新增/修改".equals(summary)) {

                BeApi.BeApiSchema requestBody = beApi.getRequestBody();

                if (requestBody == null) {

                    log.info("处理失败，requestBody是 null，path：{}", beApi.getPath());
                    return;

                }

                for (Map.Entry<String, BeApi.BeApiField> subItem : requestBody.getFieldMap().entrySet()) {

                    // 一个字段
                    BeApi.BeApiField beApiField = subItem.getValue();

                    if (beApiField instanceof BeApi.BeApiParameter) {

                        BeApi.BeApiParameter beApiParameter = (BeApi.BeApiParameter) beApiField;

                        String description = beApiParameter.getDescription();
                        description = StrUtil.subBefore(description, "（", false);
                        description = StrUtil.subBefore(description, "，", false);

                        if ("boolean".equals(beApiParameter.getType())) {

                            importClassForTs(tempStrBuilder, importClassNameSet, getYesNoDict(), getImportYesNoDict());

                            // 获取：formTooltip
                            String formTooltip = getFormTooltip(beApiParameter, description);

                            formJsonStrBuilder.append(StrUtil
                                    .format(getAdminFormJsonItemYesNoDictSwitch(), getTabIndent(), description,
                                            beApiParameter.getName(), formTooltip));

                        } else if (getRemark().equals(beApiParameter.getName())) {

                            formJsonStrBuilder
                                    .append(StrUtil.format(getAdminFormJsonItemTextarea300Remark(), description));

                        } else if (CollUtil.newArrayList("string", "integer").contains(beApiParameter.getType())) {

                            // 添加：formItemProps
                            appendFormItemProps(otherStrBuilder, formItemPropsStrBuilder, beApiParameter);

                            // 添加：select
                            appendSelect(otherStrBuilder, formItemPropsStrBuilder, beApiParameter, tempStrBuilder,
                                    importClassNameSet);

                            // 添加：formTooltip
                            otherStrBuilder.append(getFormTooltip(beApiParameter, description));

                            formJsonStrBuilder.append(StrUtil
                                    .format(getAdminFormJsonItemNormal(), description, beApiParameter.getName(),
                                            otherStrBuilder.toStringAndReset()));

                        } else {

                            log.info("暂不支持此类型，path：{}，name：{}，type：{}", beApi.getPath(), beApiParameter.getName(),
                                    beApiParameter.getType());

                        }

                    }

                }

                break;

            }

        }

        // 执行替换
        tempStrBuilder = StrBuilder
                .create(equalsAndReplace(tempStrBuilder.toString(), adminInsertOrUpdateApi, getAdminInsertOrUpdateApi()));

        tempStrBuilder = StrBuilder
                .create(equalsAndReplace(tempStrBuilder.toString(), adminInsertOrUpdateDTO, getAdminInsertOrUpdateDTO()));

        tempStrBuilder = StrBuilder
                .create(equalsAndReplace(tempStrBuilder.toString(), formJsonStrBuilder.toString(), getAdminFormJson()));

        // 写入内容到文件里
        FileUtil.writeUtf8String(tempStrBuilder.toString(), touchFile);

    }

    /**
     * 添加：select
     */
    public void appendSelect(StrBuilder otherStrBuilder, StrBuilder formItemPropsStrBuilder,
                             BeApi.BeApiParameter beApiParameter, StrBuilder tempStrBuilder, Set<String> importClassNameSet) {

        if (BooleanUtil.isTrue(beApiParameter.getArrFlag())) {

            otherStrBuilder.append(StrUtil.format(getAdminJsonFieldPropsSelectMultiple(), ""));

        }

    }

    /**
     * 通过：关键字导入包
     */
    public void importByKeyWord(StrBuilder strBuilder, String keyword, String objectStr, boolean insertFlag,
                                VoidFunc0 voidFunc0) {

        String regexp = "^.*import \\{(.*?)\\} from .*/" + keyword + "\";.*$";

        String group1 = ReUtil.getGroup1(regexp, strBuilder.toString()); // 例如：GetDictList, YesNoDict

        if (StrUtil.isBlank(group1)) {
            voidFunc0.callWithRuntimeException(); // 执行：其他操作
            return;
        }

        List<String> splitTrimList = StrUtil.splitTrim(group1, ",");

        if (splitTrimList.contains(objectStr)) {
            return;
        }

        if (insertFlag) {
            splitTrimList.add(0, objectStr);
        } else {
            splitTrimList.add(objectStr);
        }

        String replaceTemp = "import {{}} from";

        String formatStr = StrUtil.format(replaceTemp, CollUtil.join(splitTrimList, ", "));

        String replaceResult =
                StrUtil.replace(strBuilder.toStringAndReset(), StrUtil.format(replaceTemp, group1), formatStr);

        strBuilder.append(replaceResult);

    }

    /**
     * 添加：formItemProps
     */
    public void appendFormItemProps(StrBuilder otherStrBuilder, StrBuilder formItemPropsStrBuilder,
                                    BeApi.BeApiParameter beApiParameter) {

        if (BooleanUtil.isTrue(beApiParameter.getRequired())) {

            formItemPropsStrBuilder.append(getAdminFormJsonItemFormItemPropsRequired());

            if ("string".equals(beApiParameter.getType())) {

                formItemPropsStrBuilder.append(getAdminFormJsonItemFormItemPropsWhitespace());

            }

        }

        if ("integer".equals(beApiParameter.getType()) && beApiParameter.getArrFlag() != null && BooleanUtil
                .isFalse(beApiParameter.getArrFlag())) { // 如果是：number，并且不是数组

            formItemPropsStrBuilder.append(getAdminFormJsonItemFormItemPropsTypeNumber());

        }

        if (StrUtil.isNotBlank(beApiParameter.getPattern())) {

            formItemPropsStrBuilder
                    .append(StrUtil.format(getAdminFormJsonItemFormItemPropsPattern(), beApiParameter.getPattern()));

        }

        if (beApiParameter.getMaxLength() != null) {

            formItemPropsStrBuilder
                    .append(StrUtil.format(getAdminFormJsonItemFormItemPropsMax(), beApiParameter.getMaxLength()));

        }

        if (beApiParameter.getMinLength() != null) {

            formItemPropsStrBuilder
                    .append(StrUtil.format(getAdminFormJsonItemFormItemPropsMin(), beApiParameter.getMinLength()));

        }

        String formItemPropsStr = formItemPropsStrBuilder.toStringAndReset();

        if (StrUtil.isNotBlank(formItemPropsStr)) { // 不为空，才添加：formItemProps

            otherStrBuilder.append(StrUtil.format(getAdminJsonItemFormItemProps(), formItemPropsStr));

        }

    }

    /**
     * 获取：formTooltip
     */
    public String getFormTooltip(BeApi.BeApiParameter beApiParameter, String description) {

        String formTooltip;

        if (description.equals(beApiParameter.getDescription())) {

            formTooltip = "";

        } else {

            formTooltip = StrUtil.format(getAdminJsonItemTooltip(), beApiParameter.getDescription());

        }

        return formTooltip;

    }

    /**
     * 生成 page页面
     */
    public void generatePage(HashMap<String, BeApi> pathBeApiMap, String pagePath, String group, String fileNamePre,
                             CallBack<String> adminPageVoCallBack, CallBack<String> adminDeleteByIdSetApiCallBack,
                             CallBack<String> adminInsertOrUpdateApiCallBack, CallBack<String> adminControllerCallBack,
                             CallBack<String> adminDeleteNameCallBack, CallBack<String> adminInsertOrUpdateDtoCallBack) {

        String pageFilePath = pagePath + fileNamePre + getTsx();

        log.info("开始生成 Admin Page页面文件：{}", pageFilePath);
        FileUtil.del(pageFilePath); // 先移除文件
        File touchFile = FileUtil.touch(pageFilePath); // 再创建文件

        String adminDeleteByIdSetApi = getAdminDeleteByIdSetApi();
        String adminInfoByIdApi = getAdminInfoByIdApi();
        String adminInsertOrUpdateApi = getAdminInsertOrUpdateApi();
        String adminInsertOrUpdateDTO = getAdminInsertOrUpdateDTO();
        String adminPageApi = getAdminPageApi();
        String adminPageDTO = getAdminPageDTO();
        String adminController = getAdminController();
        String adminTsxTitle = getAdminTsxTitle();
        String adminModalFormTitle = getAdminModalFormTitle();
        String adminAddOrderNoApi = getAdminAddOrderNoApi();
        String adminTreeApi = getAdminTreeApi();
        String adminPageVO = getAdminPageVO();
        String adminDeleteName = getAdminDeleteName();

        adminDeleteByIdSetApi = adminDeleteByIdSetApiCallBack.getValue();
        adminInsertOrUpdateApi = adminInsertOrUpdateApiCallBack.getValue();
        adminInsertOrUpdateDTO = adminInsertOrUpdateDtoCallBack.getValue();
        adminController = adminControllerCallBack.getValue();
        adminPageVO = adminPageVoCallBack.getValue();
        adminDeleteName = adminDeleteNameCallBack.getValue();

        adminTsxTitle = CollUtil.getFirst(pathBeApiMap.values()).getTag();
        adminModalFormTitle = StrUtil.subBefore(adminTsxTitle, "-", false);

        String tempStr = getAdminPageTemp();

        for (Map.Entry<String, BeApi> item : pathBeApiMap.entrySet()) {

            BeApi beApi = item.getValue();

            String summary = beApi.getSummary();

            if ("分页排序查询".equals(summary)) {

                adminPageApi = GenerateApiUtil.getApiName(beApi.getPath());
                adminPageDTO = beApi.getRequestBody().getClassName();

            } else if ("通过主键id，查看详情".equals(summary)) {

                adminInfoByIdApi = GenerateApiUtil.getApiName(beApi.getPath());

            } else if ("通过主键 idSet，加减排序号".equals(summary)) {

                adminAddOrderNoApi = GenerateApiUtil.getApiName(beApi.getPath());

            } else if ("查询：树结构".equals(summary)) {

                adminTreeApi = GenerateApiUtil.getApiName(beApi.getPath());
                tempStr = getAdminTreeTemp(); // 使用：tree模板

            }

        }

        // 执行替换
        tempStr = equalsAndReplace(tempStr, adminDeleteName, getAdminDeleteName());

        tempStr = equalsAndReplace(tempStr, adminInsertOrUpdateApi, getAdminInsertOrUpdateApi());

        tempStr = equalsAndReplace(tempStr, adminInsertOrUpdateDTO, getAdminInsertOrUpdateDTO());

        tempStr = equalsAndReplace(tempStr, adminInfoByIdApi, getAdminInfoByIdApi());

        tempStr = equalsAndReplace(tempStr, adminPageVO, getAdminPageVO());

        tempStr = equalsAndReplace(tempStr, adminPageApi, getAdminPageApi());

        tempStr = equalsAndReplace(tempStr, adminPageDTO, getAdminPageDTO());

        tempStr = equalsAndReplace(tempStr, adminDeleteByIdSetApi, getAdminDeleteByIdSetApi());

        tempStr = equalsAndReplace(tempStr, adminController, getAdminController());

        tempStr = equalsAndReplace(tempStr, adminTsxTitle, getAdminTsxTitle());

        tempStr = equalsAndReplace(tempStr, adminModalFormTitle, getAdminModalFormTitle());

        tempStr = equalsAndReplace(tempStr, adminAddOrderNoApi, getAdminAddOrderNoApi());

        tempStr = equalsAndReplace(tempStr, adminTreeApi, getAdminTreeApi());

        // 写入内容到文件里
        FileUtil.writeUtf8String(tempStr, touchFile);

    }

    /**
     * 生成 table页面
     */
    public void generateTableColumnList(HashMap<String, BeApi> pathBeApiMap, String pagePath, String group,
                                        CallBack<String> adminPageVoCallBack, CallBack<String> adminDeleteByIdSetApiCallBack,
                                        CallBack<String> adminInsertOrUpdateApiCallBack, CallBack<String> adminControllerCallBack,
                                        CallBack<String> adminDeleteNameCallBack, CallBack<String> adminInsertOrUpdateDtoCallBack) {

        String tableFilePath = pagePath + getAdminTableFileName();

        log.info("开始生成 Admin Table页面文件：{}", tableFilePath);
        FileUtil.del(tableFilePath); // 先移除文件
        File touchFile = FileUtil.touch(tableFilePath); // 再创建文件

        // 需要写入的内容
        StrBuilder tempStrBuilder = StrBuilder.create(getAdminTableTemp());

        StrBuilder tableJsonStrBuilder = StrBuilder.create();

        // 需要替换的字符串
        String adminPageVO = getAdminPageVO();
        String adminDeleteByIdSetApi = getAdminDeleteByIdSetApi();
        String adminInsertOrUpdateApi = getAdminInsertOrUpdateApi();
        String adminInsertOrUpdateDTO = getAdminInsertOrUpdateDTO();
        String adminController = getAdminController();
        String adminDeleteName = getAdminDeleteName();

        adminController = group; // 设置：api的文件名
        adminDeleteName = getAdminDefaultDeleteName(); // 设置：删除名

        Set<String> importClassNameSet = new HashSet<>(); // 防止重复写入

        String pageCheckStr = ApiResultVO.class.getSimpleName() + Page.class.getSimpleName();

        for (Map.Entry<String, BeApi> item : pathBeApiMap.entrySet()) {

            BeApi beApi = item.getValue();

            String summary = beApi.getSummary();

            if ("分页排序查询".equals(summary)) {

                BeApi.BeApiSchema response = (BeApi.BeApiSchema) beApi.getResponse();

                boolean pageFlag = response.getClassName().startsWith(pageCheckStr);

                if (pageFlag) {

                    adminPageVO = StrUtil.subAfter(response.getClassName(), pageCheckStr, false);

                    // 拿到：返回值的对象 ↓
                    BeApi.BeApiSchema beApiSchema =
                            (BeApi.BeApiSchema) response.getFieldMap().get(response.getClassName());

                    BeApi.BeApiField data = beApiSchema.getFieldMap().get("data");

                    BeApi.BeApiSchema dataBeApiSchema = (BeApi.BeApiSchema) data;

                    BeApi.BeApiSchema dataRealBeApiSchema =
                            (BeApi.BeApiSchema) dataBeApiSchema.getFieldMap().get(dataBeApiSchema.getClassName());

                    BeApi.BeApiSchema records = (BeApi.BeApiSchema) dataRealBeApiSchema.getFieldMap().get("records");

                    BeApi.BeApiSchema recordsBeApiSchema =
                            (BeApi.BeApiSchema) records.getFieldMap().get(records.getClassName());
                    // 拿到：返回值的对象 ↑

                    for (Map.Entry<String, BeApi.BeApiField> subItem : recordsBeApiSchema.getFieldMap().entrySet()) {

                        // 一个字段
                        BeApi.BeApiField beApiField = subItem.getValue();

                        if (beApiField instanceof BeApi.BeApiParameter) {

                            BeApi.BeApiParameter beApiParameter = (BeApi.BeApiParameter) beApiField;

                            String description = beApiParameter.getDescription();
                            description = StrUtil.subBefore(description, "（", false);
                            description = StrUtil.subBefore(description, "，", false);

                            if ("boolean".equals(beApiParameter.getType())) {

                                importClassForTs(tempStrBuilder, importClassNameSet, getYesNoDict(),
                                        getImportYesNoDict());

                                tableJsonStrBuilder.append(StrUtil
                                        .format(getAdminTableJsonItemYesNoDictSelect(), "", description,
                                                beApiParameter.getName()));

                            } else if ("date-time".equals(beApiParameter.getFormat())) {

                                tableJsonStrBuilder.append(StrUtil
                                        .format(getAdminTableJsonItemFromNowAndHideInSearch(), "", description,
                                                beApiParameter.getName()));

                            } else if (CollUtil.newArrayList("string", "integer").contains(beApiParameter.getType())) {

                                tableJsonStrBuilder.append(StrUtil
                                        .format(getAdminTableJsonItemNormal(), description, beApiParameter.getName(), ""));

                            } else {

                                log.info("暂不支持此类型，path：{}，name：{}，type：{}", beApi.getPath(), beApiParameter.getName(),
                                        beApiParameter.getType());

                            }

                        }

                    }

                } else {

                    log.info("分页排序查询：类型错误，path：{}", beApi.getPath());

                }

            } else if ("新增/修改".equals(summary)) {

                adminInsertOrUpdateApi = GenerateApiUtil.getApiName(beApi.getPath());
                adminInsertOrUpdateDTO = beApi.getRequestBody().getClassName();

            } else if ("批量删除".equals(summary)) {

                adminDeleteByIdSetApi = GenerateApiUtil.getApiName(beApi.getPath());

            }

        }

        // 执行替换
        tempStrBuilder = StrBuilder
                .create(equalsAndReplace(tempStrBuilder.toString(), adminInsertOrUpdateApi, getAdminInsertOrUpdateApi()));

        tempStrBuilder = StrBuilder
                .create(equalsAndReplace(tempStrBuilder.toString(), adminInsertOrUpdateDTO, getAdminInsertOrUpdateDTO()));

        tempStrBuilder = StrBuilder.create(equalsAndReplace(tempStrBuilder.toString(), adminPageVO, getAdminPageVO()));

        tempStrBuilder = StrBuilder
                .create(equalsAndReplace(tempStrBuilder.toString(), adminDeleteByIdSetApi, getAdminDeleteByIdSetApi()));

        tempStrBuilder =
                StrBuilder.create(equalsAndReplace(tempStrBuilder.toString(), adminController, getAdminController()));

        tempStrBuilder =
                StrBuilder.create(equalsAndReplace(tempStrBuilder.toString(), adminDeleteName, getAdminDeleteName()));

        tempStrBuilder = StrBuilder
                .create(equalsAndReplace(tempStrBuilder.toString(), tableJsonStrBuilder.toString(), getAdminTableJson()));

        // 设置：回调值
        adminPageVoCallBack.setValue(adminPageVO);
        adminDeleteByIdSetApiCallBack.setValue(adminDeleteByIdSetApi);
        adminInsertOrUpdateApiCallBack.setValue(adminInsertOrUpdateApi);
        adminInsertOrUpdateDtoCallBack.setValue(adminInsertOrUpdateDTO);
        adminControllerCallBack.setValue(adminController);
        adminDeleteNameCallBack.setValue(adminDeleteName);

        // 写入内容到文件里
        FileUtil.writeUtf8String(tempStrBuilder.toString(), touchFile);

    }

    /**
     * 比较并替换
     */
    public String equalsAndReplace(String tempStr, String newValue, String oldValue) {

        if (BooleanUtil.isFalse(newValue.equals(oldValue))) {

            return StrUtil.replace(tempStr, oldValue, newValue);

        }

        return tempStr;

    }

    /**
     * ts：导入包
     */
    public void importClassForTs(StrBuilder tempStrBuilder, Set<String> importClassNameSet, String className,
                                 String importClassStr) {

        if (BooleanUtil.isFalse(importClassNameSet.contains(className))) {

            importClassNameSet.add(className);
            tempStrBuilder.insert(0, importClassStr); // 添加导入

        }

    }

}
