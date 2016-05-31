package org.jw.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 文件操作 工具类
 * Created by CaiDongYu on 2016/4/25.
 */
public final class FileUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

    private FileUtil() {}
    
    /**
     * 获取真实文件名 (自动去掉文件路径)
     */
    public static String getRealFileName(String fileName){
        return FilenameUtils.getName(fileName);
    }

    /**
     * 创建文件
     */
    public static File createFile(String filePath){
        File file;
        try {
            file = new File(filePath);
            File parentDir = file.getParentFile();
            if(parentDir != null && !parentDir.exists()){
                FileUtils.forceMkdir(parentDir);
            }
        }catch (Exception e){
            LOGGER.error("create file failure",e);
            throw new RuntimeException(e);
        }
        return file;
    }
    
    public static File backupAndCreateNewFile(String filePath){
        try {
        	File file = new File(filePath);
            File parentDir = file.getParentFile();
            if(parentDir != null && !parentDir.exists()){
                FileUtils.forceMkdir(parentDir);
            }else{
            	if(file.exists()){
            		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            		file.renameTo(new File(filePath+"-"+sdf.format(new Date())));
            	}
            }
            file = new File(filePath);
            return file;
        }catch (Exception e){
            LOGGER.error("create file failure",e);
            throw new RuntimeException(e);
        }
    }

}
