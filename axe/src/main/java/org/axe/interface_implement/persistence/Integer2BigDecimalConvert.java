package org.axe.interface_implement.persistence;

import java.math.BigDecimal;

import org.axe.interface_.persistence.BaseTypeConvert;

/**
 * java.lang.Integer=>java.math.BigDecimal
 */
public class Integer2BigDecimalConvert implements BaseTypeConvert{

	public BigDecimal convert(Object arg){
		return new BigDecimal((Integer)arg);
	}
}
