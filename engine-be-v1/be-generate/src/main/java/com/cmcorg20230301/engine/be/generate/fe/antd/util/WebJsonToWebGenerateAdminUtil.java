package com.cmcorg20230301.engine.be.generate.fe.antd.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.cmcorg.engine.web.model.generate.model.enums.FormInputTypeEnum;
import com.cmcorg.engine.web.model.generate.model.enums.PageTypeEnum;
import com.cmcorg.engine.web.model.model.constant.LogTopicConstant;
import com.cmcorg.engine.web.util.util.CallBack;
import com.cmcorg.generate.page.h5.util.model.dto.PageDTO;
import com.cmcorg.generate.page.h5.util.model.dto.RequestDTO;
import com.cmcorg.generate.page.h5.util.model.dto.RequestFieldDTO;
import com.cmcorg.generate.page.h5.util.model.dto.WebDTO;
import com.cmcorg.generate.page.h5.util.model.enums.ColumnTypeRefEnum;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j(topic = LogTopicConstant.JAVA_TO_WEB)
public class WebJsonToWebGenerateAdminUtil {

    private static final String TEMPLATE_NAME =
        "E:/GitHub/CmcOrg/generate-page-h5-v1/generate-page-h5/src/main/resources/template/admin/";
    public static String PAGE_PATH = "D:/GitHub/CmcOrg/frontend-h5-web-v1/src/page/";

    // 其他常量
    private static final String ADMIN_PAGE_SUF = ".tsx"; // 页面文件后缀
    private static final String DO = "DO"; // do文件后缀
    private static final String CONTROLLER = "Controller"; // Controller class名后缀
    private static final String MANAGE = "管理";
    private static final String TAB_INDENT = "    "; // 缩进

    // Admin Table Page 页面模板
    private static final String ADMIN_PAGE_TEMP = FileUtil.readUtf8String(TEMPLATE_NAME + "AdminPageTableTemp.tsx");
    // Admin Tree Page 页面模板
    private static final String ADMIN_TREE_TEMP = FileUtil.readUtf8String(TEMPLATE_NAME + "AdminPageTreeTemp.tsx");
    // Admin 表单 页面模板
    private static final String ADMIN_FORM_FILE_NAME = "SchemaFormColumnList" + ADMIN_PAGE_SUF;
    private static final String ADMIN_FORM_TEMP = FileUtil.readUtf8String(TEMPLATE_NAME + "SchemaFormColumnList.tsx");
    // Admin Table 页面模板
    private static final String ADMIN_TABLE_FILE_NAME = "TableColumnList" + ADMIN_PAGE_SUF;
    private static final String ADMIN_TABLE_TEMP = FileUtil.readUtf8String(TEMPLATE_NAME + "TableColumnList.tsx");

    // 要识别：路径
    private static final String ADD_ORDER_NO = "/addOrderNo";
    private static final String DELETE_BY_ID_SET = "/deleteByIdSet";
    private static final String INFO_BY_ID = "/infoById";
    private static final String INSERT_OR_UPDATE = "/insertOrUpdate";
    private static final String PAGE = "/page";
    private static final String TREE = "/tree";

    // 要替换的字符
    private static final String ADMIN_DELETE_BY_ID_SET = "AdminDeleteByIdSet";
    private static final String ADMIN_DO = "AdminDO";
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

    /**
     * 生成 admin相关文件
     */
    public static void generateAdmin(WebDTO webDTO, String fileName) {

        log.info("生成 admin相关文件：执行开始 =====================>");
        TimeInterval timer = DateUtil.timer();

        doGenerateAdmin(webDTO, fileName);

        long interval = timer.interval();
        log.info("生成 admin相关文件：执行结束 =====================> 耗时：{}毫秒", interval);
    }

    interface IImportByKeyWordHandler {
        void handler();
    }

