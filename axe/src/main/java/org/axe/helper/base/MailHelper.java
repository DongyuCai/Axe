/**
 * MIT License
 * 
 * Copyright (c) 2017 CaiDongyu
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.axe.helper.base;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.axe.interface_.base.Helper;
import org.axe.util.CollectionUtil;
import org.axe.util.IpUtil;
import org.axe.util.StringUtil;
import org.axe.util.mail.MailSenderInfo;
import org.axe.util.mail.SimpleMailSender;

/**
 * 邮件 助手类
 * 目前只发送两种邮件
 * 1.异常邮件
 * 2.密码找回邮件
 * @author CaiDongyu on 2016年6月2日 上午11:11:40.
 */
public final class MailHelper implements Helper{

	private static List<MailSenderInfo> mailInfoList;
	
	@Override
	public void init() throws Exception{
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
				mailInfo.setSubject("系统异常提醒，IP："+IpUtil.getLocalHostIpAddress());
				mailInfo.setContent(exception);
				SimpleMailSender.sendHtmlMail(mailInfo);
			}
		}
	}

	@Override
	public void onStartUp() throws Exception {}
	
}
