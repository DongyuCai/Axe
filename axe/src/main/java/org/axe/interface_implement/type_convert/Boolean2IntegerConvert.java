package org.axe.interface_implement.type_convert;

import org.axe.interface_.type_convert.BaseTypeConvert;

/**
 * java.lang.Boolean=>java.lang.Integer
 */
public class Boolean2IntegerConvert implements BaseTypeConvert{

	public Integer convert(Object arg){
		return (Boolean)arg?1:0;
	}
}
