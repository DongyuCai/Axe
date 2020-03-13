## 安装Axe，首先取保你已经安装好maven、git环境
1、git clone或者下载Axe项目到本地文件夹。

2、打开cmd，进入到Axe/axe文件夹下，就是有pom.xml的文件夹下。

3、执行mvn clean install命令，等待安装完成。

## Get Start 快速开始
1、新建工程。打开cmd，切换到你自己的目录下，创建一个空的maven项目。
```
mvn archetype:generate -DgroupId=你的groupId -DartifactId=你的artifactId
```
2、修改pom.xml。打开工程目录下的pom.xml，替换成下面的内容。注意groupId和artifactId换成自己的。
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>

	<groupId>com.test</groupId>
	<artifactId>test</artifactId>
	<version>1.0-SNAPSHOT</version>
	<packaging>war</packaging>

	<dependencies>
		<!-- log4j -->
		<dependency>
			<groupId>org.slf4j</groupId>
			<artifactId>slf4j-log4j12</artifactId>
			<version>1.7.7</version>
		</dependency>

		<!-- ############################################################## -->
		<!-- Axe 核心依赖 -->
		<dependency>
			<groupId>org.axe</groupId>
			<artifactId>axe</artifactId>
			<version>19.8.6</version>
		</dependency>
		<!-- Apache DBCP 数据源(这是默认数据源，可以自我指定数据源) -->
		<dependency>
			<groupId>org.apache.commons</groupId>
			<artifactId>commons-dbcp2</artifactId>
			<version>2.0.1</version>
		</dependency>

		<!-- ######################### java web ################################# -->
		<!-- cross domain 可选，如果不跨域可以去掉 -->

		<dependency>
			<groupId>com.thetransactioncompany</groupId>
			<artifactId>java-property-utils</artifactId>
			<version>1.9.1</version>
		</dependency>

		<dependency>
			<groupId>com.thetransactioncompany</groupId>
			<artifactId>cors-filter</artifactId>
			<version>2.4</version>
		</dependency>

		<dependency>
			<groupId>javax.servlet</groupId>
			<artifactId>servlet-api</artifactId>
			<version>2.5</version>
		</dependency>

		<dependency>
			<groupId>jstl</groupId>
			<artifactId>jstl</artifactId>
			<version>1.2</version>
		</dependency>
	</dependencies>

	<build>
		<finalName>test</finalName>
		<plugins>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-compiler-plugin</artifactId>
				<version>3.1</version>
				<configuration>
					<source>1.7</source>
					<target>1.7</target>
					<encoding>utf8</encoding>
				</configuration>
			</plugin>
		</plugins>
	</build>
</project>


```
3、转化工程。如果IDE是eclipse，打开cmd，切换到pom.xml所在路径下，执行以下命令。
```
mvn clean eclipse:eclipse -Dwtpversion=1.0
```
4、导入工程，打开eclipse，import工程，当作java普通工程导入，然后删掉两个报错的AppTest.java文件即可。

5、配置工程。进入src\main文件夹下，新建一个文件夹叫resources。右击resources文件夹，选择Build Path，再选择Use as Source Folder。进入resources后新建一个axe.properties文件，内容如下。
```
#==================axe框架配置文件==================

#------------------1.axe基础配置--------------------

#框架扫描的包路径，多个路径用","号隔开
#配置的包路径粒度越细越好，但是越细的粒度不利于开发时候类和包的新增与变更
app.base_package=com.axe

#如果工程内有jsp或者其他view层的静态文件，这里需要指定这些文件的路径
#	比如src/main/webapp下，有static文件夹里放的是静态css、图片、js等文件
#	比如src/main/webapp下，有view文件夹里放的是jsp文件
app.asset_path=/static
app.jsp_path=/view

#文件上传大小限制
#app.upload_limit=0

#指定好邮箱，框架会将异常信息发送给这些邮箱，多个邮箱用","号分隔
axe.email=1157656909@qq.com

#是否可以访问axe后台，false表示关闭/axe的访问，建议只在本地和测试环境打开
axe.home=true

