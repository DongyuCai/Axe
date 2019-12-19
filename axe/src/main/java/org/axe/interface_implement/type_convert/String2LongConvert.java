package org.axe.interface_implement.type_convert;

import org.axe.interface_.type_convert.BaseTypeConvert;

/**
 * java.lang.String=>java.lang.Long
 */
public final class String2LongConvert implements BaseTypeConvert{

	public Long convert(Object arg,Object ...args){
		return Long.parseLong((String)arg);
	}
}
