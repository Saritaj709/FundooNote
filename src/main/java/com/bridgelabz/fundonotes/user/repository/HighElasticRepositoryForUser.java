package com.bridgelabz.fundonotes.user.repository;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.bridgelabz.fundonotes.note.exception.RestHighLevelClientException;
import com.bridgelabz.fundonotes.user.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class HighElasticRepositoryForUser {

	private final String INDEX = "userdb";
	private final String TYPE = "user";

	@Autowired
	private RestHighLevelClient restHighLevelClient;

	@Autowired
	private ObjectMapper objectMapper;

	public HighElasticRepositoryForUser(ObjectMapper objectMapper, RestHighLevelClient restHighLevelClient) {
		this.objectMapper = objectMapper;
		this.restHighLevelClient = restHighLevelClient;
	}

	public void save(User user) {
		@SuppressWarnings("unchecked")
		Map<String, Object> dataMap = objectMapper.convertValue(user, Map.class);
		IndexRequest indexRequest = new IndexRequest(INDEX, TYPE, user.getId()).source(dataMap);
		try {
			@SuppressWarnings("unused")
			IndexResponse response = restHighLevelClient.index(indexRequest);
		} catch (ElasticsearchException e) {
			e.getDetailedMessage();
		} catch (java.io.IOException ex) {
			ex.getLocalizedMessage();
		}
	}

	public <T> Optional<T> findById(String id, Class<T> clazz) throws RestHighLevelClientException {
		GetRequest getRequest = new GetRequest(INDEX, TYPE, id);
		GetResponse getResponse = null;
		Optional<T> user1 = null;
		try {
			getResponse = restHighLevelClient.get(getRequest);
			Map<String, Object> sourceAsMap = getResponse.getSourceAsMap();
			T user = objectMapper.convertValue(sourceAsMap, clazz);
			user1 = Optional.of(user);
		} catch (IOException exception) {
			throw new RestHighLevelClientException("Fail to get response,user");
		}

		return user1;
	}

	public Optional<User> findById(String userId) throws RestHighLevelClientException {

		GetRequest getRequest = new GetRequest(INDEX, TYPE, userId);

		GetResponse getResponse = null;

		Optional<User> optionalUser = null;

		try {
			getResponse = restHighLevelClient.get(getRequest);

			String userData = getResponse.getSourceAsString();

			optionalUser = Optional.of(objectMapper.readValue(userData, User.class));
		} catch (IOException exception) {
			throw new RestHighLevelClientException("Fail to get response");
		}

		return optionalUser;
	}

	public Optional<User> findByEmail(String userEmail) {

		SearchRequest searchRequest = new SearchRequest(INDEX);
		searchRequest.types(TYPE);

		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(QueryBuilders.matchPhrasePrefixQuery("email", userEmail));

		searchRequest.source(sourceBuilder);

		Optional<User> myUser = null;
		SearchResponse searchResponse = null;
		String user;

		try {
			searchResponse = restHighLevelClient.search(searchRequest);
		} catch (IOException e) {
			e.printStackTrace();
		}

		SearchHit[] hits = searchResponse.getHits().getHits();

		try {
			user = hits[0].getSourceAsString();
		} catch (Exception e) {
			return myUser;
		}

		try {
			myUser = Optional.of(objectMapper.readValue(user, User.class));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return myUser;
	}
	 
	public Map<String, Object> updateById(String id, Object user) throws RestHighLevelClientException, IOException {

		GetRequest getRequest = new GetRequest(INDEX, TYPE, id);
		GetResponse getResponse = null;
		getResponse = restHighLevelClient.get(getRequest);

		if (getResponse.equals(null)) {
			throw new RestHighLevelClientException("Some exception related to rest high level client occured");
		}

		UpdateRequest updateRequest = new UpdateRequest(INDEX, TYPE, id).fetchSource(true);
		try {
			String updateDetails = objectMapper.writeValueAsString(user);
			updateRequest.doc(updateDetails, XContentType.JSON);
			UpdateResponse updateResponse = restHighLevelClient.update(updateRequest);
			Map<String, Object> sourceAsMap = updateResponse.getGetResult().sourceAsMap();
			return sourceAsMap;
		} catch (JsonProcessingException e) {
			e.getMessage();
		} catch (java.io.IOException e) {
			e.getLocalizedMessage();
		}
		return null;
	}

	public void deleteById(String id) {
		DeleteRequest deleteRequest = new DeleteRequest(INDEX, TYPE, id);
		try {

			@SuppressWarnings("unused")
			DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest);
		} catch (java.io.IOException e) {
			e.getLocalizedMessage();
		}
	}
}
