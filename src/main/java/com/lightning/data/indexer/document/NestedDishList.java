package com.lightning.data.indexer.document;

import org.json.JSONObject;

public class NestedDishList implements BaseDocument{
	
	private String objectId;
	private String name;

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

	@Override
	public JSONObject getJSONRepresentation() {
		JSONObject json = new JSONObject();
		json.putOpt("objectId", this.objectId);
		json.putOpt("name", this.name);
		return json;
	}

}
