package com.bridgelabz.fundonotes.note.repository;

import org.springframework.stereotype.Repository;

import com.bridgelabz.fundonotes.note.model.Note;
import com.bridgelabz.fundonotes.note.model.ViewDTO;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

@Repository
public interface NoteRepository extends MongoRepository<Note,String>{

	Optional<Note> findByNoteId(String noteId);

	void save(ViewDTO viewNote);

	void deleteByNoteId(String noteId);

	Optional<Note> findByUserId(String token);

}
