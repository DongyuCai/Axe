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
import org.axe.util.LogUtil;
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

	private static MailSenderInfo MAILINFO;
	
	public static MailSenderInfo getMAILINFO() {
		return MAILINFO;
	}
	
	@Override
	public void init() throws Exception{
		synchronized (this) {
			if(ConfigHelper.getAxeEmailNotification()){
				MAILINFO = new MailSenderInfo();
				MAILINFO.setMailServerHost(ConfigHelper.getAxeEmailServerHost());
				MAILINFO.setMailServerPort(ConfigHelper.getAxeEmailServerPort());
				MAILINFO.setValidate(true);
				MAILINFO.setUserName(ConfigHelper.getAxeEmailServerUserName());
				MAILINFO.setPassword(ConfigHelper.getAxeEmailServerPassword());
				MAILINFO.setFromAddress(ConfigHelper.getAxeEmailServerUserName());
			}
		}
	}
	
	public static void errorMail(Exception e){
		if(MAILINFO != null){
			ByteArrayOutputStream baos = new ByteArrayOutputStream();  
			e.printStackTrace(new PrintStream(baos));  
			String exception = baos.toString();
			MAILINFO.setSubject(ConfigHelper.getAxeEmailTitle()+"系统异常提醒，IP："+IpUtil.getLocalHostIpAddress());
			MAILINFO.setContent(exception);
			try {
				String[] toAddress = ConfigHelper.getAxeEmailErrorAddressee().split(",");
				SimpleMailSender.sendHtmlMail(MAILINFO,toAddress);
			} catch (Exception e1) {
				LogUtil.error(e1);
			}
		}
	}

	@Override
	public void onStartUp() throws Exception {}
	
}
