package com.rsi.devjam.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "projects")
public class Project {
	@Id
	private String id;
	private String summary;
	private String skills;
	private boolean claimed;
	private String description;
	private Team team;
	private Participant teamLead;
	private Participant submittedBy;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getSkills() {
		return skills;
	}

	public void setSkills(String skills) {
		this.skills = skills;
	}

	public boolean isClaimed() {
		return claimed;
	}

	public void setClaimed(boolean claimed) {
		this.claimed = claimed;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	public Participant getTeamLead() {
		return teamLead;
	}

	public void setTeamLead(Participant teamLead) {
		this.teamLead = teamLead;
	}

	public Participant getSubmittedBy() {
		return submittedBy;
	}

	public void setSubmittedBy(Participant submittedBy) {
		this.submittedBy = submittedBy;
	}

}
