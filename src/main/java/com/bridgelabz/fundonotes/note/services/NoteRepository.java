package com.bridgelabz.fundonotes.note.services;

import org.springframework.stereotype.Repository;

import com.bridgelabz.fundonotes.note.model.NoteDTO;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

@Repository
public interface NoteRepository extends MongoRepository<NoteDTO,String>{

	Optional<NoteDTO> findByNoteId(String noteId);

}
