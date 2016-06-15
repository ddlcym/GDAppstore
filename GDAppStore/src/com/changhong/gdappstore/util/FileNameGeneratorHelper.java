package com.changhong.gdappstore.util;

import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;

public class FileNameGeneratorHelper implements FileNameGenerator {

	private static FileNameGeneratorHelper fngHelper;

	@Override
	public String generate(String arg0) {
//		System.out.println("加密前的图片名字----->" + arg0);
//		return MD5Util.getMD5Str(arg0);
		return arg0;
	}

	private FileNameGeneratorHelper() {

	}

	public static FileNameGeneratorHelper getInstance() {

		if (null == fngHelper) {
			fngHelper = new FileNameGeneratorHelper();
		}

		return fngHelper;
	}

}
