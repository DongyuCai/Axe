package org.axe.interface_implement.type_convert;

import org.axe.interface_.type_convert.BaseTypeConvert;

/**
 * java.lang.String=>java.lang.Integer
 */
public class String2IntegerConvert implements BaseTypeConvert{

	public Integer convert(Object arg,Object ...args){
		return Integer.parseInt((String)arg);
	}
}
