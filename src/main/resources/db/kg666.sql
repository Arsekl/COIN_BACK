# drop database if exists `kg666`;
# create database `kg666`;
# use kg666;
drop table if exists `default_layout`;
drop table if exists `link_layout`;
drop table if exists `node_layout`;
drop table if exists `user`;
drop table if exists `question`;
create table `user`
(
	`uid` BIGINT auto_increment,
    `name`	varchar(32) not null unique,
    `password` varchar(128) not null,
    primary key (`uid`)
) AUTO_INCREMENT=0 ENGINE=InnoDB DEFAULT CHARSET=utf8;
truncate table user;
insert into `user` values (1, 'admin', '!@#');
create table `node_layout`
(
    `id`             BIGINT         not null,
    `pic_name`       varchar(32)    not null,
    `uid`            BIGINT         not null default 0,
    primary key (`id`, `pic_name`, `uid`),
    `x`              numeric(10, 3) not null,
    `y`              numeric(10, 3) not null,
    `color`          varchar(16),
    `symbol`         varchar(16),
    `label_show`     boolean,
    `label_fontsize` numeric(10, 3),
    `tooltip_show`   boolean,
	index (`uid`),
	FOREIGN KEY (`uid`) REFERENCES user(`uid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
insert into `node_layout`
values (0, 'test', 1, 0.0, 1.0, 'red', 'square', false, 12.5, true);
create table `link_layout`
(
    `id`             BIGINT      not null,
    `pic_name`       varchar(32) not null,
    `uid`            BIGINT      not null default 0,
    primary key (`id`, `pic_name`, `uid`),
    `color`          varchar(16),
    `width`          numeric(10, 3),
    `type`           varchar(16),
    `curveness`      numeric(10, 3),
    `label_show`     boolean,
    `label_fontsize` numeric(3, 1),
    `tooltip_show`   boolean,
	index (`uid`),
	FOREIGN KEY (`uid`) REFERENCES user(`uid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
insert into `link_layout`
values (0, 'test', 1, 'red', 0.5, 'solid', 0.5, true, 12.5, false);
create table `default_layout`
(
    `pic_name`       varchar(32) not null,
    `uid`            BIGINT      not null default 0,
    primary key (`pic_name`, `uid`),
    `x`              numeric(10, 3),
    `y`              numeric(10, 3),
    `zoom`           numeric(5, 3),
    `item_color`     varchar(16),
    `line_color`     varchar(16),
    `line_width`     numeric(10, 3),
    `line_type`      varchar(16),
    `line_cur`       numeric(10, 3),
    `label_show`     boolean,
    `label_fontsize` numeric(10, 3),
    `tooltip_show`   boolean,
	index (`uid`),
	FOREIGN KEY (`uid`) REFERENCES user(`uid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
insert into `default_layout`
values ('temp', 1, 102.36, 29.71, 1, 'red', 'blue', 1, 'solid', 0.5, false, 12, false);
create table `question`(
    `question` varchar(512) not null,
    `id` BIGINT auto_increment not null,
    primary key (`id`),
    `create_date` timestamp not null default current_timestamp
)