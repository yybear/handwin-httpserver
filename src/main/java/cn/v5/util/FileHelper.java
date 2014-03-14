package cn.v5.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * @author qgan
 * @version 2014年2月25日 下午2:21:10
 */
public class FileHelper {
	private static final Logger log = LoggerFactory.getLogger(FileHelper.class);
	public static enum FILE_STATUS {
		DELETED(-1),
		UNAUDITED(0), 
		AUDITED(1);
		
		private int flag;
		private FILE_STATUS(int flag) {
			this.flag = flag;
		}
		
		public int getFlag() {
			return flag;
		}
	};
	
	public static final Set<String> IMG_SUFFIX = new HashSet<String>(Arrays.asList(new String[]{"gif", "bmp", "jpg", "jpeg", "png"}));
    public static final Set<String> VIDEO_SUFFIX = new HashSet<String>(Arrays.asList(new String[]{"mpg", "mpeg", "mpe", "avi", "mov", "asf", "mp4", "wmv", "flv", "3gp"}));
	
    public static final String IMG = "image";
    public static final String VIDEO = "video";
    public static final String NORMAL = "normal";
    
    public static final String TMP_SUFFIX = ".utmp";
    
    public static final byte CR = 0x0D;
    public static final byte LF = 0x0A;
    public static final byte DASH = 0x2D;
	
	private static String getStorgeRoot() {
		String root = ConfigUtils.getString("file.storage.path");
		if(StringUtils.isBlank(root)) {
			root = "/opt/faceshow/data/file/";
		}
		if(!root.endsWith("/"))
			root += "/";
		return root;
	}
	
	/**
	 * 获取服务器上面的文件名称
	 * @param fileName
	 * @return
	 */
	public static String getStorgeName(String fileName) {
		return fileName;//DigestUtils.md5Hex(fileName);
	} 
	
	/**
	 * 获取服务器上面文件的存储目录
	 * @param fileName
	 * @return
	 */
	public static String getStorgeDir(String fileName) {
		StringBuilder sb = new StringBuilder(getStorgeRoot());
		String suffix = getFileNameSuffix(fileName);
		
		if(IMG_SUFFIX.contains(suffix)) {
			sb.append(IMG).append(File.separator);
		} else if(VIDEO_SUFFIX.contains(suffix)) {
			sb.append(VIDEO).append(File.separator);
		} else {
			sb.append(NORMAL).append(File.separator);
		}
		sb.append(getRelativeStorgeDir(fileName));
		return sb.toString();
	}
	
	public static String getRelativeStorgeDir(String fileName) {
		String storgeName = getStorgeName(fileName);
		StringBuilder sb = new StringBuilder();
		sb.append(storgeName.substring(0, 2))
				.append(/*File.separator*/"/").append(storgeName.substring(2, 4)).append(/*File.separator*/"/");
		return sb.toString();
	}
	
	/**
	 * 获取服务器上面文件的存储路径
	 * @param fileName
	 * @return
	 */
	public static String getStorgePath(String fileName) {
		String storgeName = getStorgeName(fileName);
		String dir = getStorgeDir(fileName);
		return dir + storgeName;
	}
	
	/**
	 * 获取服务器上面文件的存储路径
	 * @param fileName
	 * @return
	 */
	public static String getRelativeStorgePath(String fileName) {
		String storgeName = getStorgeName(fileName);
		String dir = getRelativeStorgeDir(fileName);
		return dir + storgeName;
	}
	
	/**
	 * 拼装数据库存储的地址得到文件存放的真实地址
	 * @param path
	 * @param typeDir
	 * @return
	 */
	public static String assemblePath(String path, String typeDir) {
		StringBuilder sb = new StringBuilder(getStorgeRoot()).append(typeDir).append(File.separator);
		sb.append(path);
		return sb.toString();
	}
	
	/**
	 * 获取服务器上面文件的存储路径
	 * @param fileName
	 * @return
	 */
	public static String getThumbPath(String fileName) {
		String storgeName = getStorgeName(fileName);
		String dir = getThumbDir(fileName);
		return dir + storgeName;
	}
	
	/**
	 * 获取服务器图片缩率图的存储目录
	 * @param fileName
	 * @return
	 */
	public static String getThumbDir(String fileName) {
		StringBuilder sb = new StringBuilder(getStorgeRoot()).append(getRelativeThumbDir(fileName));
		return sb.toString();
	}
	
