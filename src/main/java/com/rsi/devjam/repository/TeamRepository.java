package com.rsi.devjam.repository;

import org.springframework.data.repository.CrudRepository;

import com.rsi.devjam.models.Team;

public interface TeamRepository extends CrudRepository<Team, String> {
	
}