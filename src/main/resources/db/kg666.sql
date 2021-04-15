drop database if exists `kg666`;
create database `kg666`;
use kg666;
drop table if exists `node_layout`;
create table `node_layout` (
`id` integer not null,
primary key(`id`),
`x` numeric(10,3) not null,
`y` numeric(10,3) not null,
`color` varchar(16),
`symbol` varchar(16),
`label_show` boolean,
`label_fontsize` numeric(10,3),
`tooltip_show` boolean
);
drop table if exists `link_layout`;
create table `link_layout` (
`id` integer not null,
primary key(`id`),
`color` varchar(16) ,
`width` numeric(10,3) ,
`type` varchar(16) ,
`curveness` numeric (10,3) ,
`label_show` boolean,
`label_fontsize` numeric(3,1) ,
`tooltip_show` boolean
);
drop table if exists `default_layout`;
create table `default_layout` (
    `pic_name` varchar(32) not null ,
    `uid` integer not null default 0,
    primary key (`pic_name`, `uid`),
    `x` numeric(10,3) not null,
    `y` numeric(10,3) not null,
    `zoom` numeric(5,3) not null,
    `item_color` varchar(16) not null,
    `line_color` varchar(16) not null,
    `line_width` numeric(10,3) not null,
    `line_type` varchar(16) not null,
    `line_cur` numeric(10,3) not null,
    `label_show` boolean not null,
    `label_fontsize` numeric(10,3) not null,
    `tooltip_show` boolean not null
);