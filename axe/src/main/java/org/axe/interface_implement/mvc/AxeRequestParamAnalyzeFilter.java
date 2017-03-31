package org.axe.interface_implement.mvc;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.axe.bean.mvc.Handler;
import org.axe.bean.mvc.Param;
import org.axe.exception.RestException;
import org.axe.helper.mvc.AjaxRequestHelper;
import org.axe.helper.mvc.FormRequestHelper;
import org.axe.interface_.mvc.Filter;

/**
 * Axe 请求参数解析Filter
 * 这是Axe 提供的Filter，层级为0
 * 所以如果想在解析参数之前来执行一些操作，可以把自己定义的Filter层级设置为小于0
 * 层级大于0的自定义Filter，都在此之后执行
 */
public class AxeRequestParamAnalyzeFilter implements Filter {

	@Override
	public void init() {
		
	}

	@Override
	public int setLevel() {
		return 0;
	}

	@Override
	public Pattern setMapping() {
		return Pattern.compile("^.*$");
	}

	@Override
	public Pattern setNotMapping() {
		return null;
	}

	@Override
	public boolean doFilter(HttpServletRequest request, HttpServletResponse response, Param param, Handler handler)
			throws RestException {
        if(FormRequestHelper.isMultipart(request)){
            //如果是文件上传
            FormRequestHelper.initParam(param,request,param.getRequestPath(),handler.getMappingPath());
        }else{
            //如果不是
            try {
				AjaxRequestHelper.initParam(param,request,param.getRequestPath(),handler.getMappingPath());
			} catch (IOException e) {
				e.printStackTrace();
				throw new RestException(RestException.SC_INTERNAL_SERVER_ERROR,e.getMessage());
			}
        }
		return true;
	}

	@Override
	public void doEnd() {}

}
