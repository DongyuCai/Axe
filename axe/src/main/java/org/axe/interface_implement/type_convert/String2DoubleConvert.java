package org.axe.interface_implement.type_convert;

import org.axe.interface_.type_convert.BaseTypeConvert;

/**
 * java.lang.String=>java.lang.Double
 */
public class String2DoubleConvert implements BaseTypeConvert{

	public Double convert(Object arg,Object ...args){
		return Double.parseDouble((String)arg);
	}
}
