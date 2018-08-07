package com.rsi.devjam.repository;

import org.springframework.data.repository.CrudRepository;

import com.rsi.devjam.models.Participant;

public interface ParticipantRepository extends CrudRepository<Participant, String> {
	
	Integer countByUser(String userId);
	
	Participant findByUser(String userId);

}