package test;

import org.axe.helper.HelperLoader;

public class TableTest {

	public static void main(String[] args) {
		testCreateTable();
	}
	
	public static void testCreateTable(){
		try {
			HelperLoader.init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