#------------------2.数据源配置--------------------
#指定数据源
#	可以指定多个，比如 jdbc.datasource=druid,api,united,pointsShop,aio,card
#	axe-datasource-dbcp是axe框架提供的默认数据源
#	可以使用自己的数据源，用法参考README里的数据源一节
#   !如果不需要连接数据，则配置 jdbc.datasource= 即可，后续配置项需全部删除
#jdbc.datasource=axe-datasource-dbcp
jdbc.datasource=

#是否自动建表
#jdbc.datasource.axe-datasource-dbcp.auto_create_table=true

#是否打印sql语句
#jdbc.datasource.axe-datasource-dbcp.show_sql=true

#数据库编码，支持emoji！MySql使用
#jdbc.datasource.axe-datasource-dbcp.character=utf8mb4

#数据库校验编码，支持emoji！MySql使用
#jdbc.datasource.axe-datasource-dbcp.collate=utf8mb4_unicode_ci

#如果使用axe-datasource-dbcp，需要在axe.properties里指定好jdbc的配置
#	如果使用自己的数据源比如druid，则此段配置（从{到}）不需要，可以另外写配置文件。
#	{
#jdbc.username=
#jdbc.password=
#jdbc.url=jdbc:mysql://{{ip:port}}/{{databseName}}?useUnicode=true&characterEncoding=utf8&autoReconnect=true&rewriteBatchedStatements=TRUE
#jdbc.driver=com.mysql.jdbc.Driver
#	}

```
6、配置eclipse的tomcat，然后发布项目到tomcat下，启动后，访问http://localhost:8080/项目名/axe，开始用吧（完）

## 让我们熟悉下怎么开发？
axe推荐的风格是前后端分离，也就是view是前台的事情，后台服务只提供数据，只负责MVC中的Model和Controller。
但是axe也支持完整的MVC，View层可以通过在Controller中返回的结果类型(View.class)来跳转，View.class对象的实例包含一个地址字符串，支持携带参数给jsp页面。
```java
@Controller(desc="这是一个Controller",basePath="")
public class ToJspController{
	
	@Request(desc="/jsp页面跳转",path="/index.html",method=RequestMethod.GET)
	public View invite_wx(HttpServletRequest request,HttpServletResponse response){
		View view = new View("/view/index.jsp").dispatcher();
		//给JSP页面传递的参数
		view.addModel("param", "123");
		...
		return view;
	}
}
```


## Restful，怎么写一个ajax后端接口
restful的具体定义这里不做解释了，axe对rest请求url中的参数支持如下的解析方式。

- 如 /get/{id}_{name}  中id和name是参数 
- 如 /get_{id}/{name}  也是id和name表示参数
- 参数只能是数字和字母，url只能是数字、字母、下划线和$符

请看下面的POST接口，我们习惯POST方式表示新增，url中的名称表示资源，这是一个新增用户的接口
```java
	@Request(path="/user",method=RequestMethod.POST)
    public String addUser(
			//框架要求必须使用包装类型，使用int（类似）类型无法准确判断参数是否有值
        	@RequestParam(name="age")Integer age,
        	@RequestParam(name="name")String name){
    	....
		return "success";
    }
