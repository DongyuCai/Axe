### <img width="25px" height="25px" src='https://github.com/DongyuCai/Axe/blob/branch-jdk1.7/axe/favicon.png'/> Axe是一个java框架，提供了使用简单的RESTful、IOC、持久化等功能。

##### 需要 jdk 1.7及以上

### [快速上手开始](https://github.com/DongyuCai/Axe/blob/branch-jdk1.7/GET_START.md)

### RESTful 简单展示，更多使用细节请直接上手
```java
@Controller(basePath="/op/label/area_open",title="分类标签-区域开放")
public class AreaOpenRest {
	
	@Autowired
	private AreaOpenService areaOpenService;
	

	@Request(title="列表查询",value = "/list", method = RequestMethod.GET)
	public List<AreaOpen> list() {
		return areaOpenService.list();
	}
	
	
}
```

### IOC 简单展示，更多使用细节请直接上手
```java
@Service
public class AreaOpenService {
	
	@Autowired
	private AreaOpenDao areaOpenDao;

	public List<AreaOpen> list(){
		return areaOpenDao.list();
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
@Dao(dataSource="api")
public interface AreaOpenDao extends BaseRepository{

	@Sql("select * from AreaOpen order by id")
	public List<AreaOpen> list();
	
	@Sql("select count(1) from Area2Operator where operatorId=?1 and provinceName=?2 and cityName=?3 and countyName=?4")
	public int count(long operatorId,
			String provinceName,
			String cityName,
			String countyName);
			
	@Sql("select * from Area2Operator where 1=1 #1")
	public Page<Area2Operator> page(
			String append,
			List<Long> operatorIdList,
			String provinceName,
			String cityName,
			String countyName,
			PageConfig pageConfig);
}
```
