package com.lightning.data.indexer.document;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class DishListDocument implements BaseDocument{
	
	private String createdAt;
	private long favoriteCount;
	private long likeCount;
	private long memberCount;
	private String name;
	private String objectId;
	private String updatedAt;
	private List<String> dishes;
	private List<String> restaurants;
	private List<GeoPoint> locations;

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public long getFavoriteCount() {
		return favoriteCount;
	}

	public void setFavoriteCount(long favoriteCount) {
		this.favoriteCount = favoriteCount;
	}

	public long getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(long likeCount) {
		this.likeCount = likeCount;
	}

	public long getMemberCount() {
		return memberCount;
	}

	public void setMemberCount(long memberCount) {
		this.memberCount = memberCount;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	public List<String> getDishes() {
		return dishes;
	}

	public void setDishes(List<String> dishes) {
		this.dishes = dishes;
	}

	public List<String> getRestaurants() {
		return restaurants;
	}

	public void setRestaurants(List<String> restaurants) {
		this.restaurants = restaurants;
	}

	public List<GeoPoint> getLocations() {
		return locations;
	}

	public void setLocations(List<GeoPoint> locations) {
		this.locations = locations;
	}

	@Override
	public JSONObject getJSONRepresentation() {
		JSONObject json = new JSONObject();
		json.putOpt("createdAt", this.createdAt);
		json.putOpt("favorite_count", this.favoriteCount);
		json.putOpt("like_count", this.likeCount);
		json.putOpt("member_count", this.memberCount);
		json.putOpt("name", this.name);
		json.putOpt("objectId", this.objectId);
		json.putOpt("updatedAt", this.updatedAt);
		json.put("dishes", this.dishes);
		if (this.locations != null && this.locations.size() > 0) {
			List<JSONObject> lists = new ArrayList<JSONObject>();
			for (GeoPoint location : locations) {
				lists.add(location.getJSONRepresentation());
			}
			json.putOpt("locations", lists);
		}
		return json;
	}
}
