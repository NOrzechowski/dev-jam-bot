package com.rsi.devjam.utilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.togglz.core.manager.FeatureManager;

import com.rsi.devjam.models.Participant;
import com.rsi.devjam.models.Team;
import com.rsi.devjam.repository.ParticipantRepository;
import com.rsi.devjam.repository.TeamRepository;

import me.ramswaroop.jbot.core.slack.models.Event;
import me.ramswaroop.jbot.core.slack.models.User;

@Component
public class TeamCommands extends BaseCommand {
	@Autowired
	private FeatureManager manager;

	@Autowired
	TeamRepository teamRepository;

	@Autowired
	ParticipantRepository particpantRepository;

	public String lookForTeamCommandResponse(Event event) {
		if (manager.isActive(Features.LOOK_FOR_TEAM)) {

			if (validateInput(event)) {
				Participant currentUser = particpantRepository.findByUser(event.getUserId());
				User userData = getUser(event);

				String username = userData.getProfile().getRealName();
				if (currentUser == null) {
					currentUser = new Participant(event.getUserId(), username);
				}
				currentUser.setName(username);
				currentUser.setEmail(userData.getProfile().getEmail());
				currentUser.setLookingForTeam(true);
				particpantRepository.save(currentUser);

				return "Thank you " + currentUser.getName() + ". You are now marked as looking for a Dev Jam Team.";
			}
		}

		return null;
	}

	public String findATeamMember(Event event) {
		StringBuilder output = new StringBuilder();
		if (manager.isActive(Features.FIND_TEAM_MEMBER)) {

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
		}
		return null;
	}

	public String currentTeams(Event event) {
		StringBuilder output = new StringBuilder();
		if (manager.isActive(Features.CURRENT_TEAMS)) {
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
		}
		return null;
	}
}
