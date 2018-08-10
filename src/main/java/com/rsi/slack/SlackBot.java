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

import com.google.common.base.Strings;
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
		stopConversation(event);
		return !event.isThreadMessage() && !Objects.isNull(matcher) && !matcher.group(0).isEmpty();

	}

	// Direct Messages

	@Controller(events = { EventType.DIRECT_MENTION, EventType.DIRECT_MESSAGE })
	public void onReceiveDM(WebSocketSession session, MyEvent event) {
		registerCommand(event);
		miscCommands.upsertUser(event);
		reply(session, event, new Message("Hi!! I am " + slackService.getCurrentUser().getName()));
	}

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
	// *************************** team commands **************************\\

	@Controller(events = EventType.MESSAGE, pattern = "(?i)^(!lookForTeam)$")
	public void lookForTeams(WebSocketSession session, MyEvent event, Matcher matcher) {
		if (validateIncomingMessage(event, matcher)) {
			if (manager.isActive(Features.LOOK_FOR_TEAM)) {
				miscCommands.upsertUser(event);
				reply(session, event, new Message(teamCommands.lookForTeamCommandResponse(event)));
			}
		}

	}

	@Controller(events = EventType.MESSAGE, pattern = "(?i)^(!findATeamMember)$")
	public void findATeamMember(WebSocketSession session, MyEvent event, Matcher matcher) {
		if (validateIncomingMessage(event, matcher)) {
			if (manager.isActive(Features.FIND_TEAM_MEMBER)) {
				miscCommands.upsertUser(event);
				reply(session, event, new Message(teamCommands.findATeamMember(event)));
			}
		}
	}

	@Controller(events = EventType.MESSAGE, pattern = "(?i)^(!currentTeams|!teams)$")
	public void currentTeams(WebSocketSession session, MyEvent event, Matcher matcher) {
		if (validateIncomingMessage(event, matcher)) {
			if (manager.isActive(Features.CURRENT_TEAMS)) {
				miscCommands.upsertUser(event);
				reply(session, event, new Message(teamCommands.currentTeams(event)));
			}
		}
	}

	// *************************** project commands **************************\\

	@Controller(events = EventType.MESSAGE, pattern = "(?i)^(!getProjects|!projects|!ideas)$")
	public void getProjects(WebSocketSession session, MyEvent event, Matcher matcher) {
		if (validateIncomingMessage(event, matcher)) {
			if (manager.isActive(Features.GET_PROJECTS)) {
				miscCommands.upsertUser(event);
				reply(session, event, new Message(projectCommands.getProjects(event)));
			}
		}
	}

	@Controller(events = { EventType.MESSAGE,
			EventType.DIRECT_MESSAGE }, pattern = "(?i)^(!submitProjectIdea|!addIdea|!addProjectIdea)$", next = "projectSummary")
	public void submitProjectIdea(WebSocketSession session, MyEvent event, Matcher matcher) {
		if (manager.isActive(Features.SUBMIT_PROJECT_IDEA)) {
			if (!Objects.isNull(matcher) && !matcher.group(0).isEmpty()) {
				miscCommands.upsertUser(event);
				startConversation(event, "projectSummary");
				ExtraRichMessage response = new ExtraRichMessage(projectCommands.addProjectIdea(event));
				response.setThreadTs(event.getTs());
				reply(session, event, response);
			}
		}
	}

	@Controller(pattern = "projectSummary")
	public void projectSummary(WebSocketSession session, MyEvent event) {
		ExtraRichMessage response = new ExtraRichMessage(projectCommands.projectWrap(event));
		System.out.println("is thread message" + event.isThreadMessage());
		if (event.isThreadMessage() && !Strings.isNullOrEmpty(response.getText())) {
			response.setThreadTs(event.getThreadTs());
			reply(session, event, response);
			stopConversation(event);

		} else if (event.isThreadMessage()) {
			stopConversation(event);
			startConversation(event, "projectSummary");
		}
	}

	// *************************** question commands
	// **************************\\

	// TODO

	// housekeeping commands
	/*
	 * @Controller(events = { EventType.MESSAGE, EventType.DIRECT_MENTION })
	 * public void defaultEndConversation(WebSocketSession session, MyEvent
	 * event) { // System.out.println(event.getText() + "."); // if
	 * (!event.getText().isEmpty() && !event.getText().startsWith("!")) //
	 * stopConversation(event);
	 * 
	 * }
	 */

	private void registerCommand(Event e) {
		Command command = new Command(e);
		command.setUserId(e.getUserId());
		command.setText(e.getText());
		command.setUpdated(new Date());
		commandRepository.save(command);
	}
}