////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 2017年11月5日 下午3:55:15
// 原始文件路径: xBoson/src/com/xboson/script/lib/LocalFile.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.script.lib;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.script.IVirtualFileSystem;
import com.xboson.util.SysConfig;

/**
 * 在本地文件系统上映射脚本文件
 */
public class LocalFile implements IVirtualFileSystem {
	
	public static final String scriptdir = "/script-dir";
	
	private Log log = LogFactory.create();
	private String id;
	private String base;
	private FileSystem fs;

	
	public LocalFile(String id) throws IOException {
		this.id = id;
		this.fs = FileSystems.getDefault();
		this.base = SysConfig.getInstance().readConfig().configPath;
		Files.createDirectories(fs.getPath(base, scriptdir, id));
	}


	@Override
	public String readFile(String path) throws IOException {
		log.debug("load file", path);
		Path p = fs.getPath(base, scriptdir, id, path);
		return new String( Files.readAllBytes(p) );
	}


	@Override
	public String getID() {
		return id;
	}
}
