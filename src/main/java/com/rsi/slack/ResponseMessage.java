package com.rsi.slack;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseMessage {
	
	String type;
	String user;
	String text;
	@JsonProperty("client_msg_id")
	String clientMsgId;
	@JsonProperty("thread_ts")
	String threadts;
	@JsonProperty("reply_count")
	int replyCount;
	Reply[] replies;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getClientMsgId() {
		return clientMsgId;
	}
	public void setClientMsgId(String clientMsgId) {
		this.clientMsgId = clientMsgId;
	}
	public String getThreadts() {
		return threadts;
	}
	public void setThreadts(String threadts) {
		this.threadts = threadts;
	}
	public int getReplyCount() {
		return replyCount;
	}
	public void setReplyCount(int replyCount) {
		this.replyCount = replyCount;
	}
	public Reply[] getReplies() {
		return replies;
	}
	public void setReplies(Reply[] replies) {
		this.replies = replies;
	}
	
	public String toJSONString() throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(this);
	}

	
}
