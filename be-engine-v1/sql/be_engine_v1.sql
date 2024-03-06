SET NAMES utf8mb4;
SET
FOREIGN_KEY_CHECKS = 0;

CREATE
DATABASE IF NOT EXISTS `be_engine_v1` CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci';
USE
`be_engine_v1`;

-- ----------------------------
-- Table structure for sys_area
-- ----------------------------
DROP TABLE IF EXISTS `sys_area`;
CREATE TABLE `sys_area`
(
    `id`          bigint                                                        NOT NULL,
    `tenant_id`   bigint                                                        NOT NULL COMMENT '租户 id',
    `create_id`   bigint                                                        NOT NULL,
    `create_time` datetime                                                      NOT NULL,
    `update_id`   bigint                                                        NOT NULL,
    `update_time` datetime                                                      NOT NULL,
    `enable_flag` tinyint(1) NOT NULL COMMENT '是否启用',
    `version`     int                                                           NOT NULL COMMENT '乐观锁',
    `del_flag`    tinyint(1) NOT NULL COMMENT '是否逻辑删除',
    `remark`      varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '备注',
    `name`        varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '区域名',
    `parent_id`   bigint                                                        NOT NULL COMMENT '父节点id（顶级则为0）',
    `order_no`    int                                                           NOT NULL COMMENT '排序号（值越大越前面，默认为 0）',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = 'v20230301：主表：区域'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_area
-- ----------------------------

-- ----------------------------
-- Table structure for sys_area_ref_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_area_ref_dept`;
CREATE TABLE `sys_area_ref_dept`
(
    `tenant_id` bigint NOT NULL COMMENT '租户 id',
    `area_id`   bigint NOT NULL COMMENT '区域主键 id',
    `dept_id`   bigint NOT NULL COMMENT '部门主键 id',
    PRIMARY KEY (`area_id`, `dept_id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = 'v20230301：关联表：区域，部门'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_area_ref_dept
-- ----------------------------

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`
(
    `id`          bigint                                                        NOT NULL,
    `tenant_id`   bigint                                                        NOT NULL COMMENT '租户 id',
    `create_id`   bigint                                                        NOT NULL,
    `create_time` datetime                                                      NOT NULL,
    `update_id`   bigint                                                        NOT NULL,
    `update_time` datetime                                                      NOT NULL,
    `enable_flag` tinyint(1) NOT NULL COMMENT '是否启用',
    `version`     int                                                           NOT NULL COMMENT '乐观锁',
    `del_flag`    tinyint(1) NOT NULL COMMENT '是否逻辑删除',
    `remark`      varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '备注',
    `name`        varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '部门名',
    `parent_id`   bigint                                                        NOT NULL COMMENT '父节点id（顶级则为0）',
    `order_no`    int                                                           NOT NULL COMMENT '排序号（值越大越前面，默认为 0）',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = 'v20230301：主表：部门'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dept
-- ----------------------------

-- ----------------------------
-- Table structure for sys_dept_ref_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept_ref_user`;
CREATE TABLE `sys_dept_ref_user`
(
    `tenant_id` bigint NOT NULL COMMENT '租户 id',
    `dept_id`   bigint NOT NULL COMMENT '部门主键 id',
    `user_id`   bigint NOT NULL COMMENT '用户主键 id',
    PRIMARY KEY (`dept_id`, `user_id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = 'v20230301：关联表：部门，用户'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dept_ref_user
-- ----------------------------

-- ----------------------------
-- Table structure for sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict`
(
    `id`          bigint                                                        NOT NULL,
    `tenant_id`   bigint                                                        NOT NULL COMMENT '租户 id',
    `create_id`   bigint                                                        NOT NULL,
    `create_time` datetime                                                      NOT NULL,
    `update_id`   bigint                                                        NOT NULL,
    `update_time` datetime                                                      NOT NULL,
    `enable_flag` tinyint(1) NOT NULL COMMENT '是否启用',
    `version`     int                                                           NOT NULL COMMENT '乐观锁',
    `del_flag`    tinyint(1) NOT NULL COMMENT '是否逻辑删除',
    `remark`      varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '备注',
    `dict_key`    varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '字典 key（不能重复），字典项要冗余这个 key，目的：方便操作',
    `name`        varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '字典/字典项 名',
    `type`        tinyint                                                       NOT NULL COMMENT '字典类型：1 字典 2 字典项',
    `value`       int                                                           NOT NULL COMMENT '字典项 value（数字 123...）备注：字典为 -1',
    `order_no`    int                                                           NOT NULL COMMENT '排序号（值越大越前面，默认为 0）',
    `uuid`        varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '该参数的 uuid，用于：同步租户参数等操作，备注：不允许修改',
    `system_flag` tinyint(1) NOT NULL COMMENT '系统内置：是 强制同步给租户 否 不同步给租户',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = 'v20230301：主表：字典'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict
-- ----------------------------
INSERT INTO `sys_dict` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`, `version`, `del_flag`, `remark`, `dict_key`, `name`, `type`,
                        `value`, `order_no`, `uuid`, `system_flag`)
VALUES (230602172317117446, 0, 0, '2023-06-02 17:23:17', 0, '2023-06-02 17:23:17', 1, 0, 0, '',
        'sys_request_category',
        '请求类别', 1, -1, 10000, '7e56dc276554434e87c045de5eff459f', 1);
INSERT INTO `sys_dict` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`, `version`, `del_flag`, `remark`, `dict_key`, `name`, `type`,
                        `value`, `order_no`, `uuid`, `system_flag`)
VALUES (230602172924117450, 0, 0, '2023-06-02 17:29:24', 0, '2023-06-02 17:32:06', 1, 0, 0, '',
        'sys_request_category',
        'windows-浏览器', 2, 101, 10000, '08a2702cc939421ebc877aef9053b241', 1);
INSERT INTO `sys_dict` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`, `version`, `del_flag`, `remark`, `dict_key`, `name`, `type`,
                        `value`, `order_no`, `uuid`, `system_flag`)
VALUES (230602173355117474, 0, 0, '2023-06-02 17:33:56', 0, '2023-06-02 17:34:00', 1, 0, 0, '',
        'sys_request_category',
        'mac-浏览器', 2, 102, 9900, '340d327435c24502ae0cd6840f8d3339', 1);
INSERT INTO `sys_dict` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`, `version`, `del_flag`, `remark`, `dict_key`, `name`, `type`,
                        `value`, `order_no`, `uuid`, `system_flag`)
VALUES (230602173407117480, 0, 0, '2023-06-02 17:34:08', 0, '2023-06-02 17:34:08', 1, 0, 0, '',
        'sys_request_category',
        'linux-浏览器', 2, 103, 9800, '8babfedd533b496f9e53a4758807d974', 1);
INSERT INTO `sys_dict` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`, `version`, `del_flag`, `remark`, `dict_key`, `name`, `type`,
                        `value`, `order_no`, `uuid`, `system_flag`)
VALUES (230602173426117483, 0, 0, '2023-06-02 17:34:27', 0, '2023-06-02 17:34:27', 1, 0, 0, '',
        'sys_request_category',
        'windows-客户端', 2, 201, 9700, 'd94105f521354c6fa72e85ab729f577d', 1);
INSERT INTO `sys_dict` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`, `version`, `del_flag`, `remark`, `dict_key`, `name`, `type`,
                        `value`, `order_no`, `uuid`, `system_flag`)
VALUES (230602173437117486, 0, 0, '2023-06-02 17:34:37', 0, '2023-06-02 17:34:37', 1, 0, 0, '',
        'sys_request_category',
        'mac-客户端', 2, 202, 9600, 'e30fa6e51c714c578358ddb509973ccc', 1);
INSERT INTO `sys_dict` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`, `version`, `del_flag`, `remark`, `dict_key`, `name`, `type`,
                        `value`, `order_no`, `uuid`, `system_flag`)
VALUES (230602173443117489, 0, 0, '2023-06-02 17:34:43', 0, '2023-06-02 17:34:43', 1, 0, 0, '',
        'sys_request_category',
        'linux-客户端', 2, 203, 9500, 'ec96af09f632477f8ba5ac5bab2f6844', 1);
INSERT INTO `sys_dict` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`, `version`, `del_flag`, `remark`, `dict_key`, `name`, `type`,
                        `value`, `order_no`, `uuid`, `system_flag`)
VALUES (230602173451117492, 0, 0, '2023-06-02 17:34:52', 0, '2023-06-02 17:34:52', 1, 0, 0, '',
        'sys_request_category',
        '安卓端', 2, 301, 9400, '49bb59c89fc64cdda36928ae85b3780c', 1);
INSERT INTO `sys_dict` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`, `version`, `del_flag`, `remark`, `dict_key`, `name`, `type`,
                        `value`, `order_no`, `uuid`, `system_flag`)
VALUES (230602173457117495, 0, 0, '2023-06-02 17:34:58', 0, '2023-06-02 17:34:58', 1, 0, 0, '',
        'sys_request_category',
        '安卓-浏览器', 2, 302, 9300, 'da949f68c67647349b57099f60ce4da0', 1);
INSERT INTO `sys_dict` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`, `version`, `del_flag`, `remark`, `dict_key`, `name`, `type`,
                        `value`, `order_no`, `uuid`, `system_flag`)
