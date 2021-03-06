package com.rsi.devjam.models;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "teams")
public class Team {
	@Id
	private String id;
	private String name;
	private List<Participant> participants;
	private Participant lead;
	private Project project;
	private boolean proposedProject;

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

	public List<Participant> getParticipants() {
		return participants;
	}

	public void setParticipants(List<Participant> participants) {
		this.participants = participants;
	}

	public Participant getLead() {
		return lead;
	}

	public void setLead(Participant lead) {
		this.lead = lead;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public boolean isProposedProject() {
		return proposedProject;
	}

	public void setProposedProject(boolean proposedProject) {
		this.proposedProject = proposedProject;
	}

}
