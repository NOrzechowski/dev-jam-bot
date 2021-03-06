package com.rsi.slack;

import java.util.Objects;
import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.togglz.core.manager.FeatureManager;

import com.rsi.devjam.repository.CommandRepository;
import com.rsi.devjam.togglz.Features;
import com.rsi.devjam.utilities.CompositeResponse;
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
	private static final Logger logger = LoggerFactory.getLogger(SlackBot.class);

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
	private static final String COMMAND_ERR = "Sorry, that command couldn't be completed at this time - please try again.";

	private static final String DIRECT_MESSAGE = "DIRECT_MESSAGE";
	String lastTs = "";

	String lastUserId = "";

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

	@Controller(events = { EventType.DIRECT_MESSAGE }, pattern = "^.*?\\!loadUsersByChannel\\b.*?\\.*\\b.*?$")
	public void getUsersByChannel(WebSocketSession session, MyEvent event, Matcher matcher) {
		if (validateIncomingMessage(event, matcher)) {
			miscCommands.upsertUser(event);
			reply(session, event, new Message(miscCommands.loadUsersByChannel(event)));
		}
	}

	@Controller(events = { EventType.MESSAGE }, pattern = "(?<=:8ball:).*$")
	public void magic8ball(WebSocketSession session, MyEvent event, Matcher matcher) {
		if (validateIncomingMessage(event, matcher)) {
			miscCommands.upsertUser(event);
			CompositeResponse res = miscCommands.eightBall(event);
			ExtraRichMessage response;
			if (res.isErrorsOccured()) {
				response = new ExtraRichMessage(res.getMessageResponse(), event.getEventTs());
			} else {
				response = new ExtraRichMessage(res.getMessageResponse());
			}
			reply(session, event, response);
		}
	}

	@Controller(events = { EventType.MESSAGE,
			EventType.DIRECT_MESSAGE }, pattern = "(?i)^(!t-shirt|!tShirt)$", next = "tShirtFinal")
	public void tShirt(WebSocketSession session, MyEvent event, Matcher matcher) {
		if (validateIncomingMessage(event, matcher)) {
			miscCommands.upsertUser(event);
			startConversation(event, "tShirtFinal");
			CompositeResponse msg = miscCommands.tShirtInit(event);
			ExtraRichMessage response = new ExtraRichMessage(msg.getMessageResponse());

			if (!event.getType().equals(DIRECT_MESSAGE)) {
				response.setThreadTs(event.getTs());
			}
			lastTs = event.getTs();
			lastUserId = event.getUserId();
			reply(session, event, response);
		}
	}

	@Controller(events = { EventType.MESSAGE, EventType.DIRECT_MESSAGE }, pattern = "tShirtFinal")
	public void tShirtFinal(WebSocketSession session, MyEvent event) {
		miscCommands.upsertUser(event);
		if (event.getUserId().equals(lastUserId)) {
			ExtraRichMessage response = new ExtraRichMessage(miscCommands.tShirtFinal(event));
			if (!event.getType().equals(DIRECT_MESSAGE)) {
				response.setThreadTs(lastTs);
			}
			lastTs = "";
			reply(session, event, response);
		}
		stopConversation(event);
	}

	@Controller(events = { EventType.MESSAGE,
			EventType.DIRECT_MESSAGE }, pattern = "(?i)^(!tshirts|!signedUpForTShirts|!t-shirts)$")
	public void getTshirts(WebSocketSession session, MyEvent event, Matcher matcher) {
		if (validateIncomingMessage(event, matcher)) {
			if (manager.isActive(Features.GET_PROJECTS)) {
				miscCommands.upsertUser(event);
				ExtraRichMessage response = new ExtraRichMessage(miscCommands.getTShirtParticipants(event));
				if (!event.getType().equals(DIRECT_MESSAGE)) {
					response.setThreadTs(event.getTs());
				}
				reply(session, event, response);
			}
		}
	}

	@Controller(events = { EventType.MESSAGE, EventType.DIRECT_MESSAGE }, pattern = "(?i)^(!exampleProjects)$")
	public void getExampleProjects(WebSocketSession session, MyEvent event, Matcher matcher) {
		if (validateIncomingMessage(event, matcher)) {
			miscCommands.upsertUser(event);
			reply(session, event, new Message(miscCommands.getExampleProjects(event)));
		}
	}

	@Controller(events = { EventType.MESSAGE, EventType.DIRECT_MESSAGE }, pattern = "(?i)^(!pixieStick)$")
	public void pixieStick(WebSocketSession session, MyEvent event, Matcher matcher) {
		if (validateIncomingMessage(event, matcher)) {
			miscCommands.upsertUser(event);
			CompositeResponse msg = miscCommands.pixieStix(event);
			if (msg.isErrorsOccured()) {
				reply(session, event, new ExtraRichMessage(msg.getMessageResponse(), event.getEventTs()));

			} else {
				reply(session, event, new Message(msg.getMessageResponse()));
			}
		}
	}

	@Controller(events = { EventType.MESSAGE, EventType.DIRECT_MESSAGE }, pattern = "(?i)^(!doorPrize)$")
	public void doorPrize(WebSocketSession session, MyEvent event, Matcher matcher) {
		if (validateIncomingMessage(event, matcher)) {
			miscCommands.upsertUser(event);
			String msg = miscCommands.doorPrize(event);
			reply(session, event, new Message(msg));
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

	@Controller(events = { EventType.MESSAGE,
			EventType.DIRECT_MESSAGE }, pattern = "(?i)^(!foundTeam|!notLookingForTeam)$")
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

	@Controller(events = { EventType.MESSAGE }, pattern = "(?i)^(!addTeamName)$", next = "addTeamNameFinal")
	public void addTeamName(WebSocketSession session, MyEvent event, Matcher matcher) {
		if (validateIncomingMessage(event, matcher)) {
			if (manager.isActive(Features.ADD_MEMBER)) {
				miscCommands.upsertUser(event);
				startConversation(event, "addTeamNameFinal");
				CompositeResponse msg = teamCommands.addTeamNameInit(event);
				if (msg.isErrorsOccured()) {
					stopConversation(event);
				}
				lastTs = event.getTs();
				ExtraRichMessage response = new ExtraRichMessage(msg.getMessageResponse(), lastTs);
				lastUserId = event.getUserId();
				reply(session, event, response);

			}
		}
	}

	@Controller(pattern = "addTeamNameFinal")
	public void addTeamNameFinal(WebSocketSession session, MyEvent event) {
		if (manager.isActive(Features.ADD_MEMBER)) {
			miscCommands.upsertUser(event);
			if (event.getUserId().equals(lastUserId)) {
				ExtraRichMessage response = new ExtraRichMessage(teamCommands.addTeamNameFinal(event), lastTs);
				lastTs = "";
				reply(session, event, response);
			}
			stopConversation(event);
		}
	}

	@Controller(events = { EventType.MESSAGE,
			EventType.DIRECT_MESSAGE }, pattern = "(?i)^(!currentTeams|!teams|!getTeams)$")
	public void currentTeams(WebSocketSession session, MyEvent event, Matcher matcher) {
		if (validateIncomingMessage(event, matcher)) {
			if (manager.isActive(Features.CURRENT_TEAMS)) {
				miscCommands.upsertUser(event);
				reply(session, event, new ExtraRichMessage(teamCommands.currentTeams(event), event.getEventTs()));
			}
		}
	}

	@Controller(events = {
			EventType.MESSAGE }, pattern = "(?i)^(!addMembers|!addTeamMembers|!addMember|!addTeamMember)$", next = "addMembersFinal")
	public void addMembers(WebSocketSession session, MyEvent event, Matcher matcher) {
		if (validateIncomingMessage(event, matcher)) {
			if (manager.isActive(Features.ADD_MEMBER)) {
				miscCommands.upsertUser(event);
				startConversation(event, "addMembersFinal");
				CompositeResponse msg = teamCommands.addTeamMembersInit(event);
				if (msg.isErrorsOccured()) {
					stopConversation(event);
				}
				lastTs = event.getTs();
				ExtraRichMessage response = new ExtraRichMessage(msg.getMessageResponse(), lastTs);
				lastUserId = event.getUserId();
				reply(session, event, response);
			}
		}
	}

	@Controller(pattern = "addMembersFinal")
	public void addMembersFinal(WebSocketSession session, MyEvent event) {
		if (manager.isActive(Features.ADD_MEMBER)) {
			miscCommands.upsertUser(event);
			if (event.getUserId().equals(lastUserId)) {
				ExtraRichMessage response = new ExtraRichMessage(teamCommands.addTeamMembersFinal(event), lastTs);
				lastTs = "";
				reply(session, event, response);
			}
			stopConversation(event);
		}
	}

	@Controller(events = { EventType.DIRECT_MESSAGE }, pattern = "(?<=addChannel).*$")
	public void addChannel(WebSocketSession session, MyEvent event) {
		logger.debug("in addChannel");
		String[] inputs = event.getText().split(" ");
		String channel = inputs[1].trim();
		slackService.addDmChannel(channel);
		reply(session, event, new ExtraRichMessage("added " + channel));

	}

	@Controller(events = { EventType.MESSAGE,
			EventType.DIRECT_MESSAGE }, pattern = "(?i)^(!removeMember|!removeTeamMember)$", next = "removeMemberFinal")
	public void removeMemberInit(WebSocketSession session, MyEvent event) {
		logger.debug("in removeTeamMember");
		System.out.println("in remove team member: " + event.getText());
		if (manager.isActive(Features.ADD_MEMBER)) {
			miscCommands.upsertUser(event);
			lastUserId = event.getUserId();
			startConversation(event, "removeMemberFinal");
			CompositeResponse msg = teamCommands.removeTeamMemberInit(event);
			if (msg.isErrorsOccured()) {
				stopConversation(event);
			}
			ExtraRichMessage response = new ExtraRichMessage(msg.getMessageResponse());
			reply(session, event, response);
		}

	}

	@Controller(events = { EventType.MESSAGE, EventType.DIRECT_MESSAGE }, pattern = "removeMemberFinal")
	public void removeMemberFinal(WebSocketSession session, MyEvent event) {
		System.out.println("in remove member final: " + event.getText());
		if (manager.isActive(Features.ADD_MEMBER)) {
			if (event.getUserId().equals(lastUserId)) {
				ExtraRichMessage response = new ExtraRichMessage(teamCommands.removeTeamMemberFinal(event));
				reply(session, event, response);
			} else {
				ExtraRichMessage response = new ExtraRichMessage(COMMAND_ERR, event.getEventTs());
				reply(session, event, response);
			}
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
				if (!event.getType().equals(DIRECT_MESSAGE)) {
					response.setThreadTs(event.getTs());
				}
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

	@Controller(events = { EventType.MESSAGE }, pattern = "^.*?\\!claimProject\\b.*?\\.*\\b.*?$")
	public void claimProject(WebSocketSession session, MyEvent event, Matcher matcher) {
		if (validateIncomingMessage(event, matcher)) {
			if (manager.isActive(Features.PICK_PROJECT)) {
				miscCommands.upsertUser(event);
				ExtraRichMessage response = new ExtraRichMessage(
						projectCommands.claimProject(event).getMessageResponse(), event.getEventTs());
				reply(session, event, response);
			}
		}
	}

	@Controller(events = { EventType.MESSAGE, EventType.DIRECT_MESSAGE }, pattern = "(?i)^(!projectProposal)$")
	public void projectProposal(WebSocketSession session, MyEvent event, Matcher matcher) {
		if (validateIncomingMessage(event, matcher)) {
			if (manager.isActive(Features.PICK_PROJECT)) {
				miscCommands.upsertUser(event);
				ExtraRichMessage response = new ExtraRichMessage(
						projectCommands.projectProposal(event).getMessageResponse(), event.getEventTs());
				reply(session, event, response);
			}
		}
	}

	// ************************ question commands ************************\\
	// To be implemented \\

}