package com.rsi.devjam.utilities;

import java.util.LinkedList;
import java.util.List;

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

	@Autowired
	MiscCommands miscCommands;

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
			List<Team> t = teamRepository.findByLead_User(currentUser.getUser());
			teamRepository.deleteAll(t);
			currentUser.setTeamLead(false);
			particpantRepository.save(currentUser);
			return "Thank you <@" + currentUser.getUser() + ">. You are no longer a team lead.";
		}
		return null;
	}

	public String currentTeams(MyEvent event) {
		StringBuilder output = new StringBuilder();
		if (validateInput(event)) {
			Iterable<Team> currentTeams = teamRepository.findAll();
			if (currentTeams.iterator().hasNext()) {
				output.append("*These are the current teams participating in Dev Jam:*\n");
				output.append(ASTERISKS);
				int n = 0;
				for (Team team : currentTeams) {
					String name = "<@" + team.getLead().getUser() + ">'s Team";
					output.append("*Team #" + n++ + ":*\n");
					output.append(String.format("*	- %s*\n", new Object[] { name }));
					if (team.getParticipants() != null) {
						output.append("*Members: *\n");
						team.getParticipants().forEach(participant -> {
							output.append(String.format("~ %s \n", new Object[] { participant.getEmail() }));
						});
					}
					output.append(DASHES + "\n");
				}
				;
			} else {
				output.append("*There are no teams signed up for Dev Jam yet.*\n");
			}
			output.append(ASTERISKS);
			return output.toString();
		}
		return null;
	}

	public String addTeamMemberInit(MyEvent event) {
		StringBuilder output = new StringBuilder();
		if (validateInput(event)) {
			Participant currentUser = particpantRepository.findByUser(event.getUserId());
			if (!currentUser.isTeamLead()) {
				output.append("*You must be a team lead to add team members*\n");
			} else {
				output.append("Thank you <@" + currentUser.getUser() + ">. Who would you like to add?");
			}
			return output.toString();
		}
		return null;
	}

	public String addTeamMemberFinal(MyEvent event) {
		StringBuilder output = new StringBuilder();
		if (validateInput(event)) {
			String userId = event.getText();
			String parsedUserId = userId.substring(userId.indexOf('@') + 1, userId.length() - 1);
			System.out.println("parsed user id: " + parsedUserId);

			Participant currentUser = particpantRepository.findByUser(event.getUserId());
			miscCommands.upsertUser(parsedUserId);

			Participant userToAdd = particpantRepository.findByUser(parsedUserId);
			if (userToAdd != null) {
				userToAdd.setLookingForTeam(false);
				particpantRepository.save(userToAdd);

				List<Team> teams = teamRepository.findByLead_User(currentUser.getUser());
				Team t = teams.get(0);
				List<Participant> participants = t.getParticipants();
				if (participants == null) {
					participants = new LinkedList<Participant>();
				}

				participants.add(userToAdd);
				t.setParticipants(participants);
				teamRepository.save(t);
				output.append("Great, they have been added to your team.\n");
			} else {
				output.append("Sorry, that user cannot be found.\n");
			}

			return output.toString();
		}
		return null;
	}

	public String removeTeamMemberInit(MyEvent event) {
		StringBuilder output = new StringBuilder();
		if (validateInput(event)) {

			return output.toString();
		}
		return null;
	}

	public String removeTeamMemberFinal(MyEvent event) {
		StringBuilder output = new StringBuilder();
		if (validateInput(event)) {

			return output.toString();
		}
		return null;
	}
}
