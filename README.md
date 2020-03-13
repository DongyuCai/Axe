### <img width="25px" height="25px" src='https://github.com/DongyuCai/Axe/blob/18.5.29/axe/favicon.png'/> Axe是一个java框架，提供了使用简单的RESTful、IOC、持久化等功能。

##### 需要 jdk 1.7及以上

### [快速上手开始](https://github.com/DongyuCai/Axe/blob/18.5.29/GET_START.md)

### RESTful 简单展示，更多使用细节请直接上手
```java
@Controller(basePath="",desc="HelloWord")
public class TestRest {
	
	@Request(path="/first",method=RequestMethod.GET,desc="第一个接口")
	public String first(
		@RequestParam(name="name",required=true,desc="姓名")String name,
		@RequestParam(name="age",desc="年龄")Integer age
			){
		return "姓名:"+name+" 年龄:"+(age == null?"不知道":age);
	}
	
}
```

### IOC 简单展示，更多使用细节请直接上手
```java
@Service
public class TestService {
	
	@Autowired
	private TestDao testDao;

	public List<TestTable> list(){
		return testDao.list();
	}
	
}
```
```java
public class Test {
	
	public static void main(String[] args) {
		try{
			Axe.init();
			TestController controller = BeanHelper.getBean(TestController.class);
			System.out.println(controller);
			TestService service = BeanHelper.getBean(TestService.class);
			System.out.println(service);
			TestDao dao = BeanHelper.getBean(TestDao.class);
			System.out.println(dao);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
```

### 持久化 简单展示，更多使用细节请直接上手
```java
@Dao
public interface TestDao extends BaseRepository{//如果不需要saveEntity、deleteEntity等持久化对象操作方法，可以不继承BaseRespository

	@Sql("select * from TestTable order by id")
	public List<TestTable> list();
}

```
