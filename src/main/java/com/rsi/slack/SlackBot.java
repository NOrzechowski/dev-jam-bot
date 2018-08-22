package com.rsi.slack;

import java.util.Date;
import java.util.Objects;
import java.util.regex.Matcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.togglz.core.manager.FeatureManager;

import com.rsi.devjam.models.Command;
import com.rsi.devjam.repository.CommandRepository;
import com.rsi.devjam.togglz.Features;
import com.rsi.devjam.utilities.MiscCommands;
import com.rsi.devjam.utilities.ProjectCommands;
import com.rsi.devjam.utilities.TeamCommands;

import me.ramswaroop.jbot.core.slack.Controller;
import me.ramswaroop.jbot.core.slack.EventType;
import me.ramswaroop.jbot.core.slack.models.Event;
import me.ramswaroop.jbot.core.slack.models.Message;

@Component
@EnableAutoConfiguration
public class SlackBot extends MyBot {

	@Autowired
	CommandRepository commandRepository;

	@Autowired
	MiscCommands miscCommands;

	@Autowired
	TeamCommands teamCommands;

	@Autowired
	ProjectCommands projectCommands;

	@Autowired
	private FeatureManager manager;

	@Value("${slackBotToken}")
	private String slackToken;

	private static final String COMMAND_NOT_ENABLED = "Sorry %s, that command is not enabled yet!";

	String lastTs = "";
	
	@Override
	public String getSlackToken() {
		return slackToken;
	}

	@Override
	public MyBot getSlackBot() {
		return this;
	}

	private boolean validateIncomingMessage(MyEvent event, Matcher matcher) {
		return !event.isThreadMessage() && !Objects.isNull(matcher) && !matcher.group(0).isEmpty();

	}

	// *************************** misc commands **************************\\

	@Controller(events = { EventType.MESSAGE, EventType.DIRECT_MESSAGE }, pattern = "(?i)^(!faq)$")
	public void getFaq(WebSocketSession session, MyEvent event, Matcher matcher) {
		if (validateIncomingMessage(event, matcher)) {
			miscCommands.upsertUser(event);
			reply(session, event, new Message(miscCommands.getFaq(event)));
		}
	}

	@Controller(events = { EventType.MESSAGE, EventType.DIRECT_MESSAGE }, pattern = "(?i)^(!help)$")
	public void getHelp(WebSocketSession session, MyEvent event, Matcher matcher) {
		if (validateIncomingMessage(event, matcher)) {
			miscCommands.upsertUser(event);
			reply(session, event, new Message(miscCommands.getHelp(event)));
		}
	}

	@Controller(events = { EventType.MESSAGE, EventType.DIRECT_MESSAGE }, pattern = "(?i)^(!deadlines|!dates)$")
	public void getDeadlines(WebSocketSession session, MyEvent event, Matcher matcher) {
		if (validateIncomingMessage(event, matcher)) {
			if (manager.isActive(Features.DATES)) {
				miscCommands.upsertUser(event);
				reply(session, event, new Message(miscCommands.getDeadlines(event)));
			}
		}
	}

	@Controller(events = { EventType.MESSAGE, EventType.DIRECT_MESSAGE }, pattern = "(?i)^(!rubric|!judgingRubric)$")
	public void getRubric(WebSocketSession session, MyEvent event, Matcher matcher) {
		if (validateIncomingMessage(event, matcher)) {
			miscCommands.upsertUser(event);
			reply(session, event, new Message(miscCommands.getRubric(event)));

		}
	}

	@Controller(events = { EventType.MESSAGE, EventType.DIRECT_MESSAGE }, pattern = "(?i)^(!exampleProjects)$")
	public void getExampleProjects(WebSocketSession session, MyEvent event, Matcher matcher) {
		if (validateIncomingMessage(event, matcher)) {
			miscCommands.upsertUser(event);
			reply(session, event, new Message(miscCommands.getExampleProjects(event)));
		}
	}
	// *************************** team commands **************************\\

