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
package org.axe.util;

/**
 * 数学 工具类
 * @author CaiDongyu on 2016年6月2日 上午9:51:01.
 */
public final class MathUtil {
	/*
	public static void main(String[] args) {
//		int[] ary = fibonacciAry(1, 10);
//		LogUtil.log(Arrays.toString(ary));
		LogUtil.log(fibonacci(360));
	}
*/
	/**
	 * 获得斐波那契数组
	 * @param start 起始位(包含起始位)，第一位是1
	 * @param end 结束位(包含结束位)
	 */
	public static int[] fibonacciAry(int start,int end){
		int[] ary = new int[end-start+1];
		for(int i=0;start<=end;start++,i++){
			ary[i] = fibonacci(start);
		}
		return ary;
	}
	
	/**
	 * 获得一个斐波那契数，只能到底50位，循环
	 * int 不能容纳太大的数，会溢出
	 * @param num 代表第几个数
	 */
	public static int fibonacci(int num){
		//斐波那契数列
		int a = 0;
		int b = 1;
		int c = 1;
		int result = 0;
		switch (num) {
		case 1:
			result = a;
			break;
		case 2:
			result = b;
			break;
		case 3:
			result = c;
			break;

		default:
			//防止溢出出现负数，也防止太大，所以削减num
			while(num > 50){
				num = num -50;
			}
			for(int i=4;i<=num;i++){
				a = b+c;
				int d = a;
				a = b;
				b = c;
				c = d;
			}
			result = c;
			break;
		}
		
		return result;
	}
}