    /**
     * 通过：关键字导入包
     */
    private static void importByKeyWord(StrBuilder strBuilder, String keyword, String objectStr, boolean insertFlag,
        IImportByKeyWordHandler iImportByKeyWordHandler) {

        String regexp = "^.*import \\{(.*?)\\} from .*/" + keyword + "\";.*$";

        String group1 = ReUtil.getGroup1(regexp, strBuilder.toString()); // 例如：GetDictList, YesNoDict

        if (StrUtil.isBlank(group1)) {
            iImportByKeyWordHandler.handler(); // 执行：其他操作
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
     * 执行
     */
    public static void doGenerateAdmin(WebDTO webDTO, String fileName) {

        Set<Integer> indexSet = webDTO.getPageTypeMap().get(PageTypeEnum.ADMIN);

        if (CollUtil.isEmpty(indexSet)) {
            return;
        }

        StrBuilder strBuilder = StrBuilder.create();

        for (Integer item : indexSet) {

            PageDTO pageDTO = webDTO.getPageList().get(item);

            if (StrUtil.isNotBlank(fileName) && !pageDTO.getFileName().equals(fileName)) {
                continue;
            }

            String[] pageDirPath = getPageDirPath(pageDTO.getPath());

            CallBack<String> callBack = new CallBack<>();

            String adminDO = generateTableColumnList(pageDTO, strBuilder, pageDirPath, callBack); // 生成 table页面

            generateAdminPage(pageDTO, strBuilder, pageDirPath, callBack.getValue(), adminDO);// 生成 page页面

            generateSchemaFormColumnList(pageDTO, strBuilder, pageDirPath); // 生成 表单页面

        }

    }

    /**
     * 生成 table页面
     */
    private static String generateTableColumnList(PageDTO pageDTO, StrBuilder strBuilder, String[] pageDirPath,
        CallBack<String> callBack) {

        String pageFilePath = pageDirPath[0] + ADMIN_TABLE_FILE_NAME;

        log.info("开始生成 Admin Table页面文件：{}", pageFilePath);
        FileUtil.del(pageFilePath); // 先移除文件
        File touchFile = FileUtil.touch(pageFilePath); // 再创建文件

        StrBuilder tempStrBuilder = StrBuilder.create(ADMIN_TABLE_TEMP);

        StrBuilder tableJsonStrBuilder = StrBuilder.create();
        AtomicReference<String> adminDO = new AtomicReference<>(ADMIN_DO);
        String adminDeleteByIdSet = ADMIN_DELETE_BY_ID_SET;
        String adminInsertOrUpdate = ADMIN_INSERT_OR_UPDATE;
        String adminController = ADMIN_CONTROLLER;
        String adminDeleteName = ADMIN_DEFAULT_DELETE_NAME;

        adminController = pageDTO.getFileName();

        callBack.setValue(adminDeleteName); // 设置：默认值

        pageDTO.getRequestList().stream().filter(RequestDTO::getPageFlag).findFirst()
            .ifPresent(it -> adminDO.set(it.getReturnRealClass().getSimpleName()));

        Set<String> importClassNameSet = new HashSet<>(); // 防止重复写入
        StrBuilder otherStrBuilder = StrBuilder.create(); // 对象复用

        for (RequestDTO item : pageDTO.getRequestList()) {
            if (INSERT_OR_UPDATE.equals(item.getUri())) {
                adminInsertOrUpdate = item.getFullUriHump();
            } else if (DELETE_BY_ID_SET.equals(item.getUri())) {
                adminDeleteByIdSet = item.getFullUriHump();
            } else if (PAGE.equals(item.getUri())) {

                // 根据：tableOrderNo 降序
                Map<String, RequestFieldDTO> returnClassFieldMap =
                    ColumnTypeRefEnum.getFieldMapByClazz(item.getReturnRealClass()).entrySet().stream()
                        .sorted(Comparator.comparing(it -> it.getValue().getTableOrderNo(), Comparator.reverseOrder()))
                        .collect(Collectors
                            .toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v2, LinkedHashMap::new));

                // 写入 json
                for (Map.Entry<String, RequestFieldDTO> subItem : returnClassFieldMap.entrySet()) {

                    RequestFieldDTO dto = subItem.getValue();

                    if (BooleanUtil.isTrue(dto.getTableIgnoreFlag())) {
                        continue;
                    }

                    if (BooleanUtil.isTrue(dto.getFormDeleteNameFlag())) {
                        adminDeleteName = subItem.getKey();
                        callBack.setValue(adminDeleteName);
                    }

                    if (ColumnTypeRefEnum.BOOLEAN.getTsType().equals(dto.getTsType())) {
                        importClassForTs(tempStrBuilder, importClassNameSet, YES_NO_DICT, IMPORT_YES_NO_DICT);
                        tableJsonStrBuilder.append(StrUtil
                            .format(ADMIN_TABLE_JSON_ITEM_YES_NO_DICT_SELECT, "", dto.getTableTitle(),
                                subItem.getKey()));
                    } else if (dto.getFieldClass().equals(Date.class)) {
                        tableJsonStrBuilder.append(StrUtil
                            .format(ADMIN_TABLE_JSON_ITEM_FROM_NOW_AND_HIDE_IN_SEARCH, "", dto.getTableTitle(),
                                subItem.getKey()));
                    } else {

                        if (BooleanUtil.isTrue(dto.getHideInSearchFlag())) {
                            otherStrBuilder.append(ADMIN_TABLE_JSON_ITEM_HIDE_IN_SEARCH);
                        }

                        tableJsonStrBuilder.append(StrUtil
                            .format(ADMIN_TABLE_JSON_ITEM_NORMAL, dto.getTableTitle(), subItem.getKey(),
                                otherStrBuilder.toStringAndReset()));
                    }
                }
            }
        }

        // 执行替换
        tempStrBuilder =
            StrBuilder.create(equalsAndReplace(tempStrBuilder.toString(), adminDeleteName, ADMIN_DELETE_NAME));
        tempStrBuilder =
            StrBuilder.create(equalsAndReplace(tempStrBuilder.toString(), adminInsertOrUpdate, ADMIN_INSERT_OR_UPDATE));
        tempStrBuilder = StrBuilder.create(equalsAndReplace(tempStrBuilder.toString(), adminDO.get(), ADMIN_DO));
        tempStrBuilder =
            StrBuilder.create(equalsAndReplace(tempStrBuilder.toString(), adminDeleteByIdSet, ADMIN_DELETE_BY_ID_SET));
        tempStrBuilder =
            StrBuilder.create(equalsAndReplace(tempStrBuilder.toString(), adminController, ADMIN_CONTROLLER));
        tempStrBuilder = StrBuilder
            .create(equalsAndReplace(tempStrBuilder.toString(), tableJsonStrBuilder.toString(), ADMIN_TABLE_JSON));

        strBuilder.append(tempStrBuilder.toString());

        // 写入内容到文件里
        FileUtil.writeUtf8String(strBuilder.toStringAndReset(), touchFile);

        return adminDO.get();
    }

    /**
     * 生成 表单页面
     */
    private static void generateSchemaFormColumnList(PageDTO pageDTO, StrBuilder strBuilder, String[] pageDirPath) {

        String pageFilePath = pageDirPath[0] + ADMIN_FORM_FILE_NAME;

        log.info("开始生成 Admin 表单页面文件：{}", pageFilePath);
        FileUtil.del(pageFilePath); // 先移除文件
        File touchFile = FileUtil.touch(pageFilePath); // 再创建文件

        StrBuilder tempStrBuilder = StrBuilder.create(ADMIN_FORM_TEMP);

        StrBuilder formJsonStrBuilder = StrBuilder.create();
        String adminInsertOrUpdate = ADMIN_INSERT_OR_UPDATE;
        String adminController = ADMIN_CONTROLLER;

        adminController = pageDTO.getFileName();

        // 执行替换
        tempStrBuilder =
            StrBuilder.create(equalsAndReplace(tempStrBuilder.toString(), adminController, ADMIN_CONTROLLER));

        Set<String> importClassNameSet = new HashSet<>(); // 防止重复写入
        StrBuilder otherStrBuilder = StrBuilder.create(); // 对象复用
        StrBuilder formItemPropsStrBuilder = StrBuilder.create(); // 对象复用

        for (RequestDTO item : pageDTO.getRequestList()) {
            if (INSERT_OR_UPDATE.equals(item.getUri())) {

                adminInsertOrUpdate = item.getFullUriHump();

                // 写入 json
                for (Map.Entry<String, RequestFieldDTO> subItem : item.getFormMap().entrySet()) {

                    RequestFieldDTO dto = subItem.getValue();

                    if (BooleanUtil.isTrue(dto.getFormIgnoreFlag())) {
                        continue;
                    }
                    if (ColumnTypeRefEnum.BOOLEAN.getTsType().equals(dto.getTsType())) {
                        importClassForTs(tempStrBuilder, importClassNameSet, YES_NO_DICT, IMPORT_YES_NO_DICT);
                        String formTooltip = dto.getFormTooltip(); // 设置：formTooltip
                        if (StrUtil.isBlank(formTooltip)) {
                            formTooltip = "";
                        } else {
                            formTooltip = StrUtil.format(ADMIN_JSON_ITEM_TOOLTIP, formTooltip);
                        }
                        formJsonStrBuilder.append(StrUtil
                            .format(ADMIN_FORM_JSON_ITEM_YES_NO_DICT_SWITCH, TAB_INDENT, dto.getTableTitle(),
                                subItem.getKey(), formTooltip));
                    } else if (REMARK.equals(subItem.getKey())) {
                        formJsonStrBuilder
                            .append(StrUtil.format(ADMIN_FORM_JSON_ITEM_TEXTAREA_300_REMARK, dto.getFormTitle()));
                    } else {

                        // 添加：formItemProps
                        appendFormItemProps(otherStrBuilder, formItemPropsStrBuilder, dto);

                        // 添加：select
                        appendSelect(otherStrBuilder, dto, importClassNameSet, tempStrBuilder);

                        // 添加：formTooltip
                        appendFormTooltip(otherStrBuilder, dto);

                        formJsonStrBuilder.append(StrUtil
                            .format(ADMIN_FORM_JSON_ITEM_NORMAL, dto.getFormTitle(), subItem.getKey(),
                                otherStrBuilder.toStringAndReset()));
                    }
                }

                break;
            }
        }

        // 执行替换
        tempStrBuilder =
            StrBuilder.create(equalsAndReplace(tempStrBuilder.toString(), adminInsertOrUpdate, ADMIN_INSERT_OR_UPDATE));
        tempStrBuilder = StrBuilder
            .create(equalsAndReplace(tempStrBuilder.toString(), formJsonStrBuilder.toString(), ADMIN_FORM_JSON));

        strBuilder.append(tempStrBuilder.toString());

        // 写入内容到文件里
        FileUtil.writeUtf8String(strBuilder.toStringAndReset(), touchFile);
    }

    /**
     * 添加：formTooltip
     */
    private static void appendFormTooltip(StrBuilder otherStrBuilder, RequestFieldDTO dto) {

        if (StrUtil.isNotBlank(dto.getFormTooltip())) {
            otherStrBuilder.append(StrUtil.format(ADMIN_JSON_ITEM_TOOLTIP, dto.getFormTooltip()));
        }

    }

    /**
     * ts：导入包
     */
    private static void importClassForTs(StrBuilder tempStrBuilder, Set<String> importClassNameSet, String className,
        String importClassStr) {
        if (!importClassNameSet.contains(className)) {
            importClassNameSet.add(className);
            tempStrBuilder.insert(0, importClassStr); // 添加导入
        }
    }

    /**
     * 添加：select
     */
    private static void appendSelect(StrBuilder otherStrBuilder, RequestFieldDTO dto, Set<String> importClassNameSet,
        StrBuilder tempStrBuilder) {

        if (FormInputTypeEnum.SELECT.equals(dto.getFormInputType())) {

            if (dto.getFormSelectMultipleFlag()) {
                String otherStr = getSelectOtherStr(dto, importClassNameSet, tempStrBuilder);
                otherStrBuilder.append(StrUtil.format(ADMIN_JSON_FIELD_PROPS_SELECT_MULTIPLE, otherStr));
            } else {
                String otherStr = getSelectOtherStr(dto, importClassNameSet, tempStrBuilder);
                otherStrBuilder.append(StrUtil.format(ADMIN_JSON_FIELD_PROPS_SELECT, otherStr));
            }

        } else if (FormInputTypeEnum.TREE_SELECT.equals(dto.getFormInputType())) {

            if (dto.getFormSelectMultipleFlag()) {

                importClassForTs(tempStrBuilder, importClassNameSet, TREE_SELECT, IMPORT_TREE_SELECT);

                String otherStr = getSelectOtherStr(dto, importClassNameSet, tempStrBuilder);
                otherStrBuilder.append(StrUtil.format(ADMIN_JSON_FIELD_PROPS_TREE_SELECT_MULTIPLE, otherStr));
            } else {
                String otherStr = getSelectOtherStr(dto, importClassNameSet, tempStrBuilder);
                otherStrBuilder.append(StrUtil.format(ADMIN_JSON_FIELD_PROPS_TREE_SELECT, otherStr));
            }

        }

    }

    /**
     * 获取：下拉选的其他属性
     */
    private static String getSelectOtherStr(RequestFieldDTO dto, Set<String> importClassNameSet,
        StrBuilder tempStrBuilder) {

        String otherStr = "";

        if (StrUtil.isNotBlank(dto.getFormSelectOptionsStr())) {

            otherStr = StrUtil.format(ADMIN_JSON_DICT_TREE_OPTIONS, dto.getFormSelectOptionsStr());

            importClassForTs(tempStrBuilder, importClassNameSet, dto.getFormSelectOptionsStr(),
                dto.getFormSelectOptionsOrRequestImportStr());

        } else if (StrUtil.isNotBlank(dto.getFormSelectRequestStr())) {

            if (BooleanUtil.isTrue(dto.getFormSelectRequestTreeFlag())) {

                otherStr = StrUtil.format(ADMIN_JSON_DICT_TREE_REQUEST, dto.getFormSelectRequestStr());

                importByKeyWord(tempStrBuilder, DICT_UTIL, GET_DICT_TREE_LIST, true,
                    () -> importClassForTs(tempStrBuilder, importClassNameSet, YES_NO_DICT,
                        StrUtil.format(IMPORT_YES_NO_DICT_AND_GET, GET_DICT_TREE_LIST)));
            } else {

                otherStr = StrUtil.format(ADMIN_JSON_DICT_REQUEST, dto.getFormSelectRequestStr());

                importByKeyWord(tempStrBuilder, DICT_UTIL, GET_DICT_LIST, true,
                    () -> importClassForTs(tempStrBuilder, importClassNameSet, YES_NO_DICT,
                        StrUtil.format(IMPORT_YES_NO_DICT_AND_GET, GET_DICT_LIST)));
            }

            String keyword = getKeywordFromImportStr(dto.getFormSelectOptionsOrRequestImportStr());

            importByKeyWord(tempStrBuilder, keyword, dto.getFormSelectRequestStr(), false,
                () -> importClassForTs(tempStrBuilder, importClassNameSet, dto.getFormSelectRequestStr(),
                    dto.getFormSelectOptionsOrRequestImportStr()));

        }

        return otherStr;
    }

    /**
     * 从 importStr里面获取：keyword
     */
    private static String getKeywordFromImportStr(String importStr) {

        String regexp = "^.*from .*/(.*?)\";.*$";

        String group1 = ReUtil.getGroup1(regexp, importStr); // 例如：SysMenuController

        return StrUtil.isBlank(group1) ? "" : group1;
    }

    /**
     * 添加：formItemProps
     */
    private static void appendFormItemProps(StrBuilder otherStrBuilder, StrBuilder formItemPropsStrBuilder,
        RequestFieldDTO dto) {

        if (BooleanUtil.orOfWrap(dto.getNotBlank(), dto.getNotEmpty(), dto.getNotNull())) {
            formItemPropsStrBuilder.append(ADMIN_FORM_JSON_ITEM_FORM_ITEM_PROPS_REQUIRED);
            if (BooleanUtil.isTrue(dto.getNotBlank())) {
                formItemPropsStrBuilder.append(ADMIN_FORM_JSON_ITEM_FORM_ITEM_PROPS_WHITESPACE);
            }
        }
        if (dto.getFieldClass().isAssignableFrom(Number.class)) { // 如果是：number的子类
            formItemPropsStrBuilder.append(ADMIN_FORM_JSON_ITEM_FORM_ITEM_PROPS_TYPE_NUMBER);
        }
        if (dto.getMin() != null) {
            formItemPropsStrBuilder.append(StrUtil.format(ADMIN_FORM_JSON_ITEM_FORM_ITEM_PROPS_MIN, dto.getMin()));
        } else if (dto.getSizeMin() != null) {
            formItemPropsStrBuilder.append(StrUtil.format(ADMIN_FORM_JSON_ITEM_FORM_ITEM_PROPS_MIN, dto.getSizeMin()));
        }
        if (dto.getMax() != null) {
            formItemPropsStrBuilder.append(StrUtil.format(ADMIN_FORM_JSON_ITEM_FORM_ITEM_PROPS_MAX, dto.getMax()));
        } else if (dto.getSizeMax() != null) {
            formItemPropsStrBuilder.append(StrUtil.format(ADMIN_FORM_JSON_ITEM_FORM_ITEM_PROPS_MAX, dto.getSizeMax()));
        }
        if (StrUtil.isNotBlank(dto.getRegexp())) {
            formItemPropsStrBuilder
                .append(StrUtil.format(ADMIN_FORM_JSON_ITEM_FORM_ITEM_PROPS_PATTERN, dto.getRegexp()));
        }
        if (StrUtil.isNotBlank(formItemPropsStrBuilder.toString())) { // 不为空，才添加：formItemProps
            otherStrBuilder
                .append(StrUtil.format(ADMIN_JSON_ITEM_FORM_ITEM_PROPS, formItemPropsStrBuilder.toStringAndReset()));
        }

    }

    /**
     * 生成 page页面
     */
    private static void generateAdminPage(PageDTO pageDTO, StrBuilder strBuilder, String[] pageDirPath,
        String adminDeleteName, String adminDO) {

        String pageFilePath = pageDirPath[0] + pageDirPath[1] + ADMIN_PAGE_SUF;

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

        adminController = pageDTO.getFileName();

        adminTsxTitle = pageDTO.getTitle();
        adminModalFormTitle = StrUtil.subBefore(pageDTO.getTitle(), MANAGE, false);

        String tempStr = ADMIN_PAGE_TEMP;

        for (RequestDTO item : pageDTO.getRequestList()) {
            if (INSERT_OR_UPDATE.equals(item.getUri())) {
                adminInsertOrUpdate = item.getFullUriHump();
            } else if (INFO_BY_ID.equals(item.getUri())) {
                adminInfoById = item.getFullUriHump();
            } else if (PAGE.equals(item.getUri())) {
                adminPage = item.getFullUriHump();
            } else if (DELETE_BY_ID_SET.equals(item.getUri())) {
                adminDeleteByIdSet = item.getFullUriHump();
            } else if (ADD_ORDER_NO.equals(item.getUri())) {
                adminAddOrderNo = item.getFullUriHump();
            } else if (TREE.equals(item.getUri())) {
                adminTree = item.getFullUriHump();
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
        tempStr = equalsAndReplace(tempStr, adminDO, ADMIN_DO);
        tempStr = equalsAndReplace(tempStr, adminTsxTitle, ADMIN_TSX_TITLE);
        tempStr = equalsAndReplace(tempStr, adminModalFormTitle, ADMIN_MODAL_FORM_TITLE);
        tempStr = equalsAndReplace(tempStr, adminAddOrderNo, ADMIN_ADD_ORDER_NO);
        tempStr = equalsAndReplace(tempStr, adminTree, ADMIN_TREE);

        strBuilder.append(tempStr);

        // 写入内容到文件里
        FileUtil.writeUtf8String(strBuilder.toStringAndReset(), touchFile);

    }

    /**
     * 比较并替换
     */
    private static String equalsAndReplace(String tempStr, String newValue, String oldValue) {
        if (!newValue.equals(oldValue)) {
            return StrUtil.replace(tempStr, oldValue, newValue);
        }
        return tempStr;
    }

    /**
     * 先转驼峰为斜杠，然后重复最后一个斜杠后面的字符串
     */
    public static String[] getPageDirPath(String path) {

        String toSymbolCaseStr = StrUtil.toSymbolCase(path, '/'); // 例如：/sys/menu

        List<String> splitList = StrUtil.splitTrim(toSymbolCaseStr, "/");

        String pageName = StrUtil.upperFirst(CollUtil.getLast(splitList)); // 取最后一个元素，并首字母大写，例如：Menu

        splitList.set(splitList.size() - 1, pageName); // 替换最后一个元素

        String pathStr = CollUtil.join(splitList, "/");

        return new String[] {PAGE_PATH + pathStr + "/", pageName}; // 组装路径
    }

}
