/**
 * MIT License
 * 
 * Copyright (c) 2017 CaiDongyu
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.axe.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 * 文件操作 工具类
 * @author CaiDongyu on 2016/4/25.
 */
public final class FileUtil {
    private FileUtil() {}
    
	/**
	 * 复制文件
	 * 
	 * @param srcPath
	 *            源文件绝对路径
	 * @param destDir
	 *            目标文件所在目录
	 * @return boolean
	 * @throws Exception 
	 */
	public static void copy(String srcPath, String destDir) throws Exception {
		File srcFile = new File(srcPath);
		if (!srcFile.exists()) { // 源文件不存在
			throw new Exception("源文件不存在！"+srcPath);
		}
		
		// 获取待复制文件的文件名
		String fileName = srcPath.substring(srcPath.lastIndexOf(File.separator));
		String destPath = destDir + fileName;
		if (destPath.equals(srcPath)) { // 源文件路径和目标文件路径重复
			throw new Exception("源文件路径和目标文件路径重复!"+srcPath);
		}
		
		File destFile = new File(destPath);
		/*if (destFile.exists() && destFile.isFile()) { // 该路径下已经有一个同名文件
			System.out.println("目标目录下已有同名文件!");
			return false;
		}*/
 
		File destFileDir = new File(destDir);
		destFileDir.mkdirs();
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(srcPath);
			fos = new FileOutputStream(destFile);
			byte[] buf = new byte[1024];
			int c;
			while ((c = fis.read(buf)) != -1) {
				fos.write(buf, 0, c);
			}
			
		} catch (Exception e) {
			throw e;
		} finally {
			if(fis != null){
				try {
					fis.close();
				} catch (Exception e2) {
				}
			}
			if(fos != null){
				try {
					fos.close();
				} catch (Exception e2) {
				}
			}
		}
	}
    
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
            LogUtil.error(e);
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
            LogUtil.error(e);
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
