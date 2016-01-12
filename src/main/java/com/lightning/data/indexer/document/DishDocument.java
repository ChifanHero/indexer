package com.lightning.data.indexer.document;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class DishDocument implements BaseDocument{
	
	private String objectId;
	private String name;
	private String englishName;
	private Picture picture;
	private long likeCount;
	private long dislikeCount;
	private long neutralCount;
	private long favoriteCount;
	private String createdAt;
	private String updatedAt;
	private NestedRestaurant fromRestaurant;
	private NestedMenu menu;
	private List<NestedDishList> lists;

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

	public Picture getPicture() {
		return picture;
	}

	public void setPicture(Picture picture) {
		this.picture = picture;
	}

	public long getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(long likeCount) {
		this.likeCount = likeCount;
	}

	public long getDislikeCount() {
		return dislikeCount;
	}

	public void setDislikeCount(long dislikeCount) {
		this.dislikeCount = dislikeCount;
	}

	public long getNeutralCount() {
		return neutralCount;
	}

	public void setNeutralCount(long neutralCount) {
		this.neutralCount = neutralCount;
	}

	public long getFavoriteCount() {
		return favoriteCount;
	}

	public void setFavoriteCount(long favoriteCount) {
		this.favoriteCount = favoriteCount;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	public NestedRestaurant getFromRestaurant() {
		return fromRestaurant;
	}

	public void setFromRestaurant(NestedRestaurant fromRestaurant) {
		this.fromRestaurant = fromRestaurant;
	}

	public NestedMenu getMenu() {
		return menu;
	}

	public void setMenu(NestedMenu menu) {
		this.menu = menu;
	}

	public List<NestedDishList> getLists() {
		return lists;
	}

	public void setLists(List<NestedDishList> lists) {
		this.lists = lists;
	}

	@Override
	public JSONObject getJSONRepresentation() {
		JSONObject json = new JSONObject();
		json.putOpt("objectId", this.objectId);
		json.putOpt("name", this.name);
		json.putOpt("english_name", this.englishName);
		if (this.picture != null) {
			json.putOpt("picture", this.picture.getJSONRepresentation());
		}
		json.putOpt("like_count", this.likeCount);
		json.putOpt("dislike_count", this.dislikeCount);
		json.putOpt("neutral_count", this.neutralCount);
		json.putOpt("favorite_count", this.favoriteCount);
		json.putOpt("createdAt", this.createdAt);
		json.putOpt("updatedAt", this.updatedAt);
		if (this.fromRestaurant != null) {
			json.putOpt("from_restaurant", this.fromRestaurant.getJSONRepresentation());
		}
		if (this.menu != null) {
			json.putOpt("menu", this.menu.getJSONRepresentation());
		}
		if (this.lists != null && this.lists.size() > 0) {
			List<JSONObject> jLists = new ArrayList<JSONObject>();
			for (NestedDishList list : this.lists) {
				jLists.add(list.getJSONRepresentation());
			}
			json.putOpt("lists", jLists);
		}
		return json;
	}	

}
