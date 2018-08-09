package com.rsi.slack;

import java.util.Date;
import java.util.regex.Matcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.togglz.core.manager.FeatureManager;

import com.rsi.devjam.models.Command;
import com.rsi.devjam.models.ExtraRichMessage;
import com.rsi.devjam.repository.CommandRepository;
import com.rsi.devjam.togglz.Features;
import com.rsi.devjam.utilities.MiscCommands;
import com.rsi.devjam.utilities.ProjectCommands;
import com.rsi.devjam.utilities.TeamCommands;

import me.ramswaroop.jbot.core.slack.Bot;
import me.ramswaroop.jbot.core.slack.Controller;
import me.ramswaroop.jbot.core.slack.EventType;
import me.ramswaroop.jbot.core.slack.models.Event;
import me.ramswaroop.jbot.core.slack.models.Message;
import me.ramswaroop.jbot.core.slack.models.RichMessage;

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
	public Bot getSlackBot() {
		return this;
	}

	// Direct Messages

	@Controller(events = { EventType.DIRECT_MENTION, EventType.DIRECT_MESSAGE })
	public void onReceiveDM(WebSocketSession session, Event event) {
		registerCommand(event);
		miscCommands.upsertUser(event);
		reply(session, event, new Message("Hi!! I am " + slackService.getCurrentUser().getName()));
	}

	// *************************** misc commands **************************\\

	@Controller(events = { EventType.MESSAGE, EventType.DIRECT_MENTION }, pattern = "(?i)^(!faq)$")
	public void getFaq(WebSocketSession session, Event event, Matcher matcher) {
		if (!matcher.group(0).isEmpty()) {
			miscCommands.upsertUser(event);
			reply(session, event, new Message(miscCommands.getFaq(event)));
			stopConversation(event);
		}
	}

	@Controller(events = { EventType.MESSAGE, EventType.DIRECT_MENTION }, pattern = "(?i)^(!help)$")
	public void getHelp(WebSocketSession session, Event event, Matcher matcher) {
		if (!matcher.group(0).isEmpty()) {
			miscCommands.upsertUser(event);
			reply(session, event, new Message(miscCommands.getHelp(event)));
			stopConversation(event);
		}
	}

	@Controller(events = { EventType.MESSAGE, EventType.DIRECT_MENTION }, pattern = "(?i)^(!deadlines|!dates)$")
	public void getDeadlines(WebSocketSession session, Event event, Matcher matcher) {
		if (!matcher.group(0).isEmpty()) {
			if (manager.isActive(Features.DATES)) {
				miscCommands.upsertUser(event);
				reply(session, event, new Message(miscCommands.getDeadlines(event)));
			}
		}
	}
	// *************************** team commands **************************\\

	@Controller(events = EventType.MESSAGE, pattern = "!lookForTeam")
	public void lookForTeams(WebSocketSession session, Event event, Matcher matcher) {
		if (!matcher.group(0).isEmpty()) {
			if (manager.isActive(Features.LOOK_FOR_TEAM)) {
				miscCommands.upsertUser(event);
				reply(session, event, new Message(teamCommands.lookForTeamCommandResponse(event)));
			}
		}

	}

	@Controller(events = EventType.MESSAGE, pattern = "!findATeamMember")
	public void findATeamMember(WebSocketSession session, Event event, Matcher matcher) {
		if (!matcher.group(0).isEmpty()) {
			if (manager.isActive(Features.FIND_TEAM_MEMBER)) {
				miscCommands.upsertUser(event);
				reply(session, event, new Message(teamCommands.findATeamMember(event)));
			}
		}
	}

	@Controller(events = EventType.MESSAGE, pattern = "!currentTeams")
	public void currentTeams(WebSocketSession session, Event event, Matcher matcher) {
		if (!matcher.group(0).isEmpty()) {
			if (manager.isActive(Features.CURRENT_TEAMS)) {
				miscCommands.upsertUser(event);
				reply(session, event, new Message(teamCommands.currentTeams(event)));
			}
		}
	}

	// *************************** project commands **************************\\

	@Controller(events = EventType.MESSAGE, pattern = "!getProjects")
	public void getProjects(WebSocketSession session, Event event, Matcher matcher) {
		if (!matcher.group(0).isEmpty()) {
			if (manager.isActive(Features.GET_PROJECTS)) {
				miscCommands.upsertUser(event);
				reply(session, event, new Message(projectCommands.getProjects(event)));
			}
		}
	}

	@Controller(events = { EventType.MESSAGE,
			EventType.DIRECT_MENTION }, pattern = "!submitProjectIdea", next = "projectSummary")
	public void submitProjectIdea(WebSocketSession session, Event event, Matcher matcher) {
		if (!matcher.group(0).isEmpty()) {
			if (manager.isActive(Features.SUBMIT_PROJECT_IDEA)) {
				miscCommands.upsertUser(event);
				startConversation(event, "projectSummary");
				reply(session, event, new ExtraRichMessage(projectCommands.addProjectIdea(event)));
			}
		}
	}

	@Controller(pattern = "projectSummary")
	public void projectSummary(WebSocketSession session, Event event) {
		reply(session, event, new Message(projectCommands.projectWrap(event)));
		stopConversation(event);
	}

	// *************************** question commands
	// **************************\\

	// TODO

	// housekeeping commands
	/*
	 * @Controller(events = { EventType.MESSAGE, EventType.DIRECT_MENTION })
	 * public void defaultEndConversation(WebSocketSession session, Event event)
	 * { // System.out.println(event.getText() + "."); // if
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