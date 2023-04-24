-- ----------------------------
-- Table structure for sys_operate_log
-- ----------------------------
CREATE TABLE `sys_operate_log`
(
    `id`           bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `title`        varchar(300)        NULL     DEFAULT NULL COMMENT '日志标题',
    `server_ip`    varchar(20)         NULL     DEFAULT NULL COMMENT '服务器IP',
    `server_host`  varchar(50)         NULL     DEFAULT NULL COMMENT '服务器名',
    `remote_ip`    varchar(20)         NULL     DEFAULT NULL COMMENT '操作IP',
    `user_agent`   varchar(400)        NULL     DEFAULT NULL COMMENT '用户代理',
    `request_uri`  varchar(100)        NULL     DEFAULT NULL COMMENT '请求URI',
    `method`       varchar(10)         NULL     DEFAULT NULL COMMENT '请求方法',
    `method_class` varchar(100)        NULL     DEFAULT NULL COMMENT '方法类',
    `method_name`  varchar(50)         NULL     DEFAULT NULL COMMENT '方法名',
    `params`       text                NULL COMMENT '操作提交的数据',
    `time`         varchar(20)         NULL     DEFAULT NULL COMMENT '执行时长',
    `create_by`    varchar(64)         NULL     DEFAULT NULL COMMENT '创建人',
    `gmt_create`   datetime            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) COMMENT = '操作日志表';
