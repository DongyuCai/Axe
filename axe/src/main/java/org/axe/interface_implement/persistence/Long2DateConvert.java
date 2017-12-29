package org.axe.interface_implement.persistence;

import java.util.Date;

import org.axe.interface_.persistence.BaseTypeConvert;

/**
 * java.lang.Long=>java.util.Date
 */
public class Long2DateConvert implements BaseTypeConvert{

	public Date convert(Object arg){
		return new Date((Long)arg);
	}
}
