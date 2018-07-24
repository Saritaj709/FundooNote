package com.bridgelabz.fundonotes.note.repository;

import org.springframework.stereotype.Repository;

import com.bridgelabz.fundonotes.note.model.NoteDTO;
import com.bridgelabz.fundonotes.note.model.ViewDTO;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

@Repository
public interface NoteRepository extends MongoRepository<NoteDTO,String>{

	Optional<NoteDTO> findByNoteId(String noteId);

	void save(ViewDTO viewNote);

	void deleteByNoteId(String noteId);

	Object findByUserId(String token);

}
