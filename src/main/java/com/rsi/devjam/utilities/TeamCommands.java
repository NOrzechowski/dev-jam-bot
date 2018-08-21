package com.rsi.devjam.utilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rsi.devjam.models.Participant;
import com.rsi.devjam.models.Team;
import com.rsi.devjam.repository.ParticipantRepository;
import com.rsi.devjam.repository.TeamRepository;
import com.rsi.slack.MyEvent;

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
			return "Thank you <@" + currentUser.getUser() + ">. You are now marked as looking for a Dev Jam Team.";
		}

		return null;
	}

	public String noLongerLookingForTeamCommandResponse(MyEvent event) {
		if (validateInput(event)) {
			Participant currentUser = particpantRepository.findByUser(event.getUserId());
			currentUser.setLookingForTeam(false);
			particpantRepository.save(currentUser);
			return "Thank you <@" + currentUser.getUser()
					+ ">. You are no longer marked as looking for a Dev Jam Team.";
		}

		return null;
	}

	public String findATeamMember(MyEvent event) {
		StringBuilder output = new StringBuilder();
		if (validateInput(event)) {
			Iterable<Participant> currentParticipants = particpantRepository.findAll();
			output.append("*These are the current participants looking for a team:*\n");
			currentParticipants.forEach(member -> {
				if (member.isLookingForTeam()) {
					output.append("		- " + member.getName() + " (" + member.getEmail() + ") \n");
				}
			});
			return output.toString();
		}
		return null;
	}

	public String becomeTeamLead(MyEvent event) {
		if (validateInput(event)) {
			Participant currentUser = particpantRepository.findByUser(event.getUserId());
			if (!currentUser.isTeamLead()) {
				currentUser.setTeamLead(true);
				Team team = new Team();
				team.setLead(currentUser);
				teamRepository.save(team);
				particpantRepository.save(currentUser);
				return "Thank you <@" + currentUser.getUser() + ">. You are now a team lead!";
			} else {
				return "Thanks <@" + currentUser.getUser() + ">. You are already a team lead.";
			}
		}

		return null;
	}

	public String stopBeingTeamLead(MyEvent event) {
		if (validateInput(event)) {
			Participant currentUser = particpantRepository.findByUser(event.getUserId());
			currentUser.setLookingForTeam(false);
			particpantRepository.save(currentUser);
			teamRepository.deleteByLead(currentUser);
			return "Thank you <@" + currentUser.getUser() + ">. You are no longer a team lead.";
		}
		return null;
	}

	public String currentTeams(MyEvent event) {
		StringBuilder output = new StringBuilder();
		if (validateInput(event)) {
			Iterable<Team> currentTeams = teamRepository.findAll();
			output.append("*These are the current teams participating in DevJam:*\n");
			currentTeams.forEach(team -> {
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
