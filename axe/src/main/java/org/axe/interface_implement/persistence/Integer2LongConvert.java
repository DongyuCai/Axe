package org.axe.interface_implement.persistence;

import org.axe.interface_.persistence.BaseTypeConvert;

/**
 * java.lang.Boolean=>java.lang.Integer
 */
public class Integer2LongConvert implements BaseTypeConvert{

	public Long convert(Object arg){
		return Long.valueOf(arg.toString());
	}
}
