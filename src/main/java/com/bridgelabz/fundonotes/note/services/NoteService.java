package com.bridgelabz.fundonotes.note.services;

import java.util.Date;
import java.util.List;

import com.bridgelabz.fundonotes.note.exception.DateException;
import com.bridgelabz.fundonotes.note.exception.LabelAdditionException;
import com.bridgelabz.fundonotes.note.exception.LabelNotFoundException;
import com.bridgelabz.fundonotes.note.exception.NoteArchievedException;
import com.bridgelabz.fundonotes.note.exception.NoteCreationException;
import com.bridgelabz.fundonotes.note.exception.NoteNotFoundException;
import com.bridgelabz.fundonotes.note.exception.NotePinnedException;
import com.bridgelabz.fundonotes.note.exception.NoteTrashedException;
import com.bridgelabz.fundonotes.note.exception.NullEntryException;
import com.bridgelabz.fundonotes.note.exception.UntrashedException;
import com.bridgelabz.fundonotes.note.exception.UserNotFoundException;
import com.bridgelabz.fundonotes.note.model.CreateDTO;
import com.bridgelabz.fundonotes.note.model.CreateLabelDTO;
import com.bridgelabz.fundonotes.note.model.Label;
import com.bridgelabz.fundonotes.note.model.Note;
import com.bridgelabz.fundonotes.note.model.UpdateDTO;
import com.bridgelabz.fundonotes.note.model.ViewDTO;

public interface NoteService {
	Note createNote(String token,CreateDTO create) throws NoteNotFoundException, NoteCreationException, UserNotFoundException, DateException;

	void updateNote(String token,UpdateDTO update) throws NoteNotFoundException, UserNotFoundException, NoteTrashedException;

	//boolean trashNote(String token,String noteId) throws NoteNotFoundException, UserNotFoundException, UntrashedException, NoteTrashedException;

	List<ViewDTO> readAllNotes() throws NullEntryException;
	
	ViewDTO findNoteById(String token,String noteId) throws UserNotFoundException, NoteNotFoundException, NoteTrashedException;

	void deleteNoteForever(String token,String noteId) throws NoteNotFoundException, UserNotFoundException, UntrashedException, NoteTrashedException;

	boolean addReminder(String token,Date date,String noteId) throws UserNotFoundException, NoteNotFoundException, NoteTrashedException, DateException;

	void deleteReminder(String token,String noteId) throws NullEntryException, UserNotFoundException, NoteNotFoundException, NoteTrashedException;

	//List<ViewDTO> readNotes() throws NullEntryException, NoteNotFoundException, NoteCreationException, UserNotFoundException;

	//void restoreNote(String token, String noteId)	throws NoteNotFoundException, UserNotFoundException, UntrashedException, NoteTrashedException;

	void archieveNote(String token, String noteId) throws NoteNotFoundException, UserNotFoundException, NoteArchievedException, NoteTrashedException;

	void pinNote(String token, String noteId) throws NoteNotFoundException, UserNotFoundException, NotePinnedException, NoteTrashedException;

	List<ViewDTO> viewArchieved() throws NullEntryException;

	List<ViewDTO> viewPinned() throws NullEntryException;

	List<ViewDTO> viewTrashed() throws NullEntryException;

	Label createLabel(String token, CreateLabelDTO createLabelDto) throws UserNotFoundException, NullEntryException, NoteNotFoundException;

	void addLabel(String token, String labelId, String noteId) throws NoteNotFoundException, UserNotFoundException, NoteTrashedException, LabelAdditionException;

	List<Label> viewLabels() throws NullEntryException;

	void editOrRemoveLabel(String token,Label labelDto,String choice) throws Exception;

	void deleteOrRestoreNote(String token, String noteId, String choice) throws NoteNotFoundException, UserNotFoundException, UntrashedException, NoteTrashedException;

	void removeLabel(String userId, String labelName) throws NoteNotFoundException, LabelNotFoundException, UserNotFoundException;

	void editLabel(String userId, String labelId, String labelName) throws LabelNotFoundException, UserNotFoundException;
}