VALUES (230602173503117498, 0, 0, '2023-06-02 17:35:03', 0, '2023-06-02 17:35:03', 1, 0, 0, '',
        'sys_request_category',
        '安卓-浏览器-微信', 2, 303, 9200, '48263073c7cb4a2e813890cc5dbae083', 1);
INSERT INTO `sys_dict` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`, `version`, `del_flag`, `remark`, `dict_key`, `name`, `type`,
                        `value`, `order_no`, `uuid`, `system_flag`)
VALUES (230602173512117501, 0, 0, '2023-06-02 17:35:12', 0, '2023-06-02 17:35:12', 1, 0, 0, '',
        'sys_request_category',
        '苹果端', 2, 401, 9100, '792d008442cd436397047698b14c0578', 1);
INSERT INTO `sys_dict` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`, `version`, `del_flag`, `remark`, `dict_key`, `name`, `type`,
                        `value`, `order_no`, `uuid`, `system_flag`)
VALUES (230602173532117507, 0, 0, '2023-06-02 17:35:32', 0, '2023-06-02 17:35:41', 1, 0, 0, '',
        'sys_request_category',
        '苹果-浏览器', 2, 402, 9000, '55a4f2f71a3c4390b540a0a6944e1418', 1);
INSERT INTO `sys_dict` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`, `version`, `del_flag`, `remark`, `dict_key`, `name`, `type`,
                        `value`, `order_no`, `uuid`, `system_flag`)
VALUES (230602173538117510, 0, 0, '2023-06-02 17:35:38', 0, '2023-06-02 17:35:38', 1, 0, 0, '',
        'sys_request_category',
        '苹果-浏览器-微信', 2, 403, 8900, 'fd05b29318004da8843470abc0e5e771', 1);
INSERT INTO `sys_dict` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`, `version`, `del_flag`, `remark`, `dict_key`, `name`, `type`,
                        `value`, `order_no`, `uuid`, `system_flag`)
VALUES (230602173552117516, 0, 0, '2023-06-02 17:35:53', 0, '2023-06-02 17:35:53', 1, 0, 0, '',
        'sys_request_category',
        '小程序-微信-安卓', 2, 501, 8800, '9c9cfed7019c487c8d469bc0e1cce7ad', 1);
INSERT INTO `sys_dict` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`, `version`, `del_flag`, `remark`, `dict_key`, `name`, `type`,
                        `value`, `order_no`, `uuid`, `system_flag`)
VALUES (230602173602117519, 0, 0, '2023-06-02 17:36:03', 0, '2023-06-02 17:36:03', 1, 0, 0, '',
        'sys_request_category',
        '小程序-微信-苹果', 2, 502, 8700, '45a5cc91202c45f58eae1f7e6955b2aa', 1);
INSERT INTO `sys_dict` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`, `version`, `del_flag`, `remark`, `dict_key`, `name`, `type`,
                        `value`, `order_no`, `uuid`, `system_flag`)
VALUES (230810115441152907, 0, 0, '2023-08-10 11:54:42', 0, '2023-08-10 12:01:29', 1, 0, 0, '',
        'sys_request_category',
        'windows-浏览器-微信', 2, 104, 9790, '174aee7dec7e4e68a599e341f4ffd706', 1);
INSERT INTO `sys_dict` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`, `version`, `del_flag`, `remark`, `dict_key`, `name`, `type`,
                        `value`, `order_no`, `uuid`, `system_flag`)
VALUES (230810120204152982, 0, 0, '2023-08-10 12:02:04', 0, '2023-08-10 12:02:04', 1, 0, 0, '',
        'sys_request_category',
        'mac-浏览器-微信', 2, 105, 9780, '450e51f96a8046db8dcb471ddb803d35', 1);
INSERT INTO `sys_dict` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`, `version`, `del_flag`, `remark`, `dict_key`, `name`, `type`,
                        `value`, `order_no`, `uuid`, `system_flag`)
VALUES (230810120224152985, 0, 0, '2023-08-10 12:02:24', 0, '2023-08-10 12:02:24', 1, 0, 0, '',
        'sys_request_category',
        'linux-浏览器-微信', 2, 106, 9770, '03f02ce14bb149dab417ba42cc1d10c3', 1);
INSERT INTO `sys_dict` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`, `version`, `del_flag`, `remark`, `dict_key`, `name`, `type`,
                        `value`, `order_no`, `uuid`, `system_flag`)
VALUES (230816153518154273, 0, 0, '2023-08-16 15:35:18', 0, '2023-08-16 15:35:18', 1, 0, 0, '',
        'sys_socket_type',
        'socket类型', 1, -1, 9900, 'bcebb3c73e204ecf9307ed40016057fd', 1);
INSERT INTO `sys_dict` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`, `version`, `del_flag`, `remark`, `dict_key`, `name`, `type`,
                        `value`, `order_no`, `uuid`, `system_flag`)
VALUES (230816153602154277, 0, 0, '2023-08-16 15:36:02', 0, '2023-08-23 17:08:24', 1, 0, 0, '',
        'sys_socket_type',
        'tcp_protobuf', 2, 101, 10000, 'c49e57d255b644c1aa4f05cb872c88c7', 1);
INSERT INTO `sys_dict` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`, `version`, `del_flag`, `remark`, `dict_key`, `name`, `type`,
                        `value`, `order_no`, `uuid`, `system_flag`)
VALUES (230816153610154281, 0, 0, '2023-08-16 15:36:11', 0, '2023-08-23 17:08:28', 1, 0, 0, '',
        'sys_socket_type',
        'web_socket', 2, 201, 9900, '652e76d2e5da41aa9df1954b13e36a5a', 1);
INSERT INTO `sys_dict` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`, `version`, `del_flag`, `remark`, `dict_key`, `name`, `type`,
                        `value`, `order_no`, `uuid`, `system_flag`)
VALUES (230816153619154284, 0, 0, '2023-08-16 15:36:19', 0, '2023-08-23 17:08:31', 1, 0, 0, '',
        'sys_socket_type',
        'udp_protobuf', 2, 301, 9800, '99c5de40bdaa4ed780861f8e851d39f4', 1);
INSERT INTO `sys_dict` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`, `version`, `del_flag`, `remark`, `dict_key`, `name`, `type`,
                        `value`, `order_no`, `uuid`, `system_flag`)
VALUES (230823164923154681, 0, 0, '2023-08-23 16:49:23', 0, '2023-08-23 16:49:43', 1, 0, 0, '',
        'sys_socket_online_type', 'socket在线状态', 1, -1, 9800, '2eb263aeb0ca4a909ca26187dcc331b5',
        1);
INSERT INTO `sys_dict` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`, `version`, `del_flag`, `remark`, `dict_key`, `name`, `type`,
                        `value`, `order_no`, `uuid`, `system_flag`)
VALUES (230823165012154695, 0, 0, '2023-08-23 16:50:12', 0, '2023-08-23 16:50:12', 1, 0, 0, '',
        'sys_socket_online_type', '在线', 2, 101, 10000, '58853fe2008b4f6991603097a1843495', 1);
INSERT INTO `sys_dict` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`, `version`, `del_flag`, `remark`, `dict_key`, `name`, `type`,
                        `value`, `order_no`, `uuid`, `system_flag`)
VALUES (230823165035154700, 0, 0, '2023-08-23 16:50:36', 0, '2023-08-23 16:50:36', 1, 0, 0, '',
        'sys_socket_online_type', '隐身', 2, 201, 9900, '4ad3dbfcf2c74538bba827e08a32834a', 1);
INSERT INTO `sys_dict` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`, `version`, `del_flag`, `remark`, `dict_key`, `name`, `type`,
                        `value`, `order_no`, `uuid`, `system_flag`)
VALUES (230823165101154704, 0, 0, '2023-08-23 16:51:01', 0, '2023-08-23 16:51:01', 1, 0, 0, '',
        'sys_socket_online_type', 'ping_test', 2, 100001, 9800, 'bfdef308355649ccb87f075f3924b1f3',
        1);
INSERT INTO `be_engine_v1`.`sys_dict` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`,
                                       `update_time`,
                                       `enable_flag`, `version`, `del_flag`, `remark`, `dict_key`,
                                       `name`, `type`,
                                       `value`, `order_no`, `uuid`, `system_flag`)
VALUES (231023100017080109, 0, 0, '2023-10-23 10:00:18', 0, '2023-10-23 10:00:18', 1, 0, 0, '',
        'sys_pay_type',
        '支付方式',
        1, -1, 9500, 'fa934b92b7e84d0d92543743ec6c369c', 1);
INSERT INTO `be_engine_v1`.`sys_dict` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`,
                                       `update_time`,
                                       `enable_flag`, `version`, `del_flag`, `remark`, `dict_key`,
                                       `name`, `type`,
                                       `value`, `order_no`, `uuid`, `system_flag`)
