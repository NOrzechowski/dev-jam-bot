package com.rsi.devjam.repository;

import org.springframework.data.repository.CrudRepository;

import com.rsi.devjam.models.Question;

public interface QuestionRepository extends CrudRepository<Question, String> {
	
}