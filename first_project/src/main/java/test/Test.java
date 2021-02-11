package test;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.axe.Axe;
import org.axe.constant.ConfigConstant;
import org.axe.extra.timer.TimerTask;
import org.axe.helper.base.ConfigHelper;
import org.axe.helper.ioc.BeanHelper;
import org.axe.helper.ioc.ClassHelper;
import org.axe.interface_.mvc.AfterClassLoaded;
import org.axe.interface_.mvc.AfterConfigLoaded;
import org.axe.interface_.mvc.Listener;
import org.axe.util.LogUtil;

import com.axe.first_project.rest.TestRest;

public class Test {
	public static void main(String[] args) {
		try {
			ConfigHelper.addAfterConfigLoadedCallback(new AfterConfigLoaded() {
				@Override
				public void doSomething(Properties config) {
					//修改一些测试时候的参数
					config.put(ConfigConstant.APP_BASE_PACKAGE, config.get(ConfigConstant.APP_BASE_PACKAGE)+",test");
					config.put("jdbc.datasource.axe-datasource-dbcp.auto_create_table", "true");
					config.put(ConfigConstant.JDBC_URL, "jdbc:mysql://192.168.199.45:3306/testdb?useUnicode=true&characterEncoding=utf8&autoReconnect=true&rewriteBatchedStatements=TRUE");
				}
			});
			ClassHelper.addAfterClassLoadedCallback(new AfterClassLoaded() {
				@Override
				public void doSomething(Set<Class<?>> classSet) {
					Set<Class<?>> classesRemove = new HashSet<>();
					for(Class<?> cls:classSet){
						//Listener不启动
						if(Listener.class.isAssignableFrom(cls)){
							classesRemove.add(cls);
						}
						//TimerTask不启动
						if(TimerTask.class.isAssignableFrom(cls)){
							classesRemove.add(cls);
						}
					}
					
					//不启动的都去除掉
					classSet.removeAll(classesRemove);
					
					
					//TODO:可以在这里添加你需要额外启动的
				}
			});
			
			//Axe.init()会完整的启动Axe所有功能，只是缺少Web容器
			//在这个工程里，我们使用了jetty来作为内置web容器，您可以去除jetty，只要pom.xml中删掉jetty依赖，并移除JettyStart类即可
			//去除jetty后，您可以使用您喜欢的任何servlet 容器比如Tomcat
			Axe.init();
			
			//BeanHelper.getBean可以获取到Axe托管的所有mvc对象
			//比如:@Controller注解的rest接口类
			//比如:@Service注解的 公共服务类，此种类一般携带事物切面
			//比如:@Dao注解的持久化接口，那么您可以方便的在这里调试你的sql语句业务逻辑
			//配合这些功能，你还可以结合junit来作方便的代码单元测试
			
			TestRest rest = BeanHelper.getBean(TestRest.class);
			System.out.println(rest.first("张三", null));
			
		} catch (Exception e) {
			LogUtil.error(e);
		}
		System.exit(0);
	}
}
