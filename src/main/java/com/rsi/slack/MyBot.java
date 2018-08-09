package com.rsi.slack;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.rsi.devjam.models.ExtraRichMessage;

import me.ramswaroop.jbot.core.slack.Bot;
import me.ramswaroop.jbot.core.slack.EventType;
import me.ramswaroop.jbot.core.slack.models.Event;

public abstract class MyBot extends Bot {
	private static final Logger logger = LoggerFactory.getLogger(MyBot.class);

	/**
	 * Method to send a reply back to Slack after receiving an {@link Event}.
	 * Learn <a href="https://api.slack.com/rtm">more on sending responses to
	 * Slack.</a>
	 *
	 * @param session
	 * @param event
	 * @param reply
	 */
	public final void reply(WebSocketSession session, Event event, ExtraRichMessage reply) {
		try {
			reply.setType(EventType.MESSAGE.name().toLowerCase());
			reply.setText(encode(reply.getText()));
			reply.setLinkUser("1");
			reply.setParse("full");
			if (reply.getChannel() == null && event.getChannelId() != null) {
				reply.setChannel(event.getChannelId());
			}
			System.out.println(reply.toJSONString());
			session.sendMessage(new TextMessage(reply.toJSONString()));
			if (logger.isDebugEnabled()) { // For debugging purpose only
				logger.debug("Reply (Message): {}", reply.toJSONString());
			}
		} catch (IOException e) {
			logger.error("Error sending event: {}. Exception: {}", event.getText(), e.getMessage());
		}
	}

	/**
	 * Encode the text before sending to Slack. Learn
	 * <a href="https://api.slack.com/docs/formatting">more on message
	 * formatting in Slack</a>
	 *
	 * @param message
	 * @return encoded text.
	 */
	private String encode(String message) {
		return message == null ? null : message.replace("&", "&amp;");
	}
}
