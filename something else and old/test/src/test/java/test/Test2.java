package test;

public class Test2 {

	public static void main(String[] args) {
		new Thread(){
			public void run() {
				while(true){
					ThreadTest.test1();
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
					}
				}
			};
		}.start();
		
		new Thread(){
			public void run() {
				while(true){
					ThreadTest.test2();
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
					}
				}
			};
		}.start();
	}
}
