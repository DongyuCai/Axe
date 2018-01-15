package org.axe.interface_implement.persistence;

import org.axe.interface_.persistence.BaseTypeConvert;

/**
 * java.lang.String=>java.lang.Double
 */
public class String2DoubleConvert implements BaseTypeConvert{

	public Double convert(Object arg){
		return Double.parseDouble((String)arg);
	}
}
