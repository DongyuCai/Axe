package test;

import org.axe.bean.persistence.Page;
import org.axe.helper.HelperLoader;
import org.axe.helper.ioc.BeanHelper;
import org.test.bean.Banner;
import org.test.service.BannerService;

public class HokeTest {
	public static void main(String[] args) {
		testHoke();
		
		/*Object[] params = {"abc",new HokeTest(),1};
		//366808998
		//366808997
		//366808997
		int hashCode = 0;
		for(Object obj:params){
			hashCode = hashCode+obj.hashCode();
		}
		System.out.println(hashCode);*/
	}
	
	public static void testHoke(){
		HelperLoader.init();
		for(int i=0;i<100;i++){
			new Thread("thread-"+i){
				public void run() {
					BannerService bean = BeanHelper.getBean(BannerService.class);
					Page<Banner> page = bean.page(1);
					System.out.println(this.getName());
					for(Banner banner:page.getRecords()){
						System.out.println(banner.getTitle());
					}
				};
			}.start();
		}
	}
}
