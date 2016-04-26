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

    @Action("get:/test")
    public Data get(Param param){
        System.out.println(param.getFieldMap().get("param"));
        Data data = new Data(param.getFieldMap().get("param")+" success");
        return data;
    }

    @Action("post:/test")
    public Data test(Param param){
        System.out.println(param.getFieldMap().get("param"));
        Data data = new Data(param.getFieldMap().get("param")+" success");
        return data;
    }

    @Action("get:/tojsp")
    public View tojsp(Param param){
        View view = new View("/test");
        return view;
    }
}
