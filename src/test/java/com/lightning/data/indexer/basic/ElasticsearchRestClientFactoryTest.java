package com.lightning.data.indexer.basic;

import static org.junit.Assert.*;

import org.junit.Test;

import io.searchbox.client.JestClient;

public class ElasticsearchRestClientFactoryTest {

	@Test
	public void test() {
		JestClient client = ElasticsearchRestClientFactory.getRestClient();
		assertNotNull(client);
	}

}
