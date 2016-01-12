package com.lightning.data.indexer.document;

import org.json.JSONObject;

public class GeoPoint implements BaseDocument{

	private Double lat;
	private Double lon;
	
	public Double getLat() {
		return lat;
	}
	public void setLat(Double lat) {
		this.lat = lat;
	}
	public Double getLon() {
		return lon;
	}
	public void setLon(Double lon) {
		this.lon = lon;
	}
	@Override
	public JSONObject getJSONRepresentation() {
		JSONObject json = new JSONObject();
		json.putOpt("lat", this.lat);
		json.putOpt("lon", this.lon);
		return json;
	}
}