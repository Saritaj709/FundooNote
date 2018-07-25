package com.bridgelabz.fundonotes.note.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.bridgelabz.fundonotes.note.model.CreateLabelDTO;
import com.bridgelabz.fundonotes.note.model.Label;

@Repository
public interface LabelRepository extends MongoRepository<Label,String>{

	Optional<Label> findByLabelId(String labelId);
	
	Optional<Label> findByLabelName(String labelId);

	void save(CreateLabelDTO createLabelDto);

	void deleteByLabelId(String labelId);

}
