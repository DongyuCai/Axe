/**
 * MIT License
 * 
 * Copyright (c) 2017 The Axe Project
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
package org.axe.util;

/**
 * 密码 工具类
 * @author CaiDongyu on 2016年6月2日 上午10:00:55.
 */
public final class PasswordUtil {
	
	public static void main(String[] args) {
		System.out.println(getPassword("axe_caidongyu", 16));
	}
	
	/**
	 * 比如aol邮箱密码
	 * @param keyword = aol
	 * @param passwordLength根据网站要求最大16位
	 */
	public static String getPassword(String keyword,int passwordLength){
		//得到字节数组
		char[] charAry = keyword.toCharArray();
		//每个字节求和得到找到斐波那契起始位
		int num = 0;
		for(char character:charAry){
			num = character+num;
		}
		//按照passwordLenth要求长度，获取多个斐波那契值
		int[] fibonacciAry = new int[passwordLength];
		for(int i=0;i<passwordLength;i++){
			fibonacciAry[i] = MathUtil.fibonacci(num+i);
		}
		//这些斐波那契值最终都要映射到char A到Z char a到z范围内的字母上
		int u1 = 'A';
		int u2 = 'Z';
		int l1 = 'a';
		int l2 = 'z';
		char[] passwordCharAry = new char[passwordLength];
		int i=0;
		int dur = l2-u1;
		for(int fibonacci:fibonacciAry){
			fibonacci = fibonacci%dur+u1;
			if(fibonacci>u2 && fibonacci<l1){
				fibonacci = l1;
			}
			passwordCharAry[i] = (char)fibonacci;
			i++;
		}
		
		return new String(passwordCharAry);
	}
}
