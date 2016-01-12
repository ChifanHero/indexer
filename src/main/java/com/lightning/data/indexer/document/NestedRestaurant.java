package com.lightning.data.indexer.document;

import org.json.JSONObject;

public class NestedRestaurant implements BaseDocument{
	
	private String objectId;
	private String name;
	private String englishName;
	private GeoPoint coordinates;

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEnglishName() {
		return englishName;
	}

	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}

	public GeoPoint getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(GeoPoint coordinates) {
		this.coordinates = coordinates;
	}

	@Override
	public JSONObject getJSONRepresentation() {
		JSONObject json = new JSONObject();
		json.putOpt("objectId", this.objectId);
		json.putOpt("name", this.name);
		json.putOpt("english_name", this.englishName);
		if (this.coordinates != null) {
			json.putOpt("coordinates", this.coordinates.getJSONRepresentation());
		}
		return json;
	}
	
}
