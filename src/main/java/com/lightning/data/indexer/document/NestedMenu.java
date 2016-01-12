package com.lightning.data.indexer.document;

import org.json.JSONObject;

public class NestedMenu implements BaseDocument{
	
	private String objectId;
	private String name;
	private String englishName;

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

	@Override
	public JSONObject getJSONRepresentation() {
		JSONObject json = new JSONObject();
		json.putOpt("objectId", this.objectId);
		json.putOpt("name", this.name);
		json.putOpt("english_name", this.englishName);
		return json;
	}

}
