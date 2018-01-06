package org.axe.interface_implement.persistence;

import org.axe.interface_.persistence.BaseTypeConvert;

/**
 * java.lang.String=>java.lang.Long
 */
public class String2LongConvert implements BaseTypeConvert{

	public Long convert(Object arg){
		return Long.parseLong((String)arg);
	}
}
