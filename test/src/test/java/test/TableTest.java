package test;

import org.axe.helper.persistence.SchemaHelper;

public class TableTest {

	public static void main(String[] args) {
		testCreateTable();
	}
	
	public static void testCreateTable(){
		SchemaHelper.createTable(BillTest.class);
	}
}
