package test;

import org.easyweb4j.helper.ControllerHelper;
import org.easyweb4j.util.RequestUtil;

/**
 * Created by CaiDongYu on 2016/4/8.
 */
public class Test {

    public static void main(String[] args) {
    	System.out.println(ControllerHelper.ACTION_MAP);
    	System.out.println(ControllerHelper.getHandler("GET", "/getOne/?_other").getActionMethod());
    	
//        Pattern paramDefFlag = Pattern.compile("\\{([A-Za-z0-9]*)\\}");
//        Matcher paramDefMatcher = paramDefFlag.matcher(path);
//        while(paramDefMatcher.find()){
//        	
//        	System.out.println(paramDefMatcher.group(1));
//        	
//        }
        
//        Set<Class<?>> classSet = ClassHelper.getClassSet();
//        Set<Class<?>> controllerSet = ClassHelper.getControllerClassSet();
//        Set<Class<?>> beanSet = ClassHelper.getBeanClassSet();
//        System.out.println(classSet.size());
//        System.out.println(controllerSet.size());
//        System.out.println(beanSet.size());
//        Set<Class<?>> classSet = ClassUtil.getClassSet("com.mysql.jdbc.log");
//        System.out.println(classSet.size());


//        Object obj = ReflectionUtil.newInstance(TestController.class);
//        System.out.println(obj);

//        Request req1 = new Request("a","b");
//        Request req2 = new Request("a","b");

//        System.out.println(req1.hashCode());
//        System.out.println(req2.hashCode());
//        System.out.println(req1.equals(req2));

//        HelperLoader.init();
//        ControllerHelper.getHandler("get","b");
//        System.out.println("abc".matches("\\w+"));

//        StringBuffer buf = new StringBuffer("");
//        buf.append("dddd");
//
//        String str = "aaa";
//        System.out.println(str + buf);

//          HelperLoader.init();
//        String sql = "select * from just4test where id in (?,?) and name like ?";
//        Object[] params = {1,2,"%asd%"};
//        List<Map<String,Object>> result = DataBaseHelper.executeQuery(sql,params);
//        System.out.println(result);
//
//        TestService ts = BeanHelper.getBean(TestService.class);
//        ts.testNoTns();
    }
}
