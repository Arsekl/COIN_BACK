drop database if exists `kg666`;
create database `kg666`;
use kg666;
drop table if exists `node_layout`;
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
    `tooltip_show`   boolean
);
drop table if exists `link_layout`;
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
    `tooltip_show`   boolean
);
drop table if exists `default_layout`;
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
    `tooltip_show`   boolean
);