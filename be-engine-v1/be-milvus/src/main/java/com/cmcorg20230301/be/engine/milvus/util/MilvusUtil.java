package com.cmcorg20230301.be.engine.milvus.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import io.milvus.client.MilvusServiceClient;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.grpc.DataType;
import io.milvus.grpc.QueryResults;
import io.milvus.grpc.SearchResults;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.collection.CreateCollectionParam;
import io.milvus.param.collection.FieldType;
import io.milvus.param.collection.LoadCollectionParam;
import io.milvus.param.dml.DeleteParam;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.QueryParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.param.index.CreateIndexParam;
import io.milvus.response.QueryResultsWrapper;
import io.milvus.response.SearchResultsWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Consumer;

/**
 * milvus工具类
 */
@Component
@Slf4j
public class MilvusUtil {

    @Nullable
    private static MilvusServiceClient milvusServiceClient;

    @Autowired(required = false)
    public void setMilvusServiceClient(MilvusServiceClient milvusServiceClient) {
        MilvusUtil.milvusServiceClient = milvusServiceClient;
    }

    /**
     * 租户 id
     */
    public static final String TENANT_ID_FIELD_NAME = "tenantId";

    /**
     * 用户 id
     */
    public static final String USER_ID_FIELD_NAME = "userId";

    /**
     * 返回值
     */
    public static final String RESULT_FIELD_NAME = "result";

    /**
     * 向量转换前的文字
     */
    public static final String VECTOR_TEXT_FIELD_NAME = "vectorText";

    /**
     * 向量
     */
    public static final String VECTOR_LIST_FIELD_NAME = "vectorList";

    /**
     * 字段名集合，备注：不包含：向量字段
     */
    public static final List<String> FIELD_NAME_LIST =
        CollUtil.newArrayList(TENANT_ID_FIELD_NAME, USER_ID_FIELD_NAME, RESULT_FIELD_NAME, VECTOR_TEXT_FIELD_NAME);

    /**
     * 创建并加载 collection
     */
    public static void createAndLoadCollection(String collectionName, @Nullable List<FieldType> fieldTypeList) {

        // 创建并加载 collection
        createAndLoadCollection(collectionName, 2000, fieldTypeList);

    }

    /**
     * 创建并加载 collection
     *
     * @param vectorLength 向量集合长度
     */
    public static void createAndLoadCollection(String collectionName, int vectorLength,
        @Nullable List<FieldType> fieldTypeList) {

        FieldType tenantIdFieldType =
            FieldType.newBuilder().withName(TENANT_ID_FIELD_NAME).withDataType(DataType.Int64).build();

        FieldType userIdFieldType =
            FieldType.newBuilder().withName(USER_ID_FIELD_NAME).withDataType(DataType.Int64).build();

        FieldType resultFieldType =
            FieldType.newBuilder().withName(RESULT_FIELD_NAME).withDataType(DataType.VarChar).withMaxLength(2000)
                .build();

        FieldType vectorTextFieldType =
            FieldType.newBuilder().withName(VECTOR_TEXT_FIELD_NAME).withDataType(DataType.VarChar).withMaxLength(2000)
                .build();

        FieldType vectorFieldType =
            FieldType.newBuilder().withName(VECTOR_LIST_FIELD_NAME).withDataType(DataType.FloatVector)
                .withDimension(vectorLength).build();

        // 创建并加载 collection
        createAndLoadCollection(collectionName, resultFieldType, vectorTextFieldType, vectorFieldType,
            tenantIdFieldType, userIdFieldType, fieldTypeList);

    }

    /**
     * 创建并加载 collection
     */
    public static void createAndLoadCollection(String collectionName, FieldType resultFieldType,
        FieldType vectorTextFieldType, FieldType vectorFieldType, FieldType tenantIdFieldType,
        FieldType userIdFieldType, @Nullable List<FieldType> fieldTypeList) {

        if (milvusServiceClient == null) {
            return;
        }

        if (collectionName == null || resultFieldType == null || vectorTextFieldType == null
            || vectorFieldType == null) {
            return;
        }

        FieldType idFieldType =
            FieldType.newBuilder().withName("id").withDataType(DataType.Int64).withPrimaryKey(true).withAutoID(true)
                .build();

        // 备注：不建议使用：withDatabaseName(databaseName)，原因：因为 search的时候不能指定 databaseName
        CreateCollectionParam.Builder builder =
            CreateCollectionParam.newBuilder().withCollectionName(collectionName).addFieldType(idFieldType)
                .addFieldType(resultFieldType).addFieldType(vectorTextFieldType).addFieldType(vectorFieldType)
                .addFieldType(tenantIdFieldType).addFieldType(userIdFieldType);

        if (CollUtil.isNotEmpty(fieldTypeList)) {
            builder.withFieldTypes(fieldTypeList); // 添加：额外的字段
        }

        // 创建集合，备注：如果存在则不会重新创建
        milvusServiceClient.createCollection(builder.build());

        IndexType indexType = IndexType.IVF_FLAT; // IndexType
        String indexParam = "{\"nlist\":1024}"; // ExtraParam，备注：值越大占用的空间越多

        // 创建索引，备注：如果存在则不会重新创建
        milvusServiceClient.createIndex(
            CreateIndexParam.newBuilder().withCollectionName(collectionName).withFieldName(vectorFieldType.getName())
                .withIndexType(indexType).withMetricType(MetricType.L2).withExtraParam(indexParam)
                .withSyncMode(Boolean.FALSE).build());

        milvusServiceClient.loadCollection(LoadCollectionParam.newBuilder().withCollectionName(collectionName).build());

    }

