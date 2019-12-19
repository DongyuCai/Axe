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
package org.axe.helper.mvc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.axe.bean.mvc.FileParam;
import org.axe.bean.mvc.FormParam;
import org.axe.bean.mvc.Param;
import org.axe.helper.base.ConfigHelper;
import org.axe.util.CollectionUtil;
import org.axe.util.FileUtil;
import org.axe.util.LogUtil;
import org.axe.util.RequestUtil;
import org.axe.util.StreamUtil;
import org.axe.util.StringUtil;

/**
 * 表单提交请求 助手类
 * @author CaiDongyu on 2016/4/25.
 */
public final class FormRequestHelper {
    /**
     * Apache Commons FileUpload 提供的 Servlet 文件上传对象
     */
    private static ServletFileUpload servletFileUpload;
    /**
     * 初始化
     */
    public static void init(ServletContext servletContext) throws Exception{
        File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
        servletFileUpload = new ServletFileUpload(new DiskFileItemFactory(DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD, repository));
        int uploadLimit = ConfigHelper.getAppUploadLimit();
        if (uploadLimit > 0) {
            servletFileUpload.setFileSizeMax(uploadLimit * 1024 * 1024);
        }
    }

    /**
     * 判断请求是否为multipart类型
     */
    public static boolean isMultipart(HttpServletRequest request){
        return ServletFileUpload.isMultipartContent(request);
    }

    /**
     * 创建请求对象
     */
    public static void initParam(Param param,HttpServletRequest request,String requestPath,String mappingPath)throws Exception{
        List<FormParam> formParamList = new ArrayList<>();
        List<FileParam> fileParamList = new ArrayList<>();
        try {
        	//解析表单请求参数、文件
            Map<String,List<FileItem>> fileItemListMap = servletFileUpload.parseParameterMap(request);
            if(CollectionUtil.isNotEmpty(fileItemListMap)){
                for(Map.Entry<String,List<FileItem>> fileItemListEntry: fileItemListMap.entrySet()){
                    String fieldName = fileItemListEntry.getKey();
                    List<FileItem> fileItemList = fileItemListEntry.getValue();
                    if(CollectionUtil.isNotEmpty(fileItemList)){
                        for(FileItem fileItem:fileItemList){
                            if(fileItem.isFormField()){
                                String fieldValue = fileItem.getString("UTF-8");
                                formParamList.add(new FormParam(fieldName,fieldValue));
                            }else{
                                String fileName = FileUtil.getRealFileName(new String(fileItem.getName().getBytes(),"UTF-8"));
                                if(StringUtil.isNotEmpty(fileName)){
                                    long fileSize = fileItem.getSize();
                                    String contentType = fileItem.getContentType();
                                    InputStream inputStream = fileItem.getInputStream();
                                    fileParamList.add(new FileParam(fieldName,fileName,fileSize,contentType,inputStream));
                                }
                            }
                        }
                    }
                }
            }
            
            //解析url请求参数
            formParamList.addAll(RequestUtil.parseParameter(request,requestPath,mappingPath));
        }catch (Exception e){
            LogUtil.error(e);
            throw e;
        }
        param.init(null,formParamList,fileParamList,null);
    }

    /**
     * 上传文件
     */
    public static void uploadFile(String basePath, FileParam fileParam){
        try {
            if(fileParam  != null){
                String filePath = basePath + fileParam.getFieldName();
                FileUtil.createFile(filePath);
                InputStream inputStream = new BufferedInputStream(fileParam.getInputStream());
                OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(filePath));
                StreamUtil.copyStream(inputStream,outputStream);
            }
        } catch (Exception e){
            LogUtil.error(e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 批量上传文件
     */
    public static void uploadFile(String basePath,List<FileParam> fileParamList){
        try {
            if(CollectionUtil.isNotEmpty(fileParamList)){
                for(FileParam fileParam:fileParamList){
                    uploadFile(basePath,fileParam);
                }
            }
        } catch (Exception e){
            LogUtil.error(e);
            throw new RuntimeException(e);
        }
    }
}
