package cn.eoe.wifi;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class PlayerActivity  extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        
        init();
    }


    private void init() {
    	
    	//初始化按钮事件
    	Button btnRefresh = (Button) findViewById(R.id.btnRefresh);
    	btnRefresh.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				final WifiManager wm = (WifiManager) PlayerActivity.this.getSystemService(Context.WIFI_SERVICE);
				final List<ScanResult> srList = new ArrayList<ScanResult>();
		    	try {
		    		//打开wifi
		    		if(!wm.isWifiEnabled()){
		    			System.out.println(">>>>正在打开wifi");
		        		wm.setWifiEnabled(true);
		    			while(wm.getWifiState() != WifiManager.WIFI_STATE_ENABLED){
		    				System.out.println("WifiState:"+String.valueOf(wm.getWifiState()));
		    				Thread.sleep(1000);
		    			}
		        	}
	    			System.out.println(">>>>wifi已经打开");
		    		//扫描网络
		        	boolean scan = wm.startScan();
		    		if(scan){
		    			List<ScanResult> scanResults = wm.getScanResults();
		    			for(ScanResult sr:scanResults){
		    				//定义:头#名称#密码(密文)#
		    				//举例:CDY#蔡东余#123
		    				System.out.println(sr.SSID);
		    				if(sr.SSID.startsWith("CDY#")){
		    					srList.add(sr);
		    				}
		    				
		    			}
		    		}
		    		
		    		//更新listView
		    		
		        	ListView lvWifiList = (ListView) findViewById(R.id.lvWifiList);
		        	lvWifiList.setAdapter(new BaseAdapter() {
						
						@Override
						public View getView(int arg0, View arg1, ViewGroup arg2) {
							
							TextView tv = new TextView(PlayerActivity.this);
							tv.setTextSize(29);
							tv.setText(getItem(arg0).SSID.split("#")[1]);
							
							return tv;
						}
						
						@Override
						public long getItemId(int arg0) {
							return arg0;
						}
						
						@Override
						public ScanResult getItem(int arg0) {
							return srList.get(arg0);
						}
						
						@Override
						public int getCount() {
							return srList.size();
						}
					});
		        	lvWifiList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							
							ScanResult sr = srList.get(arg2);
							
							String ssid = sr.SSID;
							String[] words = ssid.split("#");
							String pws = words[2];
							pws = MD5Util.getMD5Code(pws);
							WifiConfiguration wifiCong = new WifiConfiguration();
							wifiCong.SSID = "\""+ssid+"\"";//\"转义字符，代表"
							wifiCong.preSharedKey = "\""+pws+"\"";//WPA-PSK密码
							wifiCong.hiddenSSID = false;
							wifiCong.status = WifiConfiguration.Status.ENABLED;
							int wifiId = wm.addNetwork(wifiCong);
							if(wifiId != -1){
								wm.getConfiguredNetworks();
								wm.enableNetwork(wifiId, true);
								System.out.println("成功链接-"+ssid);
							}
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
    	


	}
    
}