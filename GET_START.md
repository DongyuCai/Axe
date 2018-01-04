## Get Start ���ٿ�ʼ
�½����̡����ȣ�ȷ���㰲װ����maven��Ȼ���cmd���л������Լ���Ŀ¼�£�����һ���յ�maven��Ŀ��
```
mvn archetype:generate -DgroupId=com.ybsl -DartifactId=shop-api
```
���ù��̡�����src\main�ļ����£��½�һ���ļ��н�resources������resources���½�һ��axe.properties�ļ����������¡�
```
#==================axe��������ļ�==================

#�Ƿ��axe��ܹ������ĵ�¼
#axe.signin=true ����axe��ܹ��������Ҫ��½¼
#axe.signin=false ����axe��ܹ�����治��Ҫ��½¼��������������true
axe.signin=true

#��¼�����md5������ƣ���ȡ��ʽ����
#String username = "axe";
#String password = "13776255717";
#String axe_signin_token = MD5Util.getMD5Code(username+":"+password);
axe.signin.token=804bd02e2548611fca83965b5f18f1d8

#�Ƿ��ͷſ�ܳ�ʼ����ɺ�ģ�ClassHelper�ڵ�classSet�����Ƿ���
#����false������������С�ڴ�ռ��
axe.classhelper.keep=false

#ָ������Դ
#����ָ����������� jdbc.datasource=druid,api,united,pointsShop,aio,card
#axe-datasource-dbcp��axe����ṩ��Ĭ������Դ
#����ʹ���Լ�������Դ���÷��ο�README�������Դһ��
jdbc.datasource=axe-datasource-dbcp

#���ʹ��axe-datasource-dbcp����Ҫ��axe.properties��ָ����jdbc������{
#���ʹ���Լ�������Դ����druid����˶����ã���{��}������Ҫ����������д�����ļ���
jdbc.username=root
jdbc.password=1234
jdbc.url=jdbc:mysql://localhost:3306/sl1288-shop?useUnicode=true&characterEncoding=utf-8
jdbc.driver=com.mysql.jdbc.Driver
#}

#�Ƿ��Զ�����
jdbc.auto_create_table=true

#�Ƿ��ӡsql���
jdbc.show_sql=false

#ָ�������䣬��ܻὫ�쳣��Ϣ���͸���Щ����
#�����Ƕ������ axe.email=1234512345@qq.com,1234512345@qq.com,1234512345@qq.com
axe.email=

#���ɨ��İ�·��
#�����Ƕ��·��
#app.base_package=com.ybsl,com.ybsl
app.base_package=com.ybsl

#�����������jsp��������view��ľ�̬�ļ���������Ҫָ����Щ�ļ���·��
#����src/main/webapp�£���static�ļ�����ŵ��Ǿ�̬css��ͼƬ��js���ļ�
#����src/main/webapp�£���view�ļ�����ŵ���jsp�ļ�
app.asset_path=/static
app.jsp_path=/view
```


�޸�pom.xml���򿪹���Ŀ¼�µ�pom.xml���滻����������ݡ�
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
        <!-- Axe �������� 0.1�ǰ汾 .7��jdk7 -->
        <dependency>
            <groupId>org.axe</groupId>
            <artifactId>axe</artifactId>
            <version>0.1.7</version>
        </dependency>
    	<!-- Apache DBCP ����Դ(����Ĭ������Դ����������ָ������Դ)-->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-dbcp2</artifactId>
            <version>2.0.1</version>
        </dependency>

        <!-- ######################### java web ################################# -->
		<!-- cross domain ��ѡ��������������ȥ�� -->

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

		<!-- java web ������ -->
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
ת�����̡����IDE��eclipse����cmd���л���pom.xml����·���£�ִ���������
```
mvn clean eclipse:eclipse -Dwtpversion=1.0
```
���빤�̣���eclipse��import���̣�����java��ͨ���̵��룬Ȼ��ɾ�����������AppTest.java�ļ����ɡ�
���꣩

## IOC��ô֧��
axe��ioc(����ע��)������BeanHelperʵ�֣����е�ע��ʵ��Ҳ���Դ�BeanHelper�л�ȡ(���潲��)��axe�ṩ�����µ�ע��������ioc��ʹ�á�

- @Controller
- @Service
- @Dao
- @Autowired  

�����÷�������������ܡ�

