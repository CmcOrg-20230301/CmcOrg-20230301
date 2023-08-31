package com.cmcorg20230301.be.engine.security.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ReflectUtil;
import com.cmcorg20230301.be.engine.redisson.util.IdGeneratorUtil;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityTree;
import lombok.SneakyThrows;

import java.util.*;
import java.util.stream.Collectors;

public class MyTreeUtil {

    /**
     * 根据底级节点 list，逆向生成整棵树
     * 备注：有子节点时，children才是集合
     */
    public static <T extends BaseEntityTree<T>> List<T> getFullTreeByDeepNode(List<T> deepNodeList,
        List<T> allCollection) {

        return listToTree(getFullTreeList(deepNodeList, allCollection), false);

    }

    /**
     * 根据底级节点 list，逆向生成整棵树 list
     *
     * @param allCollection 所有的集合
     * @param deepNodeList  底级节点list
     */
    public static <T extends BaseEntityTree<T>> List<T> getFullTreeList(List<T> deepNodeList,
        Collection<T> allCollection) {

        List<T> resultList = new ArrayList<>(deepNodeList); // 先添加底级节点

        doGetFullTree(deepNodeList, allCollection, resultList);

        return resultList;

    }

    /**
     * 根据底级节点 set，逆向生成整棵树 set
     *
     * @param allCollection 所有的节点
     * @param deepNodeSet   底级节点set
     */
    public static <T extends BaseEntityTree<T>> Set<T> getFullTreeSet(Set<T> deepNodeSet, Collection<T> allCollection) {

        Set<T> resultSet = new HashSet<>(deepNodeSet); // 先添加底级节点

        doGetFullTree(deepNodeSet, allCollection, resultSet);

        return resultSet;

    }

    private static <T extends BaseEntityTree<T>> void doGetFullTree(Collection<T> deepNodeSet,
        Collection<T> allCollection, Collection<T> resultSet) {

        Set<Long> parentIdSet = deepNodeSet.stream().map(BaseEntityTree::getParentId).collect(Collectors.toSet());

        Map<Long, T> allMap = allCollection.stream().collect(Collectors.toMap(BaseEntityTree::getId, it -> it));

        // 已经添加了 idSet
        Set<Long> resultIdSet = resultSet.stream().map(BaseEntity::getId).collect(Collectors.toSet());

        for (Long item : parentIdSet) {
            getFullTreeListHandle(allMap, resultSet, item, resultIdSet); // 添加父级节点
        }

    }

    private static <T extends BaseEntityTree<T>> void getFullTreeListHandle(Map<Long, T> allMap,
        Collection<T> resultList, Long parentId, Set<Long> resultIdSet) {

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
        resultList.add(item); // 添加父节点

        getFullTreeListHandle(allMap, resultList, item.getParentId(), resultIdSet);

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

        // 处理：topIdSet：通过：父级 id分组，value：子级 idSet
        Map<Long, Set<Long>> groupParentIdMap = listMap.values().stream().collect(Collectors
            .groupingBy(BaseEntityTree::getParentId, Collectors.mapping(BaseEntity::getId, Collectors.toSet())));

        for (Map.Entry<Long, Set<Long>> item : groupParentIdMap.entrySet()) {

            if (!groupParentIdMap.containsKey(item.getKey())) { // 如果：不存在该父节点，则表示是：顶层节点

                for (Long subItem : item.getValue()) {

                    resultList.add(listMap.get(subItem)); // 添加：顶层节点

                }

            }

        }

    }

    /**
     * 给树形结构的 集合（还没有转换为树形结构），重新设置 id和 parentId
     */
    public static <T extends BaseEntityTree<T>> void treeListSetNewIdAndParentId(List<T> flatList) {

        // 旧 id，新 id，map
        Map<Long, Long> idMap =
            flatList.stream().collect(Collectors.toMap(BaseEntity::getId, it -> IdGeneratorUtil.nextId()));

        for (T item : flatList) {

            Long newId = idMap.get(item.getId());

            item.setId(newId);

            Long newParentId = idMap.get(item.getParentId());

            item.setParentId(MyEntityUtil.getNotNullParentId(newParentId));

        }

    }

}