```
> 需要注意的是，@RequestParam 要求必须使用包装类型，比如例子中的age参数，不可以是int类型。

## 大表自动分片
当我们的表数据量过大的时候，sql操作的耗时会急剧上升，成为系统瓶颈，这时我们需要做分库分表等一些优化，axe对此自带了分片功能。

- 如果一张表是需要分片的，那么在框架启动时，自动创建的，不只有第一张数据表，还有他的分片管理表

## 框架有哪些重要的组成

### Controller部分
- Filter
- CharSetFilter
- HeaderFilter
- Listener
- RestException
- RedirectorInterrupt
- @Controller
- @Filter
- @FilterFuckOff
- @UnFuckOff
- @Interceptor
- @Autowired
- BeanHelper
- @Request
- @RequestParam
- @RequestEntity
- @Default
- Param
- FileParam
- HttpServletResponse
- HttpServletRequest
- Data
- View

### Service部分
- @Service

### Dao部分
- @Tns
- BaseDataSource
- @DataSource
- @Table
- @Id
- @Unique
- @Comment
- @ColumnDefine
- @Transient
- @Dao
- BaseRepository
- @Sql
- @ResultProxy
- SqlResultProxy
- Page
- PageConfig
- Sharding


## IOC怎么支持
axe的ioc(依赖注入)功能由BeanHelper实现，所有的注入实例也可以从BeanHelper中获取(后面讲到)。axe提供了如下的注解来方便ioc的使用。

- @Controller
- @Service
- @Dao
- @Autowired  

具体用法下面会慢慢介绍。

## Controller Class
略

## Action Method
目前支持POST、DELETE、PUT、GET四种。在其他场景下需要HEAD、OPTION等类型可以自行扩展。

## Param
略

## FileParam
略

## Data
略

## View
略

## Filter
过滤器用以过滤请求，在过滤的过程中判断是否需要继续往下跳转直至Controller。

| 方法          | 返回值               | 描述                                    |
| ------------- |:--------------------:| ---------------------------------------:|
| setLevel      | int                  | 过滤器层级，按递增排列，数字小的先执行  |
| setMapping    | Pattern              | 设置url匹配规则                         |
| setNotMapping | Pattern              | 设置url匹配的集合中，需要排除的url规则  |

> setMapping可以设置 "^.*$" 来匹配所有，再加一个setNotMapping设置 "login"来放开登录，就实现了简单的权限过滤。

#### 有顺序的Filter链
过滤器有顺序，框架启动后每个action上都有一个过滤器列表。
#### FuckOff
上面说可以通过setNotMapping来排除不需要过滤的url(从需要过滤的url集合中)，这适合需要排除的url不止一个的情况。
我们也可以在Controller的方法上直接使用@FilterFuckOff来排除指定的Filter，在有些场景下更方便。

## Interceptor
拦截器需要指定在Controller或者Controller方法上，才会工作。

## Listener
监听器在系统启动时候会被执行。

## RedirectorException
在系统的任何地方抛出这个异常给框架，框架会执行跳转命名，地址在异常对象中指定，并且可以携带参数。

## Service
Service在大部分情况下都与Component一样，但是@Tns事务控制只在@Service下有效。

## Table Eentity
#### Table自建
可以配置参数jdbc.auto_create_table 来自动创建表结构。如果表结构已经存在则不会创建，即便字段有变化，需先要手动删除表。
axe只会创建带有@Table注解的Entity表结构，并且可以指定数据库多个。

#### 主键、联合主键
@Id注解标注的字段表示主键，联合主键很简单，多个字段都加上@Id注解即可。

#### 如果不需要持久化的方式
实体类的字段会被持久化，必须有对应的set、get方法，而且命名要符合规范。因此，不希望被持久化的字段，只要去掉set、get方法即可。
去掉set方法不被保存，去掉get方法不会查询。
在我们以往的实践中，往往不是去掉set和get方法，而是在不需要持久化的字段的set和get方法末尾加"_"，表示扩展字段，这样既不会被持久化，也方便json工具格式化，可直接用于前台的json数据渲染。

#### 用@ColumnDefine来自定义表字段
axe对于字段的定义性注解较少，如果希望自定义字段的类型、长度、必填等等特殊情况，那么可以直接使用@ColumnDefine来写表字段的sql语句。
```java
@Table("Test")
public class TestTable {

    @Id
	private long id;
	@ColumnDefine("char(100) NOT NULL UNIQUE")
	private String name;
	
	private int status;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int _getStatus() {
		return status;
	}
	public void _setStatus(int status) {
		this.status = status;
	}
}
```
我们注意到，字段名是不许自定义的，这点需要注意。

## Dao
凡是被@Dao注解标注的接口，axe都会识别为dao层入口，框架会自动实现接口中带@SQL注解的方法，以供实例注入到service或component后调用。
Dao除了需要@Dao注解外，还可以选择性的继承BaseRepository接口，BaseRepository提供了针对Entity对象的增删改查方法，但需要注意，
使用这个接口内的方法，要求Entity类必须有@Id标注的字段，也就是必须有主键。

## Sql and Entity
Entity可以没有@Table注解，这种情况下，必须类名与表名能够符合规范可以转换，如TableA对应table_a表。如果有@Table注解，则不需要这种默认关系。
Sql语句支持表名、字段名用类名、类字段来代替书写，类似HQL。SQL中的占位符可以使用?，也可以使用?加数字，前者默认使用方法参数顺序，
后者可以用数字指定占位符对应方法参数中的第几个参数。
```java
@Dao
public interface TestDao extends BaseRepository{
	
