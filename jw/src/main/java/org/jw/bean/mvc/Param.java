package org.jw.bean.mvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jw.util.CollectionUtil;

/**
 * 请求参数对象
 * Created by CaiDongYu on 2016/4/11.
 */
public class Param {
    private List<FormParam> formParamList;
    private List<FileParam> fileParamList;
    private Map<String,Object> bodyParamMap;
//    private Map<String,Object> paramMap;


    public Param(List<FormParam> formParamList, List<FileParam> fileParamList, Map<String,Object> bodyParamMap) {
        this.formParamList = formParamList;
        this.fileParamList = fileParamList;
        this.bodyParamMap = bodyParamMap;
    }

    /**
     * 获取请求参数的映射
     */
	public Map<String,List<FormParam>> getFieldMap(){
        Map<String,List<FormParam>> fieldMap = new HashMap<>();
        if(CollectionUtil.isNotEmpty(formParamList)){
            for(FormParam formParam:formParamList){
                String fieldName = formParam.getFieldName();
                List<FormParam> formParamList = fieldMap.get(fieldName);
                if(formParamList == null){
                	formParamList = new ArrayList<FormParam>();
                    fieldMap.put(fieldName,formParamList);
                }
                formParamList.add(formParam);
            }
        }
        return fieldMap;
    }

    public Map<String,List<FileParam>> getFileMap(){
        Map<String,List<FileParam>> fileMap = new HashMap<>();
        if(CollectionUtil.isNotEmpty(fileParamList)){
            for(FileParam fileParam:fileParamList){
                String fieldName = fileParam.getFieldName();
                List<FileParam> fileParamList = fileMap.get(fieldName);
                if(fileParamList == null){
                    fileParamList = new ArrayList<>();
                    fileMap.put(fieldName,fileParamList);
                }
                fileParamList.add(fileParam);
            }
        }
        return fileMap;
    }

    public Map<String,Object> getBodyMap(){
        return bodyParamMap;
    }

    public boolean isEmpty(){
        return CollectionUtil.isEmpty(formParamList) && 
        		CollectionUtil.isEmpty(fileParamList) && 
        		CollectionUtil.isEmpty(bodyParamMap);
    }
}
