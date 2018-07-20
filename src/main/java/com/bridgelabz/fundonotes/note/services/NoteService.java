package com.bridgelabz.fundonotes.note.services;

import java.util.Date;
import java.util.List;

import com.bridgelabz.fundonotes.note.exception.NoteCreationException;
import com.bridgelabz.fundonotes.note.exception.NoteNotFoundException;
import com.bridgelabz.fundonotes.note.exception.NullEntryException;
import com.bridgelabz.fundonotes.note.exception.UntrashedException;
import com.bridgelabz.fundonotes.note.exception.UserNotFoundException;
import com.bridgelabz.fundonotes.note.model.CreateDTO;
import com.bridgelabz.fundonotes.note.model.NoteDTO;
import com.bridgelabz.fundonotes.note.model.UpdateDTO;
import com.bridgelabz.fundonotes.note.model.ViewDTO;

public interface NoteService {
	void createNote(String token,CreateDTO create) throws NoteNotFoundException, NoteCreationException, UserNotFoundException;

	void updateNote(String token,UpdateDTO update,String noteId) throws NoteNotFoundException, UserNotFoundException;

	boolean moveNoteToTrash(String token,String userId,String noteId) throws NoteNotFoundException, UserNotFoundException;

	List<NoteDTO> readAllNotes() throws NullEntryException;
	
	ViewDTO findNoteById(String token,String noteId,String userId) throws UserNotFoundException, NoteNotFoundException;

	void deleteNote(String token,String userId,String noteId) throws NoteNotFoundException, UserNotFoundException, UntrashedException;

	boolean addReminder(String token,String userId,Date date,String noteId) throws UserNotFoundException, NoteNotFoundException;

	void deleteReminder(String token,String userId,Date date,String noteId) throws NullEntryException, UserNotFoundException, NoteNotFoundException;

	List<ViewDTO> readNotes() throws NullEntryException, NoteNotFoundException, NoteCreationException, UserNotFoundException;
}
