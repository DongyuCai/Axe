package org.axe.interface_implement.persistence;

import java.math.BigDecimal;

import org.axe.interface_.persistence.BaseTypeConvert;

/**
 * java.lang.Long=>java.math.BigDecimal
 */
public class Long2BigDecimalConvert implements BaseTypeConvert{

	public BigDecimal convert(Object arg){
		return new BigDecimal((Long)arg);
	}
}
