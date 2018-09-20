package com.bridgelabz.fundonotes.note.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.bridgelabz.fundonotes.note.exception.RestHighLevelClientException;
import com.bridgelabz.fundonotes.note.model.Label;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class HighElasticRepositoryForLabel {

	private final String INDEX = "labeldb";
	private final String TYPE = "label";

	@Autowired
	private RestHighLevelClient restHighLevelClient;

	@Autowired
	private ObjectMapper objectMapper;

	public HighElasticRepositoryForLabel(ObjectMapper objectMapper, RestHighLevelClient restHighLevelClient) {
		this.objectMapper = objectMapper;
		this.restHighLevelClient = restHighLevelClient;
	}

	public void save(Label label) {
		@SuppressWarnings("unchecked")
		Map<String, Object> dataMap = objectMapper.convertValue(label, Map.class);
		IndexRequest indexRequest = new IndexRequest(INDEX, TYPE, label.getLabelId()).source(dataMap);
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
		Optional<T> object = null;
		try {
			getResponse = restHighLevelClient.get(getRequest);
			Map<String, Object> sourceAsMap = getResponse.getSourceAsMap();
			T user = objectMapper.convertValue(sourceAsMap, clazz);
			object = Optional.of(user);
		} catch (IOException exception) {
			throw new RestHighLevelClientException("Fail to get response");
		}

		return object;
	}

	public Optional<Label> findById(String labelId) throws RestHighLevelClientException {

		GetRequest getRequest = new GetRequest(INDEX, TYPE, labelId);

		GetResponse getResponse = null;

		Optional<Label> optionalLabel = null;

		try {
			getResponse = restHighLevelClient.get(getRequest);

			String labelData = getResponse.getSourceAsString();

			optionalLabel = Optional.of(objectMapper.readValue(labelData, Label.class));
		} catch (IOException exception) {
			throw new RestHighLevelClientException("Fail to get response");
		}

		return optionalLabel;
	}

	public List<Label> findByUserId(String userId) throws IOException {

		SearchRequest searchRequest = new SearchRequest(INDEX);
		searchRequest.types(TYPE);

		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(QueryBuilders.matchQuery("userId", userId));

		searchRequest.source(sourceBuilder);

		SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
		SearchHits hits = searchResponse.getHits();
		SearchHit[] searchHits = hits.getHits();

		List<Label> labels = new ArrayList<>();

		for (SearchHit hit : searchHits) {
			Map<String, Object> sourceAsMap = hit.getSourceAsMap();
			labels.add(objectMapper.convertValue(sourceAsMap, Label.class));
		}

		return labels;
	}

	public Map<String, Object> updateByLabelId(String id, Object label)
			throws RestHighLevelClientException, IOException {

		GetRequest getRequest = new GetRequest(INDEX, TYPE, id);
		GetResponse getResponse = null;
		getResponse = restHighLevelClient.get(getRequest);

		if (getResponse.equals(null)) {
			throw new RestHighLevelClientException("Some exception related to rest high level client occured");
		}

		UpdateRequest updateRequest = new UpdateRequest(INDEX, TYPE, id).fetchSource(true);
		try {
			String updateDetails = objectMapper.writeValueAsString(label);
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

	public List<Label> findByLabelNameAndUserId(String labelName, String userId) throws IOException {
		SearchRequest searchRequest = new SearchRequest(INDEX);
		searchRequest.types(TYPE);

		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(QueryBuilders.matchPhrasePrefixQuery("labelName", labelName));
		sourceBuilder.query(QueryBuilders.matchPhrasePrefixQuery("userId", userId));

		searchRequest.source(sourceBuilder);

		SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
		SearchHits hits = searchResponse.getHits();
		SearchHit[] searchHits = hits.getHits();

		List<Label> labels = new ArrayList<>();

		for (SearchHit hit : searchHits) {
			Map<String, Object> sourceAsMap = hit.getSourceAsMap();
			labels.add(objectMapper.convertValue(sourceAsMap, Label.class));
		}

		return labels;
	}

	public List<Label> findAll() throws IOException {
		
		SearchRequest searchRequest = new SearchRequest(INDEX);
		searchRequest.types(TYPE);
		
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		
		sourceBuilder.fetchSource();

		SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
		SearchHits hits=searchResponse.getHits();
		SearchHit[] searchHits=hits.getHits();
		List<Label> labels=new ArrayList<>();
		for(SearchHit hit:searchHits) {
			Map<String,Object> sourceAsMap=hit.getSourceAsMap();
			labels.add(objectMapper.convertValue(sourceAsMap,Label.class));
		}
		return labels;
	}
}
