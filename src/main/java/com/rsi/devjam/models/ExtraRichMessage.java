package com.rsi.devjam.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.ramswaroop.jbot.core.slack.models.Message;

/**
 * @author ramswaroop
 * @version 21/06/2016
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExtraRichMessage extends Message {

	public ExtraRichMessage(String text) {
		this.text = text;
		this.linkUser = "1";
		this.parse = "full";
	}

	@JsonProperty("link_user")
	private String linkUser;
	private String text;
	private String parse;

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
