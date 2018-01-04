## Get Start 快速开始
新建工程。首先，确认你安装好了maven，然后打开cmd，切换到你自己的目录下，创建一个空的maven项目。
```
mvn archetype:generate -DgroupId=com.ybsl -DartifactId=shop-api
```
配置工程。进入src\main文件夹下，新建一个文件夹叫resources。进入resources后新建一个axe.properties文件，内容如下。
```
#==================axe框架配置文件==================

#是否打开axe框架管理界面的登录
#axe.signin=true 进入axe框架管理界面需要登陆录
#axe.signin=false 进入axe框架管理界面不需要登陆录，生产环境建议true
axe.signin=true

#登录密码的md5后的令牌，获取方式如下
#String username = "axe";
#String password = "13776255717";
#String axe_signin_token = MD5Util.getMD5Code(username+":"+password);
axe.signin.token=804bd02e2548611fca83965b5f18f1d8

#是否释放框架初始化完成后的，ClassHelper内的classSet集合是否保留
#建议false，不保留，减小内存占用
axe.classhelper.keep=false

#指定数据源
#可以指定多个，比如 jdbc.datasource=druid,api,united,pointsShop,aio,card
#axe-datasource-dbcp是axe框架提供的默认数据源
#可以使用自己的数据源，用法参考README里的数据源一节
jdbc.datasource=axe-datasource-dbcp

#如果使用axe-datasource-dbcp，需要在axe.properties里指定好jdbc的配置{
#如果使用自己的数据源比如druid，则此段配置（从{到}）不需要，可以另外写配置文件。
jdbc.username=root
jdbc.password=1234
jdbc.url=jdbc:mysql://localhost:3306/sl1288-shop?useUnicode=true&characterEncoding=utf-8
jdbc.driver=com.mysql.jdbc.Driver
#}

#是否自动建表
jdbc.auto_create_table=true

#是否打印sql语句
jdbc.show_sql=false

#指定好邮箱，框架会将异常信息发送给这些邮箱
#可以是多个邮箱 axe.email=1234512345@qq.com,1234512345@qq.com,1234512345@qq.com
axe.email=

#框架扫描的包路径
#可以是多个路径
#app.base_package=com.ybsl,com.ybsl
app.base_package=com.ybsl

#如果工程内有jsp或者其他view层的静态文件，这里需要指定这些文件的路径
#比如src/main/webapp下，有static文件夹里放的是静态css、图片、js等文件
#比如src/main/webapp下，有view文件夹里放的是jsp文件
app.asset_path=/static
app.jsp_path=/view
```


修改pom.xml。打开工程目录下的pom.xml，替换成下面的内容。
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.ybsl</groupId>
    <artifactId>shop-api</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>war</packaging>
	
	<properties>
		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
	</properties>

    <dependencies>
        <!-- Axe 核心依赖 0.1是版本 .7是jdk7 -->
        <dependency>
            <groupId>org.axe</groupId>
            <artifactId>axe</artifactId>
            <version>0.1.7</version>
        </dependency>
    	<!-- Apache DBCP 数据源(这是默认数据源，可以自我指定数据源)-->
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

		<!-- java web 基础包 -->
        <!-- Servlet -->
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>javax.servlet-api</artifactId>
            <version>3.1.0</version>
            <scope>provided</scope>
        </dependency>
        <!-- JSP -->
        <dependency>
            <groupId>javax.servlet.jsp</groupId>
            <artifactId>jsp-api</artifactId>
            <version>2.2</version>
            <scope>provided</scope>
        </dependency>
        <!-- JSTL -->
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>jstl</artifactId>
            <version>1.2</version>
            <scope>runtime</scope>
        </dependency>
    </dependencies>
	
	<profiles>
		<profile>
			<id>dev</id>
			<activation>
				<activeByDefault>true</activeByDefault>
			</activation>
			<properties>
				<runtime.env>config/dev</runtime.env>
			</properties>
		</profile>
		<profile>
			<id>prod</id>
			<activation>
				<activeByDefault>false</activeByDefault>
			</activation>
			<properties>
				<runtime.env>config/prod</runtime.env>
			</properties>
		</profile>
	</profiles>
	
	<build>
		<finalName>shop-api</finalName>
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
转化工程。如果IDE是eclipse，打开cmd，切换到pom.xml所在路径下，执行以下命令。
```
mvn clean eclipse:eclipse -Dwtpversion=1.0
```
导入工程，打开eclipse，import工程，当作java普通工程导入，然后删掉两个报错的AppTest.java文件即可。
（完）

