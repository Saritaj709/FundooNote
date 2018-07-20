package com.bridgelabz.fundonotes.note.services;

import java.util.Date;
import java.util.List;

import com.bridgelabz.fundonotes.note.exception.NoteCreationException;
import com.bridgelabz.fundonotes.note.model.CreateDTO;
import com.bridgelabz.fundonotes.note.model.NoteDTO;
import com.bridgelabz.fundonotes.note.model.UpdateDTO;

public interface NoteService {
	void createNote(String token,CreateDTO create) throws NoteCreationException;

	void updateNote(String token,UpdateDTO update,String noteId) throws NoteCreationException;

	boolean moveNoteToTrash(String token,String userId,String noteId) throws NoteCreationException;

	List<NoteDTO> readAllNotes() throws NoteCreationException;
	
	boolean findNoteById(String token,String noteId,String userId) throws NoteCreationException;

	void deleteNote(String token,String userId,String noteId) throws NoteCreationException;

	boolean addReminder(String token,String userId,Date date,String noteId) throws NoteCreationException;

	void deleteReminder(String token,String userId,Date date,String noteId) throws NoteCreationException;
}
