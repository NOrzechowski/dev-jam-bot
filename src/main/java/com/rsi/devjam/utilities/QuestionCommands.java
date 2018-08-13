package com.rsi.devjam.utilities;

import org.springframework.stereotype.Component;

import com.rsi.slack.MyEvent;

@Component
public class QuestionCommands extends BaseCommand {
	public String askQuestion(MyEvent event) {
		if (validateInput(event)) {

		}
		return null;
	}
	
	public String addQuestion(MyEvent event) {
		if (validateInput(event)) {

		}
		return null;
	}
	
	public String answerQuestion(MyEvent event) {
		if (validateInput(event)) {

		}
		return null;
	}
}
