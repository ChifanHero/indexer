package com.lightning.data.indexer.document;

import static org.junit.Assert.*;

import org.junit.Test;

public class GeoPointTest {

	@Test
	public void test() {
		GeoPoint geoPoint = new GeoPoint();
		geoPoint.setLat(123.4);
		geoPoint.setLon(-123.4);
		assertEquals("{\"lon\":-123.4,\"lat\":123.4}", geoPoint.getJSONRepresentation().toString());
	}

}
