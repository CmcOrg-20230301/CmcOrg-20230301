package com.cmcorg20230301.engine.be.generate.util.generate;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import com.cmcorg20230301.engine.be.generate.model.bo.BeApi;
import com.cmcorg20230301.engine.be.generate.util.apitest.ApiTestHelper;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

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
    private static final String MANAGE = "管理";
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

    public static void main(String[] args) {

        String group = ApiTestHelper.getStrFromScanner("请输入要生成页面的 group");

        // 执行
        exec(SPRING_DOC_ENDPOINT, PAGE_PATH, TSX, group);

    }

    /**
     * 执行
     */
    private static void exec(String springDocEndpoint, String pagePath, String ts, String group) {

        HashMap<String, HashMap<String, BeApi>> apiMap = SpringDocUtil.get(springDocEndpoint);

        //        System.out.println(JSONUtil.toJsonStr(apiMap));

        HashMap<String, BeApi> pathBeApiMap = apiMap.get(group);

        if (CollUtil.isEmpty(pathBeApiMap)) {

            log.info("操作失败：group不存在");
            return;

        }

    }

}
