package com.lightning.data.indexer.document;

import org.json.JSONObject;

public class Picture implements BaseDocument{
	
	private String thumbnail;
	private String original;
	
	public String getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	public String getOriginal() {
		return original;
	}
	public void setOriginal(String original) {
		this.original = original;
	}
	@Override
	public JSONObject getJSONRepresentation() {
		JSONObject json = new JSONObject();
		json.putOpt("thumbnail", this.thumbnail);
		json.putOpt("original", this.original);
		return json;
	}

}