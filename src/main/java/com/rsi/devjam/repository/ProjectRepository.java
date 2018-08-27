package com.rsi.devjam.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.rsi.devjam.models.Project;

public interface ProjectRepository extends CrudRepository<Project, String> {
	
	List<Project> findBySummary(String summary);
	
	List<Project> findByUniqueIdentifier(String uniqueIdentifier);
	
}