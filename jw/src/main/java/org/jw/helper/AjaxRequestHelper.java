package org.jw.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jw.bean.FormParam;
import org.jw.bean.Param;
import org.jw.util.RequestUtil;

/**
 * 请求助手类
 * Created by CaiDongYu on 2016/4/25.
 */
public class AjaxRequestHelper {
    public static Param createParam(HttpServletRequest request,String requestPath,String mappingPath)throws IOException{
        List<FormParam> formParamList = new ArrayList<>();
        formParamList.addAll(RequestUtil.parseParameter(request,requestPath,mappingPath));
        Map<String,Object> bodyParamMap = RequestUtil.parsePayload(request);
        return new Param(formParamList,null,bodyParamMap);
    }
}