	@Controller(events = EventType.MESSAGE, pattern = "(?i)^(!lookForTeam|!lookForATeam|!lookingForTeam)$")
	public void lookForTeams(WebSocketSession session, MyEvent event, Matcher matcher) {
		if (validateIncomingMessage(event, matcher)) {
			if (manager.isActive(Features.LOOK_FOR_TEAM)) {
				miscCommands.upsertUser(event);
				reply(session, event, new ExtraRichMessage(teamCommands.lookForTeamCommandResponse(event)));
			}
		}
	}

	@Controller(events = EventType.MESSAGE, pattern = "(?i)^(!foundTeam|!notLookingForTeam)$")
	public void stopLookingForTeam(WebSocketSession session, MyEvent event, Matcher matcher) {
		if (validateIncomingMessage(event, matcher)) {
			if (manager.isActive(Features.LOOK_FOR_TEAM)) {
				miscCommands.upsertUser(event);
				reply(session, event, new ExtraRichMessage(teamCommands.noLongerLookingForTeamCommandResponse(event)));
			}
		}
	}

	@Controller(events = { EventType.MESSAGE,
			EventType.DIRECT_MESSAGE }, pattern = "(?i)^(!findATeamMember|!findTeamMember)$")
	public void findATeamMember(WebSocketSession session, MyEvent event, Matcher matcher) {
		if (validateIncomingMessage(event, matcher)) {
			if (manager.isActive(Features.FIND_TEAM_MEMBER)) {
				miscCommands.upsertUser(event);
				reply(session, event, new Message(teamCommands.findATeamMember(event)));
			}
		}
	}

	@Controller(events = { EventType.MESSAGE, EventType.DIRECT_MESSAGE }, pattern = "(?i)^(!becomeTeamLead)$")
	public void becomeTeamLead(WebSocketSession session, MyEvent event, Matcher matcher) {
		if (validateIncomingMessage(event, matcher)) {
			if (manager.isActive(Features.TEAM_LEAD)) {
				miscCommands.upsertUser(event);
				reply(session, event, new ExtraRichMessage(teamCommands.becomeTeamLead(event)));

			}
		}
	}

	@Controller(events = { EventType.MESSAGE, EventType.DIRECT_MESSAGE }, pattern = "(?i)^(!stopBeingTeamLead)$")
	public void stopBeingTeamLead(WebSocketSession session, MyEvent event, Matcher matcher) {
		if (validateIncomingMessage(event, matcher)) {
			if (manager.isActive(Features.TEAM_LEAD)) {
				miscCommands.upsertUser(event);
				reply(session, event, new ExtraRichMessage(teamCommands.stopBeingTeamLead(event)));

			}
		}
	}

	@Controller(events = { EventType.MESSAGE,
			EventType.DIRECT_MESSAGE }, pattern = "(?i)^(!currentTeams|!teams|!getTeams)$")
	public void currentTeams(WebSocketSession session, MyEvent event, Matcher matcher) {
		if (validateIncomingMessage(event, matcher)) {
			if (manager.isActive(Features.CURRENT_TEAMS)) {
				miscCommands.upsertUser(event);
				reply(session, event, new ExtraRichMessage(teamCommands.currentTeams(event)));
			}
		}
	}

	@Controller(events = { EventType.MESSAGE,
			EventType.DIRECT_MESSAGE }, pattern = "(?i)^(!addMember|!addTeamMember)$", next = "addMemberFinal")
	public void addMember(WebSocketSession session, MyEvent event, Matcher matcher) {
		if (validateIncomingMessage(event, matcher)) {
			if (manager.isActive(Features.ADD_MEMBER)) {
				miscCommands.upsertUser(event);
				startConversation(event, "addMemberFinal");
				ExtraRichMessage response = new ExtraRichMessage(teamCommands.addTeamMemberInit(event));
				response.setThreadTs(event.getTs());
				lastTs = event.getTs();
				reply(session, event, response);
			}
		}
	}

