package org.test.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.axe.annotation.ioc.Autowired;
import org.axe.annotation.ioc.Controller;
import org.axe.annotation.mvc.FilterFuckOff;
import org.axe.annotation.mvc.Request;
import org.axe.annotation.mvc.RequestParam;
import org.axe.bean.mvc.Data;
import org.axe.bean.mvc.FileParam;
import org.axe.bean.mvc.FormParam;
import org.axe.bean.mvc.Param;
import org.axe.bean.mvc.View;
import org.axe.constant.RequestMethod;
import org.axe.exception.RestException;
import org.test.bean.TestTable;
import org.test.dao.TestDao;
import org.test.filter.TestFilter1;
import org.test.service.TestService;

/**
 * Created by Administrator on 2016/4/8.
 */
@Controller(basePath = "test")
public class TestController {
	
	@Autowired
	private TestService testService;
	
	@Autowired
	private TestDao testDao;
	
	@Request(value="/all",method=RequestMethod.GET)
	public Data getAll(){
		return new Data(testService.getAll());
	}

    @Request(value="/post{money}/4{id}_{name}",method=RequestMethod.POST)
    public Data postPathParam(
    		@RequestParam("money")Integer money,//如果money是整数，这里就有值，如果是别的，甚至是字符串，就会是null
    		
    		@RequestParam("file")FileParam file1,//单个文件，如果上传的是多文件，只会拿到最后一个
    		@RequestParam("file")Object file2,
    		@RequestParam("file")List<FileParam> filesList1,
    		@RequestParam("file")List<?> filesList2,
    		@RequestParam("file")List filesList3,
    		@RequestParam("file")FileParam[] filesAry1,
    		@RequestParam("file")Object[] filesAry2,
    		
    		@RequestParam("ids")Integer ids,//如果传递的参数是多个，只会拿到最后一个
    		@RequestParam("ids")List<String> idsList1,
    		@RequestParam("ids")List<?> idsList2,
    		@RequestParam("ids")List<Integer> idsList3,
    		@RequestParam("ids")List idsList4,
    		@RequestParam("ids")String[] idsAry1,//如果传递的参数是多个，会用","拼接
    		@RequestParam("ids")Integer[] idsAry2,
    		@RequestParam("ids")Double[] idsAry3,
    		@RequestParam("ids")Object[] idsAry4,
    		
    		@RequestParam("name")String name1,
    		@RequestParam("name")Object name2,
    		@RequestParam("name")List<String> nameList1,
    		@RequestParam("name")List<?> nameList2,
    		@RequestParam("name")List nameList3,
    		@RequestParam("name")String[] nameAry1,
    		@RequestParam("name")Object[] nameAry2,
    		
    		HttpServletRequest request,
    		HttpServletResponse response,
    		Param param,
    		Map<String,String> body,
    		String otherParam){//这里总是null，如果有人这么写，那只能在别的地方手工调用这个方法时候传值了，框架不会映射的。
    	System.out.println("postPathParam");
//    	Data data = analysisParam(param);
        return null;
    }
    

    @Request(value="/post100/4{id}_{name}",method=RequestMethod.POST)
    public Data postPathParam2(@RequestParam("id")String id,Param param){
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
    public Data getOne(@RequestParam("id")Long id,Param param){
//    	Data data = analysisParam(param);
    	if(id == null){
    		throw new RestException(RestException.SC_BAD_REQUEST, "id不正确");
    	}
    	TestTable one = testService.get(id);
    	Data data = new Data(one);
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
        
//jdk1.8
//        fieldMap.entrySet().forEach(entry->model.put(entry.getKey(),entry.getValue()));
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
