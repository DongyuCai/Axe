package org.axe.interface_implement.type_convert;

import java.sql.Timestamp;
import java.util.Date;

import org.axe.interface_.type_convert.BaseTypeConvert;

/**
 * java.sql.Timestamp=>java.util.Date
 */
public final class Timestamp2DateConvert implements BaseTypeConvert{

	public Date convert(Object arg,Object ...args){
		return (Timestamp)arg;
	}
}
