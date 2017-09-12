/*
Navicat MySQL Data Transfer

Source Server         : 127.0.0.1
Source Server Version : 50711
Source Host           : 127.0.0.1:3306
Source Database       : quartz_learn

Target Server Type    : MYSQL
Target Server Version : 50711
File Encoding         : 65001

Date: 2017-09-12 10:11:09
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for task_config
-- ----------------------------
DROP TABLE IF EXISTS `task_config`;
CREATE TABLE `task_config` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `group_name` varchar(255) NOT NULL,
  `task_name` varchar(255) NOT NULL,
  `task_status` varchar(255) DEFAULT NULL,
  `task_cron` varchar(16) NOT NULL,
  `task_desc` varchar(1024) DEFAULT NULL,
  `previous_fire_time` datetime DEFAULT NULL,
  `next_fire_time` datetime DEFAULT NULL,
  `contact_name` varchar(255) DEFAULT NULL,
  `contact_email` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`,`group_name`,`task_name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for task_log
-- ----------------------------
DROP TABLE IF EXISTS `task_log`;
CREATE TABLE `task_log` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `group_name` varchar(255) NOT NULL,
  `task_name` varchar(255) NOT NULL,
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `task_status` varchar(255) DEFAULT NULL,
  `fire_info` text,
  `server_host` varchar(255) DEFAULT NULL,
  `server_duid` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_task_log_group` (`group_name`),
  KEY `idx_task_log_task_name` (`task_name`),
  KEY `idx_task_log_start_time` (`start_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1259 DEFAULT CHARSET=utf8;
