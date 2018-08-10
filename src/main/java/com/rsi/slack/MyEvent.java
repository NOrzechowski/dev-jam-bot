package com.rsi.slack;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;

import me.ramswaroop.jbot.core.slack.models.Event;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MyEvent extends Event {

	@JsonProperty("thread_ts")
	private String threadTs;

	public String getThreadTs() {
		return threadTs;
	}

	public void setThreadTs(String threadTs) {
		this.threadTs = threadTs;
	}

	public boolean isThreadMessage() {
		return !Strings.isNullOrEmpty(this.threadTs);
	}
}
