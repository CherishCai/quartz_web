/*
Navicat MySQL Data Transfer

Source Server         : 127.0.0.1
Source Server Version : 50711
Source Host           : 127.0.0.1:3306
Source Database       : quartz_learn

Target Server Type    : MYSQL
Target Server Version : 50711
File Encoding         : 65001

Date: 2017-09-12 10:11:36
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
-- Records of task_config
-- ----------------------------
INSERT INTO `task_config` VALUES ('1', 'cherish_test', 'tast_test', '1', '0/20 * * * * ?', '每20秒执行', null, null, null, null);
INSERT INTO `task_config` VALUES ('2', 'cherish_test', 'tast_test222', '1', '0/30 * * * * ?', '每30秒执行', null, null, 'Cherish', '785427346@qq.com');
