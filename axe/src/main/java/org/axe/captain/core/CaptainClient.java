package org.axe.captain.core;

import java.util.ArrayList;
import java.util.List;

import org.axe.captain.constant.CaptainExceptionEnum;
import org.axe.captain.helper.CaptainConfigHelper;
import org.axe.util.HttpUtil;
import org.axe.util.JsonUtil;
import org.axe.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 队长模式的客户端
 * Created by CaiDongYu on 2016年6月14日 下午4:03:58.
 */
public final class CaptainClient {
	private Logger LOGGER = LoggerFactory.getLogger(CaptainClient.class);

	private List<String> hosts = new ArrayList<>();
	
	public boolean signIn() throws Exception {
		String captain = CaptainConfigHelper.getAxeCaptainCaptainHost();
		if(StringUtil.isNotEmpty(captain)){
			String myHost = CaptainConfigHelper.getAxeCaptainMyHost();
			if(StringUtil.isNotEmpty(myHost)){
				
				String signIn = captain;
				if(signIn.endsWith("/")){
					signIn = signIn+"captain/signIn";
				}else{
					signIn = signIn+"/captain/signIn";
				}
				signIn = signIn+"?host="+myHost;
				
				String result = HttpUtil.sendGet(signIn);
				CaptainExceptionEnum exception = CaptainExceptionEnum.getException(result);
				if(exception != null){
					throw new Exception("Captain start failed ："+exception.desc);
				}
				try {
					//#新表
					List<?> newHosts = JsonUtil.fromJson(result,ArrayList.class);
					this.hosts.clear();
					for(Object obj:newHosts){
						this.hosts.add(String.valueOf(obj));
					}
					
					if(LOGGER.isInfoEnabled()){
						LOGGER.info("Captain client signIn success!");
						LOGGER.info("###Team Table START###");
						for(String host:hosts){
							LOGGER.info(host);
						}
						LOGGER.info("###Team Table END###");
					}
					return true;
				} catch (Throwable e) {
					throw new Exception("Captain start failed ："+result);
				}
			}
		}
		
		return false;
	}
}
