/* CatfoOD 2017年11月5日 下午3:55:15 yanming-sohu@sohu.com Q.412475540 */

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
