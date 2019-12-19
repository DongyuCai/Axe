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
package org.axe.bean.mvc;

import java.util.HashMap;
import java.util.Map;

/**
 * Handler处理结果，返回如果是jsp
 * @author CaiDongyu on 2016/4/11.
 */
public final class View {
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
