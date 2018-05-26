package org.axe.interface_implement.type_convert;

import java.math.BigDecimal;

import org.axe.interface_.type_convert.BaseTypeConvert;

/**
 * java.lang.Integer=>java.math.BigDecimal
 */
public class Integer2BigDecimalConvert implements BaseTypeConvert{

	public BigDecimal convert(Object arg){
		return new BigDecimal((Integer)arg);
	}
}