    /**
     * 向量匹配
     *
     * @return null 则表示没有匹配上
     */
    @Nullable
    public static String match(List<Float> floatList, String collectionName, long tenantId, long userId,
        @Nullable String exprStr) {

        return match(floatList, collectionName, RESULT_FIELD_NAME, VECTOR_LIST_FIELD_NAME, tenantId, userId, exprStr);

    }

    /**
     * 向量匹配
     *
     * @return null 则表示没有匹配上
     */
    @Nullable
    public static String match(List<Float> floatList, String collectionName, String resultFieldName,
        String vectorFieldName, long tenantId, long userId, @Nullable String exprStr) {

        // 得分在 0.2以下，则算匹配上了向量数据库
        return match(floatList, collectionName, resultFieldName, vectorFieldName, 0.2f, tenantId, userId, exprStr);

    }

    /**
     * 向量匹配
     *
     * @param exprStr 额外的查询条件
     * @return null 则表示没有匹配上
     */
    @Nullable
    public static String match(List<Float> floatList, String collectionName, String resultFieldName,
        String vectorFieldName, float score, long tenantId, long userId, @Nullable String exprStr) {

        if (milvusServiceClient == null) {
            return null;
        }

        if (CollUtil.isEmpty(floatList)) {
            return null;
        }

        Integer topK = 1; // TopK，返回多少条数据
        String param = "{\"nprobe\":10}"; // Params，精确度，值越大越精确，但是越慢，默认值为：10

        List<String> outFieldList = Collections.singletonList(resultFieldName);
        List<List<Float>> vectorList = Collections.singletonList(floatList);

        // 过滤器表达式
        StrBuilder exprStrBuilder = StrBuilder.create();

        exprStrBuilder.append(" tenantId == ").append(tenantId).append(" and userId == ").append(userId);

        if (StrUtil.isNotBlank(exprStr)) {
            exprStrBuilder.append(" ").append(exprStr); // 添加：额外的查询条件
        }

        SearchParam searchParam = SearchParam.newBuilder().withCollectionName(collectionName)
            .withConsistencyLevel(ConsistencyLevelEnum.STRONG).withMetricType(MetricType.L2).withOutFields(outFieldList)
            .withTopK(topK).withVectors(vectorList).withVectorFieldName(vectorFieldName).withParams(param)
            .withExpr(exprStrBuilder.toString()).build();

        R<SearchResults> searchResults = milvusServiceClient.search(searchParam);

        if (!searchResults.getData().getResults().getIds().hasIntId()) {
            return null;
        }

        SearchResultsWrapper searchResultsWrapper = new SearchResultsWrapper(searchResults.getData().getResults());

        if (searchResultsWrapper.getIDScore(0).get(0).getScore() > score) {
            return null;
        }

        // 获取：查询字段的值
        return (String)searchResultsWrapper.getFieldData(resultFieldName, 0).get(0);

    }

    /**
     * 查询
     */
    @NotNull
    public static <T> List<T> query(String collectionName, @Nullable String exprStr, List<String> outFieldList,
        long offset, long limit, Class<T> tClass) {

        if (milvusServiceClient == null) {
            return new ArrayList<>();
        }

        QueryParam.Builder builder = QueryParam.newBuilder().withCollectionName(collectionName)
            .withConsistencyLevel(ConsistencyLevelEnum.STRONG);

        if (StrUtil.isNotBlank(exprStr)) {
            builder.withExpr(exprStr);
        }

        QueryParam queryParam = builder.withOutFields(outFieldList).withOffset(offset).withLimit(limit).build();

        R<QueryResults> queryResultsR = milvusServiceClient.query(queryParam);

        QueryResultsWrapper queryResultsWrapper = new QueryResultsWrapper(queryResultsR.getData());

        List<T> resultList = new ArrayList<>(); // 本方法返回值

        for (QueryResultsWrapper.RowRecord item : queryResultsWrapper.getRowRecords()) {

            Map<String, Object> fieldValueMap = item.getFieldValues();

            T t = BeanUtil.copyProperties(fieldValueMap, tClass); // 属性拷贝

            resultList.add(t);

        }

        return resultList;

    }

    /**
     * 新增，备注：milvus 暂时不支持修改，请删除之后，再新增，以达到修改的目的
     */
    public static <T> boolean insert(String collectionName, List<T> insertRowList, @Nullable Consumer<T> consumer) {

        if (milvusServiceClient == null) {
            return false;
        }

        if (CollUtil.isEmpty(insertRowList)) {
            return true;
        }

        List<JSONObject> insertList = new ArrayList<>();

        for (T item : insertRowList) {

            if (consumer != null) {
                consumer.accept(item);
            }

            JSONObject jsonObject = BeanUtil.copyProperties(item, JSONObject.class);

            insertList.add(jsonObject);

        }

        InsertParam insertParam =
            InsertParam.newBuilder().withCollectionName(collectionName).withRows(insertList).build();

        milvusServiceClient.insert(insertParam);

        return true;

    }

    /**
     * 删除：根据主键 idSet
     */
    public static boolean delete(String collectionName, Set<Long> idSet) {

        // 条件
        StrBuilder exprStrBuilder = StrBuilder.create();

        exprStrBuilder.append("id in [");

        String joinStr = CollUtil.join(idSet, ",");

        exprStrBuilder.append(joinStr).append("]");

        return delete(collectionName, exprStrBuilder.toString());

    }

    /**
     * 删除
     */
    public static boolean delete(String collectionName, String exprStr) {

        if (milvusServiceClient == null) {
            return false;
        }

        milvusServiceClient
            .delete(DeleteParam.newBuilder().withCollectionName(collectionName).withExpr(exprStr).build());

        return true;

    }

}
