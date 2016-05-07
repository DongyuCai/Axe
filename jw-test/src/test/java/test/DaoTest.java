package test;

import java.util.Date;

import org.jw.HelperLoader;
import org.jw.helper.BeanHelper;
import org.jw.util.JsonUtil;
import org.test_jw.bean.Export;
import org.test_jw.dao.TestDao;

public class DaoTest {
	public static void main(String[] args) {
	
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
}
