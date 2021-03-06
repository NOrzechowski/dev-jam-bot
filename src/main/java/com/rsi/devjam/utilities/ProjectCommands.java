package com.rsi.devjam.utilities;

import java.util.List;
import java.util.Objects;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.rsi.devjam.models.Participant;
import com.rsi.devjam.models.Project;
import com.rsi.devjam.models.Team;
import com.rsi.devjam.repository.CommandRepository;
import com.rsi.devjam.repository.ParticipantRepository;
import com.rsi.devjam.repository.ProjectRepository;
import com.rsi.devjam.repository.TeamRepository;
import com.rsi.slack.MyEvent;

import me.ramswaroop.jbot.core.slack.models.Event;

@Component
public class ProjectCommands extends BaseCommand {

	@Autowired
	CommandRepository commandRepository;

	@Autowired
	ProjectRepository projectRepository;

	@Autowired
	ParticipantRepository particpantRepository;

	@Autowired
	TeamRepository teamRepository;

	@Value("${projectSubmissionUrl}")
	private String projectSubmissionUrl;

	@Value("${projectComitteeMembers}")
	private String projectComitteeMembers;

	private String generateProjectId() {
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

	public String getProjects(MyEvent event) {
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
					if (project.isClaimed() && project.getTeamLead() != null) {
						output.append(SPACE).append("*Project Claimed By:* " + project.getTeamLead().getEmail() + "\n");
					} else {
						output.append(SPACE).append("--- Project is not claimed yet ---\n");
					}

					// submitter
					if (!Objects.isNull(project.getSubmittedBy()))
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

	public String addProjectIdea(MyEvent event) {
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
		System.out.println("event user id: " + event.getUserId());
		if (validateInput(event)) {
			Participant currentUser = particpantRepository.findByUser(event.getUserId());
			Project project = new Project();
			project.setSummary(event.getText());
			project.setUniqueIdentifier(generateProjectId());
			project.setSubmittedBy(currentUser);
			projectRepository.save(project);
			output.append("Great, your project idea has been submitted.\n");
			return output.toString();
		}
		return null;
	}

	public CompositeResponse claimProject(MyEvent event) {
		String couldNotBeFound = "Sorry, that project could not be found.\n";
		boolean errorsFound = false;
		StringBuilder output = new StringBuilder();
		if (validateInput(event)) {
			Participant currentUser = particpantRepository.findByUser(event.getUserId());
			if (!currentUser.isTeamLead()) {
				output.append("*You must be a team lead to claim a project.*\n");
			} else {
				List<Team> teams = teamRepository.findByLead_User(currentUser.getUser());
				Team currentTeam = teams.get(0);

				// set new claimed project
				String[] entries = event.getText().split(" ");
				if (entries.length >= 1) {
					// unset existing claimed project
					if (currentTeam.getProject() != null) {
						List<Project> existingProjects = projectRepository
								.findByUniqueIdentifier(currentTeam.getProject().getUniqueIdentifier());

						Project p = existingProjects.get(0);
						p.setTeamLead(null);
						p.setClaimed(false);
						projectRepository.save(p);

						currentTeam.setProject(null);
						teamRepository.save(currentTeam);
					}

					String pId = entries[1].trim();
					List<Project> projects = projectRepository.findByUniqueIdentifier(pId);
					if (!projects.isEmpty()) {
						Project project = projects.get(0);
						if (project.isClaimed()) {
							output.append("*Sorry, that project is already claimed.*\n");
						} else {
							project.setClaimed(true);
							project.setTeamLead(currentUser);
							projectRepository.save(project);

							currentTeam.setProject(project);
							teamRepository.save(currentTeam);
							output.append(
									"*Thanks! In order to fully submit your team you must fill out the following form:* "
											+ projectSubmissionUrl + "\n");
							output.append(
									"- If you need additional help, feel free to reach out to a member of the project comittee ("
											+ projectComitteeMembers + ")\n");
						}
					} else {
						output.append(couldNotBeFound);
					}
				} else {
					// nothing for now
				}
			}

			return new CompositeResponse(output.toString(), errorsFound);
		}
		return null;
	}

	public CompositeResponse projectProposal(MyEvent event) {
		boolean errorsFound = false;
		StringBuilder output = new StringBuilder();
		if (validateInput(event)) {
			Participant currentUser = particpantRepository.findByUser(event.getUserId());
			if (!currentUser.isTeamLead()) {
				output.append("*You must be a team lead to complete the project proposal form.*\n");
			} else {
				List<Team> teams = teamRepository.findByLead_User(currentUser.getUser());
				Team currentTeam = teams.get(0);
				currentTeam.setProposedProject(true);
				teamRepository.save(currentTeam);
				output.append("*In order to fully submit your team you must fill out the following form:* "
						+ projectSubmissionUrl + "\n");
				output.append(
						"- If you need additional help, feel free to reach out to a member of the project comittee ("
								+ projectComitteeMembers + ")\n"); 
			}
			return new CompositeResponse(output.toString(), errorsFound);
		}
		return null;
	}
}
