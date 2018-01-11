package org.axe.interface_implement.persistence;

import java.math.BigDecimal;

import org.axe.interface_.persistence.BaseTypeConvert;

/**
 * java.lang.String=>java.math.BigDecimal
 */
public class String2BigDecimalConvert implements BaseTypeConvert{

	public BigDecimal convert(Object arg){
		return new BigDecimal((String)arg);
	}
}
