package com.lightning.data.indexer.app;



import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import org.elasticsearch.common.settings.ImmutableSettings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lightning.data.indexer.basic.ElasticsearchRestClientFactory;

import io.searchbox.indices.aliases.AddAliasMapping;
import io.searchbox.indices.aliases.AliasMapping;
import io.searchbox.indices.aliases.GetAliases;
import io.searchbox.indices.aliases.ModifyAliases;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import io.searchbox.indices.mapping.PutMapping;

public class IndexManager {
	
	private final static String RESTAURANT_MAPPING = "restaurant_mapping.json";
	private final static String DISH_MAPPING = "dish_mapping.json";
	private final static String DISH_LIST_MAPPING = "dish_list_mapping.json";
	private final static String INDEX_ALIAS = "food";
	private JestClient client;
	
	private static IndexManager INSTANCE;
	
	private IndexManager() {
		client = ElasticsearchRestClientFactory.getRestClient();
	}
	
	
	public static IndexManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new IndexManager();
		}
		return INSTANCE;
	}
	
	private String indexName;
	
	public void createIndex() {
		try {
			initializeIndex();
			createMappings();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	private void initializeIndex() throws IOException {
		String settings = getSettings();
		client.execute(new CreateIndex.Builder(getIndexName()).settings(ImmutableSettings.builder().loadFromSource(settings).build().getAsMap()).build());
	}
	
	public void deleteOldIndex() {
		List<String> oldIndexNames = getOldIndexNames();
		try {
			for (String name : oldIndexNames) {
				client.execute(new DeleteIndex.Builder(name).build());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private List<String> getOldIndexNames() {
		try {
			JestResult aliasResult = client.execute(new GetAliases.Builder().addIndex(INDEX_ALIAS).build());
			JsonObject resultBody = aliasResult.getJsonObject();
			Set<Entry<String, JsonElement>> entrySet = resultBody.entrySet();
			List<String> oldNames = new ArrayList<String>();
			for (Entry<String, JsonElement> entry : entrySet) {
				String indexName = entry.getKey();
				String currentIndexName = getIndexName();
				if (!currentIndexName.equals(indexName)) {
					oldNames.add(indexName);
				}
			}
			return oldNames;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private void createMappings() throws IOException {
		createRestaurantMapping();
		createDishMapping();
		createDishListMapping();
	}
	
	private void createDishListMapping() throws IOException {
		String dishListMapping = getMapping(DISH_LIST_MAPPING);
		PutMapping putMapping = new PutMapping.Builder(getIndexName(), "list", dishListMapping).build();
		client.execute(putMapping);
	}

	private void createDishMapping() throws IOException {
		String dishMapping = getMapping(DISH_MAPPING);
		PutMapping putMapping = new PutMapping.Builder(getIndexName(), "dish", dishMapping).build();
		client.execute(putMapping);
	}

	private void createRestaurantMapping() throws IOException {
		String restaurantMapping = getMapping(RESTAURANT_MAPPING);
		PutMapping putMapping = new PutMapping.Builder(getIndexName(), "restaurant", restaurantMapping).build();
		client.execute(putMapping);
	}
	
	public String getIndexName() {
		if (indexName == null) {
			long timeStamp = System.currentTimeMillis();
			indexName = "food_" + String.valueOf(timeStamp);
		} 
		return indexName;
		
	}
	
	public void setAliasToNewIndex() {
		AliasMapping mapping = new AddAliasMapping.Builder(getIndexName(), INDEX_ALIAS).build();
		try {
			client.execute(new ModifyAliases.Builder(mapping).build());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	private String getMapping(String mapping) {
		return readJsonFile(mapping);
	}
	
	private String getSettings() {
		return readJsonFile("index_settings.json");
	}
	
	private String readJsonFile(String fileName) {
		StringBuilder result = new StringBuilder("");

		//Get file from resources folder
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(fileName).getFile());

		try (Scanner scanner = new Scanner(file)) {

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				result.append(line).append("\n");
			}

			scanner.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
			
		return result.toString();
	}

}