## MVC��axe�г�ʲô��
axe�Ƽ��ķ����ǰ��˷��룬Ҳ����view��ǰ̨�����飬��̨����ֻ�ṩ���ݣ�ֻ����MVC�е�Model��Controller��
����axeҲ֧��������MVC��View�����ͨ����Controller�з��صĽ������(View.class)����ת��View.class�����ʵ������һ����ַ�ַ�����֧��Я��������jspҳ�档
```java
@Controller(title="��תjsp��controller",basePath="/to")
public class ToJspController extends BaseRest{
	
	@Autowired
	private CustomerService customerService;
	....
	@Autowired
	private AreaOpenService areaOpenService;
	
	/**
	 * ӦΪ΢�Ž������Ǵ���code�ģ������ַ����sendRedirect���ĵ���
	 * ����ˢ��ҳ�棬code�ǲ����ظ��õ�
	 */
	@Request(title="����ע��[΢�����]",value="/invite_wx",method=RequestMethod.GET)
	public View invite_wx(HttpServletRequest request,HttpServletResponse response){
		return new View("/to/invite").addModel("token", getToken(request));
	}
	
	@Request(title="����ע��",value="/invite",method=RequestMethod.GET)
	public View invite(HttpServletRequest request,HttpServletResponse response){
		View view = new View("/view/invite.jsp").dispatcher();
		//token �� �û���Ϣ
		view.addModel("token", getToken(request));
		Long customertUserId = getCustomerUserId(request);
		if(customertUserId != null){
			//�����ע�ᣬֱ������ҳ
			return new View("/to/index").addModel("token", getToken(request));
		}
		
		return view;
	}
	
	/**
	 * ӦΪ΢�Ž������Ǵ���code�ģ������ַ����sendRedirect���ĵ���
	 * ����ˢ��ҳ�棬code�ǲ����ظ��õ�
	 */
	@Request(title="��ҳ[΢�����]",value="/index_wx",method=RequestMethod.GET)
	public View index_wx(HttpServletRequest request,HttpServletResponse response){
		return new View("/to/index").addModel("token", getToken(request));
	}
	
	@Request(title="��ҳ",value="/index",method=RequestMethod.GET)
	public View index(HttpServletRequest request,HttpServletResponse response){
		View view = new View("/view/index.jsp").dispatcher();
		//token �� �û���Ϣ
		view.addModel("token", getToken(request));
		Long customertUserId = getCustomerUserId(request);
		if(customertUserId != null){
			CustomerVO customerVO = customerService.getCustomerVO(customertUserId);
			view.addModel("customerVO", customerVO);
		}
		
		//���ų����б�
		view.addModel("openAreaList", areaOpenService.list());
		
		//��ҳ���ֲ�ͼ�����ѡ
		List<CustomerBanner> list = customerBannerService.getList();
		view.addModel("customerBannerList", list);
		
		return view;
	}
	
	@Request(title="��������[΢�����]",value="/user_wx",method=RequestMethod.GET)
	public View user_wx(HttpServletRequest request,HttpServletResponse response){
		return new View("/to/user").addModel("token", getToken(request));
	}
	
	@Request(title="��������",value="/user",method=RequestMethod.GET)
	public View user(HttpServletRequest request,HttpServletResponse response){
		View view = new View("/view/user.jsp").dispatcher();
		//token �� �û���Ϣ
		view.addModel("token", getToken(request));
		Long customertUserId = getCustomerUserId(request);
		if(customertUserId != null){
			CustomerVO customerVO = customerService.getCustomerVO(customertUserId);
			view.addModel("customerVO", customerVO);
			if(customerVO != null && customerVO.getBaseUserInfo() != null){
				//����˻���Ϣ
				RestData<BalanceAccountVO> balanceAccount = UnitedUserRestClient.getBalanceAccountByUid(customerVO.getBaseUserInfo().getUid());
				if(RestDataCode.SUCCESS.equals(balanceAccount.getCode())){
					if("000".equals(balanceAccount.getData().get_CODE_())){
						view.addModel("balanceAccount", balanceAccount.getData());
					}
				}
				//�����˻���Ϣ
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
	

	@Request(title="��ַ����",value="/address_manager",method=RequestMethod.GET)
	public View address_manager(HttpServletRequest request,HttpServletResponse response){
		View view = new View("/view/address_manager.jsp").dispatcher();
		//token �� �û���Ϣ
		view.addModel("token", getToken(request));
		Long customertUserId = getCustomerUserId(request);
		if(customertUserId != null){
			//�û��ĵ�ַ�б�
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
	
	@Request(title="��ַ����-������ַ",value="/address_new",method=RequestMethod.GET)
	public View address_new(HttpServletRequest request,HttpServletResponse response){
		View view = new View("/view/address_new.jsp").dispatcher();
		//token �� �û���Ϣ
		view.addModel("token", getToken(request));

		//���ų����б�
		view.addModel("openAreaList", areaOpenService.list());
		return view;
	}
	
	@Request(title="��ַ����-�༭��ַ",value="/address_edit/{addressId}",method=RequestMethod.GET)
	public View address_edit(
			@Required
			@RequestParam("addressId")Long addressId,
			HttpServletRequest request,HttpServletResponse response){
		View view = new View("/view/address_edit.jsp").dispatcher();
		//token �� �û���Ϣ
		view.addModel("token", getToken(request));
		//�û��ĵ�ַ�б�
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
	
	@Request(title="�ҵĶ���",value="/order_list",method=RequestMethod.GET)
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
		
		//token �� �û���Ϣ
		view.addModel("token", getToken(request));
		Long customertUserId = getCustomerUserId(request);
		if(customertUserId != null){
			//�����б�
			view.addModel("toDoorOrderList", toDoorOrderService.getList(customertUserId));
			view.addModel("collectionBoxOrderList", collectionBoxOrderService.getList(customertUserId,null));
		}

		
		return view;
	}
	

	@Request(title="ԤԼ���Ŷ�������",value="/to_door_order_comment",method=RequestMethod.GET)
	public View to_door_order_comment(
			@Required
			@RequestParam("id")Long id,
			@Required
			@RequestParam("buyerUserId")Long buyerUserId,
			HttpServletRequest request,HttpServletResponse response){
		View view = new View("/view/to_door_order_comment.jsp").dispatcher();
		view.addModel("id", id);
		
		//token �� �û���Ϣ
		view.addModel("token", getToken(request));
		
		//���ػ���Ա��Ϣ
		BuyerUser buyerUser = buyerService.getEntity(buyerUserId);
		view.addModel("buyerUser", buyerUser);
		
		return view;
	}
	

	@Request(title="ԤԼ���Ŷ�������",value="/to_door_order",method=RequestMethod.GET)
	public View to_door_order(
			@Required
			@RequestParam("id")Long id,
			HttpServletRequest request,HttpServletResponse response){
		View view = new View("/view/to_door_order.jsp").dispatcher();
		//token �� �û���Ϣ
		view.addModel("token", getToken(request));
		//������Ϣ
		ToDoorOrder toDoorOrder = toDoorOrderService.getEntity(id);
		if(toDoorOrder != null){
			toDoorOrder = toDoorOrderService.setOtherIfo(toDoorOrder);
			
			//���ػ���Ա��Ϣ
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
	
	@Request(title="�����䶩������",value="/collection_box_order",method=RequestMethod.GET)
	public View collection_box_order(
			@Required
			@RequestParam("id")Long id,
			HttpServletRequest request,HttpServletResponse response){
		View view = new View("/view/collection_box_order.jsp").dispatcher();
		//token �� �û���Ϣ
		view.addModel("token", getToken(request));
		//������Ϣ
		CollectionBoxOrder collectionBoxOrder = collectionBoxOrderService.getEntity(id);
		view.addModel("id", id);
		view.addModel("collectionBoxOrder", collectionBoxOrder);
		if(collectionBoxOrder != null && collectionBoxOrder.getCollectionBoxId() != null){
			//������Ϣ
			CollectionBox collectionBox = collectionBoxService.getEntity(collectionBoxOrder.getCollectionBoxId());
			view.addModel("collectionBox", collectionBox);
		}
		
		return view;
	}
	
	//��΢�Ž�����Ϊ�˱���code���ڵ�ַ����ˢ�º���Ч������ת��һ��
	@Request(title="�����䶩��Ͷ��[΢�����]",value="/collection_box_order_put_wx",method=RequestMethod.GET)
	public View collection_box_order_put_wx(
			@Required
			@RequestParam("state")String collectionBoxNumber,
			HttpServletRequest request,HttpServletResponse response){
		
		return new View("/to/collection_box_order_put")
				.addModel("token", getToken(request))
				.addModel("collectionBoxNumber", collectionBoxNumber);
	}

	//��ʱ��˵�����ҳ��û�л��������Ϣ��ֻ�д�Ͷ�ŵĶ����б�
	@Request(title="�����䶩��Ͷ��",value="/collection_box_order_put",method=RequestMethod.GET)
	public View collection_box_order_put(
			@Required
			@RequestParam("collectionBoxNumber")String collectionBoxNumber,
			@RequestParam("collectionBoxOrderId")Long collectionBoxOrderId,
			HttpServletRequest request,HttpServletResponse response){
		View view = new View("/view/collection_box_order_put.jsp").dispatcher();
		//token �� �û���Ϣ
		view.addModel("token", getToken(request));
		view.addModel("collectionBoxNumber", collectionBoxNumber);
		//Ĭ�Ϲ�ѡ�Ķ���id
		view.addModel("collectionBoxOrderId", collectionBoxOrderId);
		
		//��Ͷ�Ŷ����б�
		Long customertUserId = getCustomerUserId(request);
		if(customertUserId != null){
			view.addModel("collectionBoxOrderList", collectionBoxOrderService.getList(customertUserId,0));
		}
		
		return view;
	}
	
	@Request(title="����������",value="/collection_box",method=RequestMethod.GET)
	public View collection_box(
			@Required
			@RequestParam("collectionBoxNumber")String collectionBoxNumber,
			HttpServletRequest request,HttpServletResponse response){
		View view = new View("/view/collection_box.jsp").dispatcher();
		//token �� �û���Ϣ
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


	@Request(title="ԤԼ�����µ�ҳ��",value="/to_door_order_create",method=RequestMethod.GET)
	public View to_door_order_create(
			@RequestParam("area")String area,
			HttpServletRequest request,HttpServletResponse response){
		View view = new View("/view/to_door_order_create.jsp").dispatcher();
		//token �� �û���Ϣ
		view.addModel("token", getToken(request));
		//ԤԼ���ţ��û��˵ķ����б�
		view.addModel("catalogList",new ArrayList<>());
		if(StringUtil.isNotEmpty(area)){
			String[] areaSplit = area.split("-");
			if(areaSplit.length == 3){
				view.addModel("catalogList",toDoorOrderCatalog4CustomerService.list(areaSplit[0],areaSplit[1],areaSplit[2]));
			}
		}
		
		//�û��ĵ�ַ�б�
		view.addModel("addressList", new ArrayList<>());
		Long customertUserId = getCustomerUserId(request);
		if(customertUserId != null){
			CustomerUser entity = customerService.getEntity(customertUserId);
			if(entity != null){
				RestData<AddressListVO> addressList =  UnitedUserRestClient.getAddressList(entity.getUid());
				if(RestDataCode.SUCCESS.equals(addressList.getCode())){
					if("000".equals(addressList.getData().get_CODE_())){
						List<AddressVO> records = addressList.getData().getRecords();
						//Ĭ�ϵ�ַ��û�еĻ���ȡ��һ��
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
	
	@Request(title="ԤԼ�����µ��ɹ�ҳ��",value="/to_door_order_create_success",method=RequestMethod.GET)
	public View to_door_order_create_success(
			@Required
			@RequestParam("orderId")Long orderId,
			@Required
			@RequestParam("orderNumber")String orderNumber,
			HttpServletRequest request,HttpServletResponse response){
		View view = new View("/view/to_door_order_create_success.jsp").dispatcher();
		view.addModel("orderId", orderId);
		view.addModel("orderNumber", orderNumber);
		//token �� �û���Ϣ
		view.addModel("token", getToken(request));
		return view;
	}
	
	@Request(title="�������µ�ҳ��",value="/collection_box_order_create",method=RequestMethod.GET)
	public View collection_box_order_create(HttpServletRequest request,HttpServletResponse response){
		View view = new View("/view/collection_box_order_create.jsp").dispatcher();
		//token �� �û���Ϣ
		view.addModel("token", getToken(request));
		
		//�����䣬�û��˵ķ����б�
		view.addModel("catalogList",collectionBoxOrderCatalogService.list());
		
		return view;
	}
	
	@Request(title="�������µ��ɹ�ҳ��",value="/collection_box_order_create_success",method=RequestMethod.GET)
	public View collection_box_order_create_success(
			@Required
			@RequestParam("orderId")Long orderId,
			@Required
			@RequestParam("orderNumber")String orderNumber,
			HttpServletRequest request,HttpServletResponse response){
		View view = new View("/view/collection_box_order_create_success.jsp").dispatcher();
		view.addModel("orderId", orderId);
		view.addModel("orderNumber", orderNumber);
		//token �� �û���Ϣ
		view.addModel("token", getToken(request));
		return view;
	}
	
	@Request(title="�����Ļ�����ҳ��",value="/nearby_collection_box",method=RequestMethod.GET)
	public View nearby_collection_box(
			@Required
			@RequestParam("lng")Double lng,
			@Required
			@RequestParam("lat")Double lat,
			HttpServletRequest request,HttpServletResponse response){
		View view = new View("/view/nearby_collection_box.jsp").dispatcher();
		//token �� �û���Ϣ
		view.addModel("token", getToken(request));
		
		//�����Ļ�����
		view.addModel("collectionBoxList",collectionBoxService.list(lng,lat));
		
		return view;
	}


	@Request(title="�ǹܾ�-��������[app���]",value="/garbage_can_order_create_app",method=RequestMethod.GET)
	public View garbage_can_order_create_app(HttpServletRequest request,HttpServletResponse response){
		return new View("/to/garbage_can_order_create")
				.addModel("token", getToken(request));
	}
	
	@Request(title="�ǹܾ�-��������",value="/garbage_can_order_create",method=RequestMethod.GET)
	public View garbage_can_order_create(HttpServletRequest request,HttpServletResponse response){
		View view = new View("/view/garbage_can_order_create.jsp").dispatcher();
		//token �� �û���Ϣ
		view.addModel("token", getToken(request));
		
		return view;
	}
	
	//��΢�Ž�����Ϊ�˱���code���ڵ�ַ����ˢ�º���Ч������ת��һ��
	@Request(title="�ǹܾ������䶩��Ͷ��[΢�����]",value="/garbage_can_order_put_wx",method=RequestMethod.GET)
	public View garbage_can_order_put_wx(
			HttpServletRequest request,HttpServletResponse response){
		
		return new View("/to/garbage_can_order_put")
				.addModel("token", getToken(request));
	}
	
	@Request(title="�ǹܾ������䶩��Ͷ��",value="/garbage_can_order_put",method=RequestMethod.GET)
	public View garbage_can_order_put(
			HttpServletRequest request,HttpServletResponse response){
		View view = new View("/view/garbage_can_order_put.jsp").dispatcher();
		//token �� �û���Ϣ
		view.addModel("token", getToken(request));
		return view;
	}
	
	//��΢�Ž�����Ϊ�˱���code���ڵ�ַ����ˢ�º���Ч������ת��һ��
	@Request(title="�����̳�[΢�����]",value="/points_shop_wx",method=RequestMethod.GET)
	public View points_shop_wx(
			HttpServletRequest request,HttpServletResponse response){
		
		return new View("/to/points_shop")
				.addModel("token", getToken(request));
	}
	
	@Request(title="�����̳�",value="/points_shop",method=RequestMethod.GET)
	public View points_shop(
			HttpServletRequest request,HttpServletResponse response){
		View view = new View("/view/points_shop.jsp").dispatcher();
		//token �� �û���Ϣ
		view.addModel("token", getToken(request));
		return view;
	}

	@Request(title="�������",value="/balance_withdraw",method=RequestMethod.GET)
	public View balance_withdraw(
			HttpServletRequest request,HttpServletResponse response){
		View view = new View("/view/balance_withdraw.jsp").dispatcher();
		//token �� �û���Ϣ
		view.addModel("token", getToken(request));

		Long customertUserId = getCustomerUserId(request);
		if(customertUserId != null){
			CustomerUser customer = customerService.getEntity(customertUserId);
			if(customer != null){
				
				//����˻���Ϣ
				RestData<BalanceAccountVO> balanceAccount = UnitedUserRestClient.getBalanceAccountByUid(customer.getUid());
				if(RestDataCode.SUCCESS.equals(balanceAccount.getCode())){
					if("000".equals(balanceAccount.getData().get_CODE_())){
						view.addModel("balanceAccount", balanceAccount.getData());
					}
				}
				
				//�Ƿ��ע�������ں�
				view.addModel("subscribe", 0);//Ĭ��δ��ע���ǾͲ������ֵ�΢��
				String openid = getOpenid(request);
				if(StringUtil.isNotEmpty(openid)){
					CustomerUserWechatLogin customerUserWechatLogin = customerService.getCustomerUserWechatLogin(openid);
					if(customerUserWechatLogin != null){
						if(customerUserWechatLogin.getSubscribe() != null){
							view.addModel("subscribe",customerUserWechatLogin.getSubscribe());
						}
					}
				}
				
				//�������
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
	
	@Request(title="������ּ�¼",value="/balance_withdraw_records",method=RequestMethod.GET)
	public View balance_withdraw_records(
			HttpServletRequest request,HttpServletResponse response){
		View view = new View("/view/balance_withdraw_records.jsp").dispatcher();
		//token �� �û���Ϣ
		view.addModel("token", getToken(request));
		
		//���ּ�¼
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
���嶨�����ﲻ�������ˣ�axe��rest����url�еĲ���֧�����µĽ�����ʽ��

- �� /get/{id}_{name}  ��id��name�ǲ��� 
- �� /get_{id}/{name}  Ҳ��id��name��ʾ����
- ����ֻ�������ֺ���ĸ��urlֻ�������֡���ĸ���»��ߺ�$��

������һ���Ƚ��������������������
```java
@Request(value="/post{money}/4{id}_{name}",method=RequestMethod.POST)
    public Data postPathParam(
        	@RequestParam("money")Integer money,//���money���������������ֵ������Ǳ�ģ��������ַ������ͻ���null
    		
    		@RequestParam("file")FileParam file1,//�����ļ�������ϴ����Ƕ��ļ���ֻ���õ����һ��
    		@RequestParam("file")Object file2,
    		@RequestParam("file")List<FileParam> filesList1,
    		@RequestParam("file")List<?> filesList2,
    		@RequestParam("file")List filesList3,
    		@RequestParam("file")FileParam[] filesAry1,
    		@RequestParam("file")Object[] filesAry2,
    		
    		@RequestParam("ids")Integer ids,//������ݵĲ����Ƕ����ֻ���õ����һ��
    		@RequestParam("ids")List<String> idsList1,
    		@RequestParam("ids")List<?> idsList2,
    		@RequestParam("ids")List<Integer> idsList3,
    		@RequestParam("ids")List idsList4,
    		@RequestParam("ids")String[] idsAry1,//������ݵĲ����Ƕ��������","ƴ��
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
    		String otherParam){//��������null�����������ôд����ֻ���ڱ�ĵط��ֹ������������ʱ��ֵ�ˣ���ܲ���ӳ��ġ�
    	System.out.println("postPathParam");
//    	Data data = analysisParam(param);
        return null;
    }
 
