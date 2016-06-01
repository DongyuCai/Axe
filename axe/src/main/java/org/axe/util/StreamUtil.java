package org.axe.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * 流操作 工具类
 * Created by CaiDongYu on 2016/4/11.
 */
public final class StreamUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(StreamUtil.class);

    private StreamUtil() {}
    
    /**
     * 从输入流中获取字符串
     */
    public static String getString(InputStream is){
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e){
            LOGGER.error("get String failure",e);
            throw new RuntimeException(e);
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
            LOGGER.error("copy stream failure",e);
            throw new RuntimeException(e);
        } finally {
            try {
                inputStream.close();
            } catch (Exception e){
                LOGGER.error("close inputStream failure",e);
            }
            try {
                outputStream.close();
            } catch (Exception e){
                LOGGER.error("close outputStream failure",e);
            }
        }
    }
}
