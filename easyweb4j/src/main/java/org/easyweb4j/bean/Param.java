package org.easyweb4j.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easyweb4j.util.CollectionUtil;

/**
 * 请求参数对象
 * Created by CaiDongYu on 2016/4/11.
 */
public class Param {
    private List<FormParam> formParamList;
    private List<FileParam> fileParamList;
//    private Map<String,Object> paramMap;


    public Param(List<FormParam> formParamList) {
        this.formParamList = formParamList;
    }

    public Param(List<FormParam> formParamList, List<FileParam> fileParamList) {
        this.formParamList = formParamList;
        this.fileParamList = fileParamList;
    }

    /**
     * 获取请求参数的映射
     */
    @SuppressWarnings("unchecked")
	public Map<String,Object> getFieldMap(){
        Map<String,Object> fieldMap = new HashMap<>();
        if(CollectionUtil.isNotEmpty(formParamList)){
            for(FormParam formParam:formParamList){
                String fieldName = formParam.getFieldName();
                Object fieldValue = formParam.getFieldValue();
                if(fieldMap.containsKey(fieldName)){
                    Object firstValue = fieldMap.get(fieldName);
                    if(firstValue instanceof List){
                        List<Object> values = (List<Object>)firstValue;
                        values.add(fieldValue);
                    }else{
                        List<Object> values = new ArrayList<>();
                        values.add(firstValue);
                        values.add(fieldValue);
                        fieldMap.put(fieldName,values);
                    }
                }else{
                    fieldMap.put(fieldName,fieldValue);
                }
            }
        }
        return fieldMap;
    }

    public Map<String,List<FileParam>> getFileMap(){
        Map<String,List<FileParam>> fileMap = new HashMap<>();
        if(CollectionUtil.isNotEmpty(fileParamList)){
            for(FileParam fileParam:fileParamList){
                String fieldName = fileParam.getFieldName();
                List<FileParam> fileParamList;
                if(fileMap.containsKey(fieldName)){
                    fileParamList = fileMap.get(fieldName);
                }else{
                    fileParamList = new ArrayList<>();
                }
                fileParamList.add(fileParam);
                fileMap.put(fieldName,fileParamList);
            }
        }
        return fileMap;
    }

    public boolean isEmpty(){
        return CollectionUtil.isEmpty(formParamList) && CollectionUtil.isEmpty(fileParamList);
    }
}
