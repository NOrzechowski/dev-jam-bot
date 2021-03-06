package com.rsi.devjam.togglz;

import org.togglz.core.Feature;
import org.togglz.core.annotation.EnabledByDefault;
import org.togglz.core.annotation.Label;
import org.togglz.core.context.FeatureContext;

public enum Features implements Feature {
	@EnabledByDefault
	@Label("!submitProjectIdea")
	SUBMIT_PROJECT_IDEA,
	
	@EnabledByDefault
	@Label("!deadlines")
	DATES,
	@EnabledByDefault
	@Label("!getProjects")
	GET_PROJECTS,
	@EnabledByDefault
	@Label("!lookForAteam")
	LOOK_FOR_TEAM,
	@EnabledByDefault
	@Label("!findATeamMember")
	FIND_TEAM_MEMBER,
	@EnabledByDefault
	@Label("!becomeTeamLead")
	TEAM_LEAD,
	@EnabledByDefault
	@Label("!addMember")
	ADD_MEMBER,
	@EnabledByDefault
	@Label("!pickProject")
	PICK_PROJECT,
	@EnabledByDefault
	@Label("!currentTeams")
	CURRENT_TEAMS;

	public boolean isActive() {
		return FeatureContext.getFeatureManager().isActive(this);
	}

}