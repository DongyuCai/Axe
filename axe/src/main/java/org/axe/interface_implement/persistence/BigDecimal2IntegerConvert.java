package org.axe.interface_implement.persistence;

import java.math.BigDecimal;

import org.axe.interface_.persistence.BaseTypeConvert;

/**
 * java.math.BigDecimal=>java.lang.Integer
 */
public class BigDecimal2IntegerConvert implements BaseTypeConvert{

	@Override
	public Object convert(Object object) {
		return ((BigDecimal)object).intValue();
	}

}
