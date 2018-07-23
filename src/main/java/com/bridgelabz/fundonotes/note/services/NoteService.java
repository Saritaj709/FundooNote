package com.bridgelabz.fundonotes.note.services;

import java.util.Date;
import java.util.List;

import com.bridgelabz.fundonotes.note.exception.NoteCreationException;
import com.bridgelabz.fundonotes.note.exception.NoteNotFoundException;
import com.bridgelabz.fundonotes.note.exception.NoteTrashedException;
import com.bridgelabz.fundonotes.note.exception.NullEntryException;
import com.bridgelabz.fundonotes.note.exception.UntrashedException;
import com.bridgelabz.fundonotes.note.exception.UserNotFoundException;
import com.bridgelabz.fundonotes.note.model.CreateDTO;
import com.bridgelabz.fundonotes.note.model.NoteDTO;
import com.bridgelabz.fundonotes.note.model.UpdateDTO;
import com.bridgelabz.fundonotes.note.model.ViewDTO;

public interface NoteService {
	void createNote(String token,CreateDTO create) throws NoteNotFoundException, NoteCreationException, UserNotFoundException;

	void updateNote(String token,UpdateDTO update) throws NoteNotFoundException, UserNotFoundException, NoteTrashedException;

	boolean trashNote(String token,String noteId) throws NoteNotFoundException, UserNotFoundException, UntrashedException, NoteTrashedException;

	List<NoteDTO> readAllNotes() throws NullEntryException;
	
	ViewDTO findNoteById(String token,String noteId) throws UserNotFoundException, NoteNotFoundException, NoteTrashedException;

	void deleteNoteForever(String token,String noteId) throws NoteNotFoundException, UserNotFoundException, UntrashedException, NoteTrashedException;

	boolean addReminder(String token,Date date,String noteId) throws UserNotFoundException, NoteNotFoundException, NoteTrashedException;

	void deleteReminder(String token,String noteId) throws NullEntryException, UserNotFoundException, NoteNotFoundException, NoteTrashedException;

	List<ViewDTO> readNotes() throws NullEntryException, NoteNotFoundException, NoteCreationException, UserNotFoundException;

	void restoreNote(String token, String noteId)
			throws NoteNotFoundException, UserNotFoundException, UntrashedException, NoteTrashedException;
}