```
> ��Ҫע����ǣ�������������ȫһ����url���ڡ�

## Controller Class
��

## Action Method
Ŀǰ֧��POST��DELETE��PUT��GET���֡���������������ҪHEAD��OPTION�����Ϳ���������չ��

## Param
��

## FileParam
��

## Data
��

## View
��

## Filter
���������Թ��������ڹ��˵Ĺ������ж��Ƿ���Ҫ����������תֱ��Controller��

| ����          | ����ֵ               | ����                                    |
| ------------- |:--------------------:| ---------------------------------------:|
| setLevel      | int                  | �������㼶�����������У�����С����ִ��  |
| setMapping    | Pattern              | ����urlƥ�����                         |
| setNotMapping | Pattern              | ����urlƥ��ļ����У���Ҫ�ų���url����  |

> setMapping�������� "^.*$" ��ƥ�����У��ټ�һ��setNotMapping���� "login"���ſ���¼����ʵ���˼򵥵�Ȩ�޹��ˡ�

#### ��˳���Filter��
��������˳�򣬿��������ÿ��action�϶���һ���������б�
#### FuckOff
����˵����ͨ��setNotMapping���ų�����Ҫ���˵�url(����Ҫ���˵�url������)�����ʺ���Ҫ�ų���url��ֹһ���������
����Ҳ������Controller�ķ�����ֱ��ʹ��@FilterFuckOff���ų�ָ����Filter������Щ�����¸����㡣

## Interceptor
��������Ҫָ����Controller����Controller�����ϣ��ŻṤ����

## Listener
��������ϵͳ����ʱ��ᱻִ�С�

## RedirectorException
��ϵͳ���κεط��׳�����쳣����ܣ���ܻ�ִ����ת��������ַ���쳣������ָ�������ҿ���Я��������

## Service
Service�ڴ󲿷�����¶���Componentһ��������@Tns�������ֻ��@Service����Ч��

## Table Eentity
#### Table�Խ�
�������ò���jdbc.auto_create_table ���Զ�������ṹ�������ṹ�Ѿ������򲻻ᴴ���������ֶ��б仯������Ҫ�ֶ�ɾ����
axeֻ�ᴴ������@Tableע���Entity��ṹ�����ҿ���ָ�����ݿ�����

#### ��������������
@Idע���ע���ֶα�ʾ���������������ܼ򵥣�����ֶζ�����@Idע�⼴�ɡ�

#### �������Ҫ�־û��ķ�ʽ
ʵ������ֶλᱻ�־û��������ж�Ӧ��set��get��������������Ҫ���Ϲ淶����ˣ���ϣ�����־û����ֶΣ�ֻҪȥ��set��get�������ɡ�
ȥ��set�����������棬ȥ��get���������ѯ��
������������ʵ���У���������ȥ��set��get�����������ڲ���Ҫ�־û����ֶε�set��get����ĩβ��"_"����ʾ��չ�ֶΣ������Ȳ��ᱻ�־û���Ҳ����json���߸�ʽ������ֱ������ǰ̨��json������Ⱦ��

#### ��@ColumnDefine���Զ�����ֶ�
axe�����ֶεĶ�����ע����٣����ϣ���Զ����ֶε����͡����ȡ�����ȵ������������ô����ֱ��ʹ��@ColumnDefine��д���ֶε�sql��䡣
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
����ע�⵽���ֶ����ǲ����Զ���ģ������Ҫע�⡣

## Dao
���Ǳ�@Daoע���ע�Ľӿڣ�axe����ʶ��Ϊdao����ڣ���ܻ��Զ�ʵ�ֽӿ��д�@SQLע��ķ������Թ�ʵ��ע�뵽service��component����á�
Dao������Ҫ@Daoע���⣬������ѡ���Եļ̳�BaseRepository�ӿڣ�BaseRepository�ṩ�����Entity�������ɾ�Ĳ鷽��������Ҫע�⣬
ʹ������ӿ��ڵķ�����Ҫ��Entity�������@Id��ע���ֶΣ�Ҳ���Ǳ�����������

## Sql and Entity
Entity����û��@Tableע�⣬��������£���������������ܹ����Ϲ淶����ת������TableA��Ӧtable_a�������@Tableע�⣬����Ҫ����Ĭ�Ϲ�ϵ��
Sql���֧�ֱ������ֶ��������������ֶ���������д������HQL��SQL�е�ռλ������ʹ��?��Ҳ����ʹ��?�����֣�ǰ��Ĭ��ʹ�÷�������˳��
���߿���������ָ��ռλ����Ӧ���������еĵڼ���������
```java
@Dao
public interface TestDao extends BaseRepository{
	
