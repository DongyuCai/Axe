package test;

import org.axe.Axe;

/**
 * Created by CaiDongYu on 2016/4/8.
 */
public class Test{
	

    public static void main(String[] args) {
    	try {
			Axe.init();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
}
