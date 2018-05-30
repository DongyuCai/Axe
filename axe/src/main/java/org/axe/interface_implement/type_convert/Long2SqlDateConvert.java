package org.axe.interface_implement.type_convert;

import java.sql.Date;

import org.axe.interface_.type_convert.BaseTypeConvert;

/**
 * java.lang.Long=>java.sql.Date
 */
public class Long2SqlDateConvert implements BaseTypeConvert{

	public Date convert(Object arg,Object ...args){
		return new Date((Long)arg);
	}
}
