CREATE TABLE `user` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `username` varchar(256) DEFAULT NULL,
                        `user_account` varchar(256) DEFAULT NULL,
                        `avatar_url` varchar(1024) DEFAULT NULL,
                        `gender` tinyint DEFAULT NULL,
                        `user_password` varchar(512) NOT NULL,
                        `phone` varchar(128) DEFAULT NULL,
                        `email` varchar(512) DEFAULT NULL,
                        `user_status` int NOT NULL DEFAULT '0',
                        `create_time` datetime DEFAULT CURRENT_TIMESTAMP /*!80023 INVISIBLE */,
                        `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        `is_delete` tinyint DEFAULT '0',
                        `user_role` int NOT NULL DEFAULT '0' COMMENT '0:cmmom 1:manager',
                        `vox_code` varchar(512) DEFAULT NULL COMMENT '编号',
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `tag` (
                       `id` bigint NOT NULL AUTO_INCREMENT,
                       `tag_name` varchar(256) DEFAULT NULL,
                       `user_id` bigint DEFAULT NULL,
                       `parent_id` bigint DEFAULT NULL,
                       `is_parent` tinyint DEFAULT 0,
                       `create_time` datetime DEFAULT CURRENT_TIMESTAMP /*!80023 INVISIBLE */,
                       `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       `is_delete` tinyint DEFAULT '0',
                       PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

create table team
(
    id           bigint auto_increment comment 'id'
        primary key,
    name   varchar(256)                   not null comment '队伍名称',
    description varchar(1024)                      null comment '描述',
    max_num    int      default 1                 not null comment '最大人数',
    expire_time    datetime  null comment '过期时间',
    userId            bigint comment '用户id',
    status    int      default 0                 not null comment '0 - 公开，1 - 私有，2 - 加密',
    password varchar(512)                       null comment '密码',

    create_time   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    is_delete     tinyint  default 0                 not null comment '是否删除'
)
    comment '队伍';