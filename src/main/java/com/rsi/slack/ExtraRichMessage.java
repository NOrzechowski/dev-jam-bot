package com.rsi.slack;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.ramswaroop.jbot.core.slack.models.Message;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExtraRichMessage extends Message {

	public ExtraRichMessage(String text) {
		this.text = text;
		this.linkUser = "1";
		this.parse = "full";
	}

	public ExtraRichMessage(String text, String threadTs) {
		this.text = text;
		this.linkUser = "1";
		this.parse = "full";
		this.threadTs = threadTs;
	}

	@JsonProperty("link_user")
	private String linkUser;
	private String text;
	private String parse;
	@JsonProperty("thread_ts")
	private String threadTs;
	@JsonProperty("reply_to")
	private int replyTo;

	public int getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(int replyTo) {
		this.replyTo = replyTo;
	}

	public String getThreadTs() {
		return threadTs;
	}

	public void setThreadTs(String threadTs) {
		this.threadTs = threadTs;
	}

	public String getLinkUser() {
		return linkUser;
	}

	public void setLinkUser(String linkUser) {
		this.linkUser = linkUser;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getParse() {
		return parse;
	}

	public void setParse(String parse) {
		this.parse = parse;
	}

	@Override
	public String toJSONString() throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(this);
	}

}
