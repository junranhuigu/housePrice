package com.junranhuigu.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtil {
	/**累加保存*/
	public static void save(List<? extends Object> records, String filePath, Charset charset) throws Exception{
		System.out.println("开始保存" + filePath);
		long time = System.currentTimeMillis();
		File file = new File(filePath);
		if(!file.exists()){
			file.createNewFile();
		}
		if(records == null){
			records = Collections.emptyList();
		}
		if(records.isEmpty()){
			System.out.println("内容为空，不予保存");
			return;
		}
		RandomAccessFile fos = new RandomAccessFile(file, "rw");
		fos.seek(fos.length());
		for(Object record : records){
			fos.write(record.toString().getBytes(charset));
		}
		fos.close();
		System.out.println(filePath + "保存完毕");
		System.out.println("共计耗时 ： " + (System.currentTimeMillis() - time) + "毫秒");
	}
	
	/**覆盖保存*/
	public static void cover(List<? extends Object> records, String filePath, Charset charset) throws Exception{
		System.out.println("开始保存" + filePath);
		long time = System.currentTimeMillis();
		File file = new File(filePath);
		if(!file.exists()){
			file.createNewFile();
		}
		if(records == null){
			records = Collections.emptyList();
		}
		if(records.isEmpty()){
			System.out.println("内容为空，不予保存");
			return;
		}
		try(	FileOutputStream fos = new FileOutputStream(file);){ 
			for(Object record : records){
				fos.write(record.toString().getBytes(charset));
			}
		}
		System.out.println(filePath + "保存完毕");
		System.out.println("共计耗时 ： " + (System.currentTimeMillis() - time) + "毫秒");
	}
	
	public static void zipFile(File file, ZipOutputStream zos){
		try {
            if(file.exists()) {
                if (file.isFile()) {
                	try(	FileInputStream IN = new FileInputStream(file);
                            BufferedInputStream bins = new BufferedInputStream(IN, 512);){
                		ZipEntry entry = new ZipEntry(file.getName());
                        zos.putNextEntry(entry);
                        // 向压缩文件中输出数据   
                        int nNumber;
                        byte[] buffer = new byte[512];
                        while ((nNumber = bins.read(buffer)) != -1) {
                            zos.write(buffer, 0, nNumber);
                        }
                	}
                } else {
                	 File[] files = file.listFiles();
                     for (int i = 0; i < files.length; ++ i) {
                         zipFile(files[i], zos);
                     }
                }
            }
        } catch (Exception e) {
            System.err.println(file.getName() + "ZIP压缩失败");
            e.printStackTrace();
        }
	}
	
	public static void zipFile(String fileName, byte[] is, ZipOutputStream zos) {
		try (	ByteArrayInputStream bis = new ByteArrayInputStream(is); 
				BufferedInputStream bins = new BufferedInputStream(bis, 512);) {
			ZipEntry entry = new ZipEntry(fileName);
			zos.putNextEntry(entry);
			// 向压缩文件中输出数据
			int nNumber;
			byte[] buffer = new byte[512];
			while ((nNumber = bins.read(buffer)) != -1) {
				zos.write(buffer, 0, nNumber);
			}
		} catch (Exception e) {
			 System.err.println(fileName + "ZIP压缩失败");
			 e.printStackTrace();
		}
	}
	
	/**
	 * 判断文件的编码格式
	 * @param fileName :file
	 * @return 文件编码格式
	 * @throws Exception
	 */
	public static String fileCodeString(FileInputStream fis) throws Exception{
		BufferedInputStream bin = new BufferedInputStream(fis);
		int p = (bin.read() << 8) + bin.read();
		String code = null;
		switch (p) {
			case 0xefbb:
				code = "UTF-8";
				break;
			case 0xfffe:
				code = "Unicode";
				break;
			case 0xfeff:
				code = "UTF-16BE";
				break;
			default:
				code = "GBK";
		}
		return code;
	}
	
	/**
	 * MD5校验
	 * */
	public static String md5(byte[] bs) {
		if(bs != null){
			try {
				MessageDigest digest = MessageDigest.getInstance("MD5"); 
				digest.update(bs);
				byte[] bytes = digest.digest();
				StringBuilder builder = new StringBuilder();
				for(int i = 0; i < bytes.length; ++ i){
					int md = bytes[i];
					if(md < 0){
						md += 256;
					}
					if(md < 16){
						builder.append("0");
					}
					builder.append(Integer.toHexString(md));
				}
				return builder.toString();
			} catch (Exception e) {
				System.out.println("MD5 加密失败");
			}
		}
		return "";
	}
}
