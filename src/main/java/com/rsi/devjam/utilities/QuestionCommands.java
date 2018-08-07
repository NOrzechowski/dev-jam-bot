package com.rsi.devjam.utilities;

import org.springframework.stereotype.Component;

import me.ramswaroop.jbot.core.slack.models.Event;

@Component
public class QuestionCommands extends BaseCommand {
	public String askQuestion(Event event) {
		if (validateInput(event)) {

		}
		return null;
	}
	
	public String addQuestion(Event event) {
		if (validateInput(event)) {

		}
		return null;
	}
	
	public String answerQuestion(Event event) {
		if (validateInput(event)) {

		}
		return null;
	}
}
