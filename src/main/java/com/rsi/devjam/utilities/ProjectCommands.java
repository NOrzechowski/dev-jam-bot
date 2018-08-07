package com.rsi.devjam.utilities;

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

	public String getProjects(Event event) {
		StringBuilder output = new StringBuilder();
		if (validateInput(event)) {
			Iterable<Project> currentProjects = projectRepository.findAll();
			output.append("*These are the current Dev Jam projects available:*\n");
			output.append("***************************************************\n");

			currentProjects.forEach(project -> {
				output.append("		*Project Idea:* " + project.getSummary() + "\n");
				if (project.isClaimed()) {
					output.append("		*Project Lead:* " + project.getTeamLead() + "\n");
				} else {
					output.append("		--- Project is not claimed yet! ---\n\n");
				}
				StringBuilder teamOutput = new StringBuilder();
				if (project.getTeam() != null) {
					for (Participant member : project.getTeam().getParticipants()) {
						teamOutput.append(" - " + member.getName() + "\n");
					}
					output.append("*Members currently signed up:* " + teamOutput.toString());
				}

			});
			return output.toString();
		}
		return null;

	}

	public String addProjectIdea(Event event) {
		StringBuilder output = new StringBuilder();
		if (validateInput(event)) {
			Participant currentUser = particpantRepository.findByUser(event.getUserId());
			output.append(String.format(
					"Thanks for submitting a project idea, %s! What is the high level overview of your project?\n",
					new Object[] { currentUser.getName() }));
			return output.toString();
		}
		return null;
	}

	public String projectWrap(Event event) {
		StringBuilder output = new StringBuilder();
		if (validateInput(event)) {
			Project project = new Project();
			project.setSummary(event.getText());
			projectRepository.save(project);
			output.append("Great, your project idea has been submitted.\n");
			return output.toString();
		}
		return null;
	}
}
