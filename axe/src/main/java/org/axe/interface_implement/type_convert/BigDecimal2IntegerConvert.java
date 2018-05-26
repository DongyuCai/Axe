package org.axe.interface_implement.type_convert;

import java.math.BigDecimal;

import org.axe.interface_.type_convert.BaseTypeConvert;

/**
 * java.math.BigDecimal=>java.lang.Integer
 */
public class BigDecimal2IntegerConvert implements BaseTypeConvert{

	@Override
	public Object convert(Object object) {
		return ((BigDecimal)object).intValue();
	}

}
