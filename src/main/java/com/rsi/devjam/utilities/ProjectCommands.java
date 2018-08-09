package com.rsi.devjam.utilities;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rsi.devjam.models.Participant;
import com.rsi.devjam.models.Project;
import com.rsi.devjam.repository.CommandRepository;
import com.rsi.devjam.repository.ParticipantRepository;
import com.rsi.devjam.repository.ProjectRepository;

import me.ramswaroop.jbot.core.slack.models.Event;

@Component
public class ProjectCommands extends BaseCommand {

	@Autowired
	CommandRepository commandRepository;

	@Autowired
	ProjectRepository projectRepository;

	@Autowired
	ParticipantRepository particpantRepository;

	private static String ASTERISKS = "*********************************************************\n";
	private static String DASHES = "------------------------------";
	private static String SPACE = "        ";

	private String getProjectId() {
		int leftLimit = 97; // letter 'a'
		int rightLimit = 122; // letter 'z'
		int targetStringLength = 10;
		Random random = new Random();
		StringBuilder buffer = new StringBuilder(targetStringLength);
		for (int i = 0; i < targetStringLength; i++) {
			int randomLimitedInt = leftLimit + (int) (random.nextFloat() * (rightLimit - leftLimit + 1));
			buffer.append((char) randomLimitedInt);
		}
		String generatedString = buffer.toString();
		return generatedString;
	}

	public String getProjects(Event event) {
		StringBuilder output = new StringBuilder();
		if (validateInput(event)) {
			Iterable<Project> currentProjects = projectRepository.findAll();
			output.append(ASTERISKS);
			output.append("*These are the current project ideas:*\n");
			output.append(ASTERISKS);
			if (currentProjects.iterator().hasNext()) {
				currentProjects.forEach(project -> {
					// idea
					output.append(SPACE).append("*Project Idea:* " + project.getSummary() + "\n");

					// project id
					output.append(SPACE).append("*ID:* " + project.getUniqueIdentifier() + "\n");

					// project lead
					if (project.isClaimed()) {
						output.append(SPACE).append("*Project Lead:* " + project.getTeamLead() + "\n");
					} else {
						output.append(SPACE).append("--- Project is not claimed yet ---\n");
					}

					// submitter
					output.append(SPACE).append("*Submitted by:* " + project.getSubmittedBy().getEmail() + "\n");

					// team info
					StringBuilder teamOutput = new StringBuilder();
					if (project.getTeam() != null) {
						for (Participant member : project.getTeam().getParticipants()) {
							teamOutput.append(" - " + member.getName() + "\n");
						}
						output.append("*Members currently signed up:* " + teamOutput.toString());
					}

					output.append(SPACE).append(DASHES).append("\n");
				});
			} else {
				output.append("- No projects ideas currently submitted -\n");
			}
			output.append(ASTERISKS);
			return output.toString();
		}
		return null;

	}

	public String addProjectIdea(Event event) {
		StringBuilder output = new StringBuilder();
		if (validateInput(event)) {
			Participant currentUser = particpantRepository.findByUser(event.getUserId());
			output.append(String.format(
					"Thanks for submitting a project idea, <@%s>! What is the high level overview of your project?\n",
					new Object[] { currentUser.getUser() }));
			return output.toString();
		}
		return null;
	}

	public String projectWrap(Event event) {
		StringBuilder output = new StringBuilder();

		if (validateInput(event)) {
			Participant currentUser = particpantRepository.findByUser(event.getUserId());
			Project project = new Project();
			project.setSummary(event.getText());
			project.setUniqueIdentifier(getProjectId());
			project.setSubmittedBy(currentUser);
			projectRepository.save(project);
			output.append("Great, your project idea has been submitted.\n");
			return output.toString();
		}
		return null;
	}
}
