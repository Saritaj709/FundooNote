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
import com.bridgelabz.fundonotes.note.exception.NoteUnArchievedException;
import com.bridgelabz.fundonotes.note.exception.NoteUnPinnedException;
import com.bridgelabz.fundonotes.note.exception.NullEntryException;
import com.bridgelabz.fundonotes.note.exception.UntrashedException;
import com.bridgelabz.fundonotes.note.exception.UserNotFoundException;
import com.bridgelabz.fundonotes.note.model.CreateDTO;
import com.bridgelabz.fundonotes.note.model.CreateLabelDTO;
import com.bridgelabz.fundonotes.note.model.Label;
import com.bridgelabz.fundonotes.note.model.Note;
import com.bridgelabz.fundonotes.note.model.UpdateDTO;
import com.bridgelabz.fundonotes.note.model.ViewNoteDTO;

public interface NoteService {
	
	/**
	 * 
	 * @param token
	 * @param create
	 * @return Note
	 * @throws NoteNotFoundException
	 * @throws NoteCreationException
	 * @throws UserNotFoundException
	 * @throws DateException
	 * @throws LabelNotFoundException
	 * @throws NullEntryException
	 */
	Note createNote(String token,CreateDTO create) throws NoteNotFoundException, NoteCreationException, UserNotFoundException, DateException, LabelNotFoundException, NullEntryException;

	/**
	 * 
	 * @param token
	 * @param update
	 * @throws NoteNotFoundException
	 * @throws UserNotFoundException
	 * @throws NoteTrashedException
	 */
	void updateNote(String token,UpdateDTO update) throws NoteNotFoundException, UserNotFoundException, NoteTrashedException;

	/**
	 * 
	 * @return List of Notes of A Particular user
	 * @throws NullEntryException
	 */
	List<ViewNoteDTO> readAllNotes() throws NullEntryException;
	
	/**
	 * 
	 * @param token
	 * @param noteId
	 * @return List of ViewNoteDTO
	 * @throws UserNotFoundException
	 * @throws NoteNotFoundException
	 * @throws NoteTrashedException
	 */
	ViewNoteDTO findNoteById(String token,String noteId) throws UserNotFoundException, NoteNotFoundException, NoteTrashedException;
    
	/**
	 * 
	 * @param token
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UserNotFoundException
	 * @throws UntrashedException
	 * @throws NoteTrashedException
	 */
	void deleteNoteForever(String token,String noteId) throws NoteNotFoundException, UserNotFoundException, UntrashedException, NoteTrashedException;

	/**
	 * 
	 * @param token
	 * @param date
	 * @param noteId
	 * @return boolean 
	 * @throws UserNotFoundException
	 * @throws NoteNotFoundException
	 * @throws NoteTrashedException
	 * @throws DateException
	 */
	boolean addReminder(String token,Date date,String noteId) throws UserNotFoundException, NoteNotFoundException, NoteTrashedException, DateException;

	/**
	 * 
	 * @param token
	 * @param noteId
	 * @throws NullEntryException
	 * @throws UserNotFoundException
	 * @throws NoteNotFoundException
	 * @throws NoteTrashedException
	 */
	void deleteReminder(String token,String noteId) throws NullEntryException, UserNotFoundException, NoteNotFoundException, NoteTrashedException;

	/**
	 * 
	 * @param token
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UserNotFoundException
	 * @throws NoteArchievedException
	 * @throws NoteTrashedException
	 * @throws NoteUnArchievedException 
	 */
	void archieveOrUnArchieveNote(String token, String noteId,boolean choice) throws NoteNotFoundException, UserNotFoundException, NoteArchievedException, NoteTrashedException, NoteUnArchievedException;

	/**
	 * 
	 * @param token
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UserNotFoundException
	 * @throws NotePinnedException
	 * @throws NoteTrashedException
	 * @throws NoteUnPinnedException 
	 */
	void pinOrUnpinNote(String userId, String noteId,boolean choice) throws NoteNotFoundException, UserNotFoundException, NotePinnedException, NoteTrashedException, NoteUnPinnedException;

	/**
	 * 
	 * @return List of Archived Notes
	 * @throws NullEntryException
	 */
	List<ViewNoteDTO> viewArchieved() throws NullEntryException;

	/**
	 * 
	 * @return List of Pinned Notes
	 * @throws NullEntryException
	 */
	List<ViewNoteDTO> viewPinned() throws NullEntryException;

	/**
	 * 
	 * @return List of Trashed Notes
	 * @throws NullEntryException
	 */
	List<ViewNoteDTO> viewTrashed() throws NullEntryException;

	/**
	 * 
	 * @param token
	 * @param createLabelDto
	 * @return Label
	 * @throws UserNotFoundException
	 * @throws NullEntryException
	 * @throws NoteNotFoundException
	 */
	Label createLabel(String token, CreateLabelDTO createLabelDto) throws UserNotFoundException, NullEntryException, NoteNotFoundException;

	/**
	 * 
	 * @param token
	 * @param labelId
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UserNotFoundException
	 * @throws NoteTrashedException
	 * @throws LabelAdditionException
	 */
	void addLabel(String token, String labelId, String noteId) throws NoteNotFoundException, UserNotFoundException, NoteTrashedException, LabelAdditionException;

	/**
	 * 
	 * @return List of Labels
	 * @throws NullEntryException
	 */
	List<Label> viewLabels() throws NullEntryException;

	/**
	 * 
	 * @param token
	 * @param noteId
	 * @param choice
	 * @throws NoteNotFoundException
	 * @throws UserNotFoundException
	 * @throws UntrashedException
	 * @throws NoteTrashedException
	 */
	void deleteOrRestoreNote(String token, String noteId, boolean choice) throws NoteNotFoundException, UserNotFoundException, UntrashedException, NoteTrashedException;

	/**
	 * 
	 * @param userId
	 * @param labelName
	 * @throws NoteNotFoundException
	 * @throws LabelNotFoundException
	 * @throws UserNotFoundException
	 */
	void removeLabel(String userId, String labelName) throws NoteNotFoundException, LabelNotFoundException, UserNotFoundException;

	/**
	 * 
	 * @param userId
	 * @param labelId
	 * @param labelName
	 * @throws LabelNotFoundException
	 * @throws UserNotFoundException
	 */
	void editLabel(String userId, String labelId, String labelName) throws LabelNotFoundException, UserNotFoundException;

	/**
	 * 
	 * @param userId
	 * @param labelId
	 * @return List of ViewNoteDTO
	 * @throws LabelNotFoundException
	 * @throws UserNotFoundException
	 * @throws NoteNotFoundException
	 */
	List<ViewNoteDTO> viewLabel(String userId,String labelId) throws LabelNotFoundException, UserNotFoundException, NoteNotFoundException;
    
	/**
	 * 
	 * @param userId
	 * @param noteId
	 * @param labelId
	 * @throws LabelNotFoundException
	 * @throws NoteNotFoundException
	 * @throws UserNotFoundException
	 */
	void removeLabelFromNote(String userId, String noteId, String labelId) throws LabelNotFoundException, NoteNotFoundException, UserNotFoundException;

	/**
	 * 
	 * @param userId
	 * @return List of ViewNoteDTO
	 * @throws NullEntryException
	 */
	List<ViewNoteDTO> readUserNotes(String userId) throws NullEntryException;

	/**
	 * 
	 * @param userId
	 * @param color
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UserNotFoundException
	 * @throws NoteTrashedException
	 */
	void addColor(String userId, String color, String noteId) throws NoteNotFoundException, UserNotFoundException, NoteTrashedException;

}
