package com.lightning.data.indexer.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConverter {
	
	public static String convert(Date date) {
		//2015-12-22T00:13:11.003Z
		if (date == null) return null;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		return formatter.format(date);
	}

}
