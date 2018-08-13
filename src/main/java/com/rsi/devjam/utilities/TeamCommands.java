package com.rsi.devjam.utilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rsi.devjam.models.Participant;
import com.rsi.devjam.models.Team;
import com.rsi.devjam.repository.ParticipantRepository;
import com.rsi.devjam.repository.TeamRepository;
import com.rsi.slack.MyEvent;

import me.ramswaroop.jbot.core.slack.models.Event;

@Component
public class TeamCommands extends BaseCommand {

	@Autowired
	TeamRepository teamRepository;

	@Autowired
	ParticipantRepository particpantRepository;

	public String lookForTeamCommandResponse(MyEvent event) {
			if (validateInput(event)) {
				Participant currentUser = particpantRepository.findByUser(event.getUserId());

				currentUser.setLookingForTeam(true);
				particpantRepository.save(currentUser);

				return "Thank you " + currentUser.getName() + ". You are now marked as looking for a Dev Jam Team.";
			}

		return null;
	}

	public String findATeamMember(MyEvent event) {
		StringBuilder output = new StringBuilder();
			if (validateInput(event)) {
				Iterable<Participant> currentParticipants = particpantRepository.findAll();
				currentParticipants.forEach(member -> {
					output.append("*These are the current participants looking for a team:*\n");
					if (member.isLookingForTeam()) {
						output.append("		- " + member.getName() + " (" + member.getEmail() + ") \n");
					}
				});
				return output.toString();
			}
		return null;
	}

	public String currentTeams(MyEvent event) {
		StringBuilder output = new StringBuilder();
			if (validateInput(event)) {
				Iterable<Team> currentTeams = teamRepository.findAll();
				currentTeams.forEach(team -> {
					output.append("*These are the current teams participating in DevJam:*\n");
					output.append(String.format("*	- Team Name: %s:*\n", new Object[] { team.getName() }));
					team.getParticipants().forEach(participant -> {
						output.append(String.format("*	~%s :*\n", new Object[] { participant.getName() }));
					});

				});
				return output.toString();
			}	
		return null;
	}
}
