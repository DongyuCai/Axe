package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Date;
import java.util.List;

import org.axe.bean.persistence.Page;
import org.axe.helper.HelperLoader;
import org.axe.helper.ioc.BeanHelper;
import org.axe.helper.persistence.DataBaseHelper;
import org.axe.util.JsonUtil;
import org.axe.util.StringUtil;
import org.test.bean.Export;
import org.test.bean.TestTable;
import org.test.bean.just4test;
import org.test.dao.TestDao;

public class DaoTest {
	
	public static void main(String[] args) {
		try {
//			testDaoEntity();
//			testDaoPaging();
	//		HelperLoader.init();
	//		just4test just4test = new just4test();
	//		just4test.setName("aaa");
	//		TestDao testDao = BeanHelper.getBean(TestDao.class);
	//		testDao.insertEntity(just4test);
				testInsert();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void testInsert() throws Exception{
		HelperLoader.init();
		
		TestTable test = new TestTable();
		test.setId(3);
		test.setName("test3");
		test = DataBaseHelper.insertOnDuplicateKeyEntity(test);
		System.out.println(JsonUtil.toJson(test));
		
		/*Export export = new Export();
		export.setName("abc");
		TestDao testDao = BeanHelper.getBean(TestDao.class);
		export = testDao.saveEntity(export);
		System.out.println(export.getId());*/
		
		System.exit(0);
	}
	
	public static void testDaoPaging() throws Exception{
		HelperLoader.init();
		TestDao testDao = BeanHelper.getBean(TestDao.class);
		List<just4test> all = testDao.getAll();
		Page<just4test> pageList = testDao.page();
		for(just4test e:all){
			System.out.print(e.getName()+"\t");
		}
		System.out.println();
		System.out.println(pageList.getCount()+"\t"+pageList.getPages());
		for(just4test e:pageList.getRecords()){
			System.out.print(e.getName()+"\t");
		}
		System.out.println();
	}
	
	public static void testDaoEntity() throws Exception{
		HelperLoader.init();
		
		TestDao testDao = BeanHelper.getBean(TestDao.class);
		Export export = new Export();
		export.setId(1l);
		int rows = 0;
		export = testDao.getEntity(export);
		System.out.println("getEntity:"+JsonUtil.toJson(export));
		
		export.setCreateTime(new Date());
		
		export = testDao.saveEntity(export);
		System.out.println("saveEntity:"+export);
		
		export.setName("新框架测试_SqlHelper");
		rows = testDao.updateEntity(export);
		System.out.println("updateEntity:"+rows);
		
		export = testDao.getEntity(export);
		System.out.println("getEntity:"+JsonUtil.toJson(export));
	
		rows = testDao.deleteEntity(export);
		System.out.println("deleteEntity:"+rows);
		
		rows = testDao.updateEntity(export);
		System.out.println("updateEntity:"+rows);
		
		export = testDao.insertEntity(export);
		System.out.println("updateEntity:"+rows);
		
		System.exit(0);
	}
	
	public static void mysqlKeywords(){
		try {
			BufferedReader br = new BufferedReader(new FileReader("data.txt"));
			String line = br.readLine();
			while(!StringUtil.isEmpty(line)){
				String[] wordAry = line.split("	");
				for(String word:wordAry){
					word = word.trim();
					if(StringUtil.isEmpty(word)) continue;
					System.out.print(word+",");
				}
				line = br.readLine();
			}
			br.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
