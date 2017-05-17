package org.axe.bean.mvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.axe.util.CollectionUtil;
import org.axe.util.StringUtil;

/**
 * 请求参数对象
 * Created by CaiDongYu on 2016/4/11.
 */
public class Param {
	private String requestPath;
	private String body;
	private List<FormParam> formParamList;
	private List<FileParam> fileParamList;
	private Map<String,Object> bodyParamMap;
	
	private List<Object> actionParamList;

	public Param(String requestPath) {
		this.requestPath = requestPath;
		body = "";
		formParamList = new ArrayList<>();
		fileParamList = new ArrayList<>();
		bodyParamMap = new HashMap<>();
		
		actionParamList = new ArrayList<>();
	}
	
    public void init(String body, List<FormParam> formParamList, List<FileParam> fileParamList, Map<String,Object> bodyParamMap) {
    	this.body = body;
    	this.formParamList = formParamList;
        this.fileParamList = fileParamList;
        this.bodyParamMap = bodyParamMap;
        
        //formParamList和bodyParamMap互相补全
        Map<String,Object> tmpMap = new HashMap<>();
        if(this.bodyParamMap != null){
        	for(Map.Entry<String, Object> entry:this.bodyParamMap.entrySet()){
        		tmpMap.put(entry.getKey(), entry.getValue());
        	}
        }
        
        if(this.formParamList != null){
        	for(FormParam fp:formParamList){
        		if(this.bodyParamMap == null){
        			this.bodyParamMap = new HashMap<>();
        		}
        		this.bodyParamMap.put(fp.getFieldName(), fp.getFieldValue());
        	}
        }
        
        if(tmpMap.size() > 0){
        	for(Map.Entry<String, Object> entry:this.bodyParamMap.entrySet()){
        		String fieldName = entry.getKey();
        		if(entry.getValue() instanceof List){
        			for(Object obj:(List<?>)entry.getValue()){
        				String fieldValue = String.valueOf(obj);
        				formParamList.add(new FormParam(fieldName, fieldValue));
        			}
        		}else{
        			String fieldValue = String.valueOf(entry.getValue());
    				formParamList.add(new FormParam(fieldName, fieldValue));
        		}
        	}
        }
    }
    
    public String getRequestPath() {
		return requestPath;
	}

	public void setRequestPath(String requestPath) {
		this.requestPath = requestPath;
	}

	public void setBody(String body) {
		this.body = body;
	}

    public String getBody() {
		return body;
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

    public List<FormParam> getFormParamList() {
		return formParamList;
	}

	public void setFormParamList(List<FormParam> formParamList) {
		this.formParamList = formParamList;
	}

	public List<FileParam> getFileParamList() {
		return fileParamList;
	}

	public void setFileParamList(List<FileParam> fileParamList) {
		this.fileParamList = fileParamList;
	}

	public Map<String, Object> getBodyParamMap() {
		return bodyParamMap;
	}

	public void setBodyParamMap(Map<String, Object> bodyParamMap) {
		this.bodyParamMap = bodyParamMap;
	}

	public boolean isEmpty(){
        return CollectionUtil.isEmpty(formParamList) && 
        		CollectionUtil.isEmpty(fileParamList) && 
        		CollectionUtil.isEmpty(bodyParamMap) && 
        		StringUtil.isEmpty(body);
    }

	public List<Object> getActionParamList() {
		return actionParamList;
	}

	public void setActionParamList(List<Object> actionParamList) {
		this.actionParamList = actionParamList;
	}
}
