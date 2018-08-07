package com.rsi.slack;

import java.util.Date;
import java.util.regex.Matcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com.rsi.devjam.models.Command;
import com.rsi.devjam.repository.CommandRepository;
import com.rsi.devjam.utilities.MiscCommands;
import com.rsi.devjam.utilities.ProjectCommands;
import com.rsi.devjam.utilities.TeamCommands;

import me.ramswaroop.jbot.core.slack.Bot;
import me.ramswaroop.jbot.core.slack.Controller;
import me.ramswaroop.jbot.core.slack.EventType;
import me.ramswaroop.jbot.core.slack.models.Event;
import me.ramswaroop.jbot.core.slack.models.Message;

@Component
@EnableAutoConfiguration
public class SlackBot extends Bot {

	@Autowired
	CommandRepository commandRepository;

	@Autowired
	MiscCommands miscCommands;

	@Autowired
	TeamCommands teamCommands;

	@Autowired
	ProjectCommands projectCommands;

	@Value("${slackBotToken}")
	private String slackToken;

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
		reply(session, event, new Message("Hi!! I am " + slackService.getCurrentUser().getName()));
	}

	// *************************** misc commands **************************\\

	@Controller(events = { EventType.MESSAGE, EventType.DIRECT_MENTION }, pattern = "!faq")
	public void getFaq(WebSocketSession session, Event event, Matcher matcher) {
		if (!matcher.group(0).isEmpty()) {
			registerCommand(event);
			reply(session, event, new Message(miscCommands.getFaq(event)));
			stopConversation(event);
		}
	}

	@Controller(events = { EventType.MESSAGE, EventType.DIRECT_MENTION }, pattern = "!help")
	public void getHelp(WebSocketSession session, Event event, Matcher matcher) {
		if (!matcher.group(0).isEmpty()) {
			registerCommand(event);
			reply(session, event, new Message(miscCommands.getHelp(event)));
			stopConversation(event);
		}
	}

	@Controller(events = { EventType.MESSAGE, EventType.DIRECT_MENTION }, pattern = "!deadlines")
	public void getDeadlines(WebSocketSession session, Event event, Matcher matcher) {
		if (!matcher.group(0).isEmpty()) {
			registerCommand(event);
			reply(session, event, new Message(miscCommands.getDeadlines(event)));
		}
	}
	// *************************** team commands **************************\\

	@Controller(events = EventType.MESSAGE, pattern = "!lookForTeam")
	public void lookForTeams(WebSocketSession session, Event event, Matcher matcher) {
		if (!matcher.group(0).isEmpty()) {
			registerCommand(event);
			reply(session, event, new Message(teamCommands.lookForTeamCommandResponse(event)));
		}
	}

	@Controller(events = EventType.MESSAGE, pattern = "!findATeamMember")
	public void findATeamMember(WebSocketSession session, Event event, Matcher matcher) {
		if (!matcher.group(0).isEmpty()) {
			registerCommand(event);
			reply(session, event, new Message(teamCommands.findATeamMember(event)));
		}
	}

	@Controller(events = EventType.MESSAGE, pattern = "!currentTeams")
	public void currentTeams(WebSocketSession session, Event event, Matcher matcher) {
		if (!matcher.group(0).isEmpty()) {
			registerCommand(event);
			reply(session, event, new Message(teamCommands.currentTeams(event)));
		}
	}

	// *************************** project commands **************************\\

	@Controller(events = EventType.MESSAGE, pattern = "!getProjects")
	public void getProjects(WebSocketSession session, Event event, Matcher matcher) {
		if (!matcher.group(0).isEmpty()) {
			registerCommand(event);
			reply(session, event, new Message(projectCommands.getProjects(event)));
		}
	}

	@Controller(events = { EventType.MESSAGE,
			EventType.DIRECT_MENTION }, pattern = "!submitProjectIdea", next = "projectSummary")
	public void submitProjectIdea(WebSocketSession session, Event event, Matcher matcher) {
		if (!matcher.group(0).isEmpty()) {
			registerCommand(event);
			startConversation(event, "projectSummary");
			reply(session, event, new Message(projectCommands.addProjectIdea(event)));
		}
	}

	@Controller(pattern="projectSummary")
	public void projectSummary(WebSocketSession session, Event event) {
		registerCommand(event);
		reply(session, event, new Message(projectCommands.projectWrap(event)));
		stopConversation(event);
	}
	
	// *************************** question commands **************************\\

	// TODO

	// housekeeping commands
/*	@Controller(events = { EventType.MESSAGE, EventType.DIRECT_MENTION })
	public void defaultEndConversation(WebSocketSession session, Event event) {
//		System.out.println(event.getText() + ".");
//		if (!event.getText().isEmpty() && !event.getText().startsWith("!"))
//			stopConversation(event);

	}*/

	private void registerCommand(Event e) {
		Command command = new Command(e);
		command.setUserId(e.getUserId());
		command.setText(e.getText());
		command.setUpdated(new Date());
		commandRepository.save(command);
	}
}