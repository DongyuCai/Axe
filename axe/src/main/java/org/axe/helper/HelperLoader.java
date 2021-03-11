/**
 * MIT License
 * 
 * Copyright (c) 2017 CaiDongyu
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
package org.axe.helper;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.axe.Axe;
import org.axe.extra.timer.TimerTaskHelper;
import org.axe.helper.aop.AopHelper;
import org.axe.helper.base.ConfigHelper;
import org.axe.helper.base.FrameworkStatusHelper;
import org.axe.helper.base.MailHelper;
import org.axe.helper.ioc.BeanHelper;
import org.axe.helper.ioc.ClassHelper;
import org.axe.helper.ioc.IocHelper;
import org.axe.helper.mvc.ControllerHelper;
import org.axe.helper.mvc.FilterHelper;
import org.axe.helper.mvc.FormRequestHelper;
import org.axe.helper.mvc.InterceptorHelper;
import org.axe.helper.mvc.ListenerHelper;
import org.axe.interface_.base.Helper;
import org.axe.interface_.mvc.AfterHelperLoaded;
import org.axe.util.LogUtil;

/**
 * 加载并初始化 Helper 类
 * @author CaiDongyu on 2016/4/11.
 */
public final class HelperLoader {
//	private static final Logger LOGGER = LoggerFactory.getLogger(HelperLoader.class);
	
	private static final List<Helper> HELPERS = new ArrayList<>();
    private static final List<AfterHelperLoaded> AFTER_HELPER_LOADED_LIST = new ArrayList<>();
	
    public static void addAfterHelperLoadedCallback(AfterHelperLoaded callback){
    	synchronized (AFTER_HELPER_LOADED_LIST) {
    		AFTER_HELPER_LOADED_LIST.add(callback);
		}
    }
    
	public static synchronized void init() throws Exception{
        init(null);
    }
    public static synchronized void init(ServletContext servletContext) throws Exception{
    	initHelpersAry();
    	
    	//加载Helpers置后，执行
    	for(AfterHelperLoaded ahl:AFTER_HELPER_LOADED_LIST){
    		ahl.doSomething(HELPERS);
    	}
    	
    	refresHelpers(servletContext);
    }
    
    private static synchronized void initHelpersAry(){
    	HELPERS.add(new ConfigHelper());//基础配置初始化
    	HELPERS.add(new FrameworkStatusHelper());//框架基础信息初始化
    	HELPERS.add(new ClassHelper());//加载package下所有class到CLASS_SET
    	HELPERS.add(new BeanHelper());//实例化CLASS_SET里的类，放到BEAN_MAP里
    	HELPERS.add(new AopHelper());//针对有代理的类，实例化代理并替换掉BEAN_MAP里class原本的实例
                
        //*MVC
    	HELPERS.add(new FilterHelper());//实例化所有Filter链表，并按层级排好序
    	HELPERS.add(new InterceptorHelper());//实例化所有Interceptor Map，interceptor没有顺序
    	HELPERS.add(new ListenerHelper());//实例化所有ListenerHelper
    	HELPERS.add(new ControllerHelper());//加载ACTION_MAP
		HELPERS.add(new TimerTaskHelper());// 加载所有定时器

		// *IOC组装
		HELPERS.add(new IocHelper());// 组装所有@Autowired

		// *邮件
		HELPERS.add(new MailHelper());//初始化邮件助手的配置
    }
    
    public static synchronized void refresHelpers(ServletContext servletContext) throws Exception{
    	for (Helper helper:HELPERS){
    		helper.init();
        }
    	for (Helper helper:HELPERS){
    		helper.onStartUp();;
        }
    	
        //特别初始化
        if(servletContext != null){//因为表单请求可能带有文件上传，需要初始化Servlet相关设置
            FormRequestHelper.init(servletContext);
        }
        
        //释放ClassHelper占用的内存
        //TODO:(ok)目前来看，框架自身只有加载91个资源，并不很多
        if(!ConfigHelper.getAxeHome()){
    		ClassHelper.getClassSet().clear();
    		FilterHelper.getSortedFilterList().clear();
    		FilterHelper.getActionSizeMap().clear();
    		InterceptorHelper.getInterceptorMap().clear();
    		InterceptorHelper.getActionSizeMap().clear();
    		ListenerHelper.getListenerList().clear();
    	}
		
		//装载的类日志分析
        URL url = Axe.class.getClassLoader().getResource("");
		String path = url==null?"null":url.getPath();
		LogUtil.log(">>>>>>>>>\t Axe started success! \t<<<<<<<<<<");
		LogUtil.log(">>>>>>>>>\t Home is \"/axe\"  \t<<<<<<<<<<");
		LogUtil.log(">>>>>>>>>\t Class path is "+path);
    }
}
