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

import java.util.HashMap;
import java.util.Map;

import org.axe.interface_.type_convert.BaseTypeConvert;
import org.axe.interface_implement.type_convert.BigDecimal2DoubleConvert;
import org.axe.interface_implement.type_convert.BigDecimal2IntegerConvert;
import org.axe.interface_implement.type_convert.BigInteger2LongConvert;
import org.axe.interface_implement.type_convert.Boolean2IntegerConvert;
import org.axe.interface_implement.type_convert.Double2BigDecimalConvert;
import org.axe.interface_implement.type_convert.Integer2BigDecimalConvert;
import org.axe.interface_implement.type_convert.Integer2LongConvert;
import org.axe.interface_implement.type_convert.Long2BigDecimalConvert;
import org.axe.interface_implement.type_convert.Long2DateConvert;
import org.axe.interface_implement.type_convert.Long2SqlDateConvert;
import org.axe.interface_implement.type_convert.String2BigDecimalConvert;
import org.axe.interface_implement.type_convert.String2DateConvert;
import org.axe.interface_implement.type_convert.String2DoubleConvert;
import org.axe.interface_implement.type_convert.String2IntegerConvert;
import org.axe.interface_implement.type_convert.String2LongConvert;
import org.axe.interface_implement.type_convert.String2SqlDateConvert;

/**
 * 类型转换工具类
 * @author CaiDongyu on 2016/4/8.
 */
public final class  CastUtil {
	private static Map<String,BaseTypeConvert> TYPE_2_TYPE_MAP = new HashMap<>();
	
	static{
		TYPE_2_TYPE_MAP.put("java.lang.Boolean=>java.lang.Integer", new Boolean2IntegerConvert());
		
		TYPE_2_TYPE_MAP.put("java.math.BigDecimal=>java.lang.Double", new BigDecimal2DoubleConvert());
		TYPE_2_TYPE_MAP.put("java.math.BigDecimal=>java.lang.Integer", new BigDecimal2IntegerConvert());
		TYPE_2_TYPE_MAP.put("java.math.BigInteger=>java.lang.Long", new BigInteger2LongConvert());

		TYPE_2_TYPE_MAP.put("java.lang.Integer=>java.lang.Long", new Integer2LongConvert());
		TYPE_2_TYPE_MAP.put("java.lang.Long=>java.util.Date", new Long2DateConvert());
		TYPE_2_TYPE_MAP.put("java.lang.Long=>java.sql.Date", new Long2SqlDateConvert());
		
		TYPE_2_TYPE_MAP.put("java.lang.String=>java.util.Date", new String2DateConvert());
		TYPE_2_TYPE_MAP.put("java.lang.String=>java.sql.Date", new String2SqlDateConvert());
		TYPE_2_TYPE_MAP.put("java.lang.String=>java.lang.Double", new String2DoubleConvert());
		TYPE_2_TYPE_MAP.put("java.lang.String=>double", new String2DoubleConvert());
		TYPE_2_TYPE_MAP.put("java.lang.String=>java.lang.Long", new String2LongConvert());
		TYPE_2_TYPE_MAP.put("java.lang.String=>long", new String2LongConvert());
		TYPE_2_TYPE_MAP.put("java.lang.String=>java.lang.Integer", new String2IntegerConvert());
		TYPE_2_TYPE_MAP.put("java.lang.String=>int", new String2IntegerConvert());
		TYPE_2_TYPE_MAP.put("java.lang.String=>java.math.BigDecimal", new String2BigDecimalConvert());
		
		TYPE_2_TYPE_MAP.put("java.lang.Double=>java.math.BigDecimal", new Double2BigDecimalConvert());
		TYPE_2_TYPE_MAP.put("java.lang.Integer=>java.math.BigDecimal", new Integer2BigDecimalConvert());
		TYPE_2_TYPE_MAP.put("java.lang.Long=>java.math.BigDecimal", new Long2BigDecimalConvert());
		
	}

	private CastUtil() {}

    
    public static <T> Object castType(Object value,T toType,Object ...args){
		do{
			if(value == null) break;
			
			if(ReflectionUtil.compareType((Class<?>)toType,String.class)){
				value = value.toString();
				break;
			}
			
			String valueTypeName = value.getClass().getName();
			String javaTypeName = ((Class<?>)toType).getName();
			BaseTypeConvert typeConvert = TYPE_2_TYPE_MAP.get(valueTypeName+"=>"+javaTypeName);
			if(typeConvert == null){
				//就用加一级String中间转换
				typeConvert = TYPE_2_TYPE_MAP.get(String.class.getName()+"=>"+javaTypeName);
				if(typeConvert != null){
					value = typeConvert.convert(value.toString(),args);
				}
			}else{
				value = typeConvert.convert(value,args);
			}
		}while(false);
		return value;
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

	public static Map<String, BaseTypeConvert> getTYPE_2_TYPE_MAP() {
		return TYPE_2_TYPE_MAP;
	}

	public static void setTYPE_2_TYPE_MAP(Map<String, BaseTypeConvert> tYPE_2_TYPE_MAP) {
		TYPE_2_TYPE_MAP = tYPE_2_TYPE_MAP;
	}
}