## IOC怎么支持
axe的ioc(依赖注入)功能由BeanHelper实现，所有的注入实例也可以从BeanHelper中获取(后面讲到)。axe提供了如下的注解来方便ioc的使用。

- @Controller
- @Service
- @Dao
- @Autowired  

具体用法下面会慢慢介绍。

## MVC在axe中长什么样
axe推荐的风格是前后端分离，也就是view是前台的事情，后台服务只提供数据，只负责MVC中的Model和Controller。
但是axe也支持完整的MVC，View层可以通过在Controller中返回的结果类型(View.class)来跳转，View.class对象的实例包含一个地址字符串，支持携带参数给jsp页面。
```java
@Controller(title="跳转jsp的controller",basePath="/to")
public class ToJspController extends BaseRest{
	
	@Autowired
	private CustomerService customerService;
	....
	@Autowired
	private AreaOpenService areaOpenService;
	
	/**
	 * 应为微信进来，是带着code的，这个地址必须sendRedirect来改掉，
	 * 否则刷新页面，code是不能重复用的
	 */
	@Request(title="邀请注册[微信入口]",value="/invite_wx",method=RequestMethod.GET)
	public View invite_wx(HttpServletRequest request,HttpServletResponse response){
		return new View("/to/invite").addModel("token", getToken(request));
	}
	
	@Request(title="邀请注册",value="/invite",method=RequestMethod.GET)
	public View invite(HttpServletRequest request,HttpServletResponse response){
		View view = new View("/view/invite.jsp").dispatcher();
		//token 和 用户信息
		view.addModel("token", getToken(request));
		Long customertUserId = getCustomerUserId(request);
		if(customertUserId != null){
			//如果已注册，直接跳首页
			return new View("/to/index").addModel("token", getToken(request));
		}
		
		return view;
	}
	
	/**
	 * 应为微信进来，是带着code的，这个地址必须sendRedirect来改掉，
	 * 否则刷新页面，code是不能重复用的
	 */
	@Request(title="首页[微信入口]",value="/index_wx",method=RequestMethod.GET)
	public View index_wx(HttpServletRequest request,HttpServletResponse response){
		return new View("/to/index").addModel("token", getToken(request));
	}
	
	@Request(title="首页",value="/index",method=RequestMethod.GET)
	public View index(HttpServletRequest request,HttpServletResponse response){
		View view = new View("/view/index.jsp").dispatcher();
		//token 和 用户信息
		view.addModel("token", getToken(request));
		Long customertUserId = getCustomerUserId(request);
		if(customertUserId != null){
			CustomerVO customerVO = customerService.getCustomerVO(customertUserId);
			view.addModel("customerVO", customerVO);
		}
		
		//开放城市列表
		view.addModel("openAreaList", areaOpenService.list());
		
		//首页的轮播图，活动精选
		List<CustomerBanner> list = customerBannerService.getList();
		view.addModel("customerBannerList", list);
		
		return view;
	}
	
	@Request(title="个人中心[微信入口]",value="/user_wx",method=RequestMethod.GET)
	public View user_wx(HttpServletRequest request,HttpServletResponse response){
		return new View("/to/user").addModel("token", getToken(request));
	}
	
	@Request(title="个人中心",value="/user",method=RequestMethod.GET)
	public View user(HttpServletRequest request,HttpServletResponse response){
		View view = new View("/view/user.jsp").dispatcher();
		//token 和 用户信息
		view.addModel("token", getToken(request));
		Long customertUserId = getCustomerUserId(request);
		if(customertUserId != null){
			CustomerVO customerVO = customerService.getCustomerVO(customertUserId);
			view.addModel("customerVO", customerVO);
			if(customerVO != null && customerVO.getBaseUserInfo() != null){
				//余额账户信息
				RestData<BalanceAccountVO> balanceAccount = UnitedUserRestClient.getBalanceAccountByUid(customerVO.getBaseUserInfo().getUid());
				if(RestDataCode.SUCCESS.equals(balanceAccount.getCode())){
					if("000".equals(balanceAccount.getData().get_CODE_())){
						view.addModel("balanceAccount", balanceAccount.getData());
					}
				}
				//积分账户信息
				RestData<PointsAccountVO> pointsAccount = UnitedUserRestClient.getPointsAccountByUid(customerVO.getBaseUserInfo().getUid());
				if(RestDataCode.SUCCESS.equals(pointsAccount.getCode())){
					if("000".equals(pointsAccount.getData().get_CODE_())){
						view.addModel("pointsAccount", pointsAccount.getData());
					}
				}
			}
		}
		return view;
	}
	

	@Request(title="地址管理",value="/address_manager",method=RequestMethod.GET)
	public View address_manager(HttpServletRequest request,HttpServletResponse response){
		View view = new View("/view/address_manager.jsp").dispatcher();
		//token 和 用户信息
		view.addModel("token", getToken(request));
		Long customertUserId = getCustomerUserId(request);
		if(customertUserId != null){
			//用户的地址列表
			CustomerUser entity = customerService.getEntity(customertUserId);
			if(entity != null){
				RestData<AddressListVO> addressList =  UnitedUserRestClient.getAddressList(entity.getUid());
				if(RestDataCode.SUCCESS.equals(addressList.getCode())){
					if("000".equals(addressList.getData().get_CODE_())){
						view.addModel("addressList", addressList.getData().getRecords());
					}
				}
			}
		}
		return view;
	}
	
	@Request(title="地址管理-新增地址",value="/address_new",method=RequestMethod.GET)
	public View address_new(HttpServletRequest request,HttpServletResponse response){
		View view = new View("/view/address_new.jsp").dispatcher();
		//token 和 用户信息
		view.addModel("token", getToken(request));

		//开放城市列表
		view.addModel("openAreaList", areaOpenService.list());
		return view;
	}
	
	@Request(title="地址管理-编辑地址",value="/address_edit/{addressId}",method=RequestMethod.GET)
	public View address_edit(
			@Required
			@RequestParam("addressId")Long addressId,
			HttpServletRequest request,HttpServletResponse response){
		View view = new View("/view/address_edit.jsp").dispatcher();
		//token 和 用户信息
		view.addModel("token", getToken(request));
		//用户的地址列表
		Long customertUserId = getCustomerUserId(request);
		if(customertUserId != null){
			CustomerUser entity = customerService.getEntity(customertUserId);
			if(entity != null){
				RestData<AddressListVO> addressList = UnitedUserRestClient.getAddressList(entity.getUid());
				if(RestDataCode.SUCCESS.equals(addressList.getCode())){
					if("000".equals(addressList.getData().get_CODE_())){
						if(CollectionUtil.isNotEmpty(addressList.getData().getRecords())){
							for(AddressVO address:addressList.getData().getRecords()){
								if(address.getId() == addressId){
									view.addModel("address", address);
									break;
								}
							}
						}
					}
				}
			}
		}
		return view;
	}
	
	@Request(title="我的订单",value="/order_list",method=RequestMethod.GET)
	public View order_list(
			@RequestParam("toDoorOrderActive")String toDoorOrderActive,
			@RequestParam("collectionBoxOrderActive")String collectionBoxOrderActive,
			HttpServletRequest request,HttpServletResponse response){
		if(StringUtil.isEmpty(toDoorOrderActive) && StringUtil.isEmpty(collectionBoxOrderActive)){
			toDoorOrderActive = "active";
		}
		
		View view = new View("/view/order_list.jsp").dispatcher();
		view.addModel("toDoorOrderActive", toDoorOrderActive);
		view.addModel("collectionBoxOrderActive", collectionBoxOrderActive);
		
		//token 和 用户信息
		view.addModel("token", getToken(request));
		Long customertUserId = getCustomerUserId(request);
		if(customertUserId != null){
			//订单列表
			view.addModel("toDoorOrderList", toDoorOrderService.getList(customertUserId));
			view.addModel("collectionBoxOrderList", collectionBoxOrderService.getList(customertUserId,null));
		}

		
		return view;
	}
	

	@Request(title="预约上门订单评价",value="/to_door_order_comment",method=RequestMethod.GET)
	public View to_door_order_comment(
			@Required
			@RequestParam("id")Long id,
			@Required
			@RequestParam("buyerUserId")Long buyerUserId,
			HttpServletRequest request,HttpServletResponse response){
		View view = new View("/view/to_door_order_comment.jsp").dispatcher();
		view.addModel("id", id);
		
		//token 和 用户信息
		view.addModel("token", getToken(request));
		
		//加载回收员信息
		BuyerUser buyerUser = buyerService.getEntity(buyerUserId);
		view.addModel("buyerUser", buyerUser);
		
		return view;
	}
	

	@Request(title="预约上门订单详情",value="/to_door_order",method=RequestMethod.GET)
	public View to_door_order(
			@Required
			@RequestParam("id")Long id,
			HttpServletRequest request,HttpServletResponse response){
		View view = new View("/view/to_door_order.jsp").dispatcher();
		//token 和 用户信息
		view.addModel("token", getToken(request));
		//订单信息
		ToDoorOrder toDoorOrder = toDoorOrderService.getEntity(id);
		if(toDoorOrder != null){
			toDoorOrder = toDoorOrderService.setOtherIfo(toDoorOrder);
			
			//加载回收员信息
			view.addModel("id", id);
			if(toDoorOrder.getBuyerUserId() != null){
				BuyerUser buyerUser = buyerService.getEntity(toDoorOrder.getBuyerUserId());
				view.addModel("buyerUser", buyerUser);
				if(buyerUser != null){
					RestData<BaseUserInfoVO> baseUserInfo = UnitedUserRestClient.getBaseUserInfoByUid(buyerUser.getUid());
					if(RestDataCode.SUCCESS.equals(baseUserInfo.getCode())){
						if("000".equals(baseUserInfo.getData().get_CODE_())){
							view.addModel("buyerUserBaseUserInfo", baseUserInfo.getData());
						}
					}
				}
			}
		}
		view.addModel("toDoorOrder", toDoorOrder);
		
		return view;
	}
	
	@Request(title="回收箱订单详情",value="/collection_box_order",method=RequestMethod.GET)
	public View collection_box_order(
			@Required
			@RequestParam("id")Long id,
			HttpServletRequest request,HttpServletResponse response){
		View view = new View("/view/collection_box_order.jsp").dispatcher();
		//token 和 用户信息
		view.addModel("token", getToken(request));
		//订单信息
		CollectionBoxOrder collectionBoxOrder = collectionBoxOrderService.getEntity(id);
		view.addModel("id", id);
		view.addModel("collectionBoxOrder", collectionBoxOrder);
		if(collectionBoxOrder != null && collectionBoxOrder.getCollectionBoxId() != null){
			//补充信息
			CollectionBox collectionBox = collectionBoxService.getEntity(collectionBoxOrder.getCollectionBoxId());
			view.addModel("collectionBox", collectionBox);
		}
		
		return view;
	}
	
	//从微信进来，为了避免code留在地址栏，刷新后无效，所以转发一次
	@Request(title="回收箱订单投放[微信入口]",value="/collection_box_order_put_wx",method=RequestMethod.GET)
	public View collection_box_order_put_wx(
			@Required
			@RequestParam("state")String collectionBoxNumber,
			HttpServletRequest request,HttpServletResponse response){
		
		return new View("/to/collection_box_order_put")
				.addModel("token", getToken(request))
				.addModel("collectionBoxNumber", collectionBoxNumber);
	}

	//暂时来说，这个页面没有回收箱的信息，只有待投放的订单列表
	@Request(title="回收箱订单投放",value="/collection_box_order_put",method=RequestMethod.GET)
	public View collection_box_order_put(
			@Required
			@RequestParam("collectionBoxNumber")String collectionBoxNumber,
			@RequestParam("collectionBoxOrderId")Long collectionBoxOrderId,
			HttpServletRequest request,HttpServletResponse response){
		View view = new View("/view/collection_box_order_put.jsp").dispatcher();
		//token 和 用户信息
		view.addModel("token", getToken(request));
		view.addModel("collectionBoxNumber", collectionBoxNumber);
		//默认勾选的订单id
		view.addModel("collectionBoxOrderId", collectionBoxOrderId);
		
		//待投放订单列表
		Long customertUserId = getCustomerUserId(request);
		if(customertUserId != null){
			view.addModel("collectionBoxOrderList", collectionBoxOrderService.getList(customertUserId,0));
		}
		
		return view;
	}
	
	@Request(title="回收箱详情",value="/collection_box",method=RequestMethod.GET)
	public View collection_box(
			@Required
			@RequestParam("collectionBoxNumber")String collectionBoxNumber,
			HttpServletRequest request,HttpServletResponse response){
		View view = new View("/view/collection_box.jsp").dispatcher();
		//token 和 用户信息
		view.addModel("token", getToken(request));
		view.addModel("collectionBoxNumber", collectionBoxNumber);
		if(StringUtil.isNotEmpty(collectionBoxNumber)){
			CollectionBox entity = collectionBoxService.getByCollectionBoxNumber(collectionBoxNumber);
			if(entity != null){
				entity = collectionBoxService.setOtherInfo(entity);
				view.addModel("collectionBox", entity);
			}
		}
		return view;
	}


	@Request(title="预约上门下单页面",value="/to_door_order_create",method=RequestMethod.GET)
	public View to_door_order_create(
			@RequestParam("area")String area,
			HttpServletRequest request,HttpServletResponse response){
		View view = new View("/view/to_door_order_create.jsp").dispatcher();
		//token 和 用户信息
		view.addModel("token", getToken(request));
		//预约上门，用户端的分类列表
		view.addModel("catalogList",new ArrayList<>());
		if(StringUtil.isNotEmpty(area)){
			String[] areaSplit = area.split("-");
			if(areaSplit.length == 3){
				view.addModel("catalogList",toDoorOrderCatalog4CustomerService.list(areaSplit[0],areaSplit[1],areaSplit[2]));
			}
		}
		
		//用户的地址列表
		view.addModel("addressList", new ArrayList<>());
		Long customertUserId = getCustomerUserId(request);
		if(customertUserId != null){
			CustomerUser entity = customerService.getEntity(customertUserId);
			if(entity != null){
				RestData<AddressListVO> addressList =  UnitedUserRestClient.getAddressList(entity.getUid());
				if(RestDataCode.SUCCESS.equals(addressList.getCode())){
					if("000".equals(addressList.getData().get_CODE_())){
						List<AddressVO> records = addressList.getData().getRecords();
						//默认地址，没有的话，取第一个
						if(CollectionUtil.isNotEmpty(records)){
							for(AddressVO address:records){
								String addressArea = address.getProvinceName()+"-"+address.getCityName()+"-"+address.getCountyName();
								if(addressArea.equals(area)){
									address.set_CODE_("");
									if(address.getIsDefault() == 1){
										view.addModel("defaultAddress", address);
									}
								}else{
									address.set_CODE_("yb-disabled");
								}
							}
							view.addModel("addressList", records);
						}
					}
				}
			}
		}
		
		return view;
	}
	
	@Request(title="预约上门下单成功页面",value="/to_door_order_create_success",method=RequestMethod.GET)
	public View to_door_order_create_success(
			@Required
			@RequestParam("orderId")Long orderId,
			@Required
			@RequestParam("orderNumber")String orderNumber,
			HttpServletRequest request,HttpServletResponse response){
		View view = new View("/view/to_door_order_create_success.jsp").dispatcher();
		view.addModel("orderId", orderId);
		view.addModel("orderNumber", orderNumber);
		//token 和 用户信息
		view.addModel("token", getToken(request));
		return view;
	}
	
	@Request(title="回收箱下单页面",value="/collection_box_order_create",method=RequestMethod.GET)
	public View collection_box_order_create(HttpServletRequest request,HttpServletResponse response){
		View view = new View("/view/collection_box_order_create.jsp").dispatcher();
		//token 和 用户信息
		view.addModel("token", getToken(request));
		
		//回收箱，用户端的分类列表
		view.addModel("catalogList",collectionBoxOrderCatalogService.list());
		
		return view;
	}
	
	@Request(title="回收箱下单成功页面",value="/collection_box_order_create_success",method=RequestMethod.GET)
	public View collection_box_order_create_success(
			@Required
			@RequestParam("orderId")Long orderId,
			@Required
			@RequestParam("orderNumber")String orderNumber,
			HttpServletRequest request,HttpServletResponse response){
		View view = new View("/view/collection_box_order_create_success.jsp").dispatcher();
		view.addModel("orderId", orderId);
		view.addModel("orderNumber", orderNumber);
		//token 和 用户信息
		view.addModel("token", getToken(request));
		return view;
	}
	
	@Request(title="附近的回收箱页面",value="/nearby_collection_box",method=RequestMethod.GET)
	public View nearby_collection_box(
			@Required
			@RequestParam("lng")Double lng,
			@Required
			@RequestParam("lat")Double lat,
			HttpServletRequest request,HttpServletResponse response){
		View view = new View("/view/nearby_collection_box.jsp").dispatcher();
		//token 和 用户信息
		view.addModel("token", getToken(request));
		
		//附近的回收箱
		view.addModel("collectionBoxList",collectionBoxService.list(lng,lat));
		
		return view;
	}


	@Request(title="城管局-垃圾分类[app入口]",value="/garbage_can_order_create_app",method=RequestMethod.GET)
	public View garbage_can_order_create_app(HttpServletRequest request,HttpServletResponse response){
		return new View("/to/garbage_can_order_create")
				.addModel("token", getToken(request));
	}
	
	@Request(title="城管局-垃圾分类",value="/garbage_can_order_create",method=RequestMethod.GET)
	public View garbage_can_order_create(HttpServletRequest request,HttpServletResponse response){
		View view = new View("/view/garbage_can_order_create.jsp").dispatcher();
		//token 和 用户信息
		view.addModel("token", getToken(request));
		
		return view;
	}
	
	//从微信进来，为了避免code留在地址栏，刷新后无效，所以转发一次
	@Request(title="城管局垃圾箱订单投放[微信入口]",value="/garbage_can_order_put_wx",method=RequestMethod.GET)
	public View garbage_can_order_put_wx(
			HttpServletRequest request,HttpServletResponse response){
		
		return new View("/to/garbage_can_order_put")
				.addModel("token", getToken(request));
	}
	
	@Request(title="城管局垃圾箱订单投放",value="/garbage_can_order_put",method=RequestMethod.GET)
	public View garbage_can_order_put(
			HttpServletRequest request,HttpServletResponse response){
		View view = new View("/view/garbage_can_order_put.jsp").dispatcher();
		//token 和 用户信息
		view.addModel("token", getToken(request));
		return view;
	}
	
	//从微信进来，为了避免code留在地址栏，刷新后无效，所以转发一次
	@Request(title="积分商城[微信入口]",value="/points_shop_wx",method=RequestMethod.GET)
	public View points_shop_wx(
			HttpServletRequest request,HttpServletResponse response){
		
		return new View("/to/points_shop")
				.addModel("token", getToken(request));
	}
	
	@Request(title="积分商城",value="/points_shop",method=RequestMethod.GET)
	public View points_shop(
			HttpServletRequest request,HttpServletResponse response){
		View view = new View("/view/points_shop.jsp").dispatcher();
		//token 和 用户信息
		view.addModel("token", getToken(request));
		return view;
	}

	@Request(title="余额提现",value="/balance_withdraw",method=RequestMethod.GET)
	public View balance_withdraw(
			HttpServletRequest request,HttpServletResponse response){
		View view = new View("/view/balance_withdraw.jsp").dispatcher();
		//token 和 用户信息
		view.addModel("token", getToken(request));

		Long customertUserId = getCustomerUserId(request);
		if(customertUserId != null){
			CustomerUser customer = customerService.getEntity(customertUserId);
			if(customer != null){
				
				//余额账户信息
				RestData<BalanceAccountVO> balanceAccount = UnitedUserRestClient.getBalanceAccountByUid(customer.getUid());
				if(RestDataCode.SUCCESS.equals(balanceAccount.getCode())){
					if("000".equals(balanceAccount.getData().get_CODE_())){
						view.addModel("balanceAccount", balanceAccount.getData());
					}
				}
				
				//是否关注事连公众号
				view.addModel("subscribe", 0);//默认未关注，那就不能提现到微信
				String openid = getOpenid(request);
				if(StringUtil.isNotEmpty(openid)){
					CustomerUserWechatLogin customerUserWechatLogin = customerService.getCustomerUserWechatLogin(openid);
					if(customerUserWechatLogin != null){
						if(customerUserWechatLogin.getSubscribe() != null){
							view.addModel("subscribe",customerUserWechatLogin.getSubscribe());
						}
					}
				}
				
				//最近提现
				RestData<BalanceWithdrawLastRecordsVO> balanceWithdrawLastRecords = UnitedUserRestClient.getBalanceWithdrawLastRecords(customer.getUid());
				if(RestDataCode.SUCCESS.equals(balanceWithdrawLastRecords.getCode())){
					if("000".equals(balanceWithdrawLastRecords.getData().get_CODE_())){
						
						List<BalanceWithdrawVO> records = balanceWithdrawLastRecords.getData().getRecords();
						List<Map<String,String>> result = new ArrayList<>();
						if(CollectionUtil.isNotEmpty(records)){
							StringBuilder buf = new StringBuilder();
							for(BalanceWithdrawVO vo:records){
								buf.setLength(0);
								Map<String,String> row = new HashMap<>();
								row.put("id", String.valueOf(vo.getId()));
								row.put("withdrawAccount", vo.getWithdrawAccount());
								row.put("withdrawRealName", vo.getWithdrawRealName());
								
								String withdrawAccount_ = vo.getWithdrawAccount();
								if(StringUtil.isNotEmpty(withdrawAccount_) && withdrawAccount_.indexOf(",") > 0){
									String withdrawAccount_1 = withdrawAccount_.substring(0, withdrawAccount_.indexOf(","));
									String withdrawAccount_2 = withdrawAccount_.substring(withdrawAccount_.indexOf(","));
									if(withdrawAccount_2.length() > 4){
										for(int i=0;i<withdrawAccount_2.length()-4;i++){
											buf.append("*");
										}
										buf.append(withdrawAccount_2.substring(withdrawAccount_2.length()-4));
										withdrawAccount_2 = buf.toString();
									}
									withdrawAccount_ = withdrawAccount_1+withdrawAccount_2;
								}
								row.put("withdrawAccount_", withdrawAccount_);
								
								String withdrawRealName_ = vo.getWithdrawRealName();
								if(withdrawRealName_.length() > 1){
									withdrawRealName_ = "*"+withdrawRealName_.substring(1);
								}
								row.put("withdrawRealName_", withdrawRealName_);
								result.add(row);
							}
							buf.setLength(0);
						}
						
						view.addModel("balanceWithdrawLastRecords", result);
					}
				}
			}
		}
		
		return view;
	}
	
	@Request(title="余额提现记录",value="/balance_withdraw_records",method=RequestMethod.GET)
	public View balance_withdraw_records(
			HttpServletRequest request,HttpServletResponse response){
		View view = new View("/view/balance_withdraw_records.jsp").dispatcher();
		//token 和 用户信息
		view.addModel("token", getToken(request));
		
		//提现记录
		Long customerUserId = getCustomerUserId(request);
		if(customerUserId != null){
			CustomerUser entity = customerService.getEntity(customerUserId);
			if(entity != null){
				RestData<BalanceWithdrawRecordsVO> balanceWithdrawRecords = UnitedUserRestClient.getBalanceWithdrawRecords(entity.getUid());
				if(RestDataCode.SUCCESS.equals(balanceWithdrawRecords.getCode())){
					if("000".equals(balanceWithdrawRecords.getData().get_CODE_())){
						List<BalanceWithdrawVO> records = balanceWithdrawRecords.getData().getRecords();
						List<Map<String,String>> result = new ArrayList<>();
						if(CollectionUtil.isNotEmpty(records)){
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							StringBuilder buf = new StringBuilder();
							for(BalanceWithdrawVO vo:records){
								buf.setLength(0);
								Map<String,String> row = new HashMap<>();
								row.put("id", String.valueOf(vo.getId()));
								row.put("withdrawCash", String.valueOf(vo.getWithdrawCash()));
								row.put("createTime", sdf.format(vo.getCreateTime()));
								row.put("status", String.valueOf(vo.getStatus()));
								if(StringUtil.isNotEmpty(vo.getRemark())){
									row.put("remark", vo.getRemark());
								}
								
								String withdrawAccount_ = vo.getWithdrawAccount();
								if(StringUtil.isNotEmpty(withdrawAccount_) && withdrawAccount_.indexOf(",") > 0){
									String withdrawAccount_1 = withdrawAccount_.substring(0, withdrawAccount_.indexOf(","));
									String withdrawAccount_2 = withdrawAccount_.substring(withdrawAccount_.indexOf(","));
									if(withdrawAccount_2.length() > 4){
										for(int i=0;i<withdrawAccount_2.length()-4;i++){
											buf.append("*");
										}
										buf.append(withdrawAccount_2.substring(withdrawAccount_2.length()-4));
										withdrawAccount_2 = buf.toString();
									}
									withdrawAccount_ = withdrawAccount_1+withdrawAccount_2;
								}
								row.put("withdrawAccount_", withdrawAccount_);
								
								String withdrawRealName_ = vo.getWithdrawRealName();
								if(withdrawRealName_.length() > 1){
									withdrawRealName_ = "*"+withdrawRealName_.substring(1);
								}
								row.put("withdrawRealName_", withdrawRealName_);
								result.add(row);
							}
							buf.setLength(0);
						}
						
						view.addModel("balanceWithdrawRecords", result);
					}
				}
			}
		}
		return view;
	}
}
```


