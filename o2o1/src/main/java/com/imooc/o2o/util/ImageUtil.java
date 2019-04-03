//改变图片
package com.imooc.o2o.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

public class ImageUtil {
	//String basePath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
	private static final SimpleDateFormat sDateFormat =new SimpleDateFormat("yyyyMMddHHmmss");
	private static final Random r= new Random();
	private static Logger logger= LoggerFactory.getLogger(ImageUtil.class);
	/**将CommonsMultipartFile转换成File类
	 * */
	public static File transferCommonsMultipartFileToFile(CommonsMultipartFile cFile) {
		File newFile = new File(cFile.getOriginalFilename());
		try {
			cFile.transferTo(newFile);
			
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			logger.error(e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.toString());
			e.printStackTrace();
		}
		return newFile;
	}
	/**处理缩略图，并返回新生成图片的相对路径
	 * */
	
	public static String genertateThumbnail(InputStream thumbnailInputStream,String fileName,String targetAddr) {
		String realFileName =getRandomFileName();
		String extension = getFileExtension(fileName);
		makeDirPath(targetAddr);
		String relativeAddr = targetAddr +realFileName + extension;
		logger.debug("current relative addr is:" + relativeAddr);
		File dest = new File(PathUtil.getImgBasePath() +relativeAddr);
		logger.debug("current complete addr is:" + PathUtil.getImgBasePath() +relativeAddr);
		try {
			Thumbnails.of(thumbnailInputStream).size(200,200).
			watermark(Positions.BOTTOM_RIGHT, ImageIO.
					read(new File("E:/新建文件夹/o2o1/src/main/resources/2.jpg")), 0.25f)
			.outputQuality(0.8f).toFile(dest);
			
		}
		catch(IOException e) {
			logger.error(e.toString());
			e.printStackTrace();
		}
		return relativeAddr;
	}
	/**获取输入文件流的扩展名
	 * */
	private static String getFileExtension(String fileName) {
		// TODO Auto-generated method stub
	
		return fileName.substring(fileName.lastIndexOf("."));
	}

	/**生成随机文件名 当前年月日小时分钟秒钟加五位随机数
	 * */
	public static String getRandomFileName() {
		int rannum =r.nextInt(89999)+10000;
		String nowTimeStr=sDateFormat.format(new Date());
		return nowTimeStr + rannum;
	}
	/**创建目标路径所涉及的目录
	 * */
	private static void makeDirPath(String targetAddr) {
		String realFileParentPath =PathUtil.getImgBasePath() +targetAddr;
		File dirPath= new File(realFileParentPath);
		if(!dirPath.exists()) {
			dirPath.mkdirs();
		}
	}
	public static void main(String[] args) throws IOException {
	
		Thumbnails.of(new File("C:/Users/37602/Desktop/1.jpg")).size(200, 200)
				.watermark(Positions.BOTTOM_RIGHT, ImageIO.read(new File("E:/新建文件夹/o2o1/src/main/resources/2.jpg")), 0.25f)
				.outputQuality(0.8f).toFile("C:/Users/37602/Desktop/3.jpg");
	}
}
 