/**
 * MIT License
 * 
 * Copyright (c) 2017 The Axe Project
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.axe.home.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.axe.annotation.ioc.Service;
import org.axe.bean.mvc.FormParam;
import org.axe.bean.mvc.Param;
import org.axe.constant.ConfigConstant;
import org.axe.helper.base.ConfigHelper;
import org.axe.util.CollectionUtil;
import org.axe.util.FileUtil;
import org.axe.util.IpUtil;
import org.axe.util.MD5Util;
import org.axe.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class HomeService {
	private static final Logger LOGGER = LoggerFactory.getLogger(HomeService.class);
	
	private String privateToken = null;

	public String saveAxeProperties(Param param){
		URL resource = Thread.currentThread().getContextClassLoader().getResource(ConfigConstant.CONFIG_FILE);
		File configFile = null;
		if(resource != null){
			configFile = FileUtil.backupAndCreateNewFile(resource.getFile());
		}else{
			configFile = new File(Thread.currentThread().getContextClassLoader().getClass().getResource("/").getPath()+ConfigConstant.CONFIG_FILE);
		}
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(configFile));
			//#保存需要保存的配置
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
					if(!fieldName.startsWith("NOSAVE.")){
						writer.write(fieldName+"="+fieldValue);
						writer.newLine();
					}
				}
			}
			
			//#登录ID和密码需要生成TOKEN后保存
			List<FormParam> signIdList = param.getFieldMap().get(ConfigConstant.AXE_SIGN_IN);
			if(CollectionUtil.isNotEmpty(signIdList)){
				if("true".equalsIgnoreCase(signIdList.get(0).getFieldValue())){
					//#需要登录才保存
					List<FormParam> idList = param.getFieldMap().get("NOSAVE.axe.signin.id");
					String id = "";
					if(CollectionUtil.isNotEmpty(idList)){
						id = idList.get(0).getFieldValue();
					}
					List<FormParam> passwordList = param.getFieldMap().get("NOSAVE.axe.signin.password");
					String password = "";
					if(CollectionUtil.isNotEmpty(passwordList)){
						password = passwordList.get(0).getFieldValue();
					}

					id = id==null?"":id;
					password = password==null?"":password;
					
					String fieldName = ConfigConstant.AXE_SIGN_IN_TOKEN;
					String fieldValue = MD5Util.getMD5Code(id+":"+password);
					writer.write(fieldName+"="+fieldValue);
					writer.newLine();
				}
			}
			
			
			writer.close();
		} catch (Exception e) {
			LOGGER.error("home error",e);
		}
		return configFile.getAbsolutePath();
	}
	
	
	public boolean signIn(HttpServletRequest request,String id,String password){
		id = id==null?"":id;
		password = password==null?"":password;
		
		String publicToken = ConfigHelper.getAxeSignInToken();
		String publicTokenFromLogin = MD5Util.getMD5Code(id+":"+password);
		boolean success = publicTokenFromLogin.equals(publicToken);
		if(success){
			generatePrivateToken(request, publicToken);
		}else{
			setPrivateToken(null);
		}
		return success;
	}
	
	public void signOut(){
		setPrivateToken(null);
	}
	
	private void generatePrivateToken(HttpServletRequest request,String publicToken){
		String ip = IpUtil.getIpAddress(request);
		String privateToken = MD5Util.getMD5Code(ip+publicToken);
		setPrivateToken(privateToken);
	}
	
	public boolean checkPrivateToken(HttpServletRequest request,String publicToken){
		String ip = IpUtil.getIpAddress(request);
		String privateToken = MD5Util.getMD5Code(ip+publicToken);
		if(privateToken.equals(this.privateToken)){
			return true;
		}else{
			setPrivateToken(null);
			return false;
		}
	}
	
	private synchronized void setPrivateToken(String privateToken) {
		this.privateToken = privateToken;
	}
	
}
