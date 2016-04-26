package org.smart4j.chapter3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easyweb4j.annotation.Request;
import org.easyweb4j.annotation.Controller;
import org.easyweb4j.bean.Data;
import org.easyweb4j.bean.FileParam;
import org.easyweb4j.bean.Param;
import org.easyweb4j.bean.View;
import org.easyweb4j.constant.RequestMethod;

/**
 * Created by Administrator on 2016/4/8.
 */
@Controller
public class TestController {

    @Request(value="/getOne/{id}",method=RequestMethod.GET)
    public Data getOne(Param param){
    	Data data = analysisParam(param);
        return data;
    }
    
    @Request(value="/getOne/{otherId}/other",method=RequestMethod.GET)
    public Data getOtherOne(Param param){
    	Data data = analysisParam(param);
        return data;
    }
    
    @Request(value="/page",method=RequestMethod.GET)
    public Data getPage(Param param){
    	Data data = analysisParam(param);
        return data;
    }
    
    //==================== postman success ================//
    @Request(value="/get",method=RequestMethod.GET)
    public Data get(Param param){
    	Data data = analysisParam(param);
        return data;
    }

    @Request(value="/post",method=RequestMethod.POST)
    public Data post(Param param){
    	Data data = analysisParam(param);
        return data;
    }
    @Request(value="/upload",method=RequestMethod.POST)
    public Data upload(Param param){
    	Data data = analysisParam(param);
        return data;
    }

    @Request(value="/tojsp",method=RequestMethod.GET)
    public View tojsp(Param param){
        View view = new View("/test");
        return view;
    }
    
    public Data analysisParam(Param param){
        Map<String,Object> fieldMap = param.getFieldMap();
        Map<String,List<FileParam>> fileMap = param.getFileMap();
        Map<String,Object>  model = new HashMap<>();
        
        fieldMap.entrySet().forEach(entry->model.put(entry.getKey(),entry.getValue()));
        for(Map.Entry<String, List<FileParam>> file:fileMap.entrySet()){
        	String fieldName = file.getKey();
        	List<FileParam> fileParamList = file.getValue();
        	List<String> fileNameList = new ArrayList<>();
        	for(FileParam fileParam:fileParamList){
        		fileNameList.add(fileParam.getFileName());
        	}
        	model.put(fieldName, fileNameList);
        }
        
        Data data = new Data(model);
        return data;
    }
}
