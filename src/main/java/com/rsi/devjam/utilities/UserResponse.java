package com.rsi.devjam.utilities;

import com.rsi.devjam.models.MyUser;

public class UserResponse {
    private boolean ok;
    private MyUser user;

    public boolean isOk() {
        return ok;
    }


    public MyUser getUser() {
        return user;
    }
    
    public String toString() {
    	return "is ok?: " + ok + ". Username = " + user.getName();
    }
}
