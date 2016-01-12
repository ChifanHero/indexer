package com.lightning.data.indexer.document;

import static org.junit.Assert.*;

import org.junit.Test;

public class PictureTest {

	@Test
	public void test() {
		Picture picture = new Picture();
		picture.setOriginal("http://original_url");
		picture.setThumbnail("http://thumbnail_url");
		assertEquals("{\"thumbnail\":\"http://thumbnail_url\",\"original\":\"http://original_url\"}", picture.getJSONRepresentation().toString());
	}

}
