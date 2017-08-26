package org.axe.interface_implement.persistence;

import java.math.BigInteger;

import org.axe.interface_.persistence.BaseTypeConvert;

/**
 * java.math.BigInteger=>java.lang.Long
 */
public class BigInteger2LongConvert implements BaseTypeConvert{

	public Long convert(Object arg){
		return ((BigInteger)arg).longValue();
	}
}
