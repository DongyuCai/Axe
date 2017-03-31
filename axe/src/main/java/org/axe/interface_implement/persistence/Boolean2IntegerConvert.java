package org.axe.interface_implement.persistence;

import org.axe.interface_.persistence.BaseTypeConvert;

/**
 * java.lang.Boolean=>java.lang.Integer
 */
public class Boolean2IntegerConvert implements BaseTypeConvert{

	public Integer convert(Object arg){
		return (Boolean)arg?1:0;
	}
}
