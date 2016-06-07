package org.axe.helper.base;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.axe.interface_.base.Helper;
import org.axe.util.CollectionUtil;
import org.axe.util.StringUtil;
import org.axe.util.mail.MailSenderInfo;
import org.axe.util.mail.SimpleMailSender;

/**
 * 邮件 助手类
 * 目前只发送两种邮件
 * 1.异常邮件
 * 2.密码找回邮件
 * Created by CaiDongYu on 2016年6月2日 上午11:11:40.
 */
public final class MailHelper implements Helper{

	private static List<MailSenderInfo> mailInfoList;
	
	@Override
	public void init() {
		synchronized (this) {
			mailInfoList = new ArrayList<>();
			String axeEmail = ConfigHelper.getAxeEmail();
			if(StringUtil.isNotEmpty(axeEmail)){
				String[] axeEmails = axeEmail.split(",");
				for(String toAddress:axeEmails){
					if(StringUtil.isEmpty(toAddress)) continue;
					
					MailSenderInfo mailInfo = new MailSenderInfo();
					// 这个类主要是设置邮件
					mailInfo = new MailSenderInfo();
					mailInfo.setMailServerHost("smtp.163.com");
					mailInfo.setMailServerPort("25");
					mailInfo.setValidate(true);
					mailInfo.setUserName("axe_caidongyu@163.com");
					mailInfo.setPassword("NiveaLlwifUAUUi1");// 您的邮箱密码
					mailInfo.setFromAddress("axe_caidongyu@163.com");
					mailInfo.setToAddress(toAddress);
					mailInfoList.add(mailInfo);
				}
			}
		}
	}
	
	public static void errorMail(Exception e){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
		e.printStackTrace(new PrintStream(baos));  
		String exception = baos.toString(); 
		
		if(CollectionUtil.isNotEmpty(mailInfoList)){
			for(MailSenderInfo mailInfo:mailInfoList){
				mailInfo.setSubject("系统异常提醒");
				mailInfo.setContent(exception);
				SimpleMailSender.sendHtmlMail(mailInfo);
			}
		}
	}
	
}
