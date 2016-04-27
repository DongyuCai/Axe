package org.easyweb4j.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.easyweb4j.bean.BodyParam;
import org.easyweb4j.bean.FormParam;
import org.easyweb4j.bean.Param;
import org.easyweb4j.util.RequestUtil;

/**
 * 请求助手类
 * Created by CaiDongYu on 2016/4/25.
 */
public class AjaxRequestHelper {
    public static Param createParam(HttpServletRequest request,String requestPath,String mappingPath)throws IOException{
        List<FormParam> formParamList = new ArrayList<>();
        List<BodyParam> bodyParamList = new ArrayList<>();
        formParamList.addAll(RequestUtil.parseParameter(request,requestPath,mappingPath));
        bodyParamList.addAll(RequestUtil.parsePayload(request));
        return new Param(formParamList,null,bodyParamList);
    }
}
