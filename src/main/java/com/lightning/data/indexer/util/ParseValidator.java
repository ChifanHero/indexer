package com.lightning.data.indexer.util;

import org.parse4j.ParseObject;

public class ParseValidator {
	
	public static boolean isValidObject(ParseObject object) {
		if (object == null) {
			return false;
		} else {
			if (object.getClassName() != null && object.getObjectId() != null) {
				return true;
			} else {
				return false;
			}
		}
	}

}