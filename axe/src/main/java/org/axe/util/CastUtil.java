package org.axe.util;

/**
 * 类型转换工具类
 * Created by CaiDongYu on 2016/4/8.
 */
public final class  CastUtil {
	
	private CastUtil() {}

    /**
     * 转为 String
     */
    public static String castString(Object obj){
        return castString(obj,"");
    }

    /**
     * 转为 String
     */
    public static String castString(Object obj,String defaultValue){
        return obj != null ? String.valueOf(obj) : defaultValue;
    }
    
    
    
    public static Byte castByte(Object obj){
    	return castByte(obj, (byte)0);
    }
    
    public static Byte castByte(Object obj,Byte defaultValue){
    	Byte value = defaultValue;
    	if(obj != null){
    		String strValue = castString(obj);
    		if(StringUtil.isNotEmpty(strValue)){
                try{
                    value = Byte.parseByte(strValue);
                }catch (NumberFormatException e){
                    value = defaultValue;
                }
            }
    	}
    	return value;
    }

    public static Boolean castBoolean(Object obj){
    	return castBoolean(obj, false);
    }
    
    public static Boolean castBoolean(Object obj,Boolean defaultValue){
    	Boolean value = defaultValue;
    	if(obj != null){
    		String strValue = castString(obj);
    		if(StringUtil.isNotEmpty(strValue)){
                try{
                    value = Boolean.parseBoolean(strValue);
                }catch (NumberFormatException e){
                    value = defaultValue;
                }
            }
    	}
    	return value;
    }
    
    public static Short castShort(Object obj){
    	return castShort(obj, (short)0);
    }
    
    public static Short castShort(Object obj,Short defaultValue){
    	Short value = defaultValue;
    	if(obj != null){
    		String strValue = castString(obj);
    		if(StringUtil.isNotEmpty(strValue)){
                try{
                    value = Short.parseShort(strValue);
                }catch (NumberFormatException e){
                    value = defaultValue;
                }
            }
    	}
    	return value;
    }
    
    public static Character castCharacter(Object obj){
    	return castCharacter(obj, '\u0000');
    }
    
    public static Character castCharacter(Object obj,Character defaultValue){
    	Character value = defaultValue;
    	if(obj != null){
    		String strValue = castString(obj);
    		if(StringUtil.isNotEmpty(strValue)){
                try{
                	//稍微特殊一些
                	char[] charAry = strValue.toCharArray();
                    value = charAry.length == 1?charAry[0]:null;
                }catch (NumberFormatException e){
                    value = defaultValue;
                }
            }
    	}
    	return value;
    }
    
    public static Integer castInteger(Object obj){
    	return castInteger(obj, 0);
    }
    
    public static Integer castInteger(Object obj,Integer defaultValue){
    	Integer value = defaultValue;
    	if(obj != null){
    		String strValue = castString(obj);
    		if(StringUtil.isNotEmpty(strValue)){
                try{
                    value = Integer.parseInt(strValue);
                }catch (NumberFormatException e){
                    value = defaultValue;
                }
            }
    	}
    	return value;
    }

    public static Long castLong(Object obj){
        return castLong(obj,0l);
    }

    public static Long castLong(Object obj,Long defaultValue){
    	Long value = defaultValue;
        if(obj != null){
            String strValue = castString(obj);
            if(StringUtil.isNotEmpty(strValue)){
                try{
                    value = Long.parseLong(strValue);
                }catch (NumberFormatException e){
                    value = defaultValue;
                }
            }
        }
        return value;
    }
    
    public static Float castFloat(Object obj){
        return castFloat(obj,0.0f);
    }

    public static Float castFloat(Object obj,Float defaultValue){
    	Float value = defaultValue;
        if(obj != null){
            String strValue = castString(obj);
            if(StringUtil.isNotEmpty(strValue)){
                try{
                    value = Float.parseFloat(strValue);
                }catch (NumberFormatException e){
                    value = defaultValue;
                }
            }
        }
        return value;
    }
    
    public static Double castDouble(Object obj){
        return castDouble(obj,0.0);
    }

    public static Double castDouble(Object obj,Double defaultValue){
    	Double value = defaultValue;
        if(obj != null){
            String strValue = castString(obj);
            if(StringUtil.isNotEmpty(strValue)){
                try{
                    value = Double.parseDouble(strValue);
                }catch (NumberFormatException e){
                    value = defaultValue;
                }
            }
        }
        return value;
    }
    
    
}
