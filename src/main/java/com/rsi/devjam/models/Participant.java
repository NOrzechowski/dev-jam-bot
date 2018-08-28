package com.rsi.devjam.models;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "participants")
public class Participant {
	@Id
	private String id;
	private String user;
	private String name;
	private String email;
	private String slackId;
	private boolean lookingForTeam;
	private boolean teamLead;
	private Date updateDate = new Date();
	private boolean wantsTShirt;
	private String tShirtSize;
	
	public Participant() {
	}

	public Participant(String user, String name) {
		this.user = user;
		this.name = name;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public boolean isTeamLead() {
		return teamLead;
	}

	public void setTeamLead(boolean teamLead) {
		this.teamLead = teamLead;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSlackId() {
		return slackId;
	}

	public void setSlackId(String slackId) {
		this.slackId = slackId;
	}

	public boolean isLookingForTeam() {
		return lookingForTeam;
	}

	public void setLookingForTeam(boolean lookingForTeam) {
		this.lookingForTeam = lookingForTeam;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	

	public boolean isWantsTShirt() {
		return wantsTShirt;
	}

	public void setWantsTShirt(boolean wantsTShirt) {
		this.wantsTShirt = wantsTShirt;
	}

	public String gettShirtSize() {
		return tShirtSize;
	}

	public void settShirtSize(String tShirtSize) {
		this.tShirtSize = tShirtSize;
	}

	@Override
	public boolean equals(Object o) {

		if (o == this)
			return true;
		if (!(o instanceof Participant)) {
			return false;
		}

		Participant p = (Participant) o;

		return p.getUser().equals(user);
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + user.hashCode();
		return result;
	}

}