package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.axe.bean.mvc.FileParam;

/**
 * Created by CaiDongYu on 2016/4/8.
 */
public class Test{
	

    public static void main(String[] args) {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	try {
    		String time = "2017-08-01 10:15:08";
    		Date smsValidate = sdf.parse(time);
    		
    		System.out.println(System.currentTimeMillis() < (smsValidate.getTime() + 600000));
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
//    	String param_temp="labelIds=,1,,2,,3,,4,,5,,9,,10,,11,&pictures=&labels=夏天衣服,冬天衣服,秋天衣服,春天衣服,铁,手机,电脑,笔记本&detailedList=[{\"L\":\"夏天衣服\",\"U\":\"公斤\",\"A\":\"1\"},{\"L\":\"冬天衣服\",\"U\":\"公斤\",\"A\":\"1\"},{\"L\":\"秋天衣服\",\"U\":\"公斤\",\"A\":\"1\"},{\"L\":\"春天衣服\",\"U\":\"公斤\",\"A\":\"1\"},{\"L\":\"铁\",\"U\":\"公斤\",\"A\":\"1\"},{\"L\":\"手机\",\"U\":\"公斤\",\"A\":\"1\"},{\"L\":\"电脑\",\"U\":\"公斤\",\"A\":\"1\"},{\"L\":\"笔记本\",\"U\":\"公斤\",\"A\":\"1\"}]";
//        StringBuffer responseResult = new StringBuffer();
//        HttpURLConnection httpURLConnection = null;
//        PrintWriter printWriter = null;
//        BufferedReader bufferedReader = null;
//        try {
//            URL realUrl = new URL("http://test.sl1288.com/sl1288-api/customer/order/box");
//            // 打开和URL之间的连接
//            httpURLConnection = (HttpURLConnection) realUrl.openConnection();
//            // 设置通用的请求属性
//            httpURLConnection.setRequestMethod("POST");
//            httpURLConnection.setRequestProperty("accept", "*/*");
//            //httpURLConnection.setRequestProperty("Content-Length", String.valueOf(params.length()));
////            httpURLConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
//            httpURLConnection.setRequestProperty("Customer-API-Token","64AF7E84A3D28ED845C6DF5578959360");
//            // 发送POST请求必须设置如下两行
//            httpURLConnection.setDoOutput(true);
//            httpURLConnection.setDoInput(true);
//            // 获取URLConnection对象对应的输出流
//            printWriter = new PrintWriter(httpURLConnection.getOutputStream());
//            // 发送请求参数
//            printWriter.write(param_temp);
//            // flush输出流的缓冲
//            printWriter.flush();
//            // 根据ResponseCode判断连接是否成功
//            int responseCode = httpURLConnection.getResponseCode();
//            if (responseCode != 200) {
//               System.out.println(" Error===" + responseCode);
//               System.out.println("网络异常，请检查网络连接");
//            } else {
//                // 定义BufferedReader输入流来读取URL的ResponseData
//                bufferedReader = new BufferedReader(new InputStreamReader(
//                        httpURLConnection.getInputStream()));
//                String line;
//                while ((line = bufferedReader.readLine()) != null) {
//                    responseResult.append(line).append("\n");
//                }
//
//                System.out.println("json:"+responseResult.toString());
//            }
//        } catch (Exception e) {
//        	System.out.println("send post request error!" + e);
//        } finally {
//            httpURLConnection.disconnect();
//            try {
//                if (printWriter != null) {
//                    printWriter.close();
//                }
//                if (bufferedReader != null) {
//                    bufferedReader.close();
//                }
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }
    	
    	
    	/*String sql = "update Tablea t set t.name = ? and `my_age`= ? and t.sex=? where t.id=t2.id";
    	Pattern colCompile = Pattern.compile(" ([a-zA-Z0-9\\.\\-_`]*) ?= ?\\?");
		Matcher colMatcher = colCompile.matcher(sql);
		
		while(colMatcher.find()){
			System.out.println(colMatcher.group(1));
		}*/
    	
    	/*String str = " A  b   c d";
    	while(str.contains("  ")){
    		str = str.replaceAll("  ", " ");
    	}
    	str = str.trim();
    	System.out.println(str);*/
    	
//    	System.out.println(StringUtil.getRandomString(32));
    	/*System.out.println(null+"1");
    	
    	System.out.println(Map.class);*/
    	/*try {
			Axe.init();
		} catch (Exception e) {
			e.printStackTrace();
		}*/
    	
    	/*Method[] dm = Test.class.getDeclaredMethods();
    	for(Method mt:dm){
    		System.out.println(mt.toGenericString());
    	}
    	*/
    	/*
    	List<String> list1 = new ArrayList<>();
    	list1.add("1");
    	list1.add("2");
    	list1.add("3");
    	list1.add("4");
    	List<String> list2 = new ArrayList<>();
    	list2.addAll(list1.subList(1, list1.size()));
    	for(String str:listt2){
    		System.out.println(str);
    	}
    	*/
    	
    	/*Thread t = new Thread(){
    		
    		@Override
    		public void run() {
    			System.out.println("thread running ----");
    		}
    	};
    	t.start();
    	
    	while(t.isAlive()){
    		try {
    			System.out.println("thread is alive");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	System.out.println("thread died");*/
    	
    	
    	/*try {
    		Method[] methods = Test.class.getDeclaredMethods();
    		for(Method method:methods){
    			System.out.println(method.getDeclaringClass().getName()+"."+method.getName()+"_"+method.toString().hashCode());
    		}
//    		FileUtil.backupAndCreateNewFile("mysql-keyword.txt");
		} catch (Exception e) {

		}*/
    	
//    	HelperLoader.init();
//    	System.out.println(ClassHelper.getClassSet().size());
    	/*try {
			Method method = TestController.class.getDeclaredMethod("postPathParam2",new Class<?>[]{String.class,Param.class});
			Annotation[][] parameterAnnotations = method.getParameterAnnotations();
			
			System.out.println(parameterAnnotations);
			
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}*/
    	
    	/*List<Account> list = new ArrayList<Account>();
    	Account a1 = new Account();
    	a1.setLoginName("login-1");
    	list.add(a1);
    	Account a2 = new Account();
    	a1.setLoginName("login-2");
    	list.add(a2);
    	
    	Page<Account> page = new Page<>(list, new PageConfig(1, 2), 12, 7);
    	System.out.println(JsonUtil.toJson(page));*/
    	
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

    public static void changeStr(String str,String str1){
    	str = str+"###";
    }
}
