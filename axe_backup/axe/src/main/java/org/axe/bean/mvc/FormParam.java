package org.axe.bean.mvc;

/**
 * 表单参数
 * Created by CaiDongYu on 2016/4/25.
 */
public class FormParam {
    private String fieldName;
    private String fieldValue;

    public FormParam(String fieldName, String fieldValue) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldValue() {
        return fieldValue;
    }

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}
}
