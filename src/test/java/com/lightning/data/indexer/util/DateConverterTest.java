package com.lightning.data.indexer.util;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

public class DateConverterTest {

	@Test
	public void test() {
		Date date = new Date();
		date.setTime(1452466266451l);
		String dateString = DateConverter.convert(date);
		assertEquals("2016-01-10T14:51:06.451Z", dateString);
	}

}