	@Controller(pattern = "addMemberFinal")
	public void addMemberFinal(WebSocketSession session, MyEvent event) {
		if (manager.isActive(Features.ADD_MEMBER)) {
			miscCommands.upsertUser(event);
			ExtraRichMessage response = new ExtraRichMessage(teamCommands.addTeamMemberFinal(event));
			response.setThreadTs(lastTs);
			lastTs = "";
			response.setReplyTo(1);
			System.out.println("response text: " + response.getText());
			reply(session, event, response);
			stopConversation(event);
		}

	}

	@Controller(events = {
			EventType.DIRECT_MESSAGE }, pattern = "(?i)^(!removeMember|!removeTeamMember)$", next = "removeMemberFinal")
	public void removeMemberInit(WebSocketSession session, MyEvent event) {
		if (manager.isActive(Features.ADD_MEMBER)) {
			miscCommands.upsertUser(event);
			startConversation(event, "addMemberFinal");
			ExtraRichMessage response = new ExtraRichMessage(teamCommands.removeTeamMemberInit(event));
			response.setThreadTs(event.getTs());
			reply(session, event, response);
		}

	}

	@Controller(pattern = "removeMemberFinal")
	public void removeMemberFinal(WebSocketSession session, MyEvent event) {
		if (manager.isActive(Features.ADD_MEMBER)) {
			miscCommands.upsertUser(event);
			ExtraRichMessage response = new ExtraRichMessage(teamCommands.removeTeamMemberFinal(event));
			response.setThreadTs(event.getTs());
			reply(session, event, response);
			stopConversation(event);
		}
	}

	// *************************** project commands **************************\\

	@Controller(events = { EventType.MESSAGE,
			EventType.DIRECT_MESSAGE }, pattern = "(?i)^(!getProjects|!projects|!ideas)$")
	public void getProjects(WebSocketSession session, MyEvent event, Matcher matcher) {
		if (validateIncomingMessage(event, matcher)) {
			if (manager.isActive(Features.GET_PROJECTS)) {
				miscCommands.upsertUser(event);
				ExtraRichMessage response = new ExtraRichMessage(projectCommands.getProjects(event));
				System.out.println("type : " + event.getType());
				if (!event.getType().equals("DIRECT_MESSAGE")) {
					response.setThreadTs(event.getTs());
					response.setReplyTo(1);
				}
				reply(session, event, response);
			}
		}
	}

	@Controller(events = { EventType.MESSAGE,
			EventType.DIRECT_MESSAGE }, pattern = "(?i)^(!submitProjectIdea|!addIdea|!addProjectIdea|!submitProject)$", next = "projectSummary")
	public void submitProjectIdea(WebSocketSession session, MyEvent event, Matcher matcher) {
		if (manager.isActive(Features.SUBMIT_PROJECT_IDEA)) {
			System.out.println("here(submitProjectIdea)");
			if (validateIncomingMessage(event, matcher)) {
				miscCommands.upsertUser(event);
				startConversation(event, "projectSummary");
				ExtraRichMessage response = new ExtraRichMessage(projectCommands.addProjectIdea(event));
				reply(session, event, response);
			} else {
				stopConversation(event);
			}
		}
	}

	@Controller(pattern = "projectSummary")
	public void projectSummary(WebSocketSession session, Event event) {
		ExtraRichMessage response = new ExtraRichMessage(projectCommands.projectWrap(event));
		reply(session, event, response);
		stopConversation(event);
	}

	@Controller(events = { EventType.MESSAGE,
			EventType.DIRECT_MESSAGE }, pattern = "(?i)^(!claimProject | !pickProject)$")
	public void claimProject(WebSocketSession session, MyEvent event, Matcher matcher) {
		if (validateIncomingMessage(event, matcher)) {
			if (manager.isActive(Features.PICK_PROJECT)) {
				miscCommands.upsertUser(event);
			}
		}
	}

	// ************************ question commands ************************\\

	// TODO

	// housekeeping commands
	private void registerCommand(Event e) {
		Command command = new Command(e);
		command.setUserId(e.getUserId());
		command.setText(e.getText());
		command.setUpdated(new Date());
		commandRepository.save(command);
	}
}