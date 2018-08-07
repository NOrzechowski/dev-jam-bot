package com.rsi.devjam.utilities;

import me.ramswaroop.jbot.core.slack.models.User;

public class UserResponse {
    private boolean ok;
    private User user;

    public boolean isOk() {
        return ok;
    }


    public User getUser() {
        return user;
    }
    
    public String toString() {
    	return "is ok?: " + ok + ". Username = " + user.getName();
    }
}
