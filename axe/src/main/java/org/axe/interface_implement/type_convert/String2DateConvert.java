package org.axe.interface_implement.type_convert;

import java.util.Date;

import org.axe.interface_.type_convert.BaseTypeConvert;

/**
 * java.lang.String=>java.util.Date
 */
public class String2DateConvert implements BaseTypeConvert{

	public Date convert(Object arg,Object ...args){
		return new Date(Long.parseLong((String)arg));
	}
}
