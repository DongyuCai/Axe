package org.axe.interface_implement.type_convert;

import java.util.Date;

import org.axe.interface_.type_convert.BaseTypeConvert;

/**
 * java.lang.Long=>java.util.Date
 */
public final class Long2DateConvert implements BaseTypeConvert{

	public Date convert(Object arg,Object ...args){
		return new Date((Long)arg);
	}
}
