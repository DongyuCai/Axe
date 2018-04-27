package test;

public class ThreadTest {

	private static Integer POOL = 0;
	
	public static void test1(){
		
		try {
			synchronized(POOL){
				POOL++;
				System.out.println("test1-"+POOL);
				throw new Exception("error 111");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void test2(){
		
		try {
			synchronized(POOL){
				POOL++;
				System.out.println("test2-"+POOL);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
