package com.example.l008accessibility;

import java.util.List;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class MyAccessibilityService extends AccessibilityService{

	@Override
	protected void onServiceConnected() {
		System.out.println("=============onServiceConnected>>>>==============");
		setServiceInfo();
		super.onServiceConnected();
	}
	
	private void setServiceInfo() {  
		
	}
	
	@Override
	@SuppressLint("NewApi")
	public void onAccessibilityEvent(AccessibilityEvent event) {
		int eventType = event.getEventType();
		AccessibilityNodeInfo rootNode = event.getSource();
		switch (eventType) {
			//第一步：监听通知栏消息
			case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
				List<CharSequence> texts = event.getText();
				if (!texts.isEmpty()) {
					for (CharSequence text : texts) {
						String content = text.toString();
						Log.i("demo", "text:"+content);
						if (content.contains("微信红包")) {
							//模拟打开通知栏消息
							if (event.getParcelableData() != null
									&& 
								event.getParcelableData() instanceof Notification) {
								Notification notification = (Notification) event.getParcelableData();
								PendingIntent pendingIntent = notification.contentIntent;
								try {
									pendingIntent.send();
								} catch (CanceledException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
				break;
		//第二步：监听是否进入微信红包消息界面
		case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
			//寻找红包
			findPacket();
			break;
		case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
			//开始打开红包
			openPacket();
			break;
		case AccessibilityEvent.TYPE_VIEW_CLICKED:
			Log.i("debug", "按键:"+rootNode.getText()+","+event.getText());
			break;
		}
	}

	/**
	 * 查找到
	 */
	@SuppressLint("NewApi")
	private void openPacket() {
		AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
		if (nodeInfo != null) {
			List<AccessibilityNodeInfo> list = nodeInfo
					.findAccessibilityNodeInfosByText("抢红包");
			for (AccessibilityNodeInfo n : list) {
				n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
			}
		}

	}

	@SuppressLint("NewApi")
	private void findPacket() {
		AccessibilityNodeInfo rootNode = getRootInActiveWindow();
		if(rootNode != null){
			recycle(rootNode);
		}
	}
	
	/**
	 * 打印一个节点的结构
	 * @param info
	 */
	@SuppressLint("NewApi")
	public void recycle(AccessibilityNodeInfo info) {  
        List<AccessibilityNodeInfo> list = info.findAccessibilityNodeInfosByText("领取红包");
        if(list != null){
            Log.i("debug", "发现"+list.size()+"个红包");
        	for(AccessibilityNodeInfo ani:list){
        		AccessibilityNodeInfo button = ani;
        		int breakOut = 0;
        		while(!button.isClickable()){
        			button = ani.getParent();
        			breakOut++;
        			if(breakOut > 100){
        				//放弃
        				break;
        			}
        		}
        		if(breakOut <= 100)
        			button.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        	}
        }else{
            Log.i("debug", "没有红包");
        }
    }  

	@Override
	public void onInterrupt() {
		
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		System.out.println("=============onUnbind>>>>==============");
		return super.onUnbind(intent);
	}

}