	//ռλ��?ȡֵ˳���շ���������˳�򰤸�ȡ
	@Sql("select * from TestTable where id = ? and name = ?")
	public TestTable getOne(long id,String name);

	@Sql("select * from TestTable")
	public List<TestTable> getAll();
	
	@Sql("select * from TestTable where name like '%test%'")
	public Page<TestTable> page();
	
	@Sql("select * from Export")
	public List<Export> getAllExport();
	
	//ռλ��ָ������ȡ���������ĵ�һ��������
	@Sql("select * from Export where name like ?1")
	public Page<Export> pagingExport(String name,PageConfig pageConfig);
}
```

## Sql ƴ��
axe��dao��Ҳ֧��sql��������ƴ�ӣ�ʹ�÷������£�
```java
@Dao
public interface TestDao extends BaseRepository{
	
	//�ڶ�������append ����ͨ��#([1-9][0-9]*)����̬ƴ�ӵ�sql�У�����ͬ��֧��������д����userAage�����øĳɱ��ֶ�д��user_age��
	@Sql("select * from TestTable where id = ? #2")
	public TestTable getOne(long id,String append);

}
```

## Transaction Tns
axe������������ֵ������ÿ�������ֻ���������򿪣����ҵ��ص��������ύ���ڲ�Ĵ��������ύ����ᱻ���ԡ�
���⣬���������Դ����£�����Ἧ��򿪣������ύ��������������쳣������������ҵ������쳣����������ع���
����ǵڶ����ߵ������������ύʱ�����쳣�����һ�������Ѿ��ύ�޷��ع����ᱻ��¼����־�С�


## BaseDataSource �� @DataSource
ʵ���Զ��������Դ����Ҫ�������£���һҪʵ��BaseDataSource�ӿڣ��ڶ�Ҫ����@DataSourceע�⣬������Ϊ������Դ�����ֵ����֣���Ȼ���ֻ��һ����Ҳ����ν�˵���ע��Ҳ��Ҫ�ӵġ�
���ϵͳ���ж������Դ����ô�Ƽ���������������֤��@Table ��@Dao ������ͬ������Դ���ã��������Ա��ⲻƥ��Ĵ�����֣���ΪAxe��ָ������Դ��ѡȡ���ȼ���@Dao>@Table>Ĭ������Դ�����ǵ��ҽ���ϵͳ�����Զ�����ʱ���⣬��Ϊ��ʱ��ֻ��ͨ��@Table��ȷ������Դ�� 

## Proxy
��ܵĴ���ӿڣ���Ҫ����ʵ�ִ�������ģ���Ҫʵ�ִ˽ӿڡ�

## /axe
��������ɹ������Է��ʴ˵�ַ��������ϵͳ���á�

## Sign In
���/axe�д�����Ҫ��¼�����������룬��ô����Ҫ�˺ŵ�¼���ܼ�������/axe��������ˡ�

## Email
��/axe������ҳ���У���һ����Email������д�ˣ�����յ�ϵͳ�Ĵ�����쳣��֪ͨ�ʼ���

## Release Resources
�������ѡ���˴����ô�ᶪʧһ���ֿ������ʱ���ʼ�����ڴ��е����ݣ���ϵͳ����������û��Ӱ�죬��������ʡ�ڴ棬ֻ��ʧȥ��һЩ��ܼ��Ŀ�ݹ��ܡ�


## BeanHelper��ʹ�ú�ע��
���Զ�ȫ�ֵ�ioc�й�ʵ�����л�ȡ�Ͳ�����һ������õ���  

| ����        | ����ֵ               | ����                      |
| ----------- |:--------------------:| -------------------------:|
| getBeanMap  | Map<Class<?>,Object> | ��ȡ�����й��е�Beanʵ��  |
| getBean     | T                    | ��ע��                    |
| setBean     |                      | ���ʵ����BeanHelpler���� |

>ע.�������͸��ݷ�������Ĳ���������ƥ�䡣���Ի�ȡ��Bean���Ͱ���@Controller��@Component��@Service��@Dao
ע���ע���࣬ע��@Dao�ǽӿ�ע�Ⲣ���ڿ�������׶Σ�@Daoע���ʵ�������ܴ�BeanHelper�л�ȡ������Ҫ�ȴ����������ɲſɻ�ȡ����

## Aspect Proxy
#### begin
#### intercept
#### before
#### after
#### error
#### end

## �������˳��

## ��ǶCaptain
#### axe.captain.captain_host
#### axe.captain.my_host
#### Captain ʵ��
#### Man ʵ��
#### CaptainHelper ע��
#### ManHelper
#### Captain ����
#### ��Ա����
#### ֻʣCaptain��ʱ��
#### ֻʣһ����Ա��ʱ��
* ��ʱ��������µ���Ա���룬���������һ����ԱΪCaptain����ô���һ����Ա���Զ����Captain��
* �������Captain�����ˣ���ô���һ����Ա����Captianʧ������Ϊ�����߳��Ѿ�ֹͣ�����Բ���������ϵ�������Captain������ͨ���˹��������һ����Ա��/captain/monitor����������������
#### ����Team��
* ��������£��������ϣ��������ҵ�Team��ֻ��Ҫ����Captain�ľͿ�����
* Ҳ������ǣ���Ҫ�޸���ԱTeam����������Ա������ָ��Captain����ô�޸ľ�����Ա��Team����
* �޸ķ�ʽ����ͨ��PUT��ʽ���� /captain/teamTable?host=host1&host=host2����