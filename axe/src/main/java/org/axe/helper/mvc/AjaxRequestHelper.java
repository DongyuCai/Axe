/**
 * MIT License
 * 
 * Copyright (c) 2017 The Axe Project
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.axe.bean.mvc.FormParam;
import org.axe.bean.mvc.Param;
import org.axe.util.JsonUtil;
import org.axe.util.RequestUtil;
import org.axe.util.StreamUtil;
import org.axe.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 请求助手类
 * @author CaiDongyu on 2016/4/25.
 */
public final class AjaxRequestHelper {
	private static final Logger LOGGER = LoggerFactory.getLogger(AjaxRequestHelper.class);
	
    @SuppressWarnings("unchecked")
	public static void initParam(Param param,HttpServletRequest request,String requestPath,String mappingPath)throws IOException{
        List<FormParam> formParamList = new ArrayList<>();
        formParamList.addAll(RequestUtil.parseParameter(request,requestPath,mappingPath));
//        String body = CodeUtil.decodeURL(StreamUtil.getString(request.getInputStream()));
        String body = StreamUtil.getString(request.getInputStream());
        Map<String,Object> bodyParamMap = null;
        if(StringUtil.isNotEmpty(body)){
            try {
            	if(StringUtil.isNotEmpty(body)){
            		if(body.startsWith("{") && body.endsWith("}")){
            			bodyParamMap = JsonUtil.fromJson(body, Map.class);
            		}
            		if(body.startsWith("[") && body.endsWith("]")){
            			List<Object> list = JsonUtil.fromJson(body, List.class);
            			bodyParamMap = new HashMap<String,Object>();
            			bodyParamMap.put("list", list);
            		}
            	}
            } catch (Exception e){
            	LOGGER.error("read body to json failure,body is: "+body);
            }
        }
        param.init(body,formParamList,null,bodyParamMap);
    }
}
