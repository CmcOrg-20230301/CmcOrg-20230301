package com.cmcorg20230301.engine.be.generate.util.generate;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.engine.be.generate.model.bo.BeApi;
import com.cmcorg20230301.engine.be.generate.util.apitest.ApiTestHelper;
import com.cmcorg20230301.engine.be.security.model.vo.ApiResultVO;
import com.cmcorg20230301.engine.be.util.util.CallBack;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
    private static final String ADMIN_TABLE_TEMP = FileUtil.readUtf8String("template/admin/TableColumnList.tsx");

    // 要识别：路径
    private static final String ADD_ORDER_NO = "/addOrderNo";
    private static final String DELETE_BY_ID_SET = "/deleteByIdSet";
    private static final String INFO_BY_ID = "/infoById";
    private static final String INSERT_OR_UPDATE = "/insertOrUpdate";
    private static final String PAGE = "/page";
    private static final String TREE = "/tree";

    // 要替换的字符
    private static final String ADMIN_DELETE_BY_ID_SET = "AdminDeleteByIdSet";
    //    private static final String ADMIN_DO = "AdminDO";
    private static final String ADMIN_PAGE_VO = "AdminPageVO";
    private static final String ADMIN_INFO_BY_ID = "AdminInfoById";
    private static final String ADMIN_INSERT_OR_UPDATE = "AdminInsertOrUpdate";
    private static final String ADMIN_PAGE = "AdminPage";
    private static final String ADMIN_CONTROLLER = "AdminController";
    private static final String ADMIN_TSX_TITLE = "AdminTsxTitle";
    private static final String ADMIN_MODAL_FORM_TITLE = "AdminModalFormTitle";
    private static final String ADMIN_ADD_ORDER_NO = "AdminAddOrderNo";
    private static final String ADMIN_TREE = "AdminTree";
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
        "\n                        type: number,";

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
            + "                placeholder: '为空则表示顶级区域',\n" + "                allowClear: true,\n"
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
        exec(SPRING_DOC_ENDPOINT, PAGE_PATH, TSX);

    }

    /**
     * 执行
     */
    private static void exec(String springDocEndpoint, String pagePath, String ts) {

        HashMap<String, HashMap<String, BeApi>> apiMap = SpringDocUtil.get(springDocEndpoint);

        System.out.println("所有的group ↓");
        System.out.println(JSONUtil.toJsonStr(apiMap.keySet()));

        String group = ApiTestHelper.getStrFromScanner("请输入要生成页面的 group");

        HashMap<String, BeApi> pathBeApiMap = apiMap.get(group);

        if (CollUtil.isEmpty(pathBeApiMap)) {

            log.info("操作失败：group不存在");
            return;

        }

        // 例如：Menu
        String fileNamePre = group.contains("Sys") ? group.replace("Sys", "") : group;

        // 例如：/fe-antd-v1/src/page/sys/Menu/
        pagePath = pagePath + "/" + fileNamePre + "/";

        CallBack<String> adminPageVOCallBack = new CallBack<>();
        CallBack<String> adminDeleteByIdSetCallBack = new CallBack<>();
        CallBack<String> adminInsertOrUpdateCallBack = new CallBack<>();
        CallBack<String> adminControllerCallBack = new CallBack<>();
        CallBack<String> adminDeleteNameCallBack = new CallBack<>();

        // 生成 table页面
        generateTableColumnList(pathBeApiMap, pagePath, group, adminPageVOCallBack, adminDeleteByIdSetCallBack,
            adminInsertOrUpdateCallBack, adminControllerCallBack, adminDeleteNameCallBack);

        // 生成 page页面
        generatePage(pathBeApiMap, pagePath, group, fileNamePre, adminPageVOCallBack, adminDeleteByIdSetCallBack,
            adminInsertOrUpdateCallBack, adminControllerCallBack, adminDeleteNameCallBack);

        // 生成 表单页面
        generateSchemaFormColumnList(pathBeApiMap, pagePath, group, fileNamePre);

    }

    /**
     * 生成 表单页面
     */
    private static void generateSchemaFormColumnList(HashMap<String, BeApi> pathBeApiMap, String pagePath, String group,
        String fileNamePre) {

    }

    /**
     * 生成 page页面
     */
    private static void generatePage(HashMap<String, BeApi> pathBeApiMap, String pagePath, String group,
        String fileNamePre, CallBack<String> adminPageVOCallBack, CallBack<String> adminDeleteByIdSetCallBack,
        CallBack<String> adminInsertOrUpdateCallBack, CallBack<String> adminControllerCallBack,
        CallBack<String> adminDeleteNameCallBack) {

        String pageFilePath = pagePath + fileNamePre + TSX;

        log.info("开始生成 Admin Page页面文件：{}", pageFilePath);
        FileUtil.del(pageFilePath); // 先移除文件
        File touchFile = FileUtil.touch(pageFilePath); // 再创建文件

        String adminDeleteByIdSet = ADMIN_DELETE_BY_ID_SET;
        String adminInfoById = ADMIN_INFO_BY_ID;
        String adminInsertOrUpdate = ADMIN_INSERT_OR_UPDATE;
        String adminPage = ADMIN_PAGE;
        String adminController = ADMIN_CONTROLLER;
        String adminTsxTitle = ADMIN_TSX_TITLE;
        String adminModalFormTitle = ADMIN_MODAL_FORM_TITLE;
        String adminAddOrderNo = ADMIN_ADD_ORDER_NO;
        String adminTree = ADMIN_TREE;
        String adminPageVO = ADMIN_PAGE_VO;
        String adminDeleteName = ADMIN_DELETE_NAME;

        StrBuilder strBuilder = StrBuilder.create();

        adminDeleteByIdSet = adminDeleteByIdSetCallBack.getValue();
        adminInsertOrUpdate = adminInsertOrUpdateCallBack.getValue();
        adminController = adminControllerCallBack.getValue();
        adminPageVO = adminPageVOCallBack.getValue();
        adminDeleteName = adminDeleteNameCallBack.getValue();

        adminTsxTitle = CollUtil.getFirst(pathBeApiMap.values()).getTag();
        adminModalFormTitle = StrUtil.subBefore(adminTsxTitle, "-", false);

        String tempStr = ADMIN_PAGE_TEMP;

        for (Map.Entry<String, BeApi> item : pathBeApiMap.entrySet()) {

            BeApi beApi = item.getValue();

            String summary = beApi.getSummary();

            if ("通过主键id，查看详情".equals(summary)) {

                adminInfoById = GenerateApiUtil.getApiName(beApi.getPath());

            } else if ("通过主键 idSet，加减排序号".equals(summary)) {

                adminAddOrderNo = GenerateApiUtil.getApiName(beApi.getPath());

            } else if ("查询：树结构".equals(summary)) {

                adminTree = GenerateApiUtil.getApiName(beApi.getPath());
                tempStr = ADMIN_TREE_TEMP; // 使用：tree模板

            }

        }

        // 执行替换
        tempStr = equalsAndReplace(tempStr, adminDeleteName, ADMIN_DELETE_NAME);
        tempStr = equalsAndReplace(tempStr, adminInsertOrUpdate, ADMIN_INSERT_OR_UPDATE);
        tempStr = equalsAndReplace(tempStr, adminInfoById, ADMIN_INFO_BY_ID);
        tempStr = equalsAndReplace(tempStr, adminPage, ADMIN_PAGE);
        tempStr = equalsAndReplace(tempStr, adminDeleteByIdSet, ADMIN_DELETE_BY_ID_SET);
        tempStr = equalsAndReplace(tempStr, adminController, ADMIN_CONTROLLER);
        tempStr = equalsAndReplace(tempStr, adminPageVO, ADMIN_PAGE_VO);
        tempStr = equalsAndReplace(tempStr, adminTsxTitle, ADMIN_TSX_TITLE);
        tempStr = equalsAndReplace(tempStr, adminModalFormTitle, ADMIN_MODAL_FORM_TITLE);
        tempStr = equalsAndReplace(tempStr, adminAddOrderNo, ADMIN_ADD_ORDER_NO);
        tempStr = equalsAndReplace(tempStr, adminTree, ADMIN_TREE);

        strBuilder.append(tempStr);

        // 写入内容到文件里
        FileUtil.writeUtf8String(strBuilder.toString(), touchFile);

    }

    /**
     * 生成 table页面
     */
    private static void generateTableColumnList(HashMap<String, BeApi> pathBeApiMap, String pagePath, String group,
        CallBack<String> adminPageVOCallBack, CallBack<String> adminDeleteByIdSetCallBack,
        CallBack<String> adminInsertOrUpdateCallBack, CallBack<String> adminControllerCallBack,
        CallBack<String> adminDeleteNameCallBack) {

        String tableFilePath = pagePath + ADMIN_TABLE_FILE_NAME;

        log.info("开始生成 Table页面文件：{}", tableFilePath);
        FileUtil.del(tableFilePath); // 先移除文件
        File touchFile = FileUtil.touch(tableFilePath); // 再创建文件

        // 需要写入的内容
        StrBuilder tempStrBuilder = StrBuilder.create(ADMIN_TABLE_TEMP);

        StrBuilder tableJsonStrBuilder = StrBuilder.create();

        // 需要替换的字符串
        String adminPageVO = ADMIN_PAGE_VO;
        String adminDeleteByIdSet = ADMIN_DELETE_BY_ID_SET;
        String adminInsertOrUpdate = ADMIN_INSERT_OR_UPDATE;
        String adminController = ADMIN_CONTROLLER;
        String adminDeleteName = ADMIN_DELETE_NAME;

        adminController = group; // 设置：api的文件名
        adminDeleteName = "name";

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

                            if ("boolean".equals(beApiParameter.getType())) {

                                importClassForTs(tempStrBuilder, importClassNameSet, YES_NO_DICT, IMPORT_YES_NO_DICT);

                                tableJsonStrBuilder.append(StrUtil.format(ADMIN_TABLE_JSON_ITEM_YES_NO_DICT_SELECT, "",
                                    beApiParameter.getDescription(), beApiParameter.getName()));

                            } else if ("date-time".equals(beApiParameter.getFormat())) {

                                tableJsonStrBuilder.append(StrUtil
                                    .format(ADMIN_TABLE_JSON_ITEM_FROM_NOW_AND_HIDE_IN_SEARCH, "",
                                        beApiParameter.getDescription(), beApiParameter.getName()));

                            } else if (CollUtil.newArrayList("string", "integer").contains(beApiParameter.getType())) {

                                tableJsonStrBuilder.append(StrUtil
                                    .format(ADMIN_TABLE_JSON_ITEM_NORMAL, beApiParameter.getDescription(),
                                        beApiParameter.getName(), ""));

                            }

                        }

                    }

                } else {

                    log.info("分页排序查询：类型错误，path：{}", beApi.getPath());

                }

            } else if ("新增/修改".equals(summary)) {

                adminInsertOrUpdate = GenerateApiUtil.getApiName(beApi.getPath());

            } else if ("批量删除".equals(summary)) {

                adminDeleteByIdSet = GenerateApiUtil.getApiName(beApi.getPath());

            }

        }

        // 执行替换
        tempStrBuilder =
            StrBuilder.create(equalsAndReplace(tempStrBuilder.toString(), adminInsertOrUpdate, ADMIN_INSERT_OR_UPDATE));
        tempStrBuilder = StrBuilder.create(equalsAndReplace(tempStrBuilder.toString(), adminPageVO, ADMIN_PAGE_VO));
        tempStrBuilder =
            StrBuilder.create(equalsAndReplace(tempStrBuilder.toString(), adminDeleteByIdSet, ADMIN_DELETE_BY_ID_SET));
        tempStrBuilder =
            StrBuilder.create(equalsAndReplace(tempStrBuilder.toString(), adminController, ADMIN_CONTROLLER));
        tempStrBuilder =
            StrBuilder.create(equalsAndReplace(tempStrBuilder.toString(), adminDeleteName, ADMIN_DELETE_NAME));

        tempStrBuilder = StrBuilder
            .create(equalsAndReplace(tempStrBuilder.toString(), tableJsonStrBuilder.toString(), ADMIN_TABLE_JSON));

        // 设置：回调值
        adminPageVOCallBack.setValue(adminPageVO);
        adminDeleteByIdSetCallBack.setValue(adminInsertOrUpdate);
        adminInsertOrUpdateCallBack.setValue(adminInsertOrUpdate);
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
