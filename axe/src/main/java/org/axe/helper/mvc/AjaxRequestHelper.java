package org.axe.helper.mvc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.axe.bean.mvc.FormParam;
import org.axe.bean.mvc.Param;
import org.axe.util.RequestUtil;

/**
 * 请求助手类
 * Created by CaiDongYu on 2016/4/25.
 */
public final class AjaxRequestHelper {
    public static Param createParam(HttpServletRequest request,String requestPath,String mappingPath)throws IOException{
        List<FormParam> formParamList = new ArrayList<>();
        formParamList.addAll(RequestUtil.parseParameter(request,requestPath,mappingPath));
        Map<String,Object> bodyParamMap = RequestUtil.parsePayload(request);
        return new Param(formParamList,null,bodyParamMap);
    }
}
