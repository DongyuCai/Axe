package org.easyweb4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easyweb4j.annotation.Controller;
import org.easyweb4j.annotation.FilterFuckOff;
import org.easyweb4j.annotation.Inject;
import org.easyweb4j.annotation.Request;
import org.easyweb4j.annotation.RequestParam;
import org.easyweb4j.bean.Data;
import org.easyweb4j.bean.FileParam;
import org.easyweb4j.bean.FormParam;
import org.easyweb4j.bean.Param;
import org.easyweb4j.bean.View;
import org.easyweb4j.constant.RequestMethod;

/**
 * Created by Administrator on 2016/4/8.
 */
@Controller(basePath = "test")
public class TestController {
	
	@Inject
	private TestService testService;

    @Request(value="/post{money}/4{id}_{name}",method=RequestMethod.POST)
    public Data postPathParam(
    		@RequestParam("money")Integer money,//如果money是整数，这里就有值，如果是别的，甚至是字符串，就会是null
    		@RequestParam("file")FileParam file,//单个文件，如果上传的是多文件，只会拿到最后一个
    		@RequestParam("file")List<FileParam> filesList,
    		@RequestParam("file")FileParam[] filesAry,
    		Param param,
    		@RequestParam("ids")Integer ids,//如果传递的参数是多个，只会拿到最后一个
    		@RequestParam("ids")List<String> idsList,
    		@RequestParam("ids")String[] idsAry,//如果传递的参数是多个，会用","拼接
    		@RequestParam("ids")Integer[] ids2Ary,
    		@RequestParam("ids")Double[] ids3Ary,
    		@RequestParam("name")String name,
    		HttpServletRequest request,
    		HttpServletResponse response,
    		String otherParam){//这里总是null，如果有人这么写，那只能在别的地方手工调用这个方法时候传值了，框架不会映射的。
    	System.out.println("postPathParam");
//    	Data data = analysisParam(param);
        return null;
    }
    

    @Request(value="/post100/4{id}_{name}",method=RequestMethod.POST)
    public Data postPathParam2(Param param){
    	System.out.println("postPathParam2");
    	Data data = analysisParam(param);
        return data;
    }
    
    @Request(value="/post100/4id_name",method=RequestMethod.POST)
    public Data postPathParam3(Param param){
    	System.out.println("postPathParam3");
    	Data data = analysisParam(param);
        return data;
    }
    
    @Request(value="/getOne/{addr}/{id}_{name}/detail",method=RequestMethod.GET)
    public Data getOneDetail(Param param){
    	Data data = analysisParam(param);
        return data;
    }
    
    @Request(value="/page",method=RequestMethod.GET)
    public Data getPage(Param param){
    	Data data = analysisParam(param);
        return data;
    }
    
    //==================== postman success ================//
    @Request(value="/getOne/{id}",method=RequestMethod.GET)
    public Data getOne(Param param){
    	Data data = analysisParam(param);
        return data;
    }
    
    @FilterFuckOff(TestFilter1.class)
    @Request(value="/get",method=RequestMethod.GET)
    public Data get(Param param){
    	Data data = analysisParam(param);
        return data;
    }

    @FilterFuckOff
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
        Map<String,List<FormParam>> fieldMap = param.getFieldMap();
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