	/**
	 * 获取服务器图片缩率图的存储相对目录
	 * @param fileName
	 * @return
	 */
	public static String getRelativeThumbDir(String fileName) {
		String storgeName = getStorgeName(fileName);
		StringBuilder sb = new StringBuilder("thumb/");
		return sb.append(storgeName.substring(0, 2)).append(File.separator)
				.append(storgeName.substring(2, 4)).append(File.separator).toString();
	}
	
	/**
	 * 获取服务器图片缩率图的存储相对路径
	 * @param fileName
	 * @return
	 */
	public static String getRelativeThumbPath(String fileName) {
		String storgeName = getStorgeName(fileName);
		StringBuilder sb = new StringBuilder();
		return sb.append(getRelativeThumbDir(fileName)).append(File.separator).append(storgeName).toString();
	}
	
	/**
	 * 获取文件的访问地址
	 * @param fileName
	 * @return
	 */
	public static String getFileUrl(String fileName) {
		String base = ConfigUtils.getString("base.url");
		StringBuilder sb = new StringBuilder(base);
		if(!base.endsWith("/"))
			sb.append("/");
		sb.append("api/file/").append(fileName);
		
		return sb.toString();
	}
	
	/**
	 * 获取图片缩率图的访问地址
	 * @param fileName
	 * @return
	 */
	public static String getThumbUrl(String fileName) {
		String base = ConfigUtils.getString("base.url");
		StringBuilder sb = new StringBuilder(base);
		if(!base.endsWith("/"))
			sb.append("/");
		sb.append("api/file/thumb/").append(fileName);
		
		return sb.toString();
	}
	
	/**
	 * 返回文件的后缀
	 * @return
	 */
	public static String getFileNameSuffix(String filename) {
		String fileSuffix = "";
		if(filename.indexOf(".") > 0)
			fileSuffix = filename.substring(filename.lastIndexOf(".") + 1);
		return fileSuffix;
	}
	
	/**
	 * 保存文件，支持断点续传
	 * @throws Exception 
	 */
	public static File storeFile(File destFile, File srcFile) throws IOException {
		String filename = srcFile.getName();
		long size = srcFile.length(); // 本次上传的大小
        if(!destFile.exists()) {
        	String dir = FileHelper.getStorgeDir(filename);
        	new File(dir).mkdirs();
        	destFile.createNewFile();
        }
        InputStream in = null;
        RandomAccessFile tempFile = null;
        try {
        	in = new FileInputStream(srcFile);
        	tempFile = new RandomAccessFile(destFile, "rw");
	    	long readTotal = 0;
	    	int tempSize = 2048;
	    	byte[] bytes = new byte[tempSize];
	    	while(((readTotal+tempSize) < size) && in.read(bytes, 0, tempSize) > 0) { // 总量不能大于size，而且能读到内容
	    		// 文件存在 则将内容附加到文件后面
	        	tempFile.seek(destFile.length());
	            tempFile.write(bytes);
	            readTotal += tempSize;
	    	}
	    	// 内容少于tempSize
	    	tempSize = (int)(size - readTotal); // 获取还差多少数据
	    	if(tempSize > 0) {
		    	bytes = new byte[tempSize];
		    	in.read(bytes, 0, tempSize);
		    	tempFile.seek(destFile.length());
		    	tempFile.write(bytes);
	    	}
        } catch (IOException e) {
        	log.error(e.getMessage(), e);
			throw e;
		} finally {
			IOUtils.closeQuietly(tempFile);
			IOUtils.closeQuietly(in);
		}
        
        return destFile;
	}
	
