package org.axe.interface_implement.type_convert;

import java.sql.Date;

import org.axe.interface_.type_convert.BaseTypeConvert;

/**
 * java.lang.String=>java.sql.Date
 */
public final class String2SqlDateConvert implements BaseTypeConvert{

	public Date convert(Object arg,Object ...args){
		return new Date(Long.parseLong((String)arg));
	}
}
