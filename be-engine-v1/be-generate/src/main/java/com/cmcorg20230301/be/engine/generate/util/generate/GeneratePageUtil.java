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
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.*;

/**
 * 生成页面的工具类
 */
@Slf4j
public class GeneratePageUtil {

    // 读取：接口的地址
    private static final String SPRING_DOC_ENDPOINT = "http://43.154.37.130:10001/v3/api-docs/be";
    //    private static final String SPRING_DOC_ENDPOINT = "http://127.0.0.1:10001/v3/api-docs/be";

    private static final String SYSTEM_USER_DIR = System.getProperty("user.dir"); // 例如：D:\GitHub\CmcOrg-20230301

    private static final String PAGE_PATH = SYSTEM_USER_DIR + "/fe-antd-v1/src/page/sys";

    // 其他常量
    private static final String TSX = ".tsx"; // 页面文件后缀
    private static final String TAB_INDENT = "    "; // 缩进

    // Admin Table Page 页面模板
    private static final String ADMIN_PAGE_TEMP = FileUtil.readUtf8String("template/admin/AdminPageTableTemp.tsx");

    // Admin Tree Page 页面模板
    private static final String ADMIN_TREE_TEMP = FileUtil.readUtf8String("template/admin/AdminPageTreeTemp.tsx");

    // Admin 表单 页面模板
    private static final String ADMIN_FORM_FILE_NAME = "SchemaFormColumnList" + TSX;
    private static final String ADMIN_FORM_TEMP = FileUtil.readUtf8String("template/admin/SchemaFormColumnList.tsx");

    // Admin Table 页面模板
    private static final String ADMIN_TABLE_FILE_NAME = "TableColumnList" + TSX;
    private static final String ADMIN_TABLE_TEMP = FileUtil.readUtf8String("template/admin/TableColumnList.txt");

    // 要识别：路径
    private static final String ADD_ORDER_NO = "/addOrderNo";
    private static final String DELETE_BY_ID_SET = "/deleteByIdSet";
    private static final String INFO_BY_ID = "/infoById";
    private static final String INSERT_OR_UPDATE = "/insertOrUpdate";
    private static final String PAGE = "/page";
    private static final String TREE = "/tree";

    // 要替换的字符
    private static final String ADMIN_DELETE_BY_ID_SET_API = "AdminDeleteByIdSetApi";
    private static final String ADMIN_PAGE_VO = "AdminPageVO";
    private static final String ADMIN_INFO_BY_ID_API = "AdminInfoByIdApi";

    private static final String ADMIN_INSERT_OR_UPDATE_API = "AdminInsertOrUpdateApi";
    private static final String ADMIN_INSERT_OR_UPDATE_DTO = "AdminInsertOrUpdateDTO";

    private static final String ADMIN_PAGE_API = "AdminPageApi";
    private static final String ADMIN_PAGE_DTO = "AdminPageDTO";

    private static final String ADMIN_CONTROLLER = "AdminController";
    private static final String ADMIN_TSX_TITLE = "AdminTsxTitle";

    private static final String ADMIN_MODAL_FORM_TITLE = "AdminModalFormTitle";
    private static final String ADMIN_ADD_ORDER_NO_API = "AdminAddOrderNoApi";

    private static final String ADMIN_TREE_API = "AdminTreeApi";

    private static final String ADMIN_DELETE_NAME = "AdminDeleteName";

    private static final String ADMIN_DEFAULT_DELETE_NAME = "name";

    private static final String ADMIN_FORM_JSON = "AdminFormJson";

    private static final String ADMIN_TABLE_JSON = "AdminTableJson";

    // 一般的 form json字段模板
    private static final String ADMIN_FORM_JSON_ITEM_NORMAL =
        "\n        {\n            title: '{}',\n            dataIndex: '{}',\n{}        },\n";

    // 一般的 table json字段模板
    private static final String ADMIN_TABLE_JSON_ITEM_NORMAL =
        "\n    {title: '{}', dataIndex: '{}', ellipsis: true, width: 90,{}},\n";