	static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
	public static void saveFileFromTmp(File tmpFile, File destFile) throws IOException {
		InputStream in = new FileInputStream(tmpFile);
		int boundaryLen = parseBoundaryLen(in);
		log.debug("boundaryLen is {}", boundaryLen);
		int headerLen = startBody(in); // 找到文件的起始位置
		log.debug("headerLen is {}", headerLen);
		int endLen = boundaryLen + 5; // 结尾最大的长度
		// 最大限度可以读取的长度
		long canReandLen = tmpFile.length() - (boundaryLen + 2) - headerLen - endLen; 
		log.debug("canReandLen is {}", canReandLen);
		if(!destFile.exists()) {
        	String dir = FileHelper.getStorgeDir(destFile.getName());
        	new File(dir).mkdirs();
        	destFile.createNewFile();
        }
		
		OutputStream out = new FileOutputStream(destFile, true);
		try {
			byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			if((canReandLen + endLen) <= DEFAULT_BUFFER_SIZE) { // 一次read就可读完
				log.debug("can read all content in one time");
				int n = in.read(buffer);
				if(buffer[n-1] == LF && buffer[n-2] == CR && buffer[n-3] == DASH && buffer[n-4] == DASH) {
	    			out.write(buffer, 0, n - endLen);
	    		} else if(buffer[n-1] == LF && buffer[n-2] == CR) { // TODO: 目前只针对视频
	    			out.write(buffer, 0, n - 2);
	    		} else {
	    			out.write(buffer, 0, n);
	    		}
			} else {
				long reandLen = 0;
	            int n = 0;
	            while (reandLen + DEFAULT_BUFFER_SIZE < canReandLen && -1 != (n = in.read(buffer))) {
	            	reandLen = reandLen + n;
	            	out.write(buffer, 0, n);
	            }	
	            log.debug("DEFAULT_BUFFER_SIZE read len is {}", reandLen);
	            buffer = new byte[DEFAULT_BUFFER_SIZE + endLen]; // 读取最后一段
	            n = in.read(buffer);
				if(buffer[n-1] == LF && buffer[n-2] == CR && buffer[n-3] == DASH && buffer[n-4] == DASH) {
	    			out.write(buffer, 0, n - endLen);
	    		} else if(buffer[n-1] == LF && buffer[n-2] == CR) { // TODO: 目前只针对视频
	    			out.write(buffer, 0, n - 2);
	    		} else {
	    			out.write(buffer, 0, n);
	    		}
			}
        } catch (IOException e) {
			throw e;
		} finally {
			if(out != null)
				out.flush();
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(in);
			
			FileUtils.deleteQuietly(tmpFile);
		}
	}
	
	public static void transferTo(File file, long position, long count,
			WritableByteChannel target) throws IOException {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			fis.getChannel().transferTo(position, count, target);
		} catch (IOException e) {
			throw e;
		} finally {
			IOUtils.closeQuietly(fis);
		}
	}
	
	private static int[] Content_Disposition = {'C','o','n','t','e','n','t','-','D','i','s','p','o','s','i','t','i','o','n'};
	private static int Content_Disposition_Size = 19;
	public static String parseFileName(InputStream in) throws IOException {
		char[] names = new char[200];
		int index = 0;
    	while(true) {
    		int ch = in.read();
    		if(ch < 0) {
    			break;
    		}
    		while(ch == Content_Disposition[index]) {
    			index++;
    			if(index == Content_Disposition_Size) {
    				// 完整了
    				break;
    			}
    			ch = in.read();
    		}
    		if(index == Content_Disposition_Size) {
    			in.read(new byte[40], 0, 36);
    			int i = 0;
    			while(true) {
    				ch = in.read();
    				if(ch != 34) { // "
    					names[i++] = (char)ch;
    				} else {
    					break;
    				}
    			}
    			break;
    		} else {
    			index = 0;
    		}
    	}
    	return new String(names).trim();
    }
    
	public static int startBody(InputStream in) throws IOException {
		int len = 0;
		while(true) {
    		int ch = in.read(); len++;
    		if(ch < 0) {
    			break;
    		}	
    		if(ch == CR) {
    			ch = in.read();len++;
	    		if(ch == LF) {
	    			ch = in.read();len++;
	    			if(ch == CR) {
	    				ch = in.read();len++;
	    				if(ch == LF) {
	    					len--;
	    					break;
	    				}
	    			}
	    		}
    		}
    	}
		
		return len;
	}
	
	public static int parseBoundaryLen(InputStream in) throws IOException {
		int index = 0;
		while(true) {
    		int ch = in.read(); index++;
    		if(ch == CR) {
    			ch = in.read(); index++;
	    		if(ch == LF) {
	    			return (index-1);
	    		}
    		}
    	}
	}
	
	public static void main(String[] args) throws IOException {
		File tmpFile = new File("d:/new.txt");
		//File destFile = new File("d:/test1");
    	
		//saveFileFromTmp(tmpFile, destFile);
		
		System.out.println("boundaryLen " + tmpFile.length());
		InputStream in = new FileInputStream(tmpFile);
		int boundaryLen = parseBoundaryLen(in);
		System.out.println("boundaryLen " + boundaryLen);
		int headerLen = startBody(in); // 找到文件的起始位置
		System.out.println("headerLen " + headerLen);
		int endLen = boundaryLen + 5; // 结尾最大的长度
		System.out.println("endLen " + endLen);
		
		long canReandLen = tmpFile.length() - (boundaryLen + 2) - headerLen - endLen; 
		System.out.println("canReandLen " + canReandLen);
    	/*RandomAccessFile rf = new RandomAccessFile(f, "r");
    	rf.seek(f.length()-4);
    	System.out.println(rf.readByte());
    	System.out.println(rf.readByte());
    	System.out.println(rf.readByte());
    	System.out.println(rf.readByte());*/
	}
}