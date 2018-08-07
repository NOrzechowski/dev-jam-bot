package com.rsi.devjam.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.rsi.devjam.models.Command;

public interface CommandRepository extends CrudRepository<Command, String> {

	List<Command> findByUserIdOrderByUpdatedDesc(String userId);

}