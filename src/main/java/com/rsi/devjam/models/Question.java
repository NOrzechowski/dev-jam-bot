package com.rsi.devjam.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "questions")
public class Question {
	@Id
	String id;

	private String question;
	private String answer; // to be filled in by a member of the project
							// committe
	private boolean is8Ball;

	private Participant askedBy;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public boolean isIs8Ball() {
		return is8Ball;
	}

	public void setIs8Ball(boolean is8Ball) {
		this.is8Ball = is8Ball;
	}

	public Participant getAskedBy() {
		return askedBy;
	}

	public void setAskedBy(Participant askedBy) {
		this.askedBy = askedBy;
	}

}