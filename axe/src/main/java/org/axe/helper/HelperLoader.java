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

import javax.servlet.ServletContext;

import org.axe.Axe;
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
import org.axe.helper.persistence.DataBaseHelper;
import org.axe.helper.persistence.DataSourceHelper;
import org.axe.helper.persistence.SchemaHelper;
import org.axe.helper.persistence.TableHelper;
import org.axe.interface_.base.Helper;

/**
 * 加载并初始化 Helper 类
 * @author CaiDongyu on 2016/4/11.
 */
public final class HelperLoader {
//	private static final Logger LOGGER = LoggerFactory.getLogger(HelperLoader.class);
	
	private static Helper[] helpers;
	
	public static void init() throws Exception{
        init(null);
    }
    public static void init(ServletContext servletContext) throws Exception{
    	initHelpersAry();
    	refresHelpers(servletContext);
    }
    
    
    private static synchronized void initHelpersAry(){
    	helpers = new Helper[]{
    			new ConfigHelper(),//基础配置初始化
        		new FrameworkStatusHelper(),//框架基础信息初始化
                new ClassHelper(),//加载package下所有class到CLASS_SET
                new BeanHelper(),//实例化CLASS_SET里的类，放到BEAN_MAP里
                new AopHelper(),//针对有代理的类，实例化代理并替换掉BEAN_MAP里class原本的实例
                
                //*DAO
                new DataSourceHelper(),//加载DataSource配置
                new DataBaseHelper(),//初始化数据库配置
                new TableHelper(),//加载所有的@Table
                new SchemaHelper(),//初始化所有entity的表结构自建
                
                //*MVC
                new FilterHelper(),//实例化所有Filter链表，并按层级排好序
                new InterceptorHelper(),//实例化所有Interceptor Map，interceptor没有顺序
                new ListenerHelper(),//实例化所有ListenerHelper
                new ControllerHelper(),//加载ACTION_MAP
                
                //*IOC组装
                new IocHelper(),//组装所有@Autowired
                
                //*邮件
                new MailHelper()//初始化邮件助手的配置
        };
    }
    
    public static synchronized void refresHelpers(ServletContext servletContext) throws Exception{
    	for (Helper helper:helpers){
    		helper.init();
        }
    	for (Helper helper:helpers){
    		helper.onStartUp();;
        }
    	
        //特别初始化
        if(servletContext != null){//因为表单请求可能带有文件上传，需要初始化Servlet相关设置
            FormRequestHelper.init(servletContext);
        }
        
        //释放ClassHelper占用的内存
        //TODO:(ok)目前来看，框架自身只有加载91个资源，并不很多
        ClassHelper.release();
		
		//装载的类日志分析
        URL url = Axe.class.getClassLoader().getResource("");
		String path = url==null?"null":url.getPath();
		System.out.println(">>>>>>>>>\t Axe started success! \t<<<<<<<<<<");
        System.out.println(">>>>>>>>>\t Home is \"/axe\"  \t<<<<<<<<<<");
        System.out.println(">>>>>>>>>\t Class path is "+path);
    }
}
