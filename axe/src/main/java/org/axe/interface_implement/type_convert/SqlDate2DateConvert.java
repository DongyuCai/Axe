package org.axe.interface_implement.type_convert;

import java.util.Date;

import org.axe.interface_.type_convert.BaseTypeConvert;

/**
 * java.sql.Date=>java.util.Date
 */
public class SqlDate2DateConvert implements BaseTypeConvert{

	public Date convert(Object arg,Object ...args){
		return (java.sql.Date)arg;
	}
}
