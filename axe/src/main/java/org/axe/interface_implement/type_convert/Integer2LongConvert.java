package org.axe.interface_implement.type_convert;

import org.axe.interface_.type_convert.BaseTypeConvert;

/**
 * java.lang.Integer=>java.lang.Long
 */
public class Integer2LongConvert implements BaseTypeConvert{

	public Long convert(Object arg,Object ...args){
		return Long.valueOf(arg.toString());
	}
}
