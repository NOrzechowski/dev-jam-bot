package com.rsi.devjam.utilities;

import org.springframework.stereotype.Component;

import com.rsi.slack.MyEvent;

@Component
public class ParticipantCommands extends BaseCommand {

	public String getParticipantCommandsResponse(MyEvent event) {
		if (validateInput(event)) {

		}

		return null;
	}
}