VALUES (231023100056080114, 0, 0, '2023-10-23 10:00:56', 0, '2023-10-23 10:00:56', 1, 0, 0, '',
        'sys_pay_type',
        '支付宝',
        2, 101, 10000, '5cd6ba1880314633a322b534410e212c', 1);
INSERT INTO `be_engine_v1`.`sys_dict` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`,
                                       `update_time`,
                                       `enable_flag`, `version`, `del_flag`, `remark`, `dict_key`,
                                       `name`, `type`,
                                       `value`, `order_no`, `uuid`, `system_flag`)
VALUES (231023100115080119, 0, 0, '2023-10-23 10:01:15', 0, '2023-10-23 10:01:15', 1, 0, 0, '',
        'sys_pay_type', '微信',
        2,
        201, 9900, '376ffa1bf407420c858d0c0a802da9e7', 1);
INSERT INTO `be_engine_v1`.`sys_dict` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`,
                                       `update_time`,
                                       `enable_flag`, `version`, `del_flag`, `remark`, `dict_key`,
                                       `name`, `type`,
                                       `value`, `order_no`, `uuid`, `system_flag`)
VALUES (231023100126080124, 0, 0, '2023-10-23 10:01:26', 0, '2023-10-23 10:01:26', 1, 0, 0, '',
        'sys_pay_type',
        '云闪付',
        2, 301, 9800, '5fd4331fd7e14a41872a2b977d3ad56b', 1);
INSERT INTO `be_engine_v1`.`sys_dict` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`,
                                       `update_time`,
                                       `enable_flag`, `version`, `del_flag`, `remark`, `dict_key`,
                                       `name`, `type`,
                                       `value`, `order_no`, `uuid`, `system_flag`)
VALUES (231023100136080128, 0, 0, '2023-10-23 10:01:37', 0, '2023-10-23 10:01:37', 1, 0, 0, '',
        'sys_pay_type', '谷歌',
        2,
        401, 9700, 'ae1954ff27a448c196170b2c20e1774f', 1);
INSERT INTO `be_engine_v1`.`sys_dict` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`,
                                       `update_time`,
                                       `enable_flag`, `version`, `del_flag`, `remark`, `dict_key`,
                                       `name`, `type`,
                                       `value`, `order_no`, `uuid`, `system_flag`)
VALUES (231023100245080135, 0, 0, '2023-10-23 10:02:46', 0, '2023-10-23 10:02:46', 1, 0, 0, '',
        'sys_other_app_type',
        '第三方应用类型', 1, -1, 9400, '73b703820ca94e50bde9b17886c26f44', 1);
INSERT INTO `be_engine_v1`.`sys_dict` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`,
                                       `update_time`,
                                       `enable_flag`, `version`, `del_flag`, `remark`, `dict_key`,
                                       `name`, `type`,
                                       `value`, `order_no`, `uuid`, `system_flag`)
VALUES (231023100257080139, 0, 0, '2023-10-23 10:02:58', 0, '2023-10-23 10:02:58', 1, 0, 0, '',
        'sys_other_app_type',
        '微信小程序', 2, 101, 10000, 'b64c2b9d483546c5ad9a1f8e36df7018', 1);
INSERT INTO `be_engine_v1`.`sys_dict` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`,
                                       `update_time`,
                                       `enable_flag`, `version`, `del_flag`, `remark`, `dict_key`,
                                       `name`, `type`,
                                       `value`, `order_no`, `uuid`, `system_flag`)
VALUES (231023100309080144, 0, 0, '2023-10-23 10:03:10', 0, '2023-10-23 10:03:10', 1, 0, 0, '',
        'sys_other_app_type',
        '微信公众号', 2, 102, 9900, '8903df79fa784e96a0fb60e3df879f6e', 1);
