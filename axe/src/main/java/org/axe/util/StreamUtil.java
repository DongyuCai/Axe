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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * 流操作 工具类
 * @author CaiDongyu on 2016/4/11.
 */
public final class StreamUtil {
    private StreamUtil() {}
    
    /**
     * 从输入流中获取字符串
     * @throws Exception 
     */
    public static String getString(InputStream is) throws Exception{
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e){
//            LOGGER.error("get String failure",e);
            throw e;
        } finally {
			if(reader != null){
				try {
					reader.close();
				} catch (Exception e2) {}
			}
		}
        return sb.toString();
    }

    /**
     * 将输入流复制到输出流
     */
    public static void copyStream(InputStream inputStream, OutputStream outputStream){
        try {
            int length;
            byte[] buffer = new byte[4*2014];
            while((length = inputStream.read(buffer,0,buffer.length)) != -1){
                outputStream.write(buffer,0,length);
            }
            outputStream.flush();
        } catch (Exception e){
            LogUtil.error(e);
            throw new RuntimeException(e);
        } finally {
            try {
                inputStream.close();
            } catch (Exception e){
                LogUtil.error(e);
            }
            try {
                outputStream.close();
            } catch (Exception e){
                LogUtil.error(e);
            }
        }
    }
}
