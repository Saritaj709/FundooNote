package com.bridgelabz.fundonotes.note.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.bridgelabz.fundonotes.note.model.Note;

public interface ElasticRepositoryForNote extends ElasticsearchRepository<Note,String>{

	List<Note> findAllByUserId(String userId);

	Optional<Note> findByNoteId(String noteId);

	void deleteByNoteId(String noteId);

}
