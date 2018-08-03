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
import com.bridgelabz.fundonotes.note.exception.NullValueException;
import com.bridgelabz.fundonotes.note.exception.UntrashedException;
import com.bridgelabz.fundonotes.note.exception.UnAuthorizedException;
import com.bridgelabz.fundonotes.note.model.CreateDTO;
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
	 * @throws UnAuthorizedException
	 * @throws DateException
	 * @throws LabelNotFoundException
	 * @throws NullValueException
	 */
	ViewNoteDTO createNote(String token,CreateDTO create) throws NoteNotFoundException, NoteCreationException, UnAuthorizedException, DateException, LabelNotFoundException, NullValueException;

	/**
	 * 
	 * @param token
	 * @param update
	 * @throws NoteNotFoundException
	 * @throws UnAuthorizedException
	 * @throws NoteTrashedException
	 */
	void updateNote(String token,UpdateDTO update) throws NoteNotFoundException, UnAuthorizedException, NoteTrashedException;

	/**
	 * 
	 * @return List of Notes of A Particular user
	 * @throws NullValueException
	 */
	List<ViewNoteDTO> readAllNotes() throws NullValueException;
	
	/**
	 * 
	 * @param token
	 * @param noteId
	 * @return List of ViewNoteDTO
	 * @throws UnAuthorizedException
	 * @throws NoteNotFoundException
	 * @throws NoteTrashedException
	 */
	ViewNoteDTO findNoteById(String token,String noteId) throws UnAuthorizedException, NoteNotFoundException, NoteTrashedException;
    
	/**
	 * 
	 * @param token
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnAuthorizedException
	 * @throws UntrashedException
	 * @throws NoteTrashedException
	 */
	void deleteNoteForever(String token,String noteId) throws NoteNotFoundException, UnAuthorizedException, UntrashedException, NoteTrashedException;

	/**
	 * 
	 * @param token
	 * @param date
	 * @param noteId
	 * @return boolean 
	 * @throws UnAuthorizedException
	 * @throws NoteNotFoundException
	 * @throws NoteTrashedException
	 * @throws DateException
	 */
	void addReminder(String token,Date date,String noteId) throws UnAuthorizedException, NoteNotFoundException, NoteTrashedException, DateException;

	/**
	 * 
	 * @param token
	 * @param noteId
	 * @throws NullValueException
	 * @throws UnAuthorizedException
	 * @throws NoteNotFoundException
	 * @throws NoteTrashedException
	 */
	void deleteReminder(String token,String noteId) throws NullValueException, UnAuthorizedException, NoteNotFoundException, NoteTrashedException;

	/**
	 * 
	 * @param token
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnAuthorizedException
	 * @throws NoteArchievedException
	 * @throws NoteTrashedException
	 * @throws NoteUnArchievedException 
	 */
	void archieveOrUnArchieveNote(String token, String noteId,boolean choice) throws NoteNotFoundException, UnAuthorizedException, NoteArchievedException, NoteTrashedException, NoteUnArchievedException;

	/**
	 * 
	 * @param token
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnAuthorizedException
	 * @throws NotePinnedException
	 * @throws NoteTrashedException
	 * @throws NoteUnPinnedException 
	 */
	void pinOrUnpinNote(String userId, String noteId,boolean choice) throws NoteNotFoundException, UnAuthorizedException, NotePinnedException, NoteTrashedException, NoteUnPinnedException;

	/**
	 * 
	 * @return List of Archived Notes
	 * @throws NullValueException
	 */
	List<ViewNoteDTO> viewArchieved(String userId) throws NullValueException;

	/**
	 * 
	 * @return List of Pinned Notes
	 * @throws NullValueException
	 */
	List<ViewNoteDTO> viewPinned(String userId) throws NullValueException;

	/**
	 * 
	 * @return List of Trashed Notes
	 * @throws NullValueException
	 */
	List<ViewNoteDTO> viewTrashed(String userId) throws NullValueException;

	/**
	 * 
	 * @param token
	 * @param labelName
	 * @return Label
	 * @throws UnAuthorizedException
	 * @throws NullValueException
	 * @throws NoteNotFoundException
	 *//*
	LabelDTO createLabel(String token, String labelName) throws UnAuthorizedException, NullValueException, NoteNotFoundException;

	*//**
	 * 
	 * @param token
	 * @param labelId
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnAuthorizedException
	 * @throws NoteTrashedException
	 * @throws LabelAdditionException
	 */
	void addLabel(String token, String labelId, String noteId) throws NoteNotFoundException, UnAuthorizedException, NoteTrashedException, LabelAdditionException;

	/**
	 * 
	 * @return List of Labels
	 * @throws NullValueException
	 *//*
	List<Label> viewLabels() throws NullValueException;

	*//**
	 * 
	 * @param token
	 * @param noteId
	 * @param choice
	 * @throws NoteNotFoundException
	 * @throws UnAuthorizedException
	 * @throws UntrashedException
	 * @throws NoteTrashedException
	 */
	void deleteOrRestoreNote(String token, String noteId, boolean choice) throws NoteNotFoundException, UnAuthorizedException, UntrashedException, NoteTrashedException;

	/**
	 * 
	 * @param userId
	 * @param labelName
	 * @throws NoteNotFoundException
	 * @throws LabelNotFoundException
	 * @throws UnAuthorizedException
	 *//*
	void removeLabel(String userId, String labelName) throws NoteNotFoundException, LabelNotFoundException, UnAuthorizedException;

	*//**
	 * 
	 * @param userId
	 * @param labelId
	 * @param labelName
	 * @throws LabelNotFoundException
	 * @throws UnAuthorizedException
	 *//*
	void editLabel(String userId, String labelId, String labelName) throws LabelNotFoundException, UnAuthorizedException;

	*//**
	 * 
	 * @param userId
	 * @param labelId
	 * @return List of ViewNoteDTO
	 * @throws LabelNotFoundException
	 * @throws UnAuthorizedException
	 * @throws NoteNotFoundException
	 *//*
	List<ViewNoteDTO> viewLabel(String userId,String labelId) throws LabelNotFoundException, UnAuthorizedException, NoteNotFoundException;
    
	*//**
	 * 
	 * @param userId
	 * @param noteId
	 * @param labelId
	 * @throws LabelNotFoundException
	 * @throws NoteNotFoundException
	 * @throws UnAuthorizedException
	 */
	void removeLabelFromNote(String userId, String noteId, String labelId) throws LabelNotFoundException, NoteNotFoundException, UnAuthorizedException;

	/**
	 * 
	 * @param userId
	 * @return List of ViewNoteDTO
	 * @throws NullValueException
	 */
	List<ViewNoteDTO> readUserNotes(String userId) throws NullValueException;

	/**
	 * 
	 * @param userId
	 * @param color
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnAuthorizedException
	 * @throws NoteTrashedException
	 */
	void addColor(String userId, String color, String noteId) throws NoteNotFoundException, UnAuthorizedException, NoteTrashedException;

	/**
	 * 
	 * @param userId
	 * @return LabelDTO
	 * @throws NullValueException 
	 *//*
	List<LabelDTO> viewUserLabels(String userId) throws NullValueException;*/

}
