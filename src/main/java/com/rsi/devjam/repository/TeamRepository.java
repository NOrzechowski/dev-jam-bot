package com.rsi.devjam.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.rsi.devjam.models.Participant;
import com.rsi.devjam.models.Team;

public interface TeamRepository extends CrudRepository<Team, String> {
	List<Team> findByLead(Participant lead);
	
	void deleteByLead(Participant lead);
}