package com.bridgelabz.fundonotes.note.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.bridgelabz.fundonotes.note.model.CreateLabelDTO;
import com.bridgelabz.fundonotes.note.model.LabelDTO;

@Repository
public interface LabelRepository extends MongoRepository<LabelDTO,String>{

	Optional<LabelDTO> findByLabelId(String labelId);

	List<LabelDTO> findByLabelName(String labelId);

	void save(CreateLabelDTO createLabelDto);

	void deleteByLabelId(String labelId);

}
