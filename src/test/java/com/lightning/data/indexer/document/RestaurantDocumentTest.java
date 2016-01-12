package com.lightning.data.indexer.document;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.lightning.data.indexer.util.DateConverter;

public class RestaurantDocumentTest {

	@Test
	public void test() {
		RestaurantDocument document = new RestaurantDocument();
		document.setAddress("Test Rd, Test, Test, 91234");
		GeoPoint geo = new GeoPoint();
		geo.setLat(123.4);
		geo.setLon(-123.5);
		document.setCoordinates(geo);
		document.setCreatedAt(DateConverter.convert(new Date()));
		List<String> dishes = new ArrayList<String>();
		dishes.add("test1");
		dishes.add("test2");
		document.setDishes(dishes);
		document.setDislikeCount(100);
		document.setEnglishName("english_name");
		document.setFavoriteCount(200);
		document.setLikeCount(300);
		document.setName("name");
		document.setNeutralCount(50);
		document.setObjectId("s8h4g3sg2w");
		document.setPhone("123456");
		Picture picture = new Picture();
		picture.setOriginal("http://original_url");
		picture.setThumbnail("http://thumb_url");
		document.setPicture(picture);
		document.setUpdatedAt(DateConverter.convert(new Date()));
		assertEquals("{\"address\":\"Test Rd, Test, Test, 91234\",\"like_count\":300,\"phone\":\"123456\",\"neutral_count\":50,\"name\":\"name\",\"coordinates\":{\"lon\":-123.5,\"lat\":123.4},\"dishes\":[\"test1\",\"test2\"],\"favorite_count\":200,\"objectId\":\"s8h4g3sg2w\",\"english_name\":\"english_name\",\"picture\":{\"thumbnail\":\"http://thumb_url\",\"original\":\"http://original_url\"},\"dislike_count\":100}", document.getJSONRepresentation().toString());
	}
}
