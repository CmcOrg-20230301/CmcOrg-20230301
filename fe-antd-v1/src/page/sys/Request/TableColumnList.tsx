import {YesNoDict} from "@/util/DictUtil";
import {SysRequestDO} from "@/api/SysRequest";
import {ProSchema} from "@ant-design/pro-utils";

const TableColumnList = (): ProSchema<SysRequestDO>[] => [

    {
        title: '序号',
        dataIndex: 'index',
        valueType: 'index',
        width: 90,
    },


    {title: 'ip', dataIndex: 'ip', ellipsis: true, width: 90,},

    {title: '请求的参数', dataIndex: 'requestParam', ellipsis: true, width: 90,},

    {title: 'uri', dataIndex: 'uri', ellipsis: true, width: 90,},

    {
        title: '是否成功',
        dataIndex: 'successFlag',
        valueEnum: YesNoDict
    },

    {title: '失败信息', dataIndex: 'errorMsg', ellipsis: true, width: 90,},

    {title: '耗时', dataIndex: 'costMsStr', ellipsis: true, width: 90,},

    {
        title: '创建时间',
        dataIndex: 'createTime',
        hideInSearch: true,
        valueType: 'fromNow',
    },

    {title: '创建人id', dataIndex: 'createId', ellipsis: true, width: 90,},

    {title: '接口名', dataIndex: 'name', ellipsis: true, width: 90,},

    {title: '请求类别', dataIndex: 'category', ellipsis: true, width: 90,},

    {title: 'ip位置', dataIndex: 'region', ellipsis: true, width: 90,},


];

export default TableColumnList
