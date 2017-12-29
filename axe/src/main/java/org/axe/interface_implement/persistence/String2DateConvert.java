package org.axe.interface_implement.persistence;

import java.util.Date;

import org.axe.interface_.persistence.BaseTypeConvert;

/**
 * java.lang.String=>java.util.Date
 */
public class String2DateConvert implements BaseTypeConvert{

	public Date convert(Object arg){
		return new Date(Long.parseLong((String)arg));
	}
}
