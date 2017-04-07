package org.axe.util;

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

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     * @param dir 将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful.
     *                 If a deletion fails, the method stops attempting to
     *                 delete and returns "false".
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }
}
