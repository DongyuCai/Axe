package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Date;
import java.util.List;

import org.jw.HelperLoader;
import org.jw.bean.Page;
import org.jw.helper.BeanHelper;
import org.jw.util.JsonUtil;
import org.jw.util.StringUtil;
import org.test_jw.bean.Export;
import org.test_jw.bean.just4test;
import org.test_jw.dao.TestDao;

public class DaoTest {
	
	public static void main(String[] args) {
//		testDaoEntity();
		testDaoPaging();
//		HelperLoader.init();
//		just4test just4test = new just4test();
//		just4test.setName("aaa");
//		TestDao testDao = BeanHelper.getBean(TestDao.class);
//		testDao.insertEntity(just4test);
		
	}
	
	public static void testDaoPaging(){
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
	
	public static void testDaoEntity(){
		HelperLoader.init();
		
		TestDao testDao = BeanHelper.getBean(TestDao.class);
		Export export = new Export();
		export.setId(68l);
		int rows = 0;
		export = testDao.getEntity(export);
		System.out.println("getEntity:"+JsonUtil.toJson(export));
		
		export.setCreateTime(new Date());
		
		rows = testDao.saveEntity(export);
		System.out.println("saveEntity:"+rows);
		
		export.setName("新框架测试_SqlHelper");
		rows = testDao.updateEntity(export);
		System.out.println("updateEntity:"+rows);
		
		export = testDao.getEntity(export);
		System.out.println("getEntity:"+JsonUtil.toJson(export));
	
		rows = testDao.deleteEntity(export);
		System.out.println("deleteEntity:"+rows);
		
		rows = testDao.updateEntity(export);
		System.out.println("updateEntity:"+rows);
		
		rows = testDao.insertEntity(export);
		System.out.println("updateEntity:"+rows);
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
