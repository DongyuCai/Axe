package org.axe.interface_implement.persistence;

import org.axe.interface_.persistence.BaseTypeConvert;

/**
 * java.lang.Integer=>java.lang.Long
 */
public class Integer2LongConvert implements BaseTypeConvert{

	public Long convert(Object arg){
		return Long.valueOf(arg.toString());
	}
}
