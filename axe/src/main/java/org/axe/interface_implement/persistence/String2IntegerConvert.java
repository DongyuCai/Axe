package org.axe.interface_implement.persistence;

import org.axe.interface_.persistence.BaseTypeConvert;

/**
 * java.lang.String=>java.lang.Integer
 */
public class String2IntegerConvert implements BaseTypeConvert{

	public Integer convert(Object arg){
		return Integer.parseInt((String)arg);
	}
}
