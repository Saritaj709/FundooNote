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
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.bridgelabz.fundonotes.note.exception.RestHighLevelClientException;
import com.bridgelabz.fundonotes.note.model.Note;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class HighElasticRepositoryForNote {

	private final String INDEX = "notedb";
	private final String TYPE = "note";

	@Autowired
	private RestHighLevelClient restHighLevelClient;

	@Autowired
	private ObjectMapper objectMapper;

	public HighElasticRepositoryForNote(ObjectMapper objectMapper, RestHighLevelClient restHighLevelClient) {
		this.objectMapper = objectMapper;
		this.restHighLevelClient = restHighLevelClient;
	}

	public void save(Note note) {
		@SuppressWarnings("unchecked")
		Map<String, Object> dataMap = objectMapper.convertValue(note, Map.class);
		IndexRequest indexRequest = new IndexRequest(INDEX, TYPE, note.getNoteId()).source(dataMap);
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
			throw new RestHighLevelClientException("Fail to get response");
		}

		return user1;
	}

	public Optional<Note> findById(String labelId) throws RestHighLevelClientException {

		GetRequest getRequest = new GetRequest(INDEX, TYPE, labelId);

		GetResponse getResponse = null;

		Optional<Note> optionalNote = null;

		try {
			getResponse = restHighLevelClient.get(getRequest);

			String noteData = getResponse.getSourceAsString();

			optionalNote = Optional.of(objectMapper.readValue(noteData, Note.class));
		} catch (IOException exception) {
			throw new RestHighLevelClientException("Fail to get response");
		}

		return optionalNote;
	}
	
	
	public Optional<Note> findByUserId(String userId) {

		SearchRequest searchRequest = new SearchRequest(INDEX);
		searchRequest.types(TYPE);

		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(QueryBuilders.matchPhrasePrefixQuery("userId", userId));


		searchRequest.source(sourceBuilder);

		Optional<Note> myNote = null;
		SearchResponse searchResponse = null;
		String note;

		try {
			searchResponse = restHighLevelClient.search(searchRequest);
		} catch (IOException e) {
			e.printStackTrace();
		}

		SearchHit[] hits = searchResponse.getHits().getHits();

		try {
			note = hits[0].getSourceAsString();
		} catch (Exception e) {
			return myNote;
		}

		try {
			myNote = Optional.of(objectMapper.readValue(note, Note.class));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return myNote;
	}
	
	public Optional<Note> findByUserIdAndNoteId(String userId,String noteId) {

		SearchRequest searchRequest = new SearchRequest(INDEX);
		searchRequest.types(TYPE);

		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		/*sourceBuilder.query(QueryBuilders.matchQuery("userId", userId));
		sourceBuilder.query(QueryBuilders.matchQuery("noteId", noteId));*/
		sourceBuilder.query(QueryBuilders.matchPhrasePrefixQuery("userId", userId));
		sourceBuilder.query(QueryBuilders.matchPhrasePrefixQuery("noteId", noteId));


		searchRequest.source(sourceBuilder);

		Optional<Note> myNote = null;
		SearchResponse searchResponse = null;
		String note;

		try {
			searchResponse = restHighLevelClient.search(searchRequest);
		} catch (IOException e) {
			e.printStackTrace();
		}

		SearchHit[] hits = searchResponse.getHits().getHits();

		try {
			note = hits[0].getSourceAsString();
		} catch (Exception e) {
			return myNote;
		}

		try {
			myNote = Optional.of(objectMapper.readValue(note, Note.class));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return myNote;
	}
	
	public List<Note> findAllByUserIdAndTrashed(String userId, boolean trashed) throws IOException {

        SearchRequest searchRequest = new SearchRequest(INDEX);
        searchRequest.types(TYPE);
        
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder bool = new BoolQueryBuilder();
        
        bool.must(new TermQueryBuilder("userId", userId));

        bool.must(new TermQueryBuilder("trashed", trashed));

        searchSourceBuilder.query(bool);
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();

        List<Note> notes = new ArrayList<>();

        for (SearchHit hit : searchHits) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            notes.add(objectMapper.convertValue(sourceAsMap, Note.class));
        }

        return notes;
    }
	
	
	public Optional<Note> findByNoteId(String noteId) throws RestHighLevelClientException {

		GetRequest getRequest = new GetRequest(INDEX, TYPE, noteId);

		GetResponse getResponse = null;

		Optional<Note> optionalNote = null;

		try {
			getResponse = restHighLevelClient.get(getRequest);

			String noteData = getResponse.getSourceAsString();

			optionalNote = Optional.of(objectMapper.readValue(noteData, Note.class));
		} catch (IOException exception) {
			throw new RestHighLevelClientException("Fail to get response,note");
		}

		return optionalNote;
	}
 
	public Map<String, Object> updateByNoteId(String id, Object note) throws RestHighLevelClientException, IOException {

		GetRequest getRequest = new GetRequest(INDEX, TYPE, id);
		GetResponse getResponse = null;
		getResponse = restHighLevelClient.get(getRequest);

		if (getResponse.equals(null)) {
			throw new RestHighLevelClientException("Some exception related to rest high level client occured");
		}

		UpdateRequest updateRequest = new UpdateRequest(INDEX, TYPE, id).fetchSource(true);
		try {
			String updateDetails = objectMapper.writeValueAsString(note);
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