	//占位符?取值顺序按照方法参数的顺序挨个取
	@Sql("select * from TestTable where id = ? and name = ?")
	public TestTable getOne(long id,String name);

	@Sql("select * from TestTable")
	public List<TestTable> getAll();
	
	@Sql("select * from TestTable where name like '%test%'")
	public Page<TestTable> page();
	
	@Sql("select * from Export")
	public List<Export> getAllExport();
	
	//占位符指定好了取方法参数的第一个参数。
	@Sql("select * from Export where name like ?1")
	public Page<Export> pagingExport(String name,PageConfig pageConfig);
}
```

## Sql 拼接
axe的dao层也支持sql语句的条件拼接，使用方法如下：
```java
@Dao
public interface TestDao extends BaseRepository{
	
	//第二个参数append 可以通过#([1-9][0-9]*)来动态拼接到sql中，并且同样支持类属性写法如userAage，不用改成表字段写法user_age。
	@Sql("select * from TestTable where id = ? #2")
	public TestTable getOne(long id,String append);

}
```

## Transaction Tns
axe的事务，如果出现迭代调用开启事务，只会在最外层打开，并且当回到最外层后提交，内层的打开事务与提交事务会被忽略。
另外，如果多数据源情况下，事务会集体打开，集体提交，但是如果存在异常的情况，如果是业务代码异常，则事务集体回滚，
如果是第二或者第三个事务在提交时发生异常，则第一个事务已经提交无法回滚，会被记录在日志中。


## BaseDataSource 和 @DataSource
实现自定义的数据源，需要作两件事，第一要实现BaseDataSource接口，第二要加上@DataSource注解，后者是为多数据源作名字的区分，当然如果只有一个，也无所谓了但是注解也是要加的。
如果系统里有多个数据源，那么推荐做法是这样：保证从@Table 到@Dao 都是相同的数据源配置，这样可以避免不匹配的错误出现，因为Axe对指定数据源的选取优先级是@Dao>@Table>默认数据源，但是当且仅当系统启动自动建表时除外，因为这时候只能通过@Table来确定数据源。 

## Proxy
框架的代理接口，需要自我实现代理切面的，需要实现此接口。

## /axe
框架启动成功，可以访问此地址来管理框架系统配置。

## Sign In
如果/axe中打开了需要登录和设置了密码，那么就需要账号登录才能继续访问/axe管理面板了。

## Email
在/axe的配置页面中，有一向是Email项，如果填写了，则会收到系统的错误和异常的通知邮件。

## Release Resources
如果配置选择了此项，那么会丢失一部分框架启动时候初始化到内存中的数据，对系统的正常运行没有影响，反而更节省内存，只是失去了一些框架级的快捷功能。


## BeanHelper的使用和注意
可以对全局的ioc托管实例进行获取和操作，一般很少用到。  

| 方法        | 返回值               | 描述                      |
| ----------- |:--------------------:| -------------------------:|
| getBeanMap  | Map<Class<?>,Object> | 获取所有托管中的Bean实例  |
| getBean     | T                    | 见注释                    |
| setBean     |                      | 添加实例给BeanHelpler管理 |

>注.返回类型根据方法传入的参数类型来匹配。可以获取的Bean类型包括@Controller、@Component、@Service、@Dao
注解标注的类，注意@Dao是接口注解并且在框架启动阶段，@Dao注解的实例并不能从BeanHelper中获取到，需要等待框架启动完成才可获取到。

## Aspect Proxy
#### begin
#### intercept
#### before
#### after
#### error
#### end

## 框架启动顺序