    // tooltip
    private static final String ADMIN_JSON_ITEM_TOOLTIP = "            tooltip: '{}',\n";

    // formItemProps
    private static final String ADMIN_JSON_ITEM_FORM_ITEM_PROPS =
        "            formItemProps: {\n" + "                rules: [\n" + "                    {" + "{}"
            + "\n                    },\n" + "                ],\n" + "            },\n";

    // formItemProps required: true
    private static final String ADMIN_FORM_JSON_ITEM_FORM_ITEM_PROPS_REQUIRED =
        "\n                        required: true,";

    // formItemProps whitespace: true
    private static final String ADMIN_FORM_JSON_ITEM_FORM_ITEM_PROPS_WHITESPACE =
        "\n                        whitespace: true,";

    // formItemProps max: {}
    private static final String ADMIN_FORM_JSON_ITEM_FORM_ITEM_PROPS_MAX = "\n                        max: {},";

    // formItemProps min: {}
    private static final String ADMIN_FORM_JSON_ITEM_FORM_ITEM_PROPS_MIN = "\n                        min: {},";

    // formItemProps pattern: {}
    private static final String ADMIN_FORM_JSON_ITEM_FORM_ITEM_PROPS_PATTERN =
        "\n                        pattern: /{}/,";

    // formItemProps type: number
    private static final String ADMIN_FORM_JSON_ITEM_FORM_ITEM_PROPS_TYPE_NUMBER =
        "\n                        type: 'number',";

    // YesNoDict 下拉选
    private static final String DICT_UTIL = "DictUtil";

    private static final String GET_DICT_LIST = "GetDictList";

    private static final String GET_DICT_TREE_LIST = "GetDictTreeList";

    private static final String IMPORT_YES_NO_DICT_AND_GET = "import {{}, YesNoDict} from \"@/util/DictUtil\";\n";

    private static final String YES_NO_DICT = "YesNoDict";

    private static final String IMPORT_YES_NO_DICT = "import {YesNoDict} from \"@/util/DictUtil\";\n";

    private static final String ADMIN_TABLE_JSON_ITEM_YES_NO_DICT_SELECT =
        "\n    {}{\n" + "        title: '{}',\n" + "        dataIndex: '{}',\n" + "        valueEnum: YesNoDict\n"
            + "    },\n";

    // YesNoDict 开关
    private static final String ADMIN_FORM_JSON_ITEM_YES_NO_DICT_SWITCH =
        "\n    {}{\n" + "            title: '{}',\n" + "            dataIndex: '{}',\n"
            + "            valueEnum: YesNoDict,\n" + "            valueType: 'switch',\n" + "{}        },\n";

    // fromNow 并且 hideInSearch
    private static final String ADMIN_TABLE_JSON_ITEM_FROM_NOW_AND_HIDE_IN_SEARCH =
        "\n    {}{\n" + "        title: '{}',\n" + "        dataIndex: '{}',\n" + "        hideInSearch: true,\n"
            + "        valueType: 'fromNow',\n" + "    },\n";

    // hideInSearch：true
    private static final String ADMIN_TABLE_JSON_ITEM_HIDE_IN_SEARCH = " hideInSearch: true,";

    // 最大长度为 300的文字域输入框，remark
    private static final String REMARK = "remark";

    private static final String ADMIN_FORM_JSON_ITEM_TEXTAREA_300_REMARK =
        "\n        {\n" + "            title: '{}',\n" + "            dataIndex: 'remark',\n"
            + "            valueType: 'textarea',\n" + "            formItemProps: {\n" + "                rules: [\n"
            + "                    {\n" + "                        whitespace: true,\n"
            + "                        max: 300,\n" + "                    },\n" + "                ],\n"
            + "            },\n" + "            fieldProps: {\n" + "                showCount: true,\n"
            + "                maxLength: 300,\n" + "                allowClear: true,\n" + "            }\n"
            + "        },\n";

