package org.axe.interface_implement.type_convert;

import java.math.BigInteger;

import org.axe.interface_.type_convert.BaseTypeConvert;

/**
 * java.math.BigInteger=>java.lang.Long
 */
public class BigInteger2LongConvert implements BaseTypeConvert{

	public Long convert(Object arg,Object ...args){
		return ((BigInteger)arg).longValue();
	}
}
