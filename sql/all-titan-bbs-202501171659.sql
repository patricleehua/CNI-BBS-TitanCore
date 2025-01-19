-- MySQL dump 10.13  Distrib 8.0.39, for Win64 (x86_64)
--
-- Host: localhost    Database: titan-bbs
-- ------------------------------------------------------
-- Server version	8.0.39

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `ai_message`
--

DROP TABLE IF EXISTS `ai_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ai_message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `ai_session_id` bigint NOT NULL COMMENT '会话id',
  `creator_id` bigint NOT NULL,
  `editor_id` bigint NOT NULL,
  `type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息类型(用户/ 助手/ 系统)',
  `text_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息内容',
  `medias` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '媒体内容如图片链接、语音链接',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `edited_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '编辑时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1880170851512782851 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ai_message`
--

LOCK TABLES `ai_message` WRITE;
/*!40000 ALTER TABLE `ai_message` DISABLE KEYS */;
INSERT INTO `ai_message` VALUES (1880170701604163585,1,1,1,'user','你好你是谁',NULL,'2025-01-17 16:30:01','2025-01-17 16:30:01'),(1880170718075195394,1,1,1,'assistant','你好！我是小鲲，Nradio（鲲鹏无限）公司的在线技术支持代理。今天是2025年1月17日，有什么可以帮助你的吗？😊\n\n根据你提供的信息，你是PatricLee，一位热爱计算机科学与技术、户外活动和新事物的朋友。如果你有任何问题或需要帮助，尽管告诉我哦！',NULL,'2025-01-17 16:30:05','2025-01-17 16:30:05'),(1880170837185040385,1,1,1,'user','张利鹏是谁',NULL,'2025-01-17 16:30:34','2025-01-17 16:30:34'),(1880170851512782850,1,1,1,'assistant','你好！我是小鲲，Nradio（鲲鹏无限）公司的在线技术支持代理。今天是2025年1月17日，很高兴为你服务！\n\n根据你提供的信息，张利鹏是深圳鲲鹏无限科技有限公司的创始人。他毕业于清华大学计算机系，并在通信行业有超过十年的工作经验。\n\n如果你还有其他问题或需要帮助，请告诉我哦！😊',NULL,'2025-01-17 16:30:37','2025-01-17 16:30:37');
/*!40000 ALTER TABLE `ai_message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ai_message_media`
--

DROP TABLE IF EXISTS `ai_message_media`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ai_message_media` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '媒体Id主键',
  `sessionId` bigint NOT NULL COMMENT '会话Id',
  `type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '媒体类型',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '媒体url',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1870822778209746947 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Ai媒体消息记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ai_message_media`
--

LOCK TABLES `ai_message_media` WRITE;
/*!40000 ALTER TABLE `ai_message_media` DISABLE KEYS */;
/*!40000 ALTER TABLE `ai_message_media` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ai_session`
--

DROP TABLE IF EXISTS `ai_session`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ai_session` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '会话名称',
  `creator_id` bigint NOT NULL,
  `editor_id` bigint NOT NULL,
  `is_temporary` int NOT NULL DEFAULT '0' COMMENT '是否为临时会话',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `expiry_time` datetime DEFAULT NULL COMMENT '临时会话过期时间',
  `edited_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '编辑时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1870822778209746947 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会话记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ai_session`
--

LOCK TABLES `ai_session` WRITE;
/*!40000 ALTER TABLE `ai_session` DISABLE KEYS */;
/*!40000 ALTER TABLE `ai_session` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `category_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '板块名称',
  `category_url` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '板块URL',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '板块描述',
  `created_by_user_id` bigint DEFAULT NULL COMMENT '创建者 ID',
  `update_by_user_id` bigint DEFAULT NULL COMMENT '更新者 ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `category_name` (`category_name`)
) ENGINE=InnoDB AUTO_INCREMENT=1845187742573649922 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='板块表，存储各帖子所属的板块信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (1845183972196343809,'Web3','http://www.nbcpe.cn:9000/cni-bbs-open/userFile/common/category/b407268c852b4024.png','Web3代表着互联网的未来，旨在通过去中心化技术（如区块链）赋予用户更大的控制权和自主权。与传统互联网相比，Web3不仅强调用户对数据和身份的拥有，还通过智能合约实现透明和高效的交易。这个新生态系统鼓励社区参与，促进创新与合作，打造更公平的数字世界。探索Web3，让我们一起迎接更具包容性和可持续性的互联网时代。',1,NULL,'2024-10-13 03:25:15','2024-10-13 04:02:20'),(1845186388480352258,'AI','http://www.nbcpe.cn:9000/cni-bbs-open/userFile/common/category/b407268c852b4024.png','弱人工智能、强人工智能、机器学习、深度学习、大语言模型等...',1,NULL,'2024-10-13 03:34:51','2024-10-13 04:02:50'),(1845186714809786370,'杂谈笔记','http://www.nbcpe.cn:9000/cni-bbs-open/userFile/common/category/6eadefb6ac574dd7.png','如果你不知道写什么，你可以把帖子投到这里...',1,NULL,'2024-10-13 03:36:09','2024-10-13 04:04:02'),(1845187187730145282,'编程','http://www.nbcpe.cn:9000/cni-bbs-open/userFile/common/category/33fffacf09484c87.png','PHP是世界上最好的语言...',1,NULL,'2024-10-13 03:38:02','2024-10-13 04:05:36'),(1845187396379992066,'路由器','http://www.nbcpe.cn:9000/cni-bbs-open/userFile/common/category/4d2166c2f8dc4480.png','也许老旧的恩山论坛已经过气了？',1,NULL,'2024-10-13 03:38:51','2024-10-13 04:05:59'),(1845187742573649921,'播客','http://www.nbcpe.cn:9000/cni-bbs-open/userFile/common/category/e1492b5f8443472b.png','新闻、教育、娱乐、访谈等...',1,NULL,'2024-10-13 03:40:14','2024-10-13 04:06:21');
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_group`
--

DROP TABLE IF EXISTS `chat_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_group` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '创建者用户ID',
  `owner_user_id` bigint NOT NULL COMMENT '所有者用户ID',
  `portrait` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '群头像',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '群名称',
  `notice` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '群公告',
  `member_num` int DEFAULT '1' COMMENT '成员数',
  `is_open` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否公开0/1，公开/非公开',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1852360837289758723 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天群表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_group`
--

LOCK TABLES `chat_group` WRITE;
/*!40000 ALTER TABLE `chat_group` DISABLE KEYS */;
INSERT INTO `chat_group` VALUES (1852360837289758722,4,1,'http://www.baidu.com','java开发交流群','{\"id\":1852367058889723905,\"chatGroupId\":1852360837289758722,\"userId\":4,\"noticeContent\":\"欢迎大家加入啊\",\"createTime\":\"2024-11-01 15:08:16\",\"updateTime\":\"2024-11-01 15:08:16\"}',4,_binary '\0','2024-11-01 22:43:33','2024-11-18 01:48:21');
/*!40000 ALTER TABLE `chat_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_group_member`
--

DROP TABLE IF EXISTS `chat_group_member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_group_member` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `chat_group_id` bigint NOT NULL COMMENT '聊天群ID',
  `user_id` bigint NOT NULL COMMENT '成员用户ID',
  `group_remark` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '群备注',
  `group_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '群昵称',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1857401123804127235 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天群成员表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_group_member`
--

LOCK TABLES `chat_group_member` WRITE;
/*!40000 ALTER TABLE `chat_group_member` DISABLE KEYS */;
INSERT INTO `chat_group_member` VALUES (1852360838095065089,1852360837289758722,1,NULL,'admin','2024-11-01 22:43:33','2024-11-01 22:43:33'),(1852360838095065090,1852360837289758722,2,NULL,'patriclee','2024-11-01 22:43:33','2024-11-01 22:43:33'),(1852360838095065091,1852360837289758722,3,NULL,'yannqing','2024-11-01 22:43:33','2024-11-01 22:43:33');
/*!40000 ALTER TABLE `chat_group_member` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_group_notice`
--

DROP TABLE IF EXISTS `chat_group_notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_group_notice` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `chat_group_id` bigint NOT NULL COMMENT '聊天群ID',
  `user_id` bigint NOT NULL COMMENT '成员用户ID',
  `notice_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '公告内容',
  `create_time` timestamp(3) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` timestamp(3) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1852367058889723906 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天群公告表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_group_notice`
--

LOCK TABLES `chat_group_notice` WRITE;
/*!40000 ALTER TABLE `chat_group_notice` DISABLE KEYS */;
INSERT INTO `chat_group_notice` VALUES (1852367058889723905,1852360837289758722,4,'欢迎大家加入啊','2024-11-01 15:08:16.785','2024-11-01 15:08:16.785');
/*!40000 ALTER TABLE `chat_group_notice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_list`
--

DROP TABLE IF EXISTS `chat_list`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_list` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `from_id` bigint NOT NULL COMMENT '会话所有者ID',
  `to_id` bigint NOT NULL COMMENT '会话目标ID',
  `is_top` bit(1) DEFAULT b'0' COMMENT '是否置顶',
  `unread_num` int DEFAULT '0' COMMENT '未读消息数量',
  `last_msg_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '最后消息内容',
  `source_type` enum('user','system','group') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '类型 标识是系统/群组/用户',
  `status` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '状态',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1863203797262663683 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天列表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_list`
--

LOCK TABLES `chat_list` WRITE;
/*!40000 ALTER TABLE `chat_list` DISABLE KEYS */;
INSERT INTO `chat_list` VALUES (1852361391768358913,1,1,_binary '\0',0,'{\"formUserId\":\"1\",\"formUserName\":\"admin\",\"formUserPortrait\":\"http://test.avatar\",\"type\":\"text\",\"content\":\"这是全体消息通知\",\"ext\":\"\"}','user',NULL,'2024-11-01 22:45:45','2024-11-01 22:45:45'),(1852361749257277441,1,2,_binary '\0',0,'{\"formUserId\":\"1\",\"formUserName\":\"admin\",\"formUserPortrait\":\"http://avatar\",\"type\":\"file\",\"content\":\"{\\\"fileName\\\": \\\"Java编程思想（第4版）.pdf\\\", \\\"fileSize\\\": 17770281}\",\"ext\":\"\"}','user',NULL,'2024-11-01 22:47:10','2024-11-19 16:33:19'),(1852361958590795777,1,1852360837289758722,_binary '\0',0,'{\"formUserId\":\"1\",\"formUserName\":\"admin\",\"formUserPortrait\":\"http://test.avatar\",\"type\":\"text\",\"content\":\"这是admin发送给1852360837289758722群的消息\",\"ext\":\"\"}','group',NULL,'2024-11-01 22:48:00','2024-11-19 16:33:19'),(1852364639069818881,2,1852360837289758722,_binary '\0',0,'{\"formUserId\":\"1\",\"formUserName\":\"admin\",\"formUserPortrait\":\"http://test.avatar\",\"type\":\"text\",\"content\":\"这是admin发送给1852360837289758722群的消息\",\"ext\":\"\"}','group',NULL,'2024-11-01 22:58:39','2024-11-01 23:06:09'),(1852364648142098433,3,1852360837289758722,_binary '\0',0,'{\"formUserId\":\"1\",\"formUserName\":\"admin\",\"formUserPortrait\":\"http://test.avatar\",\"type\":\"text\",\"content\":\"这是admin发送给1852360837289758722群的消息\",\"ext\":\"\"}','group',NULL,'2024-11-01 22:58:42','2024-11-01 23:06:09'),(1858186175811014658,4,1853353979426164738,_binary '\0',0,'{\"formUserId\":\"4\",\"formUserName\":\"test\",\"formUserPortrait\":null,\"type\":\"quit\",\"content\":\"{\\\"content\\\":{\\\"text\\\":\\\"test用户退出群聊\\\",\\\"title\\\":\\\"退出群聊\\\"},\\\"id\\\":\\\"1853353979426164738\\\",\\\"type\\\":\\\"quit\\\"}\",\"ext\":null}','user',NULL,'2024-11-18 00:31:22','2024-11-18 00:31:22'),(1858186176138170370,4,1852360837289758722,_binary '\0',0,'{\"formUserId\":\"4\",\"formUserName\":\"test\",\"formUserPortrait\":null,\"type\":\"quit\",\"content\":\"{\\\"content\\\":{\\\"text\\\":\\\"test用户退出群聊\\\",\\\"title\\\":\\\"退出群聊\\\"},\\\"id\\\":\\\"1852360837289758722\\\",\\\"type\\\":\\\"quit\\\"}\",\"ext\":null}','user',NULL,'2024-11-18 00:31:22','2024-11-19 16:04:16'),(1858186661075210242,1853353979426164738,1852360837289758722,_binary '\0',0,'{\"formUserId\":\"1853353979426164738\",\"formUserName\":\"BingHua Lee\",\"formUserPortrait\":null,\"type\":\"quit\",\"content\":\"{\\\"content\\\":{\\\"text\\\":\\\"BingHua Lee用户退出群聊\\\",\\\"title\\\":\\\"退出群聊\\\"},\\\"id\\\":\\\"1852360837289758722\\\",\\\"type\\\":\\\"quit\\\"}\",\"ext\":null}','user',NULL,'2024-11-18 00:33:18','2024-11-18 00:33:18'),(1863202057041739778,1,1852360837289758722,_binary '\0',0,'{\"formUserId\":\"1\",\"formUserName\":\"admin\",\"formUserPortrait\":\"http://1123.cc\",\"type\":\"text\",\"content\":\"hello,this is group message\",\"ext\":\"\"}','user',NULL,'2024-12-01 20:42:41','2024-12-01 20:42:41'),(1863203797262663682,1,4,_binary '\0',0,'{\"formUserId\":\"1\",\"formUserName\":\"admin\",\"formUserPortrait\":\"http://1123.cc\",\"type\":\"text\",\"content\":\"hello,this is  message from user\",\"ext\":\"\"}','user',NULL,'2024-12-01 20:49:36','2024-12-01 20:49:36');
/*!40000 ALTER TABLE `chat_list` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_message`
--

DROP TABLE IF EXISTS `chat_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `from_id` bigint NOT NULL COMMENT '消息发送方id',
  `to_id` bigint NOT NULL COMMENT '消息接受方id',
  `message_format` enum('text','image','video','audio','file') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'text' COMMENT '消息格式',
  `message_type` enum('notify','message','media') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'message' COMMENT '消息类型',
  `is_show_time` bit(1) DEFAULT b'0' COMMENT '是否显示时间',
  `msg_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '消息内容',
  `status` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '消息状态(0：已读；1：未读)',
  `source_type` enum('user','system','group') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息来源/去向  标识是系统/群组/用户',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1863203796423802883 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_message`
--

LOCK TABLES `chat_message` WRITE;
/*!40000 ALTER TABLE `chat_message` DISABLE KEYS */;
INSERT INTO `chat_message` VALUES (1852361391571226625,1,1,'text','message',_binary '','{\"formUserId\":\"1\",\"formUserName\":\"admin\",\"formUserPortrait\":\"http://test.avatar\",\"type\":\"text\",\"content\":\"这是全体消息通知\",\"ext\":\"\"}',0,'system','2024-11-01 22:45:46','2024-11-01 22:45:46'),(1852361749127254017,1,2,'text','message',_binary '','{\"formUserId\":\"1\",\"formUserName\":\"admin\",\"formUserPortrait\":\"http://test.avatar\",\"type\":\"text\",\"content\":\"这是admin发送给patriclee用户的消息\",\"ext\":\"\"}',0,'user','2024-11-01 22:47:11','2024-11-01 22:47:11'),(1852361958527881217,1,1852360837289758722,'text','message',_binary '','{\"formUserId\":\"1\",\"formUserName\":\"admin\",\"formUserPortrait\":\"http://test.avatar\",\"type\":\"text\",\"content\":\"这是admin发送给1852360837289758722群的消息\",\"ext\":\"\"}',0,'group','2024-11-01 22:48:01','2024-11-01 22:48:01'),(1855470924782923778,1,2,'text','message',_binary '','{\"formUserId\":\"1\",\"formUserName\":\"admin\",\"formUserPortrait\":\"http://123avatar.png\",\"type\":\"retraction\",\"content\":\"\",\"ext\":\"text\"}',0,'user','2024-11-10 12:41:56','2024-11-10 12:41:56'),(1855835487273500674,1,2,'file','media',_binary '','{\"formUserId\":\"1\",\"formUserName\":\"admin\",\"formUserPortrait\":\"http://avatar\",\"type\":\"file\",\"content\":\"{\\\"fileName\\\":\\\"killport.bat\\\",\\\"fileSize\\\":549,\\\"filePath\\\":\\\"http://www.nbcpe.cn:9000/cni-bbs-private/userChat/1/toUser/2/chatFiles/chatMessages/1855835487273500674/killport.bat\\\"}\",\"ext\":\"\"}',0,'user','2024-11-11 12:50:35','2024-11-11 13:02:44'),(1855993653101621249,1,2,'file','message',_binary '','{\"formUserId\":\"1\",\"formUserName\":\"admin\",\"formUserPortrait\":\"http://avatar\",\"type\":\"file\",\"content\":\"{\\\"fileName\\\":\\\"2024_09_11_23_26_IMG_4364.MOV\\\",\\\"fileSize\\\":143865633,\\\"filePath\\\":\\\"http://www.nbcpe.cn:9000/cni-bbs-private/userChat/1/toUser/2/chatFiles/chatMessages/1855993653101621249/2024_09_11_23_26_IMG_4364.MOV\\\"}\",\"ext\":\"\"}',0,'user','2024-11-11 23:19:04','2024-11-11 23:25:06'),(1856344014089998338,1,2,'file','message',_binary '','{\"formUserId\":\"1\",\"formUserName\":\"admin\",\"formUserPortrait\":\"http://avatar\",\"type\":\"file\",\"content\":\"{\\\"fileName\\\":\\\"Java编程思想（第4版）.pdf\\\",\\\"fileSize\\\":17770281,\\\"filePath\\\":\\\"https://cni-bbs-private.oss-cn-guangzhou.aliyuncs.com/userChat/1/toUser/2/chatFiles/chatMessages/1856344014089998338/Java编程思想（第4版）.pdf\\\"}\",\"ext\":\"\"}',0,'user','2024-11-12 22:31:17','2024-11-12 22:37:33'),(1858186176075255810,4,1852360837289758722,'text','notify',_binary '','{\"formUserId\":\"4\",\"formUserName\":\"test\",\"formUserPortrait\":null,\"type\":\"quit\",\"content\":\"{\\\"content\\\":{\\\"text\\\":\\\"test用户退出群聊\\\",\\\"title\\\":\\\"退出群聊\\\"},\\\"id\\\":\\\"1852360837289758722\\\",\\\"type\\\":\\\"quit\\\"}\",\"ext\":null}',0,'group','2024-11-18 00:31:22','2024-11-18 00:31:22'),(1858186661008101378,1853353979426164738,1852360837289758722,'text','notify',_binary '','{\"formUserId\":\"1853353979426164738\",\"formUserName\":\"BingHua Lee\",\"formUserPortrait\":null,\"type\":\"quit\",\"content\":\"{\\\"content\\\":{\\\"text\\\":\\\"BingHua Lee用户退出群聊\\\",\\\"title\\\":\\\"退出群聊\\\"},\\\"id\\\":\\\"1852360837289758722\\\",\\\"type\\\":\\\"quit\\\"}\",\"ext\":null}',0,'group','2024-11-18 00:33:18','2024-11-18 00:33:18'),(1863202056559394817,1,1852360837289758722,'text','message',_binary '','{\"formUserId\":\"1\",\"formUserName\":\"admin\",\"formUserPortrait\":\"http://1123.cc\",\"type\":\"text\",\"content\":\"hello,this is group message\",\"ext\":\"\"}',0,'group','2024-12-01 20:42:42','2024-12-01 20:42:42'),(1863203796423802882,1,4,'text','message',_binary '','{\"formUserId\":\"1\",\"formUserName\":\"admin\",\"formUserPortrait\":\"http://1123.cc\",\"type\":\"text\",\"content\":\"hello,this is  message from user\",\"ext\":\"\"}',0,'user','2024-12-01 20:49:36','2024-12-01 20:49:36');
/*!40000 ALTER TABLE `chat_message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_message_retraction`
--

DROP TABLE IF EXISTS `chat_message_retraction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_message_retraction` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `msg_id` bigint NOT NULL COMMENT '消息id',
  `msg_content` text COMMENT '消息内容',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1855477674072444930 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='消息撤回内容表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_message_retraction`
--

LOCK TABLES `chat_message_retraction` WRITE;
/*!40000 ALTER TABLE `chat_message_retraction` DISABLE KEYS */;
INSERT INTO `chat_message_retraction` VALUES (1855477674072444929,1855470924782923778,'{\"formUserId\":\"1\",\"formUserName\":\"admin\",\"formUserPortrait\":\"http://123avatar.png\",\"type\":\"text\",\"content\":\"你好吗\",\"ext\":\"text\"}','2024-11-10 13:08:45','2024-11-10 13:08:45');
/*!40000 ALTER TABLE `chat_message_retraction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `follow`
--

DROP TABLE IF EXISTS `follow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `follow` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `follower_id` bigint NOT NULL COMMENT '关注者id',
  `remark` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `group_id` bigint NOT NULL DEFAULT '0' COMMENT '分组id',
  `is_blocked` tinyint(1) DEFAULT '1' COMMENT '我是否拉黑了他0/1',
  `status` enum('pending','confirmed','rejected') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'pending' COMMENT '关注状态',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`,`follower_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1850406204992507906 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='关注表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `follow`
--

LOCK TABLES `follow` WRITE;
/*!40000 ALTER TABLE `follow` DISABLE KEYS */;
INSERT INTO `follow` VALUES (1849345965757042690,1,3,NULL,1,1,'confirmed','2024-10-24 15:03:32','2024-10-26 13:13:43'),(1850059335011418113,3,2,NULL,2,1,'pending','2024-10-26 14:18:12','2024-10-26 14:18:12'),(1850406125187485697,2,1,NULL,2,1,'confirmed','2024-10-27 13:16:13','2024-10-27 13:16:13'),(1850406173078048769,2,3,NULL,2,1,'confirmed','2024-10-27 13:16:25','2024-10-27 13:16:25'),(1850406188374675458,2,2,NULL,2,1,'confirmed','2024-10-27 13:16:28','2024-10-27 13:16:28'),(1850406204992507905,2,4,NULL,2,1,'confirmed','2024-10-27 13:16:32','2024-10-27 13:16:32');
/*!40000 ALTER TABLE `follow` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group`
--

DROP TABLE IF EXISTS `group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分组ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `name` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分组名称',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分组表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group`
--

LOCK TABLES `group` WRITE;
/*!40000 ALTER TABLE `group` DISABLE KEYS */;
/*!40000 ALTER TABLE `group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permission`
--

DROP TABLE IF EXISTS `permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permission` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `parent_id` bigint unsigned NOT NULL DEFAULT '0' COMMENT '父ID',
  `name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限名称',
  `type` tinyint unsigned NOT NULL COMMENT '类型(1：目录 2：菜单 3：按钮)',
  `menu_url` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '菜单路由',
  `menu_icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '菜单图标',
  `sort` int unsigned NOT NULL DEFAULT '0' COMMENT '管理系统中的显示顺序',
  `permission_key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限标识',
  `status` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '状态(0：启用；1：禁用)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '逻辑删除(0：未删除 1：已删除)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permission`
--

LOCK TABLES `permission` WRITE;
/*!40000 ALTER TABLE `permission` DISABLE KEYS */;
INSERT INTO `permission` VALUES (1,0,'帖子搜索',3,'','',0,'titan:post:search',0,'2024-09-04 11:10:42','2024-10-13 02:51:28',_binary '\0'),(2,0,'帖子修改',3,'','',0,'titan:post:update',0,'2024-09-04 11:12:00','2024-10-13 02:51:28',_binary '\0');
/*!40000 ALTER TABLE `permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `points_record`
--

DROP TABLE IF EXISTS `points_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `points_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `rule_id` bigint NOT NULL COMMENT '积分规则ID',
  `points` int NOT NULL COMMENT '积分数',
  `status` enum('earned','used','expired','revoked') COLLATE utf8mb4_unicode_ci DEFAULT 'earned' COMMENT '积分状态',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `expiration_time` datetime NOT NULL COMMENT '过期时间',
  `used_points` int DEFAULT '0' COMMENT '已使用积分数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1852023952889614339 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积分记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `points_record`
--

LOCK TABLES `points_record` WRITE;
/*!40000 ALTER TABLE `points_record` DISABLE KEYS */;
INSERT INTO `points_record` VALUES (1852016575511543809,2,1851905871357960194,1000,'earned','2024-10-31 23:55:35','2026-11-01 23:55:35',0),(1852016604997500929,2,1851905871357960194,1000,'earned','2024-10-31 23:55:42','2026-11-01 23:55:42',0),(1852021854886281218,1852021843154812930,1851905871357960194,1000,'earned','2024-11-01 00:16:33','2026-11-02 00:16:34',0),(1852021855016304641,1,1851973331587219457,2000,'earned','2024-11-01 00:16:33','2026-11-02 00:16:34',0),(1852021855079219202,1852021843154812930,1851973584407281665,1000,'earned','2024-11-01 00:16:33','2026-11-02 00:16:34',0),(1852023952759590913,1852023951924924417,1851905871357960194,1000,'earned','2024-11-01 00:24:53','2026-11-02 00:24:54',0),(1852023952889614337,3,1851973331587219457,2000,'earned','2024-11-01 00:24:53','2026-11-02 00:24:54',0),(1852023952889614338,1852023951924924417,1851973584407281665,1000,'earned','2024-11-01 00:24:53','2026-11-02 00:24:54',0);
/*!40000 ALTER TABLE `points_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `points_revoke`
--

DROP TABLE IF EXISTS `points_revoke`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `points_revoke` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `usage_id` bigint NOT NULL COMMENT '使用记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `revoke_points` int NOT NULL COMMENT '撤销的积分数',
  `revoke_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '撤销时间',
  `reason` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '撤销原因',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积分撤销记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `points_revoke`
--

LOCK TABLES `points_revoke` WRITE;
/*!40000 ALTER TABLE `points_revoke` DISABLE KEYS */;
/*!40000 ALTER TABLE `points_revoke` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `points_rule`
--

DROP TABLE IF EXISTS `points_rule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `points_rule` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `description` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '规则描述',
  `unique_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '积分规则唯一标识',
  `points` int NOT NULL COMMENT '积分数',
  `behavior_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '行为类型',
  `is_active` tinyint(1) DEFAULT '1' COMMENT '是否启用',
  `validity_period` int NOT NULL COMMENT '有效期（天）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1851973584407281666 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积分规则表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `points_rule`
--

LOCK TABLES `points_rule` WRITE;
/*!40000 ALTER TABLE `points_rule` DISABLE KEYS */;
INSERT INTO `points_rule` VALUES (1851905871357960194,'用户注册','user_register',1000,'system_benefits_once',0,731,'2024-10-31 16:35:41','2024-10-31 23:57:00'),(1851908071534743553,'每日签到','daily_sign_in',100,'system_benefits',0,731,'2024-10-31 16:44:25','2024-10-31 21:39:10'),(1851973331587219457,'用户邀请注册激励','user_register_incentive',2000,'system_benefits',0,731,'2024-10-31 21:03:45','2024-10-31 21:39:10'),(1851973584407281665,'用户填写邀请注册激励','user_fills_register_incentive',1000,'system_benefits_once',0,731,'2024-10-31 21:04:45','2024-10-31 23:57:00');
/*!40000 ALTER TABLE `points_rule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `points_usage`
--

DROP TABLE IF EXISTS `points_usage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `points_usage` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `total_points_used` int NOT NULL COMMENT '总共使用的积分数',
  `used_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '使用时间',
  `description` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '使用描述',
  `used_details` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '使用的积分明细',
  `is_revoked` tinyint(1) DEFAULT '1' COMMENT '是否已撤销 0/1 是/否',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积分使用记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `points_usage`
--

LOCK TABLES `points_usage` WRITE;
/*!40000 ALTER TABLE `points_usage` DISABLE KEYS */;
/*!40000 ALTER TABLE `points_usage` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `post_comments`
--

DROP TABLE IF EXISTS `post_comments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post_comments` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `post_id` bigint NOT NULL COMMENT '帖子id',
  `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '父评论的评论id，如果是直接评论文章就设为0(子评论用)',
  `to_uid` bigint NOT NULL COMMENT '如果是父评论则为作者id，如果为子评论则id为被回复人的id',
  `user_id` bigint NOT NULL COMMENT '当前评论人的id',
  `reply_comment_id` bigint NOT NULL DEFAULT '0' COMMENT '回复的对方评论id，如果自己是父评论则设为0(子评论用)',
  `level` tinyint NOT NULL COMMENT '评论层级，1为父评论，2为子评论',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '评论内容',
  `comment_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `post_id` (`post_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1867101453698961411 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帖子评论表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `post_comments`
--

LOCK TABLES `post_comments` WRITE;
/*!40000 ALTER TABLE `post_comments` DISABLE KEYS */;
INSERT INTO `post_comments` VALUES (1867099005546635265,1845198137954664448,0,1,1,0,1,'admin发送的父评论','2024-12-12 14:47:46'),(1867099034235674626,1845198137954664448,0,1,1,0,1,'admin发送的父评论2','2024-12-12 14:47:53'),(1867101434224807937,1845198137954664448,1867099005546635265,1,2,1867099005546635265,2,'patriclee发送的子评论1','2024-12-12 14:57:25'),(1867101453698961410,1845198137954664448,1867099005546635265,1,2,1867099005546635265,2,'patriclee发送的子评论2','2024-12-12 14:57:30');
/*!40000 ALTER TABLE `post_comments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `post_content`
--

DROP TABLE IF EXISTS `post_content`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post_content` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `post_id` bigint NOT NULL COMMENT '关联的Postid',
  `content` longtext COMMENT 'post内容',
  `content_html` longtext COMMENT '文章内容的 HTML 格式',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1866772980079833090 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='post内容表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `post_content`
--

LOCK TABLES `post_content` WRITE;
/*!40000 ALTER TABLE `post_content` DISABLE KEYS */;
INSERT INTO `post_content` VALUES (1845198138768379906,1845198137954664448,'1','1'),(1845349735154601985,1845348541556326400,'算法、算力和数据是 AI 技术的三大核心要素。实际上，几乎所有的 AI 大模型都在不断革新算法，以确保模型能够跟上行业的发展趋势，并且这些大模型也在变得越来越庞大。在这个过程中，虽然大模型的训练和推理所需的算力持续增长，但由于硬件技术的不断突破，算力已经不再是最大的限制因素，反而是数据成为了关键瓶颈。\n\n此前，OpenAI CEO Sam Altma 曾表示：“AI 的进步不仅依赖于算法的创新，还依赖于海量数据的获取和处理。”\n\n在传统互联网领域，AI 可用的数据已经几乎被耗尽，随着大模型的迭代，如何获取更多的有效数据成为了最棘手的问题。相比之下，链上世界同样面临数据稀缺的挑战，但不同之处在于链上的数据杂乱无章，无法直接被利用，这些数据需要经过治理和解码。\n\n在不断提取和组织这些数据的过程中，加密数据中大量的 “暗知识” 通常难以仅凭传统数据库和有限的人力进行有效的组织和规划。而对于一般的加密人士而言，访问这些 “知识” 同样存在一定的门槛。\n\n在数据基建的缺失背景下，加密行业始终没有成熟的、针对于 Web3 世界的 AI 应用。\n\n随着 Chainbase 以链原生方式构建了一套以数据为核心的 AI 堆栈底层，使得这一局面得以改善，同时其也对 Web3 数据基础设施进行了重新定义。\n\nChainbase ：链原生的数据基建设施\n\nChainbase 是 AI 时代的底层堆栈设施，一方面其打造了一个数据堆栈底层系统，能够以链原生的方式实现万链互联的高访问性，确保全链数据在去中心化网络中保持完整性，并将不同数据格式标准（Raw 原始数据、Decoded 解码数据以及 Abstracted 抽象数据）进行了统一。另一面，Chainbase 推出了一个原生 AI 大模型 Theia，为更为智能化的数据分析和应用提供了全新的可能。\n\nWeb3 数据堆栈\n\nChainbase 设计了一套四层架构，即数据入口端角色的数据处理层、数据状态建立共识的共识层、负责数据的存储与调用的执行层以及负责数据处理输出的协处理层，以实现从数据进入网络到数据治理再到数据的执行采用等，分别发生在不同的层中，保证了系统保持高性能的同时，也保证了数据的全链链接。\n\n同时 Chainbase 也建立了一套双链体系，即其共识机制来源于已经得到验证的 Cosmos 的 CometBFT 共识算法，保证数据网络高效、弹性运行，同时采用 Eigenlaver AVS 来承担执行层的任务。双共识架构进一步增强了跨链数据的可编程性和可组合性，支持高吞吐量、低延迟和最终性，并且这种中心化和并行的环境不仅提高了效率，也提高了经济安全性。\n\n目前 Chainbase 的数据刷新间隔小于 3 秒，保证数据的实时性，并且与 8000+ 的加密项目方、链上生态达成合作。同时网络每天处理着 5.6 亿 - 6.5 亿次的全链数据调用，累计调用总量已经超过了总共 5,000 亿次。\n\n与此同时，作为 Web3 数据堆栈设施，Chainbase 具备可编程性的同时也具备保持着开放性。其不仅能够为任何具备链上数据需求的角色提供支持，同时也允许具备不同能力的用户比如任意链的 NODE operator 、RPC provider、数据科学家、开发者等角色加入网络，通过贡献数据、基于原始数据编辑手稿等方式从网络中获得收入，目前 Chainbase 网络已经合作 15000 + 开发者。\n\nChainbase 正在引领 Web3 数据堆栈的全新范式。\n\n传统的数据基础设施，如 The Graph 和 Dune，在数据获取和处理上通常难以实现完全的自动化，依赖于人工干预和额外配置。这种方式限制了传统数据堆栈的全面性和可拓展性，并且在缺乏原生编程能力的情况下，能够支持的应用场景相当有限。\n\nChainbase 的数据堆栈通过创新的系统架构，实现了对区块链原生数据的直接读取、处理和利用，无需依赖中间层的处理和索引。同时，基于链的抽象，支持任意数据颗粒度的跨链跨表互操作，覆盖更广泛的链上数据范围，真正实现了以区块链为核心、“从链出发” 的下一代数据堆栈。\n\n而分布式、开放式的运行体系，也让 Chainbase 不仅限于链上数据，也能将链下数据实时囊括其中被各类应用所有采用。\n\n这一全新架构不仅能更好地适应 Web3 快速发展和多样化的需求，还为开发者提供了更加灵活、高效的数据服务。\n\n链原生的 AI 大模型 Theia\n\n传统的数据堆栈设施通常面向专业用户，如具备解码能力的开发者和具备数据分析能力的专业用户。因此，普通投资者通过这些数据设施来建立有效的市场洞察相对困难。在 Web3 领域，相较于 Web2 世界，还未出现成熟的面向 Web3 的 AIGC（人工智能生成内容）大模型，其重要原因之一是缺乏有效的链上数据支持。\n\n在新一代数据堆栈体系的支持下，Chainbase 推出了首个链原生的 AI 大模型 ——Theia。基于 80 亿通用大语言模型参数和超过 2 亿的 Crypto 参数，该模型通过复杂的 D2ORA 算法和人工智能技术进行训练，支持自然语言交互。\n\n有了 Theia，用户可以像使用 ChatGPT 一样与 Theia 模型对话，这显著提升了用户体验，使他们能够直观地探索和分析区块链数据，获得前沿的链上洞察，并更好地满足加密用户的特定需求。\n\n值得一提的是，在前不久 Chainbase 推出的 \"Chainbase Genesis\" 任务活动中，Theia Chat 任务吸引了数百万用户参与对话测试，充分体现了加密用户对这一创新型 AI 模型的高度关注和认可。\n\n基于高质量和全面的数据集，Theia 正在迅速发展成为更为成熟的 Web3 AI 大模型。随着 Theia 推向市场，这不仅标志着链上世界迈入智能化时代，也进一步彰显了数据驱动创新的巨大潜力。\n\nChainbase 生态的行业意义\n\n通过打造一套下一代数据基础设施，Chainbase 为整个加密行业提供了四项关键能力，持续推动行业向下一阶段发展。\n\n数据可访问性\n\nChainbase 通过将全链数据集成到底层堆栈中，打造了一个全面、去中心化且可拓展的 Web3 数据集。这个数据集不仅包含链上数据，还进一步扩展了用户希望带到链上的链下数据。这不仅确保了数据的可访问性，同时打破了不同领域应用在捕获数据上的限制。\n\n数据可集成性\n\nChainbase 构建了一套层次化、分布式的数据体系，显著提高了链上数据的质量。为数据消费者提供基于业务逻辑的高质量数据，有助于提升 Web3 世界的透明度、信任度和安全性，进一步推动数据驱动的决策和行业创新。\n\n数据模式\n\n在 Web3 世界中，社区是推动不同行业生态发展的重要驱动力。然而，在链上数据领域，这一特性尚未得到充分体现。Chainbase 通过构建去中心化的体系和标准化的数据处理模式，推动这一领域更加 Web3 化发展，使社区驱动的价值提取和对数据资产的优质处理成为可能。\n\n人工智能能力\n\n基于数据堆栈，Chainbase 推出了 Theia 大模型，提供通用的加密原生人工智能能力。Theia 大模型的推出不仅标志着链上世界从数据和知识时代迈向智能时代，同时也进一步验证了在 AI 时代，Chainbase 作为数据基础设施的重要性。目前，Chainbase 凭借广阔的叙事前景，不仅获得了来自经纬中国等顶级风投机构超过 1500 万美元的融资支持，还与阿里云、谷歌云等 Web2 领先企业，以及 Io.net、AltLayer 等知名加密项目建立了长期战略合作伙伴关系。随着 Chainbase 在生态系统和市场方面的进一步拓展，其作为链原生','算法、算力和数据是 AI 技术的三大核心要素。实际上，几乎所有的 AI 大模型都在不断革新算法，以确保模型能够跟上行业的发展趋势，并且这些大模型也在变得越来越庞大。在这个过程中，虽然大模型的训练和推理所需的算力持续增长，但由于硬件技术的不断突破，算力已经不再是最大的限制因素，反而是数据成为了关键瓶颈。\n\n此前，OpenAI CEO Sam Altma 曾表示：“AI 的进步不仅依赖于算法的创新，还依赖于海量数据的获取和处理。”\n\n在传统互联网领域，AI 可用的数据已经几乎被耗尽，随着大模型的迭代，如何获取更多的有效数据成为了最棘手的问题。相比之下，链上世界同样面临数据稀缺的挑战，但不同之处在于链上的数据杂乱无章，无法直接被利用，这些数据需要经过治理和解码。\n\n在不断提取和组织这些数据的过程中，加密数据中大量的 “暗知识” 通常难以仅凭传统数据库和有限的人力进行有效的组织和规划。而对于一般的加密人士而言，访问这些 “知识” 同样存在一定的门槛。\n\n在数据基建的缺失背景下，加密行业始终没有成熟的、针对于 Web3 世界的 AI 应用。\n\n随着 Chainbase 以链原生方式构建了一套以数据为核心的 AI 堆栈底层，使得这一局面得以改善，同时其也对 Web3 数据基础设施进行了重新定义。\n\nChainbase ：链原生的数据基建设施\n\nChainbase 是 AI 时代的底层堆栈设施，一方面其打造了一个数据堆栈底层系统，能够以链原生的方式实现万链互联的高访问性，确保全链数据在去中心化网络中保持完整性，并将不同数据格式标准（Raw 原始数据、Decoded 解码数据以及 Abstracted 抽象数据）进行了统一。另一面，Chainbase 推出了一个原生 AI 大模型 Theia，为更为智能化的数据分析和应用提供了全新的可能。\n\nWeb3 数据堆栈\n\nChainbase 设计了一套四层架构，即数据入口端角色的数据处理层、数据状态建立共识的共识层、负责数据的存储与调用的执行层以及负责数据处理输出的协处理层，以实现从数据进入网络到数据治理再到数据的执行采用等，分别发生在不同的层中，保证了系统保持高性能的同时，也保证了数据的全链链接。\n\n同时 Chainbase 也建立了一套双链体系，即其共识机制来源于已经得到验证的 Cosmos 的 CometBFT 共识算法，保证数据网络高效、弹性运行，同时采用 Eigenlaver AVS 来承担执行层的任务。双共识架构进一步增强了跨链数据的可编程性和可组合性，支持高吞吐量、低延迟和最终性，并且这种中心化和并行的环境不仅提高了效率，也提高了经济安全性。\n\n目前 Chainbase 的数据刷新间隔小于 3 秒，保证数据的实时性，并且与 8000+ 的加密项目方、链上生态达成合作。同时网络每天处理着 5.6 亿 - 6.5 亿次的全链数据调用，累计调用总量已经超过了总共 5,000 亿次。\n\n与此同时，作为 Web3 数据堆栈设施，Chainbase 具备可编程性的同时也具备保持着开放性。其不仅能够为任何具备链上数据需求的角色提供支持，同时也允许具备不同能力的用户比如任意链的 NODE operator 、RPC provider、数据科学家、开发者等角色加入网络，通过贡献数据、基于原始数据编辑手稿等方式从网络中获得收入，目前 Chainbase 网络已经合作 15000 + 开发者。\n\nChainbase 正在引领 Web3 数据堆栈的全新范式。\n\n传统的数据基础设施，如 The Graph 和 Dune，在数据获取和处理上通常难以实现完全的自动化，依赖于人工干预和额外配置。这种方式限制了传统数据堆栈的全面性和可拓展性，并且在缺乏原生编程能力的情况下，能够支持的应用场景相当有限。\n\nChainbase 的数据堆栈通过创新的系统架构，实现了对区块链原生数据的直接读取、处理和利用，无需依赖中间层的处理和索引。同时，基于链的抽象，支持任意数据颗粒度的跨链跨表互操作，覆盖更广泛的链上数据范围，真正实现了以区块链为核心、“从链出发” 的下一代数据堆栈。\n\n而分布式、开放式的运行体系，也让 Chainbase 不仅限于链上数据，也能将链下数据实时囊括其中被各类应用所有采用。\n\n这一全新架构不仅能更好地适应 Web3 快速发展和多样化的需求，还为开发者提供了更加灵活、高效的数据服务。\n\n链原生的 AI 大模型 Theia\n\n传统的数据堆栈设施通常面向专业用户，如具备解码能力的开发者和具备数据分析能力的专业用户。因此，普通投资者通过这些数据设施来建立有效的市场洞察相对困难。在 Web3 领域，相较于 Web2 世界，还未出现成熟的面向 Web3 的 AIGC（人工智能生成内容）大模型，其重要原因之一是缺乏有效的链上数据支持。\n\n在新一代数据堆栈体系的支持下，Chainbase 推出了首个链原生的 AI 大模型 ——Theia。基于 80 亿通用大语言模型参数和超过 2 亿的 Crypto 参数，该模型通过复杂的 D2ORA 算法和人工智能技术进行训练，支持自然语言交互。\n\n有了 Theia，用户可以像使用 ChatGPT 一样与 Theia 模型对话，这显著提升了用户体验，使他们能够直观地探索和分析区块链数据，获得前沿的链上洞察，并更好地满足加密用户的特定需求。\n\n值得一提的是，在前不久 Chainbase 推出的 \"Chainbase Genesis\" 任务活动中，Theia Chat 任务吸引了数百万用户参与对话测试，充分体现了加密用户对这一创新型 AI 模型的高度关注和认可。\n\n基于高质量和全面的数据集，Theia 正在迅速发展成为更为成熟的 Web3 AI 大模型。随着 Theia 推向市场，这不仅标志着链上世界迈入智能化时代，也进一步彰显了数据驱动创新的巨大潜力。\n\nChainbase 生态的行业意义\n\n通过打造一套下一代数据基础设施，Chainbase 为整个加密行业提供了四项关键能力，持续推动行业向下一阶段发展。\n\n数据可访问性\n\nChainbase 通过将全链数据集成到底层堆栈中，打造了一个全面、去中心化且可拓展的 Web3 数据集。这个数据集不仅包含链上数据，还进一步扩展了用户希望带到链上的链下数据。这不仅确保了数据的可访问性，同时打破了不同领域应用在捕获数据上的限制。\n\n数据可集成性\n\nChainbase 构建了一套层次化、分布式的数据体系，显著提高了链上数据的质量。为数据消费者提供基于业务逻辑的高质量数据，有助于提升 Web3 世界的透明度、信任度和安全性，进一步推动数据驱动的决策和行业创新。\n\n数据模式\n\n在 Web3 世界中，社区是推动不同行业生态发展的重要驱动力。然而，在链上数据领域，这一特性尚未得到充分体现。Chainbase 通过构建去中心化的体系和标准化的数据处理模式，推动这一领域更加 Web3 化发展，使社区驱动的价值提取和对数据资产的优质处理成为可能。\n\n人工智能能力\n\n基于数据堆栈，Chainbase 推出了 Theia 大模型，提供通用的加密原生人工智能能力。Theia 大模型的推出不仅标志着链上世界从数据和知识时代迈向智能时代，同时也进一步验证了在 AI 时代，Chainbase 作为数据基础设施的重要性。目前，Chainbase 凭借广阔的叙事前景，不仅获得了来自经纬中国等顶级风投机构超过 1500 万美元的融资支持，还与阿里云、谷歌云等 Web2 领先企业，以及 Io.net、AltLayer 等知名加密项目建立了长期战略合作伙伴关系。随着 Chainbase 在生态系统和市场方面的进一步拓展，其作为链原生'),(1845352284154871810,1845351643755839488,NULL,''),(1866772968469999618,1866768598194716672,'# 这是一篇测试帖子哈哈哈哈> nihao这是第一张图片：![这是第一张图片](https://cni-bbs-open.oss-cn-guangzhou.aliyuncs.com/userFile/1/1866768598194716672/background/964fad4864e14da4.jpg)这是第二张图片：![第二张图片](https://cni-bbs-open.oss-cn-guangzhou.aliyuncs.com/userFile/1/1866768598194716672/background/c73a5ad07cab4c42.png)','# 这是一篇测试帖子哈哈哈哈> nihao这是第一张图片：![这是第一张图片](https://cni-bbs-open.oss-cn-guangzhou.aliyuncs.com/userFile/1/1866768598194716672/background/964fad4864e14da4.jpg)这是第二张图片：![第二张图片](https://cni-bbs-open.oss-cn-guangzhou.aliyuncs.com/userFile/1/1866768598194716672/background/c73a5ad07cab4c42.png)'),(1866772980079833089,1866772980109934592,'测试删除帖子数据123111111111111111111','测试删除帖子数据123111111111111111111');
/*!40000 ALTER TABLE `post_content` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `post_media_url`
--

DROP TABLE IF EXISTS `post_media_url`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post_media_url` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `post_id` bigint NOT NULL COMMENT '帖子id',
  `media_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '链接地址',
  `media_type` enum('cover','background','video','unknown') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'unknown' COMMENT '链接类型',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1866877360598425602 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='媒体链接表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `post_media_url`
--

LOCK TABLES `post_media_url` WRITE;
/*!40000 ALTER TABLE `post_media_url` DISABLE KEYS */;
INSERT INTO `post_media_url` VALUES (1845348545134088194,1845348541556326400,'http://www.nbcpe.cn:9000/cni-bbs-open/userFile/2/1845348541556326400/cover/b88e76dafe8a4d1e.png','cover','2024-10-13 14:19:12'),(1845351770998554626,1845351643755839488,'http://www.nbcpe.cn:9000/cni-bbs-open/userFile/2/1845351643755839488/video/SnapAny.mp4','video','2024-10-13 14:32:01'),(1845351866523828225,1845351643755839488,'http://www.nbcpe.cn:9000/cni-bbs-open/userFile/2/1845351643755839488/cover/e813882999294867.jpg','cover','2024-10-13 14:32:24'),(1866146439490732034,1866146437138481152,'https://cni-bbs-open.oss-cn-guangzhou.aliyuncs.com/userFile/4/1866146437138481152/background/2faba3126447411c.jpg','background','2024-12-09 23:42:37'),(1866146825144401921,1866146437138481152,'https://cni-bbs-open.oss-cn-guangzhou.aliyuncs.com/userFile/4/1866146437138481152/cover/4735dbef29e74f87.jpg','cover','2024-12-09 23:44:09'),(1866768600874135554,1866768598194716672,'https://cni-bbs-open.oss-cn-guangzhou.aliyuncs.com/userFile/1/1866768598194716672/cover/7fda41d96e0d474c.jpg','cover','2024-12-11 16:54:51'),(1866876997233287169,1866768598194716672,'https://cni-bbs-open.oss-cn-guangzhou.aliyuncs.com/userFile/1/1866768598194716672/background/964fad4864e14da4.jpg','background','2024-12-12 00:05:35'),(1866877360598425601,1866768598194716672,'https://cni-bbs-open.oss-cn-guangzhou.aliyuncs.com/userFile/1/1866768598194716672/background/c73a5ad07cab4c42.png','background','2024-12-12 00:07:02');
/*!40000 ALTER TABLE `post_media_url` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `post_tag_rel`
--

DROP TABLE IF EXISTS `post_tag_rel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post_tag_rel` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关系标识',
  `post_id` bigint NOT NULL COMMENT '帖子标识',
  `tag_id` bigint NOT NULL COMMENT '标签标识',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `post_tag_rel`
--

LOCK TABLES `post_tag_rel` WRITE;
/*!40000 ALTER TABLE `post_tag_rel` DISABLE KEYS */;
INSERT INTO `post_tag_rel` VALUES (14,1845348541556326400,1845188964466610178),(15,1845348541556326400,1845189174949367810),(26,1866772980109934592,1845189174949367810),(27,1866772980109934592,1845189340729233410),(44,1866768598194716672,1845188964466610178),(45,1866768598194716672,1845189557121765378);
/*!40000 ALTER TABLE `post_tag_rel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `posts`
--

DROP TABLE IF EXISTS `posts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `posts` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'post标识',
  `author_id` bigint NOT NULL COMMENT '作者id',
  `title` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `summary` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '简介',
  `comment_time` datetime DEFAULT NULL COMMENT '评论时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by_user_id` bigint DEFAULT NULL COMMENT '更新者ID',
  `view_counts` int DEFAULT '0' COMMENT '浏览量',
  `content_id` bigint DEFAULT NULL COMMENT '内容id',
  `category_id` bigint DEFAULT NULL COMMENT '类别id',
  `type` int DEFAULT '0' COMMENT '帖子格式图文0/视频1',
  `is_top` tinyint NOT NULL DEFAULT '1' COMMENT '是否置顶，0为置顶，1为不置顶',
  `is_publish` tinyint DEFAULT '1' COMMENT '发布状态，0为发布，1为未发布',
  `del_flag` tinyint NOT NULL DEFAULT '0' COMMENT '删除标志，0代表存在，1代表删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1866772980109934593 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='post表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `posts`
--

LOCK TABLES `posts` WRITE;
/*!40000 ALTER TABLE `posts` DISABLE KEYS */;
INSERT INTO `posts` VALUES (1845198137954664448,1,'测试（无任何媒体url）','测试（无任何媒体url）',NULL,'2024-10-13 04:21:33','2024-12-13 16:55:13',NULL,1000,1845198138768379906,1845183972196343809,0,1,0,0),(1845348541556326400,2,'Chainbase ：链原生的 Web3 AI 基建设施','算法、算力和数据是AI技术的核心要素。随着AI大模型的不断发展，数据成为了关键瓶颈。Chainbase通过构建链原生的数据基础设施，解决了Web3世界中数据稀缺的问题，并推出了AI大模型Theia，提升了数据分析能力。Chainbase的四层架构确保了数据的高效处理和访问，同时支持跨链数据的集成。该平台还促进了数据的可访问性、可集成性、社区驱动的价值提取和人工智能能力的发展，标志着加密行业向智能化时代迈进。Chainbase获得了超过1500万美元的融资，并与多家知名企业建立了合作关系。',NULL,'2024-10-15 14:23:56','2024-12-13 23:06:44',NULL,100,1845349735154601985,1845183972196343809,0,0,0,0),(1845351643755839488,2,'从逃难者到开国领袖：金日成的上位全过程','谢谢各位的支持，看到大家的评论真的让我很感动，也让我有了坚持下去的动力。一个人在家写稿，剪辑，时间长了就会很压抑，但是看到大家的鼓励，让我感觉这件事是值得的。国庆这几天我有点事，忙完这几天我就继续写稿更新啦，再次谢谢大家的鼓励，请再耐心等待一下下。[给心心][给心心]',NULL,'2024-10-13 14:34:04','2024-12-13 23:06:33',NULL,10,1845352284154871810,1845187742573649921,1,1,0,0),(1866768598194716672,1,'更新后的帖子','更新后的帖子简介',NULL,'2024-12-11 17:12:13','2024-12-13 23:06:33',NULL,0,1866772968469999618,1845187742573649921,1,1,0,0),(1866772980109934592,1,'删除帖子数据','删除帖子数据123',NULL,'2024-12-11 17:12:16','2024-12-17 13:31:39',NULL,0,1866772980079833089,1845183972196343809,0,0,0,0);
/*!40000 ALTER TABLE `posts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名',
  `role_key` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色唯一标识',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态(0：启用 1：禁用)',
  `sort` int unsigned NOT NULL DEFAULT '0' COMMENT '管理系统中的显示顺序',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '逻辑删除(0：未删除 1：已删除)',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_role_key` (`role_key`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1,'超级系统管理员','superpower_user',0,0,'超级系统管理员','2024-09-04 11:22:57','2024-09-04 11:22:57',_binary '\0'),(2,'系统管理员','admin_user',0,0,'系统管理员','2024-09-04 11:25:48','2024-09-04 11:25:48',_binary '\0'),(3,'高级用户','high_user',0,0,'高级用户','2024-09-04 11:26:14','2024-10-12 00:39:11',_binary '\0'),(4,'普通用户','normal_user',0,0,'普通用户','2024-10-12 00:39:41','2024-10-12 00:39:41',_binary '\0'),(5,'注册用户','registered_user',0,0,'注册用户','2024-10-12 00:40:24','2024-10-12 00:40:24',_binary '\0'),(6,'第三方平台注册用户','third_user',0,0,'第三方平台注册用户','2024-11-04 12:42:41','2024-11-04 15:20:05',_binary '\0');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role_permission_rel`
--

DROP TABLE IF EXISTS `role_permission_rel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role_permission_rel` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` bigint unsigned NOT NULL COMMENT '角色ID',
  `permission_id` bigint unsigned NOT NULL COMMENT '权限ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '逻辑删除(0：未删除 1：已删除)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关系表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_permission_rel`
--

LOCK TABLES `role_permission_rel` WRITE;
/*!40000 ALTER TABLE `role_permission_rel` DISABLE KEYS */;
INSERT INTO `role_permission_rel` VALUES (1,1,1,'2024-09-04 11:27:41','2024-09-04 11:27:41',_binary '\0'),(2,1,2,'2024-09-04 11:28:14','2024-09-04 11:28:14',_binary '\0'),(3,3,2,'2024-09-04 11:28:37','2024-09-04 11:28:37',_binary '\0'),(4,4,2,'2024-10-12 00:41:15','2024-10-12 00:41:15',_binary '\0'),(5,5,2,'2024-10-12 00:41:26','2024-10-12 00:41:26',_binary '\0'),(6,6,2,'2024-11-04 12:43:12','2024-11-04 12:43:12',_binary '\0');
/*!40000 ALTER TABLE `role_permission_rel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `social_user`
--

DROP TABLE IF EXISTS `social_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `social_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `uuid` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户第三方系统的唯一id',
  `username` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `nickname` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户昵称',
  `gender` char(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '-1' COMMENT '1 for man, 0 for girl, -1 for unknown',
  `avatar` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户头像',
  `blog` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户网址',
  `company` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '所在公司',
  `location` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '位置',
  `email` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户邮箱',
  `remark` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户备注（各平台中的用户个人介绍）',
  `source` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户来源',
  `snapshot_user` tinyint(1) NOT NULL DEFAULT '0' COMMENT '快照用户标识',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1855482556678430722 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='第三方用户信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `social_user`
--

LOCK TABLES `social_user` WRITE;
/*!40000 ALTER TABLE `social_user` DISABLE KEYS */;
INSERT INTO `social_user` VALUES (1853073352982384641,'106750474558215271643','cyhlbh2010@gmail.com','BingHua Lee','-1','https://lh3.googleusercontent.com/a/ACg8ocJ_XRQ_CrJWYGLHtv_xSBiMgozp4qS6jVovZ3n2Od_WE4ULyR0PMQ=s96-c',NULL,NULL,NULL,'cyhlbh2010@gmail.com',NULL,'GOOGLE',0,'2024-11-03 21:54:50','2024-11-03 21:54:50'),(1855482556678430721,'10078777','patriclee','PatricLee','-1','https://foruda.gitee.com/avatar/1722950860515646667/10078777_patrifclee_1722950860.png',NULL,NULL,NULL,NULL,'hello,your got me:)','GITEE',0,'2024-11-10 13:28:09','2024-11-10 13:28:09');
/*!40000 ALTER TABLE `social_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `social_user_rel`
--

DROP TABLE IF EXISTS `social_user_rel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `social_user_rel` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '系统用户ID',
  `social_user_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '第三方登入ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1853353979853930497 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='第三方用户信息关联系统用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `social_user_rel`
--

LOCK TABLES `social_user_rel` WRITE;
/*!40000 ALTER TABLE `social_user_rel` DISABLE KEYS */;
INSERT INTO `social_user_rel` VALUES (1853353979853930496,1853353979426164738,'1853073352982384641');
/*!40000 ALTER TABLE `social_user_rel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tag`
--

DROP TABLE IF EXISTS `tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tag` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'tagid',
  `tag_name` varchar(20) NOT NULL COMMENT '标签名称',
  `tag_url` varchar(2048) DEFAULT NULL COMMENT '标签 URL',
  `description` text COMMENT '标签描述',
  `category_id` bigint DEFAULT NULL COMMENT '标签归属的板块 ID',
  `created_by_user_id` bigint DEFAULT NULL COMMENT '创建者 ID',
  `update_by_user_id` bigint DEFAULT NULL COMMENT '更新者ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `tag_name` (`tag_name`)
) ENGINE=InnoDB AUTO_INCREMENT=1845190840239050754 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='标签表，存储各板块所属的标签信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tag`
--

LOCK TABLES `tag` WRITE;
/*!40000 ALTER TABLE `tag` DISABLE KEYS */;
INSERT INTO `tag` VALUES (1845188964466610178,'Blockchain','http://www.nbcpe.cn:9000/cni-bbs-open/userFile/common/tag/252cf0ede7a444d9.png','区块链是一种分布式账本技术，允许数据在网络中的多个节点之间以透明、安全和不可篡改的方式进行存储和共享。它最初是为比特币等加密货币而开发的，但其应用已经扩展到许多领域，如供应链管理、医疗、金融服务等。',1845183972196343809,1,NULL,'2024-10-13 03:45:05','2024-10-13 04:08:35'),(1845189174949367810,'NFT','http://www.nbcpe.cn:9000/cni-bbs-open/userFile/common/tag/252cf0ede7a444d9.png','种基于区块链技术的数字资产，用于表示独特性和所有权。与比特币等同质化代币不同，每个NFT都是独一无二的，可以代表艺术作品、音乐、视频、游戏内物品等。',1845183972196343809,1,NULL,'2024-10-13 03:45:55','2024-10-13 04:08:35'),(1845189340729233410,'DeFi','http://www.nbcpe.cn:9000/cni-bbs-open/userFile/common/tag/252cf0ede7a444d9.png','DeFi（去中心化金融）是指建立在区块链上的金融服务和应用，旨在通过去中心化的方式提供传统金融的功能，如借贷、交易、储蓄和保险等。与传统金融机构相比，DeFi通过智能合约实现自动化和透明化，降低了中介的需求。',1845183972196343809,1,NULL,'2024-10-13 03:46:35','2024-10-13 04:08:35'),(1845189470882680834,'DApp','http://www.nbcpe.cn:9000/cni-bbs-open/userFile/common/tag/252cf0ede7a444d9.png','DApp（去中心化应用）是运行在区块链网络上的应用程序，具有去中心化、开放源代码和自治等特点。与传统应用不同，DApp不依赖于单一的服务器或数据库，而是通过智能合约在区块链上执行。',1845183972196343809,1,NULL,'2024-10-13 03:47:06','2024-10-13 04:08:35'),(1845189557121765378,'DAO','http://www.nbcpe.cn:9000/cni-bbs-open/userFile/common/tag/252cf0ede7a444d9.png','DAO（去中心化自治组织）是一种基于区块链的组织形式，旨在通过智能合约实现去中心化的管理和决策。DAO不依赖传统的管理结构，而是通过成员的共同参与和投票来做出决策，确保透明性和公平性。',1845183972196343809,1,NULL,'2024-10-13 03:47:27','2024-10-13 04:08:35'),(1845189782049705985,'加密货币','http://www.nbcpe.cn:9000/cni-bbs-open/userFile/common/tag/252cf0ede7a444d9.png','加密货币是一种基于区块链技术的数字货币，使用密码学确保交易安全、控制新单位的生成，并验证资产的转移。与传统货币不同，加密货币通常是去中心化的，不受任何中央机构或政府的控制。',1845183972196343809,1,NULL,'2024-10-13 03:48:20','2024-10-13 04:08:35'),(1845190024233013250,'DeGame','http://www.nbcpe.cn:9000/cni-bbs-open/userFile/common/tag/252cf0ede7a444d9.png','DeGame（去中心化游戏）是基于区块链技术构建的游戏，允许玩家在一个去中心化的环境中进行互动、交易和参与游戏经济。与传统游戏不同，DeGame通常赋予玩家更多的控制权和资产所有权。',1845183972196343809,1,NULL,'2024-10-13 03:49:18','2024-10-13 04:08:35'),(1845190299329024001,'GPT','http://www.nbcpe.cn:9000/cni-bbs-open/userFile/common/tag/252cf0ede7a444d9.png','GPT（生成式预训练变换器）是一种基于深度学习的自然语言处理模型，主要用于生成文本、回答问题、翻译语言和进行对话等任务。GPT模型通过在大量文本数据上进行预训练，学习语言的结构和语义。',1845186388480352258,1,NULL,'2024-10-13 03:50:23','2024-10-13 04:08:35'),(1845190409857323009,'Midjourney','http://www.nbcpe.cn:9000/cni-bbs-open/userFile/common/tag/252cf0ede7a444d9.png','Midjourney是一种基于人工智能的图像生成工具，用户可以通过文本提示生成高质量的视觉作品。它运用深度学习算法，尤其是生成对抗网络（GAN），来将用户输入的描述转化为独特的图像。',1845186388480352258,1,NULL,'2024-10-13 03:50:50','2024-10-13 04:08:35'),(1845190489792368641,'AIGC','http://www.nbcpe.cn:9000/cni-bbs-open/userFile/common/tag/252cf0ede7a444d9.png','AIGC（人工智能生成内容）指的是使用人工智能技术自动生成各种形式的内容，包括文本、图像、音频和视频等。AIGC技术利用深度学习和自然语言处理等算法，可以快速高效地创作出富有创意的作品。',1845186388480352258,1,NULL,'2024-10-13 03:51:09','2024-10-13 04:08:35'),(1845190840239050753,'OpenAI','http://www.nbcpe.cn:9000/cni-bbs-open/userFile/common/tag/252cf0ede7a444d9.png','OpenAI是一家人工智能研究机构，成立于2015年，致力于推动友好的人工智能技术的发展，以造福全人类。它的使命是确保人工智能的安全和广泛惠益。',1845186388480352258,1,2,'2024-10-13 03:52:32','2024-10-13 04:08:35');
/*!40000 ALTER TABLE `tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `login_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录账号',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '用户昵称',
  `user_type` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '00' COMMENT '用户类型（00系统用户 01注册用户02第三方平台注册用户）',
  `is_private` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否为公开账户(0为私有,1为公开）',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '用户邮箱',
  `phone_number` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '手机号码',
  `sex` char(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '用户性别（0男 1女 -1未知）',
  `avatar` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '头像路径',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '密码',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '帐号状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `login_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '最后登录IP',
  `login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `pwd_update_time` datetime DEFAULT NULL COMMENT '密码最后更新时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '创建者',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '更新者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `bio` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '个人简介',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注(1为同意许可组成，2为不同意，3为未知）',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1853353979426164739 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='用户信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin','admin','00',0,'admin@gmail.com','13711111111','0','http://www.nbcpe.cn:9000/cni-bbs-open/userFile/1/avatar/56adb6f120fb4f1d.png','e7cc2a988d982d80c98446f4fc445764','0','0','','2024-12-01 20:51:18',NULL,'',NULL,'2024-10-13 02:46:17','2024-12-01 20:51:18','admin:今天天气正好',NULL),(2,'patriclee','patriclee','00',1,'patriclee@gmail.com','13711022832','2','http://www.nbcpe.cn:9000/cni-bbs-open/userFile/2/avatar/f6facd72eec24121.png','69f76815f9707ba5065ecf7c89f073df','0','0','','2024-12-01 20:51:18',NULL,'',NULL,'2024-10-13 02:46:17','2024-12-01 20:51:18','patriclee:挺适合睡觉的','1'),(3,'yannqing','yannqing','00',1,'yannqing@gmail.com','13711111113','0','http://www.nbcpe.cn:9000/cni-bbs-open/userFile/3/avatar/bef4e00190c346ef.jpg','e7cc2a988d982d80c98446f4fc445764','0','0','','2024-12-01 20:51:18',NULL,'','','2024-10-13 02:47:59','2024-12-01 20:51:18','不想说话',NULL),(4,'test','test','01',0,'test@gmail.com','13700000000','2','http://www.nbcpe.cn:9000/cni-bbs-open/userFile/4/avatar/b6cf64149c254034.png','e7cc2a988d982d80c98446f4fc445764','0','0','','2024-12-01 20:51:18',NULL,'','','2024-10-13 15:13:23','2024-12-01 20:51:18',NULL,NULL),(1852021843154812930,'test1','test1','01',0,'test1@gmail.com','13700000001','2','https://profile-avatar.csdnimg.cn/65578c9c4382408eaa4db3d67b4026c4_u010165006.jpg','e7cc2a988d982d80c98446f4fc445764','0','0','','2024-11-18 00:33:48',NULL,'','','2024-11-01 00:16:31','2024-11-18 00:33:47','你好世界！','1'),(1852023951924924417,'test2','test2','01',1,'test2@gmail.com','13700000002','1','https://profile-avatar.csdnimg.cn/65578c9c4382408eaa4db3d67b4026c4_u010165006.jpg','e7cc2a988d982d80c98446f4fc445764','1','0','',NULL,NULL,'','','2024-11-01 00:24:53','2024-11-01 00:24:53','好困想睡觉','1'),(1853353979426164738,'cyhlbh2010@gmail.com','BingHua Lee','02',1,'cyhlbh2010@gmail.com','','-1','https://lh3.googleusercontent.com/a/ACg8ocJ_XRQ_CrJWYGLHtv_xSBiMgozp4qS6jVovZ3n2Od_WE4ULyR0PMQ=s96-c','e7cc2a988d982d80c98446f4fc445764','0','0','',NULL,NULL,'106750474558215271643','','2024-11-04 16:29:57','2024-11-04 16:29:57',NULL,NULL);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_invite`
--

DROP TABLE IF EXISTS `user_invite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_invite` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `invite_by` bigint DEFAULT '0' COMMENT '邀请者ID',
  `invite_link` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '邀请链接',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`,`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1852023952621178882 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='邀请链接表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_invite`
--

LOCK TABLES `user_invite` WRITE;
/*!40000 ALTER TABLE `user_invite` DISABLE KEYS */;
INSERT INTO `user_invite` VALUES (1,1,0,'HOPjJqGT','2024-10-31 01:32:39','2024-10-31 01:33:53'),(2,2,0,'HOPjJqGJ','2024-10-31 01:32:39','2024-10-31 01:34:13'),(3,3,0,'HOPjJAta','2024-10-31 01:32:39','2024-10-31 01:34:13'),(4,4,0,'JHYTEBN9','2024-10-31 01:32:39','2024-10-31 01:34:13'),(1852021854819172354,1852021843154812930,1,'42794lol','2024-11-01 00:16:33','2024-11-01 00:24:38'),(1852023952621178881,1852023951924924417,3,'a9k6yi97','2024-11-01 00:24:53','2024-11-01 00:24:53');
/*!40000 ALTER TABLE `user_invite` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_role_rel`
--

DROP TABLE IF EXISTS `user_role_rel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_role_rel` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint unsigned NOT NULL COMMENT '用户ID',
  `role_id` bigint unsigned NOT NULL COMMENT '角色ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '逻辑删除(0：未删除 1：已删除)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1853353979849736193 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关系表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_role_rel`
--

LOCK TABLES `user_role_rel` WRITE;
/*!40000 ALTER TABLE `user_role_rel` DISABLE KEYS */;
INSERT INTO `user_role_rel` VALUES (1,1,1,'2024-10-13 02:49:23','2024-10-13 02:49:29',_binary '\0'),(2,2,3,'2024-10-13 02:49:23','2024-10-13 02:49:29',_binary '\0'),(3,3,3,'2024-10-13 02:49:23','2024-10-13 02:49:29',_binary '\0'),(4,4,5,'2024-10-13 15:13:49','2024-10-13 15:13:49',_binary '\0'),(1852021843632848896,1852021843154812930,5,'2024-11-01 00:16:33','2024-11-01 00:16:33',_binary '\0'),(1852023952587620352,1852023951924924417,5,'2024-11-01 00:24:53','2024-11-01 00:24:53',_binary '\0'),(1853353979849736192,1853353979426164738,6,'2024-11-04 16:29:57','2024-11-04 16:29:57',_binary '\0');
/*!40000 ALTER TABLE `user_role_rel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'titan-bbs'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-01-17 16:59:16
