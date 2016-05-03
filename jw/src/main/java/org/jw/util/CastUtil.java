package org.jw.util;

import java.util.ArrayList;
import java.util.List;

import org.jw.bean.FormParam;

/**
 * 类型转换工具类
 * Created by CaiDongYu on 2016/4/8.
 */
public final class  CastUtil {
	
	private CastUtil() {}

	public static Object smartCast(List<FormParam> formParamList,Class<?> type){
		Object result = null;
		try {
			if(ReflectionUtil.compareType(type, List.class)){
				//TODO:现在只支持List<?>这样的参数，其实就是List<String>，需要开发自己在Controller里判断，或者直接接值时候用List<String>
				List<Object> list = new ArrayList<>();
				for(FormParam formParam:formParamList){
					list.add(formParam.getFieldValue());
				}
				result = list.size()>0?list:null;
			} else if(type.isArray()){
				if(ReflectionUtil.compareType(type.getComponentType(), String.class)){
					String[] list = new String[formParamList.size()];
					for(int i=0;i<formParamList.size();i++){
						String value =  castString(formParamList.get(i).getFieldValue(), null);
						list[i] = value;
					}
					result = list.length>0?list:null;
				}else if(ReflectionUtil.compareType(type.getComponentType(), Byte.class)){
					Byte[] list = new Byte[formParamList.size()];
					for(int i=0;i<formParamList.size();i++){
						Byte value =  castByte(formParamList.get(i).getFieldValue(),null);
						list[i] = value;
					}
					result = list.length>0?list:null;
				}else if(ReflectionUtil.compareType(type.getComponentType(), Boolean.class)){
					Boolean[] list = new Boolean[formParamList.size()];
					for(int i=0;i<formParamList.size();i++){
						Boolean value =  castBoolean(formParamList.get(i),null);
						list[i] = value;
					}
					result = list.length>0?list:null;
				}else if(ReflectionUtil.compareType(type.getComponentType(), Short.class)){
					Short[] list = new Short[formParamList.size()];
					for(int i=0;i<formParamList.size();i++){
						Short value =  castShort(formParamList.get(i).getFieldValue(),null);
						list[i] = value;
					}
					result = list.length>0?list:null;
				}else if(ReflectionUtil.compareType(type.getComponentType(), Character.class)){
					Character[] list = new Character[formParamList.size()];
					for(int i=0;i<formParamList.size();i++){
						Character value =  castCharacter(formParamList.get(i).getFieldValue());
						list[i] = value;
					}
					result = list.length>0?list:null;
				}else if(ReflectionUtil.compareType(type.getComponentType(), Integer.class)){
					Integer[] list = new Integer[formParamList.size()];
					for(int i=0;i<formParamList.size();i++){
						Integer value =  castInteger(formParamList.get(i).getFieldValue(),null);
						list[i] = value;
					}
					result = list.length>0?list:null;
				}else if(ReflectionUtil.compareType(type.getComponentType(), Long.class)){
					Long[] list = new Long[formParamList.size()];
					for(int i=0;i<formParamList.size();i++){
						Long value =  castLong(formParamList.get(i).getFieldValue(),null);
						list[i] = value;
					}
					result = list.length>0?list:null;
				}else if(ReflectionUtil.compareType(type.getComponentType(), Float.class)){
					Float[] list = new Float[formParamList.size()];
					for(int i=0;i<formParamList.size();i++){
						Float value =  castFloat(formParamList.get(i).getFieldValue(),null);
						list[i] = value;
					}
					result = list.length>0?list:null;
				}else if(ReflectionUtil.compareType(type.getComponentType(), Double.class)){
					Double[] list = new Double[formParamList.size()];
					for(int i=0;i<formParamList.size();i++){
						Double value =  castDouble(formParamList.get(i).getFieldValue(),null);
						list[i] = value;
					}
					result = list.length>0?list:null;
				}
			}else{
				if(ReflectionUtil.compareType(type, String.class)){
					StringBuffer list = new StringBuffer("");
					for(int i=0;i<formParamList.size();i++){
						String value =  castString(formParamList.get(i).getFieldValue(), null);
						if(value != null){
							list.append(value).append(",");
						}
					}
					result = list.length()>0?list.substring(0, list.length()-1):null;
				}else if(ReflectionUtil.compareType(type, Byte.class)){
					for(int i=0;i<formParamList.size();i++){
						Byte value =  castByte(formParamList.get(i).getFieldValue(),null);
						result = value;
					}
				}else if(ReflectionUtil.compareType(type, Boolean.class)){
					for(int i=0;i<formParamList.size();i++){
						Boolean value =  castBoolean(formParamList.get(i),null);
						result = value;
					}
				}else if(ReflectionUtil.compareType(type, Short.class)){
					for(int i=0;i<formParamList.size();i++){
						Short value =  castShort(formParamList.get(i).getFieldValue(),null);
						result = value;
					}
				}else if(ReflectionUtil.compareType(type, Character.class)){
					for(int i=0;i<formParamList.size();i++){
						Character value =  castCharacter(formParamList.get(i).getFieldValue());
						result = value;
					}
				}else if(ReflectionUtil.compareType(type, Integer.class)){
					for(int i=0;i<formParamList.size();i++){
						Integer value =  castInteger(formParamList.get(i).getFieldValue(),null);
						result = value;
					}
				}else if(ReflectionUtil.compareType(type, Long.class)){
					for(int i=0;i<formParamList.size();i++){
						Long value =  castLong(formParamList.get(i).getFieldValue(),null);
						result = value;
					}
				}else if(ReflectionUtil.compareType(type, Float.class)){
					for(int i=0;i<formParamList.size();i++){
						Float value =  castFloat(formParamList.get(i).getFieldValue(),null);
						result = value;
					}
				}else if(ReflectionUtil.compareType(type, Double.class)){
					for(int i=0;i<formParamList.size();i++){
						Double value =  castDouble(formParamList.get(i).getFieldValue(),null);
						result = value;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
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
