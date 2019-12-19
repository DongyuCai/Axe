package org.axe.interface_implement.type_convert;

import java.math.BigDecimal;

import org.axe.interface_.type_convert.BaseTypeConvert;

/**
 * java.lang.Long=>java.math.BigDecimal
 */
public final class Long2BigDecimalConvert implements BaseTypeConvert{

	public BigDecimal convert(Object arg,Object ...args){
		return new BigDecimal((Long)arg);
	}
}
