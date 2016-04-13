package org.smart4j.chapter3;

import org.smart4j.framework.annotation.Action;
import org.smart4j.framework.annotation.Controller;
import org.smart4j.framework.bean.Data;
import org.smart4j.framework.bean.Param;
import org.smart4j.framework.bean.View;

/**
 * Created by Administrator on 2016/4/8.
 */
@Controller
public class TestController {

    @Action("get:/get")
    public Data get(Param param){
        System.out.println(param.getParamMap().get("param"));
        Data data = new Data(param.getParamMap().get("param")+" success");
        return data;
    }

    @Action("get:/tojsp")
    public View tojsp(Param param){
        View view = new View("/test");
        return view;
    }
}
