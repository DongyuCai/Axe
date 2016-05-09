package test;

import java.util.ArrayList;
import java.util.List;

import org.jw.bean.FileParam;
import org.jw.bean.Page;
import org.jw.bean.PageConfig;
import org.jw.util.JsonUtil;
import org.test_jw.bean.Account;

/**
 * Created by CaiDongYu on 2016/4/8.
 */
public class Test{
	

    public static void main(String[] args) {
    	List<Account> list = new ArrayList<Account>();
    	Account a1 = new Account();
    	a1.setLoginName("login-1");
    	list.add(a1);
    	Account a2 = new Account();
    	a1.setLoginName("login-2");
    	list.add(a2);
    	
    	Page<Account> page = new Page<>(list, new PageConfig(1, 2), 12, 7);
    	System.out.println(JsonUtil.toJson(page));
    	
//    	System.out.println(str.substring(1));
//    	String sql = "select t1.* from Account t1,GroupUser t2 where t2.id in(select id from Account) and t1.id = t2.user_id";
//    	String sql = "insert into Account(LoginName)values('abc')";
//    	sql = DataBaseHelper.convertSql(sql);
//    	List<Map<String,Object>> result  = DataBaseHelper.queryList(sql);
//    	List<Account> accountList  = DataBaseHelper.queryEntityList(Account.class,sql);
//    	System.out.println(sql);
//    	System.out.println(result);
//    	System.out.println(accountList.size());
    	
//    	System.out.println(Test.class.getSimpleName());
    	
    	
//    	HelperLoader.init();
//    	TestDao testDao = BeanHelper.getBean(TestDao.class);
//    	System.out.println(testDao.getAll().size());
//    	AccountDao testDao = BeanHelper.getBean(AccountDao.class);
//    	System.out.println(JsonUtil.toJson(testDao.getAll().get(0)));
//    	Account account = testDao.getLimit1();
//    	System.out.println(JsonUtil.toJson(account));
//    	System.out.println(testDao.getCount().get("all"));
//    	System.out.println(testDao.getIntegerList());
//    	System.out.println(testDao.getLongList());
//    	System.out.println(JsonUtil.toJson(testDao.getMap()));
    	
//    	cls.isAssignableFrom(Filter.class) && !
//    	System.out.println(Filter.class.isAssignableFrom(TestFilter1.class));
//    	System.out.println(ReflectionUtil.compareType(TestFilter1.class, Filter.class));
    	
    	/*List<Test> list = new ArrayList<>();
    	list.add(new Test());
    	list.add(new Test());
    	list.add(new Test());
    	
    	Test[] ary = new Test[0];
    	ary = list.toArray(ary);
    	System.out.println(ary.length);
    	
    	System.out.println(list.getClass());
    	*/
    	
    	
//    	Method[] methodAry = Test.class.getDeclaredMethods();
//    	for(Method actionMethod:methodAry){
//
//        	Parameter[] parameterAry = actionMethod.getParameters();
//        	parameterAry = parameterAry == null?new Parameter[0]:parameterAry;
//        	Class<?>[] parameterTypes = actionMethod.getParameterTypes();
//        	parameterTypes = parameterTypes == null?new Class<?>[0]:parameterTypes;
//        	//按顺序来，塞值
//        	List<Object> parameterValue = new ArrayList<>();
//        	for(int i=0;i<parameterAry.length && parameterAry.length == parameterTypes.length;i++){
//    			Parameter parameter = parameterAry[i];
//    			Class<?> parameterType = parameterTypes[i];
//    			System.out.println(parameterType+"\t"+parameterType.isArray()+"\t"+parameterType.getComponentType());
//        	}
//    	}
//    	
    	
//    	List<Object> list = new ArrayList<>();
//    	list.add(null);
//    	list.add(null);
//    	list.add(new Test());
//    	list.add(null);
//    	
//    	for(Object obj:list){
//    		System.out.println(obj);
//    	}
//    	
//    	String nodeName = "ssabc1_other";
//    	String pathParamNodeName = "?_?";
//    	System.out.println(RequestUtil.compareNodeNameAndPathParamNodeName(nodeName, pathParamNodeName));
    	
//    	System.out.println(ControllerHelper.ACTION_MAP);
//    	System.out.println(ControllerHelper.getHandler("GET", "/getOne/abc").getActionMethod());
    	
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
    
    public static void testParameter(List<FileParam> files,int p1,Integer p2,double p3,Double p4,boolean p5,Boolean p6,char p7,Character p8){
    	
    }
    
    public static void changeStr(String str){
    	str = str+"###";
    }
    
}
