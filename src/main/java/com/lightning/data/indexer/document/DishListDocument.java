package com.lightning.data.indexer.document;

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
	private GeoPoint centerLocation;
	private Picture picture;

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

	

	public GeoPoint getCenterLocation() {
		return centerLocation;
	}

	public void setCenterLocation(GeoPoint centerLocation) {
		this.centerLocation = centerLocation;
	}

	public Picture getPicture() {
		return picture;
	}

	public void setPicture(Picture picture) {
		this.picture = picture;
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
		if (this.centerLocation != null) {
			json.putOpt("center_location", this.centerLocation.getJSONRepresentation());
		}
		if (this.picture != null) {
			json.putOpt("picture", this.picture.getJSONRepresentation());
		}
		return json;
	}
}
