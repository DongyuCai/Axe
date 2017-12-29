package org.axe.interface_implement.persistence;

import java.sql.Date;

import org.axe.interface_.persistence.BaseTypeConvert;

/**
 * java.lang.String=>java.sql.Date
 */
public class String2SqlDateConvert implements BaseTypeConvert{

	public Date convert(Object arg){
		return new Date(Long.parseLong((String)arg));
	}
}
