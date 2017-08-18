package org.axe.interface_implement.persistence;

import java.math.BigDecimal;

import org.axe.interface_.persistence.BaseTypeConvert;

/**
 * java.math.BigDecimal=>java.lang.Double
 */
public class BigDecimal2DoubleConvert implements BaseTypeConvert{

	@Override
	public Object convert(Object object) {
		return ((BigDecimal)object).doubleValue();
	}

}
