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

	// Direct Messages

	/*
	 * @Controller(events = { EventType.DIRECT_MENTION, EventType.DIRECT_MESSAGE
	 * }) public void onReceiveDM(WebSocketSession session, MyEvent event) {
	 * registerCommand(event); miscCommands.upsertUser(event); reply(session,
	 * event, new Message("Hi!! I am " +
	 * slackService.getCurrentUser().getName())); }
	 */
	// *************************** misc commands **************************\\

	@Controller(events = { EventType.MESSAGE, EventType.DIRECT_MESSAGE }, pattern = "(?i)^(!faq)$")
	public void getFaq(WebSocketSession session, MyEvent event, Matcher matcher) {
		if (validateIncomingMessage(event, matcher)) {
			miscCommands.upsertUser(event);
			reply(session, event, new Message(miscCommands.getFaq(event)));
			stopConversation(event);
		}
	}

	@Controller(events = { EventType.MESSAGE, EventType.DIRECT_MESSAGE }, pattern = "(?i)^(!help)$")
	public void getHelp(WebSocketSession session, MyEvent event, Matcher matcher) {
		if (validateIncomingMessage(event, matcher)) {
			miscCommands.upsertUser(event);
			reply(session, event, new Message(miscCommands.getHelp(event)));
			stopConversation(event);
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
			if (manager.isActive(Features.CURRENT_TEAMS)) {
				miscCommands.upsertUser(event);
				reply(session, event, new ExtraRichMessage(teamCommands.becomeTeamLead(event)));

			}
		}
	}

	@Controller(events = { EventType.MESSAGE, EventType.DIRECT_MESSAGE }, pattern = "(?i)^(!stopBeingTeamLead)$")
	public void stopBeingTeamLead(WebSocketSession session, MyEvent event, Matcher matcher) {
		if (validateIncomingMessage(event, matcher)) {
			if (manager.isActive(Features.CURRENT_TEAMS)) {
				miscCommands.upsertUser(event);
				reply(session, event, new ExtraRichMessage(teamCommands.stopBeingTeamLead(event)));

			}
		}
	}

	@Controller(events = { EventType.MESSAGE, EventType.DIRECT_MESSAGE }, pattern = "(?i)^(!currentTeams|!teams)$")
	public void currentTeams(WebSocketSession session, MyEvent event, Matcher matcher) {
		if (validateIncomingMessage(event, matcher)) {
			if (manager.isActive(Features.CURRENT_TEAMS)) {
				miscCommands.upsertUser(event);
				reply(session, event, new Message(teamCommands.currentTeams(event)));
			}
		}
	}

	@Controller(events = { EventType.MESSAGE, EventType.DIRECT_MESSAGE }, pattern = "(?i)^(!createTeam)$")
	public void createTeam(WebSocketSession session, MyEvent event, Matcher matcher) {
		if (validateIncomingMessage(event, matcher)) {
			if (manager.isActive(Features.CURRENT_TEAMS)) {
				miscCommands.upsertUser(event);
			}
		}
	}

	@Controller(events = { EventType.MESSAGE, EventType.DIRECT_MESSAGE }, pattern = "(?i)^(!getTeams)$")
	public void getTeams(WebSocketSession session, MyEvent event, Matcher matcher) {
		if (validateIncomingMessage(event, matcher)) {
			if (manager.isActive(Features.CURRENT_TEAMS)) {
				miscCommands.upsertUser(event);
			}
		}
	}

	@Controller(events = { EventType.MESSAGE, EventType.DIRECT_MESSAGE }, pattern = "(?i)^(!addMember)$")
	public void addMember(WebSocketSession session, MyEvent event, Matcher matcher) {
		if (validateIncomingMessage(event, matcher)) {
			if (manager.isActive(Features.CURRENT_TEAMS)) {
				miscCommands.upsertUser(event);
			}
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
				if (!event.getType().equals("DIRECT_MESSAGE"))
					response.setThreadTs(event.getTs());
				reply(session, event, response);
			}
		}
	}

	@Controller(events = { EventType.MESSAGE,
			EventType.DIRECT_MESSAGE }, pattern = "(?i)^(!submitProjectIdea|!addIdea|!addProjectIdea|!submitProject)$", next = "projectSummary")
	public void submitProjectIdea(WebSocketSession session, MyEvent event, Matcher matcher) {
		if (manager.isActive(Features.SUBMIT_PROJECT_IDEA)) {
			if (validateIncomingMessage(event, matcher)) {
				miscCommands.upsertUser(event);
				startConversation(event, "projectSummary");
				ExtraRichMessage response = new ExtraRichMessage(projectCommands.addProjectIdea(event));
				reply(session, event, response);
			}
		}
	}

	@Controller(pattern = "projectSummary")
	public void projectSummary(WebSocketSession session, MyEvent event) {
		ExtraRichMessage response = new ExtraRichMessage(projectCommands.projectWrap(event));
		reply(session, event, response);
		stopAllConversations(event);
	}

	@Controller(events = { EventType.MESSAGE, EventType.DIRECT_MESSAGE }, pattern = "(?i)^(!claimProject)$")
	public void claimProject(WebSocketSession session, MyEvent event, Matcher matcher) {
		if (validateIncomingMessage(event, matcher)) {
			if (manager.isActive(Features.GET_PROJECTS)) {
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