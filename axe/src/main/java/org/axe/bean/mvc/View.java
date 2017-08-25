package org.axe.bean.mvc;

import java.util.HashMap;
import java.util.Map;

/**
 * Handler处理结果，返回如果是jsp
 * Created by CaiDongYu on 2016/4/11.
 */
public class View {
    /**
     * 视图路径
     */
    private String path;
    
    //默认redirect
    private boolean redirect = true;
    
    //默认是uri，认为是相对地址
    private boolean uri = true;

    /**
     * 模型数据
     */
    private Map<String,Object> model;

    public View(String path){
        this.path = path;
        model = new HashMap<>();
    }

    public  View addModel(String key,Object value){
        model.put(key,value);
        return this;
    }
    
    public String getPath() {
        return path;
    }

    public Map<String, Object> getModel() {
        return model;
    }
    
    public View dispatcher(){
    	redirect = false;
    	return this;
    }

	public boolean isRedirect() {
		return redirect;
	}
	
	public View asUrl(){
		uri = false;
		return this;
	}
	
	public boolean isUri(){
		return uri;
	}
}
