package org.axe.interface_implement.type_convert;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

import org.axe.interface_.type_convert.BaseTypeConvert;

/**
 * java.lang.String=>java.sql.Timestamp
 */
public final class String2SqlTimestampConvert implements BaseTypeConvert{

	public Timestamp convert(Object arg,Object ...args){
		String str = (String)arg;
		Pattern compile = Pattern.compile("^[0-9]{4}\\-[0-9]{2}\\-[0-9]{2} [0-9]{2}\\:[0-9]{2}:[0-9]{2}\\.[0-9]+$");;
		if(compile.matcher(str).find()){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
			try {
				return new Timestamp(sdf.parse(str).getTime());
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
		}else{
			return new Timestamp(Long.parseLong(str));
		}
	}
}