## Restful
具体定义这里不做解释了，axe对rest请求url中的参数支持如下的解析方式。

- 如 /get/{id}_{name}  中id和name是参数 
- 如 /get_{id}/{name}  也是id和name表示参数
- 参数只能是数字和字母，url只能是数字、字母、下划线和$符

下面是一个比较完整的请求参数事例。
```java
@Request(value="/post{money}/4{id}_{name}",method=RequestMethod.POST)
    public Data postPathParam(
        	@RequestParam("money")Integer money,//如果money是整数，这里就有值，如果是别的，甚至是字符串，就会是null
    		
    		@RequestParam("file")FileParam file1,//单个文件，如果上传的是多文件，只会拿到最后一个
    		@RequestParam("file")Object file2,
    		@RequestParam("file")List<FileParam> filesList1,
    		@RequestParam("file")List<?> filesList2,
    		@RequestParam("file")List filesList3,
    		@RequestParam("file")FileParam[] filesAry1,
    		@RequestParam("file")Object[] filesAry2,
    		
    		@RequestParam("ids")Integer ids,//如果传递的参数是多个，只会拿到最后一个
    		@RequestParam("ids")List<String> idsList1,
    		@RequestParam("ids")List<?> idsList2,
    		@RequestParam("ids")List<Integer> idsList3,
    		@RequestParam("ids")List idsList4,
    		@RequestParam("ids")String[] idsAry1,//如果传递的参数是多个，会用","拼接
    		@RequestParam("ids")Integer[] idsAry2,
    		@RequestParam("ids")Double[] idsAry3,
    		@RequestParam("ids")Object[] idsAry4,
    		
    		@RequestParam("name")String name1,
    		@RequestParam("name")Object name2,
    		@RequestParam("name")List<String> nameList1,
    		@RequestParam("name")List<?> nameList2,
    		@RequestParam("name")List nameList3,
    		@RequestParam("name")String[] nameAry1,
    		@RequestParam("name")Object[] nameAry2,
    		
    		HttpServletRequest request,
    		HttpServletResponse response,
    		Param param,
    		Map<String,String> body,
    		String otherParam){//这里总是null，如果有人这么写，那只能在别的地方手工调用这个方法时候传值了，框架不会映射的。
    	System.out.println("postPathParam");
//    	Data data = analysisParam(param);
        return null;
    }
 
```
> 需要注意的是，不允许两条完全一样的url存在。

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

## 内嵌Captain
#### axe.captain.captain_host
#### axe.captain.my_host
#### Captain 实现
#### Man 实现
#### CaptainHelper 注入
#### ManHelper
#### Captain 死掉
#### 组员死掉
#### 只剩Captain的时候
#### 只剩一个组员的时候
* 这时候如果有新的组员加入，并且以最后一个组员为Captain，那么最后一个组员会自动变成Captain。
* 如果重启Captain机器了，那么最后一个组员会与Captian失联，因为心跳线程已经停止，所以不会主动联系重启后的Captain，可以通过人工访问最后一个组员的/captain/monitor来激活他的心跳。
#### 修正Team表
* 正常情况下，如果我们希望修正大家的Team表，只需要修正Captain的就可以了
* 也有情况是，需要修改组员Team表来逼迫组员归属到指定Captain，那么修改具体组员的Team表即可
* 修改方式都是通过PUT方式访问 /captain/teamTable?host=host1&host=host2这样