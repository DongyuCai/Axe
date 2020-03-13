### <img width="25px" height="25px" src='https://github.com/DongyuCai/Axe/blob/18.5.29/axe/favicon.png'/> Axe是一个java框架，提供了使用简单的RESTful、IOC、持久化等功能。

##### 需要 jdk 1.7及以上

### [快速上手开始](https://github.com/DongyuCai/Axe/blob/18.5.29/GET_START.md)

### RESTful 简单展示，更多使用细节请直接上手
```java
@Controller(basePath="",desc="hello word")
public class TestRest {
	
	@Request(title="hi",value="/hi",method=RequestMethod.GET)
	public String hi(
		@RequestParam(desc="说",value="say")String say	
			){
		return "u say:"+say;
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
