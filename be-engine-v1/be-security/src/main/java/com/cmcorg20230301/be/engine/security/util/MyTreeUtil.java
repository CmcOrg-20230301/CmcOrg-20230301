package com.cmcorg20230301.be.engine.security.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ReflectUtil;
import com.cmcorg20230301.be.engine.redisson.util.IdGeneratorUtil;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityTree;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class MyTreeUtil {

    /**
     * 根据底级节点 list，逆向生成整棵树
     * 备注：有子节点时，children才是集合
     */
    public static <T extends BaseEntityTree<T>> List<T> getFullTreeByDeepNode(Collection<T> deepNodeCollection,
        List<T> allCollection) {

        return listToTree(getFullTreeList(deepNodeCollection, allCollection), false);

    }

    /**
     * 根据底级节点 list，逆向生成整棵树 list
     *
     * @param allCollection      所有的集合
     * @param deepNodeCollection 底级节点集合
     */
    public static <T extends BaseEntityTree<T>> List<T> getFullTreeList(Collection<T> deepNodeCollection,
        Collection<T> allCollection) {

        List<T> resultList = new ArrayList<>(deepNodeCollection); // 先添加底级节点

        doGetFullTree(deepNodeCollection, allCollection, resultList);

        return resultList;

    }

    /**
     * 根据底级节点 set，逆向生成整棵树 set
     *
     * @param allCollection      所有的节点
     * @param deepNodeCollection 底级节点
     */
    public static <T extends BaseEntityTree<T>> Set<T> getFullTreeSet(Set<T> deepNodeCollection,
        Collection<T> allCollection) {

        Set<T> resultSet = new HashSet<>(deepNodeCollection); // 先添加底级节点

        doGetFullTree(deepNodeCollection, allCollection, resultSet);

        return resultSet;

    }

    private static <T extends BaseEntityTree<T>> void doGetFullTree(Collection<T> deepNodeCollection,
        Collection<T> allCollection, Collection<T> resultCollection) {

        Set<Long> parentIdSet =
            deepNodeCollection.stream().map(BaseEntityTree::getParentId).collect(Collectors.toSet());

        Map<Long, T> allMap = allCollection.stream().collect(Collectors.toMap(BaseEntityTree::getId, it -> it));

        // 已经添加了 idSet
        Set<Long> resultIdSet = resultCollection.stream().map(BaseEntity::getId).collect(Collectors.toSet());

        for (Long item : parentIdSet) {
            getFullTreeListHandle(allMap, resultCollection, item, resultIdSet); // 添加父级节点
        }

    }

    private static <T extends BaseEntityTree<T>> void getFullTreeListHandle(Map<Long, T> allMap,
        Collection<T> resultCollection, Long parentId, Set<Long> resultIdSet) {

        if (parentId == 0) {
            return;
        }

        T item = allMap.get(parentId); // 找到父节点

        if (item == null) {
            return;
        }

        if (resultIdSet.contains(item.getId())) {

            // 如果父节点已经添加，则结束方法
            return;

        }

        resultIdSet.add(item.getId());
        resultCollection.add(item); // 添加父节点

        getFullTreeListHandle(allMap, resultCollection, item.getParentId(), resultIdSet);

    }

    /**
     * 比原始的递归快
     * 原理：运用了对象地址引用原理
     *
     * @param childrenFlag 【true】 children 一直为集合 【false】 有子节点时，children为集合，无子节点时，children 为 null
     */
    @SneakyThrows
    public static <T extends BaseEntityTree<T>> List<T> listToTree(List<T> list, boolean childrenFlag) {

        Map<Long, T> listMap = MapUtil.newHashMap(list.size()); // 把 list的所有元素转换为：id -> 元素，格式

        List<T> resultList = new LinkedList<>(); // 返回值

        for (T item : list) {

            T mapDTO = listMap.get(item.getId());

            if (mapDTO == null) {

                mapDTO = item;

                if (CollUtil.isEmpty(mapDTO.getChildren())) { // 避免：mapDTO里面原来就有 children

                    if (childrenFlag) {

                        mapDTO.setChildren(new LinkedList<>()); // children 一直为集合

                    } else {

                        mapDTO.setChildren(null); // 无子节点时，children 为 null

                    }

                }

                listMap.put(mapDTO.getId(), mapDTO);

            } else {

                // 如果存在，则只保留 children属性，并补全其他属性
                BeanUtil.copyProperties(item, mapDTO, "children");

            }

            if (mapDTO.getParentId() == 0) {

                resultList.add(mapDTO);

                continue;

            }

            // 把自己添加到：父节点的 children上
            T parentDTO = listMap.get(mapDTO.getParentId());

            if (parentDTO == null) {

                parentDTO = (T)ReflectUtil.newInstance(item.getClass());

                List<T> children = new LinkedList<>();

                children.add(mapDTO);

                parentDTO.setChildren(children); // 给父节点设置 children属性

                listMap.put(mapDTO.getParentId(), parentDTO);

            } else {

                List<T> children = parentDTO.getChildren();

                if (children == null) {

                    children = new LinkedList<>();

                    children.add(mapDTO);

                    parentDTO.setChildren(children); // 给父节点设置 children属性

                } else {

                    children.add(mapDTO);

                }

            }

        }

        // 如果，顶层的节点不是 0，则需要找到顶层节点的 id
        listToTreeHandleResultList(resultList, listMap);

        return resultList;

    }

    /**
     * 如果，顶层的节点不是 0，则需要找到顶层节点的 id
     */
    private static <T extends BaseEntityTree<T>> void listToTreeHandleResultList(List<T> resultList,
        Map<Long, T> listMap) {

        if (listMap.size() == 0 || resultList.size() != 0) {
            return;
        }

        for (T item : listMap.values()) {

            Long parentId = item.getParentId();

            if (parentId == null) { // 如果不存在，父级 id，则不处理
                continue;
            }

            if (listMap.get(parentId).getId() == null) { // 如果：不存在该父节点，则表示是：顶层节点

                resultList.add(item); // 添加：顶层节点

            }

        }

    }

    /**
     * 给树形结构的 集合（还没有转换为树形结构），重新设置 id和 parentId
     */
    public static <T extends BaseEntityTree<T>> void treeListSetNewIdAndParentId(Collection<T> flatCollection) {

        // 旧 id，新 id，map
        Map<Long, Long> idMap =
            flatCollection.stream().collect(Collectors.toMap(BaseEntity::getId, it -> IdGeneratorUtil.nextId()));

        for (T item : flatCollection) {

            Long newId = idMap.get(item.getId());

            item.setId(newId);

            Long newParentId = idMap.get(item.getParentId());

            item.setParentId(MyEntityUtil.getNotNullParentId(newParentId));

        }

    }

    /**
     * 获取：包含本节点，以及所有下级节点的 idSet的 map
     * key：id，value：包含本节点，以及所有下级节点的 idSet
     *
     * @param matchIdSet 需要匹配的 idSet，如果为 null，则表示需要匹配所有 id
     */
    public static <T extends BaseEntityTree<T>> Map<Long, Set<Long>> getIdAndDeepIdSetMap(Collection<T> collection,
        @Nullable Set<Long> matchIdSet) {

        // 通过：父级 id分组，value：子级 idSet
        Map<Long, Set<Long>> groupParentIdMap = collection.stream().collect(Collectors
            .groupingBy(BaseEntityTree::getParentId, Collectors.mapping(BaseEntity::getId, Collectors.toSet())));

        Map<Long, Set<Long>> resultMap;

        if (CollUtil.isEmpty(matchIdSet)) {

            resultMap = new HashMap<>(collection.size());

            for (T item : collection) {

                // 处理
                getIdAndDeepIdSetMapHandle(groupParentIdMap, resultMap, item.getId());

            }

        } else {

            resultMap = new HashMap<>(matchIdSet.size());

            for (Long id : matchIdSet) {

                // 处理
                getIdAndDeepIdSetMapHandle(groupParentIdMap, resultMap, id);

            }

        }

        return resultMap;

    }

    /**
     * 处理
     */
    private static void getIdAndDeepIdSetMapHandle(Map<Long, Set<Long>> groupParentIdMap,
        Map<Long, Set<Long>> resultMap, Long id) {

        Set<Long> resultSet = new HashSet<>();

        resultSet.add(id);

        // 获取：下级节点
        getIdAndDeepIdSetMapNext(resultSet, id, groupParentIdMap);

        resultMap.put(id, resultSet);

    }

    /**
     * 获取：下级节点
     */
    private static void getIdAndDeepIdSetMapNext(Set<Long> resultSet, Long parentId,
        Map<Long, Set<Long>> groupParentIdMap) {

        // 获取：自己下面的子级
        Set<Long> childrenIdSet = groupParentIdMap.get(parentId);

        if (CollUtil.isEmpty(childrenIdSet)) {
            return;
        }

        for (Long item : childrenIdSet) {

            resultSet.add(item);

            // 继续匹配下一级
            getIdAndDeepIdSetMapNext(resultSet, item, groupParentIdMap);

        }

    }

}