INSERT INTO `be_engine_v1`.`sys_dict` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`,
                                       `update_time`,
                                       `enable_flag`, `version`, `del_flag`, `remark`, `dict_key`,
                                       `name`, `type`,
                                       `value`, `order_no`, `uuid`, `system_flag`)
VALUES (231023100318080148, 0, 0, '2023-10-23 10:03:19', 0, '2023-10-23 10:03:19', 1, 0, 0, '',
        'sys_other_app_type',
        '支付宝小程序', 2, 201, 9800, '1d559f6e02484b608a69c16b255a0792', 1);


-- ----------------------------
-- Table structure for sys_file
-- ----------------------------
DROP TABLE IF EXISTS `sys_file`;
CREATE TABLE `sys_file`
(
    `id`               bigint                                                        NOT NULL,
    `tenant_id`        bigint                                                        NOT NULL COMMENT '租户 id',
    `create_id`        bigint                                                        NOT NULL,
    `create_time`      datetime                                                      NOT NULL,
    `update_id`        bigint                                                        NOT NULL,
    `update_time`      datetime                                                      NOT NULL,
    `enable_flag` tinyint(1) NOT NULL COMMENT '是否启用',
    `version`          int                                                           NOT NULL COMMENT '乐观锁',
    `del_flag`    tinyint(1) NOT NULL COMMENT '是否逻辑删除',
    `remark`           varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '备注',
    `belong_id`        bigint                                                        NOT NULL COMMENT '归属者用户主键 id（拥有全部权限）',
    `bucket_name`      varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '桶名，例如：be-bucket',
    `uri`              varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件完整路径（包含文件类型，不包含请求端点），例如：avatar/uuid.xxx',
    `origin_file_name` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件原始名（包含文件类型）',
    `new_file_name`    varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '新的文件名（包含文件类型），例如：uuid.xxx',
    `file_ext_name`    varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '文件类型（不含点），备注：这个是读取文件流的头部信息获得文件类型',
    `extra_json`       varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '额外信息（json格式）',
    `upload_type`      int                                                           NOT NULL COMMENT '文件上传类型：101 头像 201 文件系统-文件',
    `storage_type`     int                                                           NOT NULL COMMENT '存放文件的服务器类型：101 阿里云oss 201 minio ',
    `parent_id`        bigint                                                        NOT NULL COMMENT '上级文件夹的文件主键 id，默认为 0',
    `type`             int                                                           NOT NULL COMMENT '类型：1 文件夹 2 文件',
    `show_file_name`   varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '展示用的文件名，默认为：原始文件名（包含文件类型）',
    `ref_file_id`      bigint                                                        NOT NULL COMMENT '引用的文件主键 id，没有则为 -1，如果有值，则文件地址从引用的文件里面获取，但是权限等信息，从本条数据获取',
    `public_flag` tinyint(1) NOT NULL COMMENT '是否公开访问：0 否 1 是',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = 'v20230301：主表：文件'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_file
-- ----------------------------

-- ----------------------------
-- Table structure for sys_file_auth
-- ----------------------------
DROP TABLE IF EXISTS `sys_file_auth`;
CREATE TABLE `sys_file_auth`
(
    `id`          bigint                                                        NOT NULL,
    `tenant_id`   bigint                                                        NOT NULL COMMENT '租户 id',
    `create_id`   bigint                                                        NOT NULL,
    `create_time` datetime                                                      NOT NULL,
    `update_id`   bigint                                                        NOT NULL,
    `update_time` datetime                                                      NOT NULL,
    `enable_flag` tinyint(1) NOT NULL COMMENT '是否启用',
    `version`     int                                                           NOT NULL COMMENT '乐观锁',
    `del_flag`    tinyint(1) NOT NULL COMMENT '是否逻辑删除',
    `remark`      varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '备注',
    `file_id`     bigint                                                        NOT NULL COMMENT '文件主键 id',
    `user_id`     bigint                                                        NOT NULL COMMENT '此权限拥有者的 userId',
    `read_flag`   tinyint(1) NOT NULL COMMENT '是否可读：0 否 1 是',
    `write_flag`  tinyint(1) NOT NULL COMMENT '是否可写：0 否 1 是',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = 'v20230301：子表：文件操作权限，主表：文件'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_file_auth
-- ----------------------------

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`
(
    `id`          bigint                                                        NOT NULL,
    `tenant_id`   bigint                                                        NOT NULL COMMENT '租户 id',
    `create_id`   bigint                                                        NOT NULL,
    `create_time` datetime                                                      NOT NULL,
    `update_id`   bigint                                                        NOT NULL,
    `update_time` datetime                                                      NOT NULL,
    `enable_flag` tinyint(1) NOT NULL COMMENT '是否启用',
    `version`     int                                                           NOT NULL COMMENT '乐观锁',
    `del_flag`    tinyint(1) NOT NULL COMMENT '是否逻辑删除',
    `remark`      varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '备注',
    `name`        varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单名',
    `path`        varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '页面的 path，备注：相同父菜单下，子菜单 path不能重复',
    `icon`        varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图标',
    `parent_id`   bigint                                                        NOT NULL COMMENT '父节点id（顶级则为0）',
    `auths`       varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限，多个可用逗号拼接，例如：menu:insertOrUpdate,menu:page,menu:deleteByIdSet,menu:infoById',
    `show_flag`   tinyint(1) NOT NULL COMMENT '是否显示在 左侧的菜单栏里面，如果为 false，也可以通过 $router.push()访问到',
    `link_flag`   tinyint(1) NOT NULL COMMENT '是否外链，即，打开页面会在一个新的窗口打开，可以配合 router',
    `router`      varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '路由',
    `redirect`    varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '重定向，优先级最高',
    `order_no`    int                                                           NOT NULL COMMENT '排序号（值越大越前面，默认为 0）',
    `first_flag`  tinyint(1) NOT NULL COMMENT '是否是起始页面，备注：只能存在一个 firstFlag === true 的菜单',
    `auth_flag`   tinyint(1) NOT NULL COMMENT '是否是权限菜单，权限菜单：不显示，只代表菜单权限',
    `uuid`        varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '该菜单的 uuid，用于：同步租户菜单等操作，备注：不允许修改',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = 'v20230301：主表：菜单'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (3, 0, 1, '2021-12-22 10:53:32', 0, '2023-05-23 22:18:42', 1, 2, 0, '', '系统管理', '',
        'SettingOutlined',
        221004205817000054, '', 1, 0, '', '', 9900, 0, 0, 'e2e208c7578547f48b21bc51690535ff');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (4, 0, 1, '2021-12-20 11:10:15', 0, '2023-06-01 14:07:47', 1, 2, 0, '', '菜单管理',
        '/admin/sys/menu', '', 3, '',
        1,
        0, 'sysMenuMenu', '', 10000, 0, 0, '7c881482370d49b182e50909b248ca74');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (5, 0, 1, '2022-05-21 01:00:18', 1, '2022-05-21 01:00:18', 1, 2, 0, '', '新增修改', '', '',
        4,
        'sysMenu:insertOrUpdate', 0, 0, '', '', 10000, 0, 1, 'c270837361cd434a8d645500ec069999');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (6, 0, 1, '2022-05-21 01:00:18', 1, '2022-05-21 01:00:18', 1, 2, 0, '', '列表查询', '', '',
        4, 'sysMenu:page', 0,
        0,
        '', '', 9990, 0, 1, 'dc7021d578894376bd2d4aa0fca7f969');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (7, 0, 1, '2022-05-21 01:00:19', 1, '2022-05-21 01:00:19', 1, 2, 0, '', '删除', '', '', 4,
        'sysMenu:deleteByIdSet',
        0, 0, '', '', 9980, 0, 1, '9116fbdc0c164ef3a443003814bb95fe');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (8, 0, 1, '2022-05-21 01:00:19', 1, '2022-05-21 01:00:19', 1, 2, 0, '', '查看详情', '', '',
        4,
        'sysMenu:infoById', 0,
        0, '', '', 9970, 0, 1, 'd00e87e7090b45f1abac8a3afa2bc941');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (9, 0, 1, '2021-12-22 11:09:35', 0, '2022-07-18 02:56:28', 1, 2, 0, '', '角色管理',
        '/admin/sys/role', '', 3, '',
        1,
        0, 'sysRoleRole', '', 9990, 0, 0, 'a17458ccd5374a0baf5dacfb66dee6b8');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (10, 0, 1, '2022-05-21 01:03:25', 1, '2022-05-21 01:03:25', 1, 2, 0, '', '新增修改', '', '',
        9,
        'sysRole:insertOrUpdate', 0, 0, '', '', 10000, 0, 1, '1638ccae8f7d471ab006a245166f8b99');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (11, 0, 1, '2022-05-21 01:03:26', 1, '2022-05-21 01:03:26', 1, 2, 0, '', '列表查询', '', '',
        9, 'sysRole:page',
        0, 0,
        '', '', 9990, 0, 1, '01fade35763c4ed0827a9b52e1540e8c');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (12, 0, 1, '2022-05-21 01:03:26', 1, '2022-05-21 01:03:26', 1, 2, 0, '', '删除', '', '', 9,
        'sysRole:deleteByIdSet', 0, 0, '', '', 9980, 0, 1, 'c477053a4fb049fc809a819b7e84e7e3');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (13, 0, 1, '2022-05-21 01:03:26', 1, '2022-05-21 01:03:26', 1, 2, 0, '', '查看详情', '', '',
        9,
        'sysRole:infoById',
        0, 0, '', '', 9970, 0, 1, 'a17e3709381346ffbb6c6d614a1d36d2');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (14, 0, 1, '2021-12-22 11:09:16', 0, '2023-06-27 15:41:02', 1, 2, 0, '', '用户管理',
        '/admin/sys/user', '', 3,
        '', 1,
        0, 'sysUserUser', '', 9980, 1, 0, 'e00a139651a44fa4a4ae5235a8918fff');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (15, 0, 1, '2022-05-21 01:03:16', 1, '2022-05-21 01:03:16', 1, 2, 0, '', '新增修改', '', '',
        14,
        'sysUser:insertOrUpdate', 0, 0, '', '', 10000, 0, 1, 'd310fd26607b4fba9f74c9e7a6731401');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (16, 0, 1, '2022-05-21 01:03:17', 1, '2022-05-21 01:03:17', 1, 2, 0, '', '列表查询', '', '',
        14, 'sysUser:page',
        0,
        0, '', '', 9990, 0, 1, '0d93bb8ebf564b6d907e520fad121990');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (17, 0, 1, '2022-05-21 01:03:17', 1, '2022-05-21 01:03:17', 1, 2, 0, '', '删除', '', '', 14,
        'sysUser:deleteByIdSet', 0, 0, '', '', 9980, 0, 1, '8d020eb2d1a84d198e4e38d14ef78096');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (18, 0, 1, '2022-05-21 01:03:17', 1, '2022-05-21 01:03:17', 1, 2, 0, '', '查看详情', '', '',
        14,
        'sysUser:infoById',
        0, 0, '', '', 9970, 0, 1, '977681cc18844823bd9e1ba0730b8ebe');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (19, 0, 1, '2021-12-22 11:09:53', 1, '2022-07-03 14:12:10', 1, 2, 0, '', '字典管理',
        '/admin/sys/dict', '', 3,
        '', 1,
        0, 'sysDictDict', '', 9970, 0, 0, '29362e6a7c244927b14b90d9773a9da9');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (20, 0, 1, '2022-05-21 01:03:32', 1, '2022-05-21 01:03:32', 1, 2, 0, '', '新增修改', '', '',
        19,
        'sysDict:insertOrUpdate', 0, 0, '', '', 10000, 0, 1, '31fda4fd348e4f8fbb4420d32c06d303');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (21, 0, 1, '2022-05-21 01:03:32', 1, '2022-05-21 01:03:32', 1, 2, 0, '', '列表查询', '', '',
        19, 'sysDict:page',
        0,
        0, '', '', 9990, 0, 1, '2b7badefb6904f6db2eebea40385bf45');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (22, 0, 1, '2022-05-21 01:03:32', 1, '2022-05-21 01:03:32', 1, 2, 0, '', '删除', '', '', 19,
        'sysDict:deleteByIdSet', 0, 0, '', '', 9980, 0, 1, 'ca1d0c485c58468ebfecf0e7a6e57c77');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (23, 0, 1, '2022-05-21 01:03:32', 1, '2022-05-21 01:03:32', 1, 2, 0, '', '查看详情', '', '',
        19,
        'sysDict:infoById',
        0, 0, '', '', 9970, 0, 1, '92191c72bee6497aaf77502fbcf051a1');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (24, 0, 1, '2021-12-22 11:10:31', 0, '2023-09-01 11:33:42', 1, 2, 0, '', '参数管理',
        '/admin/sys/param', '', 3,
        '',
        1, 0, 'sysParamParam', '', 9960, 0, 0, 'e802321ab4804f749b69705d60af19f2');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (25, 0, 1, '2022-05-21 01:03:57', 1, '2022-05-21 01:03:57', 1, 2, 0, '', '新增修改', '', '',
        24,
        'sysParam:insertOrUpdate', 0, 0, '', '', 10000, 0, 1, '510ec0d7e07d4f45b4edce278a9dd7ee');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (26, 0, 1, '2022-05-21 01:03:57', 1, '2022-05-21 01:03:57', 1, 2, 0, '', '列表查询', '', '',
        24, 'sysParam:page',
        0,
        0, '', '', 9990, 0, 1, 'b3990737b39147d9b56436a11b078f8d');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (27, 0, 1, '2022-05-21 01:03:57', 1, '2022-05-21 01:03:57', 1, 2, 0, '', '删除', '', '', 24,
        'sysParam:deleteByIdSet', 0, 0, '', '', 9980, 0, 1, 'c0fb13728a524687aeacb36285a4a7d4');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (28, 0, 1, '2022-05-21 01:03:57', 1, '2022-05-21 01:03:57', 1, 2, 0, '', '查看详情', '', '',
        24,
        'sysParam:infoById',
        0, 0, '', '', 9970, 0, 1, 'ba76e238e98a4b7ab1dcaba0c70b8172');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (55, 0, 1, '2021-12-24 11:37:27', 0, '2022-10-05 13:23:04', 1, 2, 0, '', '个人管理', '', '',
        0, '', 0, 0, '', '',
        0,
        0, 0, '05ba09064c9047a5b1f13e4b4fb92d37');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (56, 0, 1, '2021-12-24 11:37:42', 0, '2022-10-05 13:24:13', 1, 2, 0, '', '个人中心',
        '/admin/user/self', '', 55,
        '',
        0, 0, 'userSelfSelf', '', 10000, 0, 0, '4f12c3aedbd54e18a17e3c1d38e7ef15');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (63, 0, 1, '2022-07-12 11:01:41', 1, '2022-07-12 11:02:01', 1, 2, 0, '', 'Helper App',
        'https://cmc0.github.io',
        'CodeOutlined', 221004205817000054, '', 1, 1, '', '', 0, 0, 0,
        'e11f796ac6be411daf824b86c902b48c');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (221004205817000054, 0, 0, '2022-10-04 12:58:17', 0, '2023-06-27 15:40:23', 1, 2, 0, '',
        '系统平台', '',
        'SettingOutlined', 0, '', 1, 0, '', '/admin/sys/user', 10000, 0, 0,
        '65836dcd36c04a59932d0318543766f9');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (221005155605000086, 0, 0, '2022-10-05 07:56:05', 0, '2023-07-11 12:04:49', 1, 2, 0, '',
        '欢迎',
        '/admin/welcome',
        'SmileOutlined', 0, '', 1, 0, 'WelcomeWelcome', '', 11000, 0, 0,
        '06e78e05c2494fa180f3d0780a75e60f');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (230523221709113901, 0, 0, '2023-05-23 22:17:10', 0, '2023-07-30 14:05:05', 1, 0, 0, '',
        '系统监控', '',
        'FundProjectionScreenOutlined', 221004205817000054, '', 1, 0, '', '', 1000, 0, 0,
        'e1746afe9ef040cfb268f3daecd68d0c');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (230523221904113916, 0, 0, '2023-05-23 22:19:05', 0, '2023-06-02 09:47:12', 1, 0, 0, '',
        '请求监控',
        '/admin/sys/request', '', 230523221709113901, '', 1, 0, 'sysRequestRequest', '', 10000, 0,
        0,
        '849a8b49827c4bf29fabb15694be23dc');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (230523222026113937, 0, 0, '2023-05-23 22:20:27', 0, '2023-05-23 22:20:27', 1, 0, 0, '',
        '列表查询', '', '',
        230523221904113916, 'sysRequest:page', 0, 0, '', '', 0, 0, 1,
        '5ff1feceb0bc4dc8956ff17bc3d22b60');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (230620155859123966, 0, 0, '2023-06-20 15:58:59', 0, '2023-06-20 16:00:52', 1, 0, 0, '',
        '组织管理', '',
        'ApartmentOutlined', 221004205817000054, '', 1, 0, '', '', 9800, 0, 0,
        '5edd19a7f68149b4b4b4ff1ee5e13e41');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (230620155944123975, 0, 0, '2023-06-20 15:59:45', 0, '2023-06-20 16:01:26', 1, 0, 0, '',
        '区域管理',
        '/admin/sys/area', '', 230620155859123966, '', 1, 0, 'sysAreaArea', '', 10000, 0, 0,
        '4467f211593c4a05ad5bca4f2c9661ac');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (230620160002123980, 0, 0, '2023-06-20 16:00:02', 0, '2023-06-20 16:01:37', 1, 0, 0, '',
        '部门管理',
        '/admin/sys/dept', '', 230620155859123966, '', 1, 0, 'sysDeptDept', '', 9900, 0, 0,
        '1c4c466f900f42a3bd8522a51c2b7e1a');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (230620160014123985, 0, 0, '2023-06-20 16:00:14', 0, '2023-06-20 16:01:48', 1, 0, 0, '',
        '岗位管理',
        '/admin/sys/post', '', 230620155859123966, '', 1, 0, 'sysPostPost', '', 9800, 0, 0,
        '65275d07b60e44e78c594d906d6e4a76');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (230620160155124026, 0, 0, '2023-06-20 16:01:56', 0, '2023-06-20 16:01:56', 1, 0, 0, '',
        '新增修改', '', '',
        230620155944123975, 'sysArea:insertOrUpdate', 0, 0, '', '', 10000, 0, 1,
        '203b615cd66e4598b6f5978b25b6a83e');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (230620160156124028, 0, 0, '2023-06-20 16:01:57', 0, '2023-06-20 16:01:57', 1, 0, 0, '',
        '列表查询', '', '',
        230620155944123975, 'sysArea:page', 0, 0, '', '', 9900, 0, 1,
        '04dc8059c9f947fa9b20a4300ac7088b');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (230620160156124029, 0, 0, '2023-06-20 16:01:57', 0, '2023-06-20 16:01:57', 1, 0, 0, '',
        '删除', '', '',
        230620155944123975, 'sysArea:deleteByIdSet', 0, 0, '', '', 9800, 0, 1,
        '02c59fade32e4795b745161223629aa1');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (230620160157124030, 0, 0, '2023-06-20 16:01:57', 0, '2023-06-20 16:01:57', 1, 0, 0, '',
        '查看详情', '', '',
        230620155944123975, 'sysArea:infoById', 0, 0, '', '', 9700, 0, 1,
        '15a294d1dc8e471689f9d0e96672f930');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (230620160215124035, 0, 0, '2023-06-20 16:02:15', 0, '2023-06-20 16:02:15', 1, 0, 0, '',
        '新增修改', '', '',
        230620160002123980, 'sysDept:insertOrUpdate', 0, 0, '', '', 10000, 0, 1,
        '50c850679e2c4419b6dc703d71f7ad43');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (230620160215124036, 0, 0, '2023-06-20 16:02:16', 0, '2023-06-20 16:02:16', 1, 0, 0, '',
        '列表查询', '', '',
        230620160002123980, 'sysDept:page', 0, 0, '', '', 9900, 0, 1,
        '4a1895d57c4c4734bb09e5c032351206');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (230620160216124037, 0, 0, '2023-06-20 16:02:16', 0, '2023-06-20 16:02:16', 1, 0, 0, '',
        '删除', '', '',
        230620160002123980, 'sysDept:deleteByIdSet', 0, 0, '', '', 9800, 0, 1,
        'cfe40ce03a7840d8b2f736883f2893fc');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (230620160216124040, 0, 0, '2023-06-20 16:02:17', 0, '2023-06-20 16:02:17', 1, 0, 0, '',
        '查看详情', '', '',
        230620160002123980, 'sysDept:infoById', 0, 0, '', '', 9700, 0, 1,
        '3fdbfe4d02b9404aa0798eadf792c228');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (230620160233124044, 0, 0, '2023-06-20 16:02:33', 0, '2023-06-20 16:02:33', 1, 0, 0, '',
        '新增修改', '', '',
        230620160014123985, 'sysPost:insertOrUpdate', 0, 0, '', '', 10000, 0, 1,
        '8ac374791d954ada89d8cc3f4013652e');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (230620160233124045, 0, 0, '2023-06-20 16:02:34', 0, '2023-06-20 16:02:34', 1, 0, 0, '',
        '列表查询', '', '',
        230620160014123985, 'sysPost:page', 0, 0, '', '', 9900, 0, 1,
        'ab1f1a3114e14dde82b8c25bcef10f06');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (230620160234124046, 0, 0, '2023-06-20 16:02:34', 0, '2023-06-20 16:02:34', 1, 0, 0, '',
        '删除', '', '',
        230620160014123985, 'sysPost:deleteByIdSet', 0, 0, '', '', 9800, 0, 1,
        'e2f6fa7820514766836b417510187e26');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (230620160234124047, 0, 0, '2023-06-20 16:02:35', 0, '2023-06-20 16:02:35', 1, 0, 0, '',
        '查看详情', '', '',
        230620160014123985, 'sysPost:infoById', 0, 0, '', '', 9700, 0, 1,
        '4bf37d5747df4f1a9d8adb490ccd7ce6');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (230728151103147771, 0, 0, '2023-07-28 15:11:04', 0, '2023-07-28 16:15:13', 1, 0, 0, '',
        '连接管理', '',
        'ApiOutlined', 221004205817000054, '', 1, 0, '', '', 9600, 0, 0,
        'cee12ae9d73b4d2a8e8e0490d9706838');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (230728151435147782, 0, 0, '2023-07-28 15:14:36', 0, '2023-07-28 15:39:06', 1, 0, 0, '',
        '连接服务',
        '/admin/sys/socket', '', 230728151103147771, '', 1, 0, 'sysSocketSocket', '', 10000, 0, 0,
        '25a4065ec5344a5e8b671ba78a9bf631');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (230728151554147791, 0, 0, '2023-07-28 15:15:54', 0, '2023-07-28 15:15:54', 1, 0, 0, '',
        '在线用户',
        '/admin/sys/socketRefUser', '', 230728151103147771, '', 1, 0,
        'sysSocketRefUserSocketRefUser', '', 9900, 0, 0,
        '0eb5961ca18f47058c889906a6f1e0a7');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (230810002442152333, 0, 0, '2023-08-10 00:24:43', 0, '2023-08-10 00:24:43', 1, 0, 0, '',
        '新增修改', '', '',
        230728151435147782, 'sysSocket:insertOrUpdate', 0, 0, '', '', 10000, 0, 1,
        '535a25615f9e44249c84068849017cc3');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (230810002442152334, 0, 0, '2023-08-10 00:24:43', 0, '2023-08-10 00:24:43', 1, 0, 0, '',
        '列表查询', '', '',
        230728151435147782, 'sysSocket:page', 0, 0, '', '', 9900, 0, 1,
        '8df7909eb49e4bb792eef753297fed2a');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (230810002442152335, 0, 0, '2023-08-10 00:24:43', 0, '2023-08-10 00:24:43', 1, 0, 0, '',
        '删除', '', '',
        230728151435147782, 'sysSocket:deleteByIdSet', 0, 0, '', '', 9800, 0, 1,
        'dd3a6d0aaed84bada06b9c013b1fcf11');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (230810002442152336, 0, 0, '2023-08-10 00:24:43', 0, '2023-08-10 00:24:43', 1, 0, 0, '',
        '查看详情', '', '',
        230728151435147782, 'sysSocket:infoById', 0, 0, '', '', 9700, 0, 1,
        'b077dd0008c44306823c19c423c7831e');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (230810002513152343, 0, 0, '2023-08-10 00:25:14', 0, '2023-08-10 00:25:14', 1, 0, 0, '',
        '新增修改', '', '',
        230728151554147791, 'sysSocketRefUser:insertOrUpdate', 0, 0, '', '', 10000, 0, 1,
        '425ed98c1c4b4e0fbca62599f50da564');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (230810002513152344, 0, 0, '2023-08-10 00:25:14', 0, '2023-08-10 00:25:14', 1, 0, 0, '',
        '列表查询', '', '',
        230728151554147791, 'sysSocketRefUser:page', 0, 0, '', '', 9900, 0, 1,
        '9faf7606b50f4850a01af28bae98a121');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (230810002513152345, 0, 0, '2023-08-10 00:25:14', 0, '2023-08-10 00:25:14', 1, 0, 0, '',
        '删除', '', '',
        230728151554147791, 'sysSocketRefUser:deleteByIdSet', 0, 0, '', '', 9800, 0, 1,
        'ce99a0638bc246119fb60263818de514');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (230810002513152346, 0, 0, '2023-08-10 00:25:14', 0, '2023-08-10 00:25:14', 1, 0, 0, '',
        '查看详情', '', '',
        230728151554147791, 'sysSocketRefUser:infoById', 0, 0, '', '', 9700, 0, 1,
        '6ec8fb97eab3434e9f6dec6880e0f608');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (230825160158155313, 0, 0, '2023-08-25 16:01:59', 0, '2023-08-28 16:37:10', 1, 0, 0, '',
        '租户管理',
        '/admin/sys/tenant', '', 230620155859123966, '', 1, 0, 'sysTenantTenant', '', 9700, 0, 0,
        'fca446313e524b2fba8a2d05efc3b4d2');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (230825160232155328, 0, 0, '2023-08-25 16:02:33', 0, '2023-08-25 16:02:33', 1, 0, 0, '',
        '新增修改', '', '',
        230825160158155313, 'sysTenant:insertOrUpdate', 0, 0, '', '', 10000, 0, 1,
        '0d8a6ed8186944478482907f3886aa22');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (230825160232155329, 0, 0, '2023-08-25 16:02:33', 0, '2023-08-25 16:02:33', 1, 0, 0, '',
        '列表查询', '', '',
        230825160158155313, 'sysTenant:page', 0, 0, '', '', 9900, 0, 1,
        '2cc3d7369c074530b59d681d01ce43a6');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (230825160232155330, 0, 0, '2023-08-25 16:02:33', 0, '2023-08-25 16:02:33', 1, 0, 0, '',
        '删除', '', '',
        230825160158155313, 'sysTenant:deleteByIdSet', 0, 0, '', '', 9800, 0, 1,
        'b9825506337242a0b3869ece8dba6ae9');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (230825160233155331, 0, 0, '2023-08-25 16:02:33', 0, '2023-08-25 16:02:33', 1, 0, 0, '',
        '查看详情', '', '',
        230825160158155313, 'sysTenant:infoById', 0, 0, '', '', 9700, 0, 1,
        '9c89c4e07f6946b7992ed2b7d386cded');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (230829100731158505, 0, 0, '2023-08-29 10:07:32', 0, '2023-08-29 10:07:32', 1, 0, 0, '',
        '下拉列表', '', '', 14,
        'sysUser:dictList', 0, 0, '', '', 9900, 0, 1, '68ba2133b1f74fb497deeea970d69917');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (230829100759158511, 0, 0, '2023-08-29 10:07:59', 0, '2023-08-29 10:07:59', 1, 0, 0, '',
        '下拉列表', '', '',
        230825160158155313, 'sysTenant:dictList', 0, 0, '', '', 9600, 0, 1,
        '06f50499ada146c697867f3c49e0abd1');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (230905190104004364, 0, 0, '2023-09-05 19:01:05', 0, '2023-09-06 09:46:20', 1, 0, 0, '',
        '新增菜单', '', '',
        230825160158155313, 'sysTenant:syncMenu', 0, 0, '', '', 9500, 0, 1,
        'c4cabde48fd64960997d311b6a822834');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (230906094600005368, 0, 0, '2023-09-06 09:46:01', 0, '2023-09-06 09:46:33', 1, 0, 0, '',
        '新增字典', '', '',
        230825160158155313, 'sysTenant:syncDict', 0, 0, '', '', 9400, 0, 1,
        'd0bcfce4d7d245e2b624f91517a3350c');
INSERT INTO `sys_menu` (`id`, `tenant_id`, `create_id`, `create_time`, `update_id`, `update_time`,
                        `enable_flag`,
                        `version`, `del_flag`, `remark`, `name`, `path`, `icon`, `parent_id`,
                        `auths`, `show_flag`,
                        `link_flag`, `router`, `redirect`, `order_no`, `first_flag`, `auth_flag`,
                        `uuid`)
VALUES (230906094702005387, 0, 0, '2023-09-06 09:47:02', 0, '2023-09-06 09:47:02', 1, 0, 0, '',
        '新增参数', '', '',
        230825160158155313, 'sysTenant:syncParam', 0, 0, '', '', 9300, 0, 1,
        'c7b3853197924276aaac7428d7d4eb86');

-- ----------------------------
-- Table structure for sys_param
-- ----------------------------
DROP TABLE IF EXISTS `sys_param`;
CREATE TABLE `sys_param`
(
    `id`          bigint                                                        NOT NULL,
    `tenant_id`   bigint                                                        NOT NULL COMMENT '租户 id',
    `create_id`   bigint                                                        NOT NULL,
    `create_time` datetime                                                      NOT NULL,
    `update_id`   bigint                                                        NOT NULL,
    `update_time` datetime                                                      NOT NULL,
    `enable_flag` tinyint(1) NOT NULL COMMENT '是否启用',
    `version`     int                                                           NOT NULL COMMENT '乐观锁',
    `del_flag`    tinyint(1) NOT NULL COMMENT '是否逻辑删除',
    `remark`      varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '备注',
    `name`        varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置名，以 uuid为不变值进行使用，不要用此属性',
    `value`       text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '值',
    `uuid`        varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '该参数的 uuid，用于：同步租户参数等操作，备注：不允许修改，并且系统内置参数的 uuid等于 id',
    `system_flag` tinyint(1) NOT NULL COMMENT '系统内置：是 强制同步给租户 否 不同步给租户',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = 'v20230301：主表：系统参数'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_param
-- ----------------------------
INSERT INTO `sys_param`
VALUES (1, 0, 1, '2021-12-26 11:32:38', 1, '2022-01-11 21:03:30', 1, 0, 0,
        '获取私钥方法：new RSA().getPrivateKeyBase64()，备注：获取公钥同理，但是必须使用同一个对象，因为必须成对',
        '非对称加密，私钥',
        'MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBANp2ZoJp983rckLlCwwr/6V7m+gIHgPM6xX3E4FmAYnKqeLOH39RwWQihFp7mt5t1ZS2c87gJOB0lu5K+79O0gg+P0N7n/CImKUUBZ3VV7uoZrHFPGpbAU2nun8UuvRikx4GM7KV2z0bIDVyeMiqu411i/6pFmex4TRKrZYRm6z1AgMBAAECgYAMjU//f0IvMS97+3gCh4alRfBjyQ+cbUo2lV8oCKne9meDcg9qO9LOQ5NyNXbk/8+NP1xxDvzfbqN7ZpCHYep8VoxJYMqr15czK9Sk34A5AdpOb5kQhUAgfyaQlIu+2s3NSjyJUXcNqLRRb0xiGhoJmH1V9zGSVFaJnGsUJuZAkwJBAPl/4VNtvcWTyoGBHFlJjto4V1lYkD63qKh66evXiI7PEQhGB4b8ubBFnEJephWQO/tWo6AYFipMrtjJ1z+KqWsCQQDgJ4IsRZleS5vr5bYhL5+YE8BN8TyzyJ/7MvSjV6ZB7Qoq+w7CSsWm4wTnO9zdSuJaXJ7QmMfDR9Y/tAx2MLsfAkBkdJOxtqbI7VeEywox/QbyX+rzg1AYoHPc2hhjJ9XIwiB2d1PCivDswypGIru2ROuRp/GbnPcXsuZXTPVIlTjfAkEAkhtukCj1pS8nfQYIR21hW6FUMfnSlWVqUjSOnYHeTw6RGB75Kc/PMc68PXUZq+zJyhihNFrBqxpCHtffX5K4BQJAIIs70dCXBmZ1AjWUTgRY1piEGtwoxPO229guHvF6P8IOyxCuFWrCgY/1UnZt3Yc/XubImBb/xQx5CcdFWYcgjw==',
        '1', 1);
INSERT INTO `sys_param`
VALUES (2, 0, 1, '2021-12-26 11:32:38', 0, '2023-09-01 18:08:56', 1, 0, 0,
        '多少秒钟，一个 ip可以请求多少次，用冒号隔开的，任意值小于等于 0，则不会进行检查，如果超过了请求次数，则一天无法访问任何接口',
        'ip请求速率', '10:75', '2', 1);
INSERT INTO `sys_param`
VALUES (3, 0, 0, '2023-09-06 16:51:08', 0, '2023-09-06 17:37:53', 1, 0, 0,
        '1：用户会自动关联子级租户\n0（默认）：用户不会自动关联子级租户，如果需要关联租户，则只能手动关联租户',
        '是否关联子级租户', '1', '3', 0);

-- ----------------------------
-- Table structure for sys_pay
-- ----------------------------
DROP TABLE IF EXISTS `sys_pay`;
CREATE TABLE `sys_pay`
(
    `id`               bigint                                                        NOT NULL,
    `tenant_id`        bigint                                                        NOT NULL COMMENT '租户 id',
    `create_id`        bigint                                                        NOT NULL,
    `create_time`      datetime                                                      NOT NULL,
    `update_id`        bigint                                                        NOT NULL,
    `update_time`      datetime                                                      NOT NULL,
    `enable_flag` tinyint(1) NOT NULL COMMENT '是否启用',
    `version`          int                                                           NOT NULL COMMENT '乐观锁',
    `del_flag`    tinyint(1) NOT NULL COMMENT '是否逻辑删除',
    `remark`           varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '描述/备注',
    `pay_type`         int                                                           NOT NULL COMMENT '支付方式',
    `user_id`          bigint                                                        NOT NULL COMMENT '用户主键 id',
    `subject`          varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单名称',
    `body`             varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品描述',
    `origin_price`     decimal(13, 3)                                                NOT NULL COMMENT '订单原始的钱',
    `pay_price`        decimal(13, 3)                                                NOT NULL COMMENT '订单支付的钱',
    `pay_currency`     varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '订单支付的钱的单位，例如：人民币 CNY',
    `expire_time`      datetime                                                      NOT NULL COMMENT '订单过期时间',
    `open_id`          varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户 openId',
    `status`           int                                                           NOT NULL COMMENT '订单状态',
    `trade_no`         varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单号',
    `pay_return_value` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '支付返回的参数',
    `ref_type`         int                                                           NOT NULL COMMENT '关联的类型',
    `ref_id`           bigint                                                        NOT NULL COMMENT '关联的 id',
    `package_name`     varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'app包名，必须是创建登录 api项目时，创建 android客户端 id使用包名，例如：谷歌支付',
    `product_id`       varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '对应购买商品的商品 id，例如：谷歌支付',
    `token`            varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '购买成功后 Purchase对象的 getPurchaseToken()，例如：谷歌支付',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = 'v20230301：主表：支付'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_pay
-- ----------------------------

-- ----------------------------
-- Table structure for sys_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post`
(
    `id`          bigint                                                        NOT NULL,
    `tenant_id`   bigint                                                        NOT NULL COMMENT '租户 id',
    `create_id`   bigint                                                        NOT NULL,
    `create_time` datetime                                                      NOT NULL,
    `update_id`   bigint                                                        NOT NULL,
    `update_time` datetime                                                      NOT NULL,
    `enable_flag` tinyint(1) NOT NULL COMMENT '是否启用',
    `version`     int                                                           NOT NULL COMMENT '乐观锁',
    `del_flag`    tinyint(1) NOT NULL COMMENT '是否逻辑删除',
    `remark`      varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '备注',
    `name`        varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '岗位名',
    `parent_id`   bigint                                                        NOT NULL COMMENT '父节点id（顶级则为0）',
    `order_no`    int                                                           NOT NULL COMMENT '排序号（值越大越前面，默认为 0）',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = 'v20230301：主表：岗位'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_post
-- ----------------------------

-- ----------------------------
-- Table structure for sys_post_ref_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_post_ref_user`;
CREATE TABLE `sys_post_ref_user`
(
    `tenant_id` bigint NOT NULL COMMENT '租户 id',
    `post_id`   bigint NOT NULL COMMENT '岗位主键 id',
    `user_id`   bigint NOT NULL COMMENT '用户主键 id',
    PRIMARY KEY (`post_id`, `user_id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = 'v20230301：关联表：岗位，用户'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_post_ref_user
-- ----------------------------

-- ----------------------------
-- Table structure for sys_request
-- ----------------------------
DROP TABLE IF EXISTS `sys_request`;
CREATE TABLE `sys_request`
(
    `id`             bigint                                                         NOT NULL,
    `tenant_id`      bigint                                                         NOT NULL COMMENT '租户 id',
    `create_id`      bigint                                                         NOT NULL,
    `create_time`    datetime                                                       NOT NULL,
    `update_id`      bigint                                                         NOT NULL,
    `update_time`    datetime                                                       NOT NULL,
    `enable_flag`  tinyint(1) NOT NULL COMMENT '是否启用',
    `version`        int                                                            NOT NULL COMMENT '乐观锁',
    `del_flag`     tinyint(1) NOT NULL COMMENT '是否逻辑删除',
    `remark`         varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '备注',
    `uri`            varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '请求的uri',
    `name`           varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '接口名（备用）',
    `cost_ms_str`    varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '耗时（字符串）',
    `cost_ms`        bigint                                                         NOT NULL COMMENT '耗时（毫秒）',
    `category`       int                                                            NOT NULL COMMENT '请求类别',
    `ip`             varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT 'ip',
    `region`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT 'Ip2RegionUtil.getRegion() 获取到的 ip所处区域',
    `success_flag` tinyint(1) NOT NULL COMMENT '请求是否成功',
    `error_msg`      varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '失败信息',
    `request_param`  varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '请求的参数',
    `type`           varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci   NOT NULL COMMENT '请求类型',
    `response_value` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '请求返回的值',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = 'v20230301：主表：请求'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_request
-- ----------------------------

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`
(
    `id`           bigint                                                        NOT NULL,
    `tenant_id`    bigint                                                        NOT NULL COMMENT '租户 id',
    `create_id`    bigint                                                        NOT NULL,
    `create_time`  datetime                                                      NOT NULL,
    `update_id`    bigint                                                        NOT NULL,
    `update_time`  datetime                                                      NOT NULL,
    `enable_flag`  tinyint(1) NOT NULL COMMENT '是否启用',
    `version`      int                                                           NOT NULL COMMENT '乐观锁',
    `del_flag`     tinyint(1) NOT NULL COMMENT '是否逻辑删除',
    `remark`       varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '备注',
    `name`         varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名（不能重复）',
    `default_flag` tinyint(1) NOT NULL COMMENT '是否是默认角色，备注：只会有一个默认角色',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = 'v20230301：主表：角色'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------

-- ----------------------------
-- Table structure for sys_role_ref_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_ref_menu`;
CREATE TABLE `sys_role_ref_menu`
(
    `tenant_id` bigint NOT NULL COMMENT '租户 id',
    `role_id`   bigint NOT NULL COMMENT '角色主键 id',
    `menu_id`   bigint NOT NULL COMMENT '菜单主键 id',
    PRIMARY KEY (`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = 'v20230301：关联表：角色，菜单'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_ref_menu
-- ----------------------------

-- ----------------------------
-- Table structure for sys_role_ref_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_ref_user`;
CREATE TABLE `sys_role_ref_user`
(
    `tenant_id` bigint NOT NULL COMMENT '租户 id',
    `role_id`   bigint NOT NULL COMMENT '角色主键 id',
    `user_id`   bigint NOT NULL COMMENT '用户主键 id',
    PRIMARY KEY (`role_id`, `user_id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = 'v20230301：关联表：角色，用户'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_ref_user
-- ----------------------------

-- ----------------------------
-- Table structure for sys_socket
-- ----------------------------
DROP TABLE IF EXISTS `sys_socket`;
CREATE TABLE `sys_socket`
(
    `id`          bigint                                                        NOT NULL,
    `tenant_id`   bigint                                                        NOT NULL COMMENT '租户 id',
    `create_id`   bigint                                                        NOT NULL,
    `create_time` datetime                                                      NOT NULL,
    `update_id`   bigint                                                        NOT NULL,
    `update_time` datetime                                                      NOT NULL,
    `enable_flag` tinyint(1) NOT NULL COMMENT '是否启用',
    `version`     int                                                           NOT NULL COMMENT '乐观锁',
    `del_flag`    tinyint(1) NOT NULL COMMENT '是否逻辑删除',
    `remark`      varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '备注',
    `scheme`      varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '协议',
    `host`        varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '主机',
    `port`        int                                                           NOT NULL COMMENT '端口',
    `path`        varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '路径',
    `type`        int                                                           NOT NULL COMMENT 'socket类型：101 tcp 201 webSocket 301 udp',
    `mac_address` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT 'mac地址，用于：和 port一起判断是否是重复启动，如果是，则需要移除之前的 socket信息',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = 'v20230301：主表：socket'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_socket
-- ----------------------------

-- ----------------------------
-- Table structure for sys_socket_ref_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_socket_ref_user`;
CREATE TABLE `sys_socket_ref_user`
(
    `id`                  bigint                                                        NOT NULL,
    `tenant_id`           bigint                                                        NOT NULL COMMENT '租户 id',
    `create_id`           bigint                                                        NOT NULL,
    `create_time`         datetime                                                      NOT NULL,
    `update_id`           bigint                                                        NOT NULL,
    `update_time`         datetime                                                      NOT NULL,
    `enable_flag` tinyint(1) NOT NULL COMMENT '是否启用',
    `version`             int                                                           NOT NULL COMMENT '乐观锁',
    `del_flag`    tinyint(1) NOT NULL COMMENT '是否逻辑删除',
    `remark`              varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '备注',
    `user_id`             bigint                                                        NOT NULL COMMENT '用户主键 id',
    `socket_id`           bigint                                                        NOT NULL COMMENT 'socket主键 id',
    `nickname`            varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '冗余字段，用户昵称',
    `scheme`              varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '冗余字段，协议',
    `host`                varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '冗余字段，主机',
    `port`                int                                                           NOT NULL COMMENT '冗余字段，端口',
    `path`                varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '路径',
    `type`                int                                                           NOT NULL COMMENT '冗余字段，socket类型',
    `online_type`         int                                                           NOT NULL COMMENT 'socket 在线状态',
    `ip`                  varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'ip',
    `region`              varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Ip2RegionUtil.getRegion() 获取到的 ip所处区域',
    `category`            int                                                           NOT NULL COMMENT '请求类别',
    `jwt_hash`            varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'jwtHash',
    `jwt_hash_expire_ts`  bigint                                                        NOT NULL COMMENT 'jwtHash未来过期的时间戳',
    `user_agent_json_str` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'User-Agent信息对象，json字符串',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = 'v20230301：关联表：socket，用户'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_socket_ref_user
-- ----------------------------

-- ----------------------------
-- Table structure for sys_tenant
-- ----------------------------
DROP TABLE IF EXISTS `sys_tenant`;
CREATE TABLE `sys_tenant`
(
    `id`          bigint                                                        NOT NULL,
    `tenant_id`   bigint                                                        NOT NULL COMMENT '租户 id',
    `create_id`   bigint                                                        NOT NULL,
    `create_time` datetime                                                      NOT NULL,
    `update_id`   bigint                                                        NOT NULL,
    `update_time` datetime                                                      NOT NULL,
    `enable_flag` tinyint(1) NOT NULL COMMENT '是否启用',
    `version`     int                                                           NOT NULL COMMENT '乐观锁',
    `del_flag`    tinyint(1) NOT NULL COMMENT '是否逻辑删除',
    `remark`      varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '备注',
    `name`        varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '租户名',
    `parent_id`   bigint                                                        NOT NULL COMMENT '父节点id（顶级则为0）',
    `order_no`    int                                                           NOT NULL COMMENT '排序号（值越大越前面，默认为 0）',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = 'v20230301：主表：租户'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_tenant
-- ----------------------------

-- ----------------------------
-- Table structure for sys_tenant_ref_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_tenant_ref_user`;
CREATE TABLE `sys_tenant_ref_user`
(
    `tenant_id` bigint NOT NULL COMMENT '租户主键 id',
    `user_id`   bigint NOT NULL COMMENT '用户主键 id',
    PRIMARY KEY (`tenant_id`, `user_id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = 'v20230301：关联表：租户，用户'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_tenant_ref_user
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`
(
    `id`           bigint                                                        NOT NULL,
    `tenant_id`    bigint                                                        NOT NULL COMMENT '租户 id',
    `create_id`    bigint                                                        NOT NULL,
    `create_time`  datetime                                                      NOT NULL,
    `update_id`    bigint                                                        NOT NULL,
    `update_time`  datetime                                                      NOT NULL,
    `enable_flag` tinyint(1) NOT NULL COMMENT '正常/冻结',
    `version`      int                                                           NOT NULL COMMENT '乐观锁',
    `del_flag`    tinyint(1) NOT NULL COMMENT '是否注销',
    `remark`       varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '备注（暂时未使用）',
    `password`     varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码，可为空，如果为空，则登录时需要提示【进行忘记密码操作】',
    `email`        varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '邮箱，可以为空',
    `sign_in_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '登录名，可以为空',
    `phone`        varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '手机号，可以为空',
    `wx_open_id`   varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '微信 openId，可以为空',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = 'v20230301：主表：用户'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user_0
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_0`;
CREATE TABLE `sys_user_0`
(
    `id`           bigint                                                        NOT NULL,
    `tenant_id`    bigint                                                        NOT NULL COMMENT '租户 id',
    `create_id`    bigint                                                        NOT NULL,
    `create_time`  datetime                                                      NOT NULL,
    `update_id`    bigint                                                        NOT NULL,
    `update_time`  datetime                                                      NOT NULL,
    `enable_flag` tinyint(1) NOT NULL COMMENT '正常/冻结',
    `version`      int                                                           NOT NULL COMMENT '乐观锁',
    `del_flag`    tinyint(1) NOT NULL COMMENT '是否注销',
    `remark`       varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '备注（暂时未使用）',
    `password`     varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码，可为空，如果为空，则登录时需要提示【进行忘记密码操作】',
    `email`        varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '邮箱，可以为空',
    `sign_in_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '登录名，可以为空',
    `phone`        varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '手机号，可以为空',
    `wx_open_id`   varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '微信 openId，可以为空',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = 'v20230301：主表：用户'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_0
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user_1
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_1`;
CREATE TABLE `sys_user_1`
(
    `id`           bigint                                                        NOT NULL,
    `tenant_id`    bigint                                                        NOT NULL COMMENT '租户 id',
    `create_id`    bigint                                                        NOT NULL,
    `create_time`  datetime                                                      NOT NULL,
    `update_id`    bigint                                                        NOT NULL,
    `update_time`  datetime                                                      NOT NULL,
    `enable_flag` tinyint(1) NOT NULL COMMENT '正常/冻结',
    `version`      int                                                           NOT NULL COMMENT '乐观锁',
    `del_flag`    tinyint(1) NOT NULL COMMENT '是否注销',
    `remark`       varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '备注（暂时未使用）',
    `password`     varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码，可为空，如果为空，则登录时需要提示【进行忘记密码操作】',
    `email`        varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '邮箱，可以为空',
    `sign_in_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '登录名，可以为空',
    `phone`        varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '手机号，可以为空',
    `wx_open_id`   varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '微信 openId，可以为空',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = 'v20230301：主表：用户'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_1
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user_info
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_info`;
CREATE TABLE `sys_user_info`
(
    `id`             bigint                                                        NOT NULL COMMENT '用户主键 id',
    `tenant_id`      bigint                                                        NOT NULL COMMENT '租户 id',
    `uuid`           varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '该用户的 uuid，本系统使用 id，不使用此字段（uuid），备注：不允许修改',
    `nickname`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '昵称',
    `bio`            varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '个人简介',
    `avatar_file_id` bigint                                                        NOT NULL COMMENT '头像 fileId（文件主键 id）',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = 'v20230301：子表：用户基本信息，主表：用户'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_info
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user_info_0
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_info_0`;
CREATE TABLE `sys_user_info_0`
(
    `id`             bigint                                                        NOT NULL COMMENT '用户主键 id',
    `tenant_id`      bigint                                                        NOT NULL COMMENT '租户 id',
    `uuid`           varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '该用户的 uuid，本系统使用 id，不使用此字段（uuid），备注：不允许修改',
    `nickname`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '昵称',
    `bio`            varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '个人简介',
    `avatar_file_id` bigint                                                        NOT NULL COMMENT '头像 fileId（文件主键 id）',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = 'v20230301：子表：用户基本信息，主表：用户'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_info_0
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user_info_1
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_info_1`;
CREATE TABLE `sys_user_info_1`
(
    `id`             bigint                                                        NOT NULL COMMENT '用户主键 id',
    `tenant_id`      bigint                                                        NOT NULL COMMENT '租户 id',
    `uuid`           varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '该用户的 uuid，本系统使用 id，不使用此字段（uuid），备注：不允许修改',
    `nickname`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '昵称',
    `bio`            varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '个人简介',
    `avatar_file_id` bigint                                                        NOT NULL COMMENT '头像 fileId（文件主键 id）',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = 'v20230301：子表：用户基本信息，主表：用户'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_info_1
-- ----------------------------

SET
FOREIGN_KEY_CHECKS = 1;
