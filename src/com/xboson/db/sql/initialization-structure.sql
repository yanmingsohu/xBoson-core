//
// xBoson 平台附加数据库初始化过程
//
CREATE TABLE `sys_upload_files` (
  `filename` varchar(80) NOT NULL,
  `dir` varchar(200) NOT NULL,
  `content` longblob,
  `create-date` datetime(6) NOT NULL,
  `update-time` timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  `id` varchar(30) NOT NULL,
  `content-type` varchar(100) DEFAULT NULL,
  `apiid` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`filename`,`dir`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
