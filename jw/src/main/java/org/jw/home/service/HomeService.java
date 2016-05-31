package org.jw.home.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.List;

import org.jw.annotation.ioc.Service;
import org.jw.bean.mvc.FormParam;
import org.jw.bean.mvc.Param;
import org.jw.constant.ConfigConstant;
import org.jw.util.FileUtil;
import org.jw.util.StringUtil;

@Service
public class HomeService {

	public void saveJwProperties(Param param){
		URL resource = Thread.currentThread().getContextClassLoader().getResource(ConfigConstant.CONFIG_FILE);
		File configFile = null;
		if(resource != null){
			configFile = FileUtil.backupAndCreateNewFile(resource.getFile());
		}else{
			configFile = new File(Thread.currentThread().getContextClassLoader().getClass().getResource("/").getPath()+ConfigConstant.CONFIG_FILE);
		}
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(configFile));
			for (List<FormParam> formParamList : param.getFieldMap().values()) {
				String fieldName = null;
				String fieldValue = null;
				for(FormParam formParam:formParamList){
					fieldName = formParam.getFieldName();
					if(StringUtil.isNotEmpty(formParam.getFieldValue()) && !"null".equalsIgnoreCase(formParam.getFieldValue())){
						fieldValue = fieldValue==null?formParam.getFieldValue():fieldValue+","+formParam.getFieldValue();
					}
				}
				if(StringUtil.isNotEmpty(fieldValue)){
					writer.write(fieldName+"="+fieldValue);
					writer.newLine();
				}
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
