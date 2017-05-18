package org.axe.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * JSON 工具类
 * Created by CaiDongYu on 2016/4/11.
 */
public final class JsonUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private JsonUtil() {}
    
    /**
     * 将 POJO 转为 JSON
     */
    public static String toJson(Object obj){
        String json;
        try {
            json = OBJECT_MAPPER.writeValueAsString(obj);
        } catch (Exception e){
            LOGGER.error("convert POJO to JSON failure",e);
            throw new RuntimeException(e);
        }
        return json;
    }

    /**
     * 将 JSON 转为 POJO
     */
    @SuppressWarnings("unchecked")
	public static <T> T fromJson(String json, Class<T> type){
        T pojo;
        try {
        	if(String.class.equals(type)){
            	pojo = (T) json;
            }else{
            	String reg = "[ ]*\"[^\"]+\"[ ]*:[ ]*null[ ]*";
        		json = json.replaceAll(""+reg+",", "");
        		json = json.replaceAll(","+reg+"\\}", "}");
        		json = json.replaceAll("\\{"+reg+"\\}", "{}");
            	pojo = OBJECT_MAPPER.readValue(json,type);
            }
        } catch (Exception e){
            LOGGER.error("convert JSON to POJO failure",e);
            throw new RuntimeException(e);
        }
        return pojo;
    }
    
}
