package com.rsi.devjam.utilities;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rsi.devjam.models.Participant;
import com.rsi.devjam.models.Project;
import com.rsi.devjam.models.Team;
import com.rsi.devjam.repository.ParticipantRepository;
import com.rsi.devjam.repository.ProjectRepository;
import com.rsi.devjam.repository.TeamRepository;
import com.rsi.slack.MyEvent;

@Component
public class TeamCommands extends BaseCommand {

	@Autowired
	TeamRepository teamRepository;

	@Autowired
	ParticipantRepository particpantRepository;
	
	@Autowired
	ProjectRepository projectRepository;

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
			List<Team> teams = teamRepository.findByLead_User(currentUser.getUser());
			Team t = teams.get(0);
			if(t != null) {
				List<Project> projects = projectRepository.findByUniqueIdentifier(t.getProject().getUniqueIdentifier());
				Project p = projects.get(0);
				p.setTeamLead(null);
				p.setClaimed(false);
				projectRepository.save(p);
			}
			teamRepository.deleteAll(teams);
			currentUser.setTeamLead(false);
			particpantRepository.save(currentUser);
			return "Thank you <@" + currentUser.getUser() + ">. You are no longer a team lead.";
		}
		return null;
	}

	public CompositeResponse addTeamNameInit(MyEvent event) {
		StringBuilder output = new StringBuilder();
		boolean errorOccured = false;
		if (validateInput(event)) {
			Participant currentUser = particpantRepository.findByUser(event.getUserId());
			if (!currentUser.isTeamLead()) {
				errorOccured = true;
				output.append("*You must be a team lead to remove a team member*\n");
			} else {
				output.append(
						"Thank you <@" + currentUser.getUser() + ">. What would you like the name of your team to be?");
			}
			return new CompositeResponse(output.toString(), errorOccured);
		}
		return null;
	}

	public String addTeamNameFinal(MyEvent event) {
		StringBuilder output = new StringBuilder();
		if (validateInput(event)) {
			Participant currentUser = particpantRepository.findByUser(event.getUserId());

			String name = event.getText();
			if (Strings.isNotBlank(name)) {

				List<Team> teams = teamRepository.findByLead_User(currentUser.getUser());
				Team t = teams.get(0);
				t.setName(name);
				teamRepository.save(t);
				output.append("Great, your team name has been updated\n");
			}

			return output.toString();
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
					String teamName = team.getLead().getName() + "'s Team";
					output.append("*Team #" + n++ + ":*\n");
					if (Strings.isNotBlank(team.getName())) {
						teamName = "Team Name: " + team.getName();
						output.append(String.format("* - %s*\n", new Object[] { teamName }));
						output.append("* - Team Lead: " + team.getLead().getEmail() + "*\n");
					} else {
						output.append(String.format("*	- %s*\n", new Object[] { teamName }));
					}

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

	public CompositeResponse removeTeamMemberInit(MyEvent event) {
		StringBuilder output = new StringBuilder();
		boolean errorOccured = false;
		if (validateInput(event)) {
			Participant currentUser = particpantRepository.findByUser(event.getUserId());
			if (!currentUser.isTeamLead()) {
				errorOccured = true;
				output.append("*You must be a team lead to remove a team member*\n");
			} else {
				output.append(
						"Thank you <@" + currentUser.getUser() + ">. Who would you like to remove from your team?");
			}
			return new CompositeResponse(output.toString(), errorOccured);
		}
		return null;
	}

	public String removeTeamMemberFinal(MyEvent event) {
		StringBuilder output = new StringBuilder();
		if (validateInput(event)) {
			String userId = event.getText();
			String parsedUserId = userId.substring(userId.indexOf('@') + 1, userId.length() - 1);
			System.out.println("removing team member: " + parsedUserId);
			Participant currentUser = particpantRepository.findByUser(event.getUserId());
			miscCommands.upsertUser(parsedUserId);

			Participant userToRemove = particpantRepository.findByUser(parsedUserId);
			if (userToRemove != null) {

				List<Team> teams = teamRepository.findByLead_User(currentUser.getUser());
				Team t = teams.get(0);
				List<Participant> participants = t.getParticipants();
				List<Participant> newParticipants = new LinkedList<Participant>();

				if (participants != null) {
					for (Participant p : participants) {
						if (!p.getName().equals(userToRemove.getName())) {
							newParticipants.add(p);
						}
					}
				}

				t.setParticipants(newParticipants);
				teamRepository.save(t);
				output.append("They have been removed from your team.\n");
			} else {
				output.append("Sorry, that user cannot be found.\n");
			}

			return output.toString();
		}
		return null;
	}

	public CompositeResponse addTeamMembersInit(MyEvent event) {
		StringBuilder output = new StringBuilder();
		boolean errors = false;
		if (validateInput(event)) {
			Participant currentUser = particpantRepository.findByUser(event.getUserId());
			if (!currentUser.isTeamLead()) {
				output.append("*You must be a team lead to add team members*\n");
				errors = true;
			} else {
				output.append("Thank you <@" + currentUser.getUser()
						+ ">. Who would you like to add? Mention as many usernames as you would like.");
			}
			return new CompositeResponse(output.toString(), errors);
		}
		return null;
	}

	public String addTeamMembersFinal(MyEvent event) {
		StringBuilder output = new StringBuilder();
		if (validateInput(event)) {
			String userIds = event.getText();
			String[] slackIds = userIds.split("<");
			System.out.println("size: " + slackIds.length);
			for (String userId : slackIds) {
				System.out.println("user id: " + userId);
			}
			boolean foundInvalids = false;
			boolean foundAny = false;
			Team teamInQuestion = null;
			for (int i = 1; i < slackIds.length; i++) {
				String userId = slackIds[i].trim();
				String parsedUserId = userId.substring(1, userId.indexOf(">"));
				System.out.println("parsedUserId: " + parsedUserId);

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
					teamInQuestion = t;
					teamRepository.save(t);
					foundAny = true;
				} else {
					foundInvalids = true;
				}
			}
			// filter out dupes
			if (teamInQuestion != null) {
				List<Participant> participants = teamInQuestion.getParticipants();
				Set<Participant> set = participants.stream().collect(
						Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Participant::getUser))));
				List<Participant> filteredParticipants = new LinkedList<Participant>();
				filteredParticipants.addAll(set);
				teamInQuestion.setParticipants(filteredParticipants);
				teamRepository.save(teamInQuestion);
			}

			// adjust return message based on errors found
			if (!foundAny) {
				output.append("Sorry, none of the users specified could be found.\n");
			} else if (foundInvalids) {
				output.append("Great, the valid usernames have been added to your team.\n");

			} else {
				output.append("Great, they have been added to your team.\n");
			}

			return output.toString();
		}
		return null;
	}
}