    // fieldProps：下拉选，多选
    private static final String ADMIN_JSON_FIELD_PROPS_SELECT_MULTIPLE =
        "            valueType: 'select',\n" + "            fieldProps: {\n" + "                showSearch: true,\n"
            + "                mode: 'multiple',\n" + "                maxTagCount: 'responsive',\n"
            + "            },\n{}\n";

    // fieldProps：下拉选，单选
    private static final String ADMIN_JSON_FIELD_PROPS_SELECT =
        "            valueType: 'select',\n" + "            fieldProps: {\n" + "                showSearch: true,\n{}"
            + "            },\n";

    // fieldProps：树形下拉选，多选
    private static final String ADMIN_JSON_FIELD_PROPS_TREE_SELECT_MULTIPLE =
        "            valueType: 'treeSelect',\n" + "            fieldProps: {\n"
            + "                placeholder: '请选择',\n" + "                allowClear: true,\n"
            + "                treeNodeFilterProp: 'title',\n" + "                maxTagCount: 'responsive',\n"
            + "                treeCheckable: true,\n"
            + "                showCheckedStrategy: TreeSelect.SHOW_PARENT,\n" + "            },\n{}\n";

    // fieldProps：树形下拉选，单选
    private static final String ADMIN_JSON_FIELD_PROPS_TREE_SELECT =
        "            valueType: \"treeSelect\",\n" + "            fieldProps: {\n"
            + "                placeholder: '为空则表示顶级节点',\n" + "                allowClear: true,\n"
            + "                showSearch: true,\n" + "                treeNodeFilterProp: 'title',\n"
            + "            },\n{}\n";

    // fieldProps：获取选项
    private static final String ADMIN_JSON_DICT_REQUEST =
        "            request: () => {\n" + "                return GetDictList({})\n" + "            }";

    // fieldProps：获取树形结构选项
    private static final String ADMIN_JSON_DICT_TREE_REQUEST =
        "            request: () => {\n" + "                return GetDictTreeList({});\n" + "            }";

    private static final String ADMIN_JSON_DICT_TREE_OPTIONS = "                options: {},\n";

    private static final String IMPORT_TREE_SELECT = "import {TreeSelect} from \"antd\";\n";

    private static final String TREE_SELECT = "TreeSelect";

    public static void main(String[] args) {

        // 执行
        exec(SPRING_DOC_ENDPOINT, PAGE_PATH);

    }

