package com.rsi.devjam.utilities;

import org.springframework.stereotype.Component;

import me.ramswaroop.jbot.core.slack.models.Event;

@Component
public class ParticipantCommands extends BaseCommand {

	public String getParticipantCommandsResponse(Event event) {
		if (validateInput(event)) {

		}

		return null;
	}
}
