package org.axe.interface_implement.type_convert;

import org.axe.interface_.type_convert.BaseTypeConvert;

/**
 * java.lang.String=>java.lang.Float
 */
public final class String2FloatConvert implements BaseTypeConvert{

	public Float convert(Object arg,Object ...args){
		return Float.parseFloat((String)arg);
	}
}
