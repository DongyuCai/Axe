package cn.eoe.wifi;

import java.lang.reflect.Method;
import java.math.BigDecimal;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class HosterActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hoster);
		
		init();
	}

	private void init() {
		Button btnCreate = (Button) findViewById(R.id.btnCreate);
		btnCreate.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Method method1 = null;
				WifiManager mWifiManager = (WifiManager) HosterActivity.this.getSystemService(Context.WIFI_SERVICE); 
				try {
					method1 = mWifiManager.getClass().getMethod("setWifiApEnabled",  
							WifiConfiguration.class, boolean.class);
				
			        if (mWifiManager.isWifiEnabled()) {  
			            mWifiManager.setWifiEnabled(false);  
			        }   
			          
			        WifiConfiguration netConfig = new WifiConfiguration();  
			        
			        
			        EditText etName = (EditText) findViewById(R.id.etName);
	
					String random = "";
			        for(int i=0;i<4;i++){
			        	random = random+new BigDecimal(Math.random()*10).intValue();
			        }
			        
		            netConfig.SSID = "CDY#"+etName.getText().toString()+"#"+random;
		            
		            netConfig.preSharedKey = MD5Util.getMD5Code(random);  
		  
		            netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);  
		            netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);  
		            netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);  
		            netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);  
		            netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);  
		            netConfig.allowedPairwiseCiphers .set(WifiConfiguration.PairwiseCipher.TKIP);  
		            netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);  
		            netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP); 
		            
		            method1.invoke(mWifiManager, netConfig, true);
		            System.out.println("成功创建wifi-"+netConfig.SSID);
				} catch (Exception e) {
					e.printStackTrace();
				}  
			}
		});
	}
}
