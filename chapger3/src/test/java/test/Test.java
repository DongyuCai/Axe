package test;

import org.smart4j.framework.HelperLoader;
import org.smart4j.framework.bean.Request;
import org.smart4j.framework.helper.ClassHelper;
import org.smart4j.framework.helper.ControllerHelper;
import org.smart4j.framework.helper.DataBaseHelper;
import org.smart4j.framework.util.ClassUtil;
import org.smart4j.framework.util.ReflectionUtil;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by CaiDongYu on 2016/4/8.
 */
public class Test {

    public static void main(String[] args) {
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

        HelperLoader.init();
        String sql = "select * from just4test";
        Object[] params = new Object[0];
        List<Map<String,Object>> result = DataBaseHelper.executeQuery(sql,params);
        System.out.println(result);
    }
}