    /**
     * 执行
     */
    private static void exec(String springDocEndpoint, String pagePath) {

        HashMap<String, HashMap<String, BeApi>> apiMap = SpringDocUtil.get(springDocEndpoint);

        System.out.println("所有的group ↓");
        System.out.println(JSONUtil.toJsonStr(apiMap.keySet()));

        String group = ApiTestHelper.getStrFromScanner(
            "请输入要生成页面的 group，多个用空格隔开，例如：SysMenu SysRequest SysUser SysParam SysDict SysRole SysArea SysDept SysPost，为【all】则生成全部");

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
    private static void doExec(String pagePath, HashMap<String, HashMap<String, BeApi>> apiMap, String group) {

        HashMap<String, BeApi> pathBeApiMap = apiMap.get(group);

        if (CollUtil.isEmpty(pathBeApiMap)) {

            log.info("操作失败：group不存在：{}", group);
            return;

        }

        // 例如：Menu
        String fileNamePre = group.contains("Sys") ? group.replace("Sys", "") : group;

        // 例如：/fe-antd-v1/src/page/sys/Menu/
        pagePath = pagePath + "/" + fileNamePre + "/";

        CallBack<String> adminPageVOCallBack = new CallBack<>();
        CallBack<String> adminDeleteByIdSetApiCallBack = new CallBack<>();
        CallBack<String> adminInsertOrUpdateApiCallBack = new CallBack<>();
        CallBack<String> adminInsertOrUpdateDTOCallBack = new CallBack<>();
        CallBack<String> adminControllerCallBack = new CallBack<>();
        CallBack<String> adminDeleteNameCallBack = new CallBack<>();

        // 生成 table页面
        generateTableColumnList(pathBeApiMap, pagePath, group, adminPageVOCallBack, adminDeleteByIdSetApiCallBack,
            adminInsertOrUpdateApiCallBack, adminControllerCallBack, adminDeleteNameCallBack,
            adminInsertOrUpdateDTOCallBack);

        // 生成 page页面
        generatePage(pathBeApiMap, pagePath, group, fileNamePre, adminPageVOCallBack, adminDeleteByIdSetApiCallBack,
            adminInsertOrUpdateApiCallBack, adminControllerCallBack, adminDeleteNameCallBack,
            adminInsertOrUpdateDTOCallBack);

        // 生成 表单页面
        generateSchemaFormColumnList(pathBeApiMap, pagePath, group, fileNamePre, adminPageVOCallBack,
            adminDeleteByIdSetApiCallBack, adminInsertOrUpdateApiCallBack, adminControllerCallBack,
            adminDeleteNameCallBack, adminInsertOrUpdateDTOCallBack);

    }

    /**
     * 生成 表单页面
     */
    private static void generateSchemaFormColumnList(HashMap<String, BeApi> pathBeApiMap, String pagePath, String group,
        String fileNamePre, CallBack<String> adminPageVOCallBack, CallBack<String> adminDeleteByIdSetApiCallBack,
        CallBack<String> adminInsertOrUpdateApiCallBack, CallBack<String> adminControllerCallBack,
        CallBack<String> adminDeleteNameCallBack, CallBack<String> adminInsertOrUpdateDTOCallBack) {

        String pageFilePath = pagePath + ADMIN_FORM_FILE_NAME;

        log.info("开始生成 Admin Form页面文件：{}", pageFilePath);
        FileUtil.del(pageFilePath); // 先移除文件
        File touchFile = FileUtil.touch(pageFilePath); // 再创建文件

        StrBuilder tempStrBuilder = StrBuilder.create(ADMIN_FORM_TEMP);

        StrBuilder formJsonStrBuilder = StrBuilder.create();
        String adminInsertOrUpdateApi = ADMIN_INSERT_OR_UPDATE_API;
        String adminInsertOrUpdateDTO = ADMIN_INSERT_OR_UPDATE_DTO;
        String adminController = ADMIN_CONTROLLER;

        adminInsertOrUpdateApi = adminInsertOrUpdateApiCallBack.getValue();
        adminInsertOrUpdateDTO = adminInsertOrUpdateDTOCallBack.getValue();
        adminController = adminControllerCallBack.getValue();

        // 执行替换
        tempStrBuilder =
            StrBuilder.create(equalsAndReplace(tempStrBuilder.toString(), adminController, ADMIN_CONTROLLER));

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

                        BeApi.BeApiParameter beApiParameter = (BeApi.BeApiParameter)beApiField;

                        String description = beApiParameter.getDescription();
                        description = StrUtil.subBefore(description, "（", false);
                        description = StrUtil.subBefore(description, "，", false);

                        if ("boolean".equals(beApiParameter.getType())) {

                            importClassForTs(tempStrBuilder, importClassNameSet, YES_NO_DICT, IMPORT_YES_NO_DICT);

                            // 获取：formTooltip
                            String formTooltip = getFormTooltip(beApiParameter, description);

                            formJsonStrBuilder.append(StrUtil
                                .format(ADMIN_FORM_JSON_ITEM_YES_NO_DICT_SWITCH, TAB_INDENT, description,
                                    beApiParameter.getName(), formTooltip));

                        } else if (REMARK.equals(beApiParameter.getName())) {

                            formJsonStrBuilder
                                .append(StrUtil.format(ADMIN_FORM_JSON_ITEM_TEXTAREA_300_REMARK, description));

                        } else if (CollUtil.newArrayList("string", "integer").contains(beApiParameter.getType())) {

                            // 添加：formItemProps
                            appendFormItemProps(otherStrBuilder, formItemPropsStrBuilder, beApiParameter);

                            // 添加：select
                            appendSelect(otherStrBuilder, formItemPropsStrBuilder, beApiParameter, tempStrBuilder,
                                importClassNameSet);

                            // 添加：formTooltip
                            otherStrBuilder.append(getFormTooltip(beApiParameter, description));

                            formJsonStrBuilder.append(StrUtil
                                .format(ADMIN_FORM_JSON_ITEM_NORMAL, description, beApiParameter.getName(),
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
            .create(equalsAndReplace(tempStrBuilder.toString(), adminInsertOrUpdateApi, ADMIN_INSERT_OR_UPDATE_API));

        tempStrBuilder = StrBuilder
            .create(equalsAndReplace(tempStrBuilder.toString(), adminInsertOrUpdateDTO, ADMIN_INSERT_OR_UPDATE_DTO));

        tempStrBuilder = StrBuilder
            .create(equalsAndReplace(tempStrBuilder.toString(), formJsonStrBuilder.toString(), ADMIN_FORM_JSON));

        // 写入内容到文件里
        FileUtil.writeUtf8String(tempStrBuilder.toString(), touchFile);

    }

    /**
     * 添加：select
     */
    private static void appendSelect(StrBuilder otherStrBuilder, StrBuilder formItemPropsStrBuilder,
        BeApi.BeApiParameter beApiParameter, StrBuilder tempStrBuilder, Set<String> importClassNameSet) {

        if (BooleanUtil.isTrue(beApiParameter.getArrFlag())) {

            otherStrBuilder.append(StrUtil.format(ADMIN_JSON_FIELD_PROPS_SELECT_MULTIPLE, ""));

        }

    }

    /**
     * 通过：关键字导入包
     */
    private static void importByKeyWord(StrBuilder strBuilder, String keyword, String objectStr, boolean insertFlag,
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
    private static void appendFormItemProps(StrBuilder otherStrBuilder, StrBuilder formItemPropsStrBuilder,
        BeApi.BeApiParameter beApiParameter) {

        if (BooleanUtil.isTrue(beApiParameter.getRequired())) {

            formItemPropsStrBuilder.append(ADMIN_FORM_JSON_ITEM_FORM_ITEM_PROPS_REQUIRED);

            if ("string".equals(beApiParameter.getType())) {

                formItemPropsStrBuilder.append(ADMIN_FORM_JSON_ITEM_FORM_ITEM_PROPS_WHITESPACE);

            }

        }

        if ("integer".equals(beApiParameter.getType()) && beApiParameter.getArrFlag() != null && BooleanUtil
            .isFalse(beApiParameter.getArrFlag())) { // 如果是：number，并且不是数组

            formItemPropsStrBuilder.append(ADMIN_FORM_JSON_ITEM_FORM_ITEM_PROPS_TYPE_NUMBER);

        }

        if (StrUtil.isNotBlank(beApiParameter.getPattern())) {

            formItemPropsStrBuilder
                .append(StrUtil.format(ADMIN_FORM_JSON_ITEM_FORM_ITEM_PROPS_PATTERN, beApiParameter.getPattern()));

        }

        if (beApiParameter.getMaxLength() != null) {

            formItemPropsStrBuilder
                .append(StrUtil.format(ADMIN_FORM_JSON_ITEM_FORM_ITEM_PROPS_MAX, beApiParameter.getMaxLength()));

        }

        if (beApiParameter.getMinLength() != null) {

            formItemPropsStrBuilder
                .append(StrUtil.format(ADMIN_FORM_JSON_ITEM_FORM_ITEM_PROPS_MIN, beApiParameter.getMinLength()));

        }

        String formItemPropsStr = formItemPropsStrBuilder.toStringAndReset();

        if (StrUtil.isNotBlank(formItemPropsStr)) { // 不为空，才添加：formItemProps

            otherStrBuilder.append(StrUtil.format(ADMIN_JSON_ITEM_FORM_ITEM_PROPS, formItemPropsStr));

        }

    }

    /**
     * 获取：formTooltip
     */
    private static String getFormTooltip(BeApi.BeApiParameter beApiParameter, String description) {

        String formTooltip;

        if (description.equals(beApiParameter.getDescription())) {

            formTooltip = "";

        } else {

            formTooltip = StrUtil.format(ADMIN_JSON_ITEM_TOOLTIP, beApiParameter.getDescription());

        }

        return formTooltip;

    }

    /**
     * 生成 page页面
     */
    private static void generatePage(HashMap<String, BeApi> pathBeApiMap, String pagePath, String group,
        String fileNamePre, CallBack<String> adminPageVOCallBack, CallBack<String> adminDeleteByIdSetApiCallBack,
        CallBack<String> adminInsertOrUpdateApiCallBack, CallBack<String> adminControllerCallBack,
        CallBack<String> adminDeleteNameCallBack, CallBack<String> adminInsertOrUpdateDTOCallBack) {

        String pageFilePath = pagePath + fileNamePre + TSX;

        log.info("开始生成 Admin Page页面文件：{}", pageFilePath);
        FileUtil.del(pageFilePath); // 先移除文件
        File touchFile = FileUtil.touch(pageFilePath); // 再创建文件

        String adminDeleteByIdSetApi = ADMIN_DELETE_BY_ID_SET_API;
        String adminInfoByIdApi = ADMIN_INFO_BY_ID_API;
        String adminInsertOrUpdateApi = ADMIN_INSERT_OR_UPDATE_API;
        String adminInsertOrUpdateDTO = ADMIN_INSERT_OR_UPDATE_DTO;
        String adminPageApi = ADMIN_PAGE_API;
        String adminPageDTO = ADMIN_PAGE_DTO;
        String adminController = ADMIN_CONTROLLER;
        String adminTsxTitle = ADMIN_TSX_TITLE;
        String adminModalFormTitle = ADMIN_MODAL_FORM_TITLE;
        String adminAddOrderNoApi = ADMIN_ADD_ORDER_NO_API;
        String adminTreeApi = ADMIN_TREE_API;
        String adminPageVO = ADMIN_PAGE_VO;
        String adminDeleteName = ADMIN_DELETE_NAME;

        adminDeleteByIdSetApi = adminDeleteByIdSetApiCallBack.getValue();
        adminInsertOrUpdateApi = adminInsertOrUpdateApiCallBack.getValue();
        adminInsertOrUpdateDTO = adminInsertOrUpdateDTOCallBack.getValue();
        adminController = adminControllerCallBack.getValue();
        adminPageVO = adminPageVOCallBack.getValue();
        adminDeleteName = adminDeleteNameCallBack.getValue();

        adminTsxTitle = CollUtil.getFirst(pathBeApiMap.values()).getTag();
        adminModalFormTitle = StrUtil.subBefore(adminTsxTitle, "-", false);

        String tempStr = ADMIN_PAGE_TEMP;

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
                tempStr = ADMIN_TREE_TEMP; // 使用：tree模板

            }

        }

        // 执行替换
        tempStr = equalsAndReplace(tempStr, adminDeleteName, ADMIN_DELETE_NAME);

        tempStr = equalsAndReplace(tempStr, adminInsertOrUpdateApi, ADMIN_INSERT_OR_UPDATE_API);

        tempStr = equalsAndReplace(tempStr, adminInsertOrUpdateDTO, ADMIN_INSERT_OR_UPDATE_DTO);

        tempStr = equalsAndReplace(tempStr, adminInfoByIdApi, ADMIN_INFO_BY_ID_API);

        tempStr = equalsAndReplace(tempStr, adminPageVO, ADMIN_PAGE_VO);

        tempStr = equalsAndReplace(tempStr, adminPageApi, ADMIN_PAGE_API);

        tempStr = equalsAndReplace(tempStr, adminPageDTO, ADMIN_PAGE_DTO);

        tempStr = equalsAndReplace(tempStr, adminDeleteByIdSetApi, ADMIN_DELETE_BY_ID_SET_API);

        tempStr = equalsAndReplace(tempStr, adminController, ADMIN_CONTROLLER);

        tempStr = equalsAndReplace(tempStr, adminTsxTitle, ADMIN_TSX_TITLE);

        tempStr = equalsAndReplace(tempStr, adminModalFormTitle, ADMIN_MODAL_FORM_TITLE);

        tempStr = equalsAndReplace(tempStr, adminAddOrderNoApi, ADMIN_ADD_ORDER_NO_API);

        tempStr = equalsAndReplace(tempStr, adminTreeApi, ADMIN_TREE_API);

        // 写入内容到文件里
        FileUtil.writeUtf8String(tempStr, touchFile);

    }

    /**
     * 生成 table页面
     */
    private static void generateTableColumnList(HashMap<String, BeApi> pathBeApiMap, String pagePath, String group,
        CallBack<String> adminPageVOCallBack, CallBack<String> adminDeleteByIdSetApiCallBack,
        CallBack<String> adminInsertOrUpdateApiCallBack, CallBack<String> adminControllerCallBack,
        CallBack<String> adminDeleteNameCallBack, CallBack<String> adminInsertOrUpdateDTOCallBack) {

        String tableFilePath = pagePath + ADMIN_TABLE_FILE_NAME;

        log.info("开始生成 Admin Table页面文件：{}", tableFilePath);
        FileUtil.del(tableFilePath); // 先移除文件
        File touchFile = FileUtil.touch(tableFilePath); // 再创建文件

        // 需要写入的内容
        StrBuilder tempStrBuilder = StrBuilder.create(ADMIN_TABLE_TEMP);

        StrBuilder tableJsonStrBuilder = StrBuilder.create();

        // 需要替换的字符串
        String adminPageVO = ADMIN_PAGE_VO;
        String adminDeleteByIdSetApi = ADMIN_DELETE_BY_ID_SET_API;
        String adminInsertOrUpdateApi = ADMIN_INSERT_OR_UPDATE_API;
        String adminInsertOrUpdateDTO = ADMIN_INSERT_OR_UPDATE_DTO;
        String adminController = ADMIN_CONTROLLER;
        String adminDeleteName = ADMIN_DELETE_NAME;

        adminController = group; // 设置：api的文件名
        adminDeleteName = ADMIN_DEFAULT_DELETE_NAME; // 设置：删除名

        Set<String> importClassNameSet = new HashSet<>(); // 防止重复写入

        String pageCheckStr = ApiResultVO.class.getSimpleName() + Page.class.getSimpleName();

        for (Map.Entry<String, BeApi> item : pathBeApiMap.entrySet()) {

            BeApi beApi = item.getValue();

            String summary = beApi.getSummary();

            if ("分页排序查询".equals(summary)) {

                BeApi.BeApiSchema response = (BeApi.BeApiSchema)beApi.getResponse();

                boolean pageFlag = response.getClassName().startsWith(pageCheckStr);

                if (pageFlag) {

                    adminPageVO = StrUtil.subAfter(response.getClassName(), pageCheckStr, false);

                    // 拿到：返回值的对象 ↓
                    BeApi.BeApiSchema beApiSchema =
                        (BeApi.BeApiSchema)response.getFieldMap().get(response.getClassName());

                    BeApi.BeApiField data = beApiSchema.getFieldMap().get("data");

                    BeApi.BeApiSchema dataBeApiSchema = (BeApi.BeApiSchema)data;

                    BeApi.BeApiSchema dataRealBeApiSchema =
                        (BeApi.BeApiSchema)dataBeApiSchema.getFieldMap().get(dataBeApiSchema.getClassName());

                    BeApi.BeApiSchema records = (BeApi.BeApiSchema)dataRealBeApiSchema.getFieldMap().get("records");

                    BeApi.BeApiSchema recordsBeApiSchema =
                        (BeApi.BeApiSchema)records.getFieldMap().get(records.getClassName());
                    // 拿到：返回值的对象 ↑

                    for (Map.Entry<String, BeApi.BeApiField> subItem : recordsBeApiSchema.getFieldMap().entrySet()) {

                        // 一个字段
                        BeApi.BeApiField beApiField = subItem.getValue();

                        if (beApiField instanceof BeApi.BeApiParameter) {

                            BeApi.BeApiParameter beApiParameter = (BeApi.BeApiParameter)beApiField;

                            String description = beApiParameter.getDescription();
                            description = StrUtil.subBefore(description, "（", false);
                            description = StrUtil.subBefore(description, "，", false);

                            if ("boolean".equals(beApiParameter.getType())) {

                                importClassForTs(tempStrBuilder, importClassNameSet, YES_NO_DICT, IMPORT_YES_NO_DICT);

                                tableJsonStrBuilder.append(StrUtil
                                    .format(ADMIN_TABLE_JSON_ITEM_YES_NO_DICT_SELECT, "", description,
                                        beApiParameter.getName()));

                            } else if ("date-time".equals(beApiParameter.getFormat())) {

                                tableJsonStrBuilder.append(StrUtil
                                    .format(ADMIN_TABLE_JSON_ITEM_FROM_NOW_AND_HIDE_IN_SEARCH, "", description,
                                        beApiParameter.getName()));

                            } else if (CollUtil.newArrayList("string", "integer").contains(beApiParameter.getType())) {

                                tableJsonStrBuilder.append(StrUtil
                                    .format(ADMIN_TABLE_JSON_ITEM_NORMAL, description, beApiParameter.getName(), ""));

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
            .create(equalsAndReplace(tempStrBuilder.toString(), adminInsertOrUpdateApi, ADMIN_INSERT_OR_UPDATE_API));

        tempStrBuilder = StrBuilder
            .create(equalsAndReplace(tempStrBuilder.toString(), adminInsertOrUpdateDTO, ADMIN_INSERT_OR_UPDATE_DTO));

        tempStrBuilder = StrBuilder.create(equalsAndReplace(tempStrBuilder.toString(), adminPageVO, ADMIN_PAGE_VO));

        tempStrBuilder = StrBuilder
            .create(equalsAndReplace(tempStrBuilder.toString(), adminDeleteByIdSetApi, ADMIN_DELETE_BY_ID_SET_API));

        tempStrBuilder =
            StrBuilder.create(equalsAndReplace(tempStrBuilder.toString(), adminController, ADMIN_CONTROLLER));

        tempStrBuilder =
            StrBuilder.create(equalsAndReplace(tempStrBuilder.toString(), adminDeleteName, ADMIN_DELETE_NAME));

        tempStrBuilder = StrBuilder
            .create(equalsAndReplace(tempStrBuilder.toString(), tableJsonStrBuilder.toString(), ADMIN_TABLE_JSON));

        // 设置：回调值
        adminPageVOCallBack.setValue(adminPageVO);
        adminDeleteByIdSetApiCallBack.setValue(adminDeleteByIdSetApi);
        adminInsertOrUpdateApiCallBack.setValue(adminInsertOrUpdateApi);
        adminInsertOrUpdateDTOCallBack.setValue(adminInsertOrUpdateDTO);
        adminControllerCallBack.setValue(adminController);
        adminDeleteNameCallBack.setValue(adminDeleteName);

        // 写入内容到文件里
        FileUtil.writeUtf8String(tempStrBuilder.toString(), touchFile);

    }

    /**
     * 比较并替换
     */
    private static String equalsAndReplace(String tempStr, String newValue, String oldValue) {

        if (BooleanUtil.isFalse(newValue.equals(oldValue))) {

            return StrUtil.replace(tempStr, oldValue, newValue);

        }

        return tempStr;

    }

    /**
     * ts：导入包
     */
    private static void importClassForTs(StrBuilder tempStrBuilder, Set<String> importClassNameSet, String className,
        String importClassStr) {

        if (BooleanUtil.isFalse(importClassNameSet.contains(className))) {

            importClassNameSet.add(className);
            tempStrBuilder.insert(0, importClassStr); // 添加导入

        }

    }

}
