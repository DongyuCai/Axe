package org.jw.bean;

/**
 * Post、Update等请求的 body 也就是payload部分
 * Created by CaiDongYu on 2016/4/25.
 */
public class BodyParam {
    private String fieldName;
    private Object fieldValue;
    
    public BodyParam(String fieldName,Object fieldValue) {
    	this.fieldName = fieldName;
    	this.fieldValue = fieldValue;
	}
    
    public String getFieldName() {
		return fieldName;
	}
    
    public Object getFieldValue() {
		return fieldValue;
	}
}
