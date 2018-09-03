package com.rsi.devjam.utilities;

import com.rsi.devjam.models.MyChannel;

public class ChannelResponse {
    private boolean ok;
    private MyChannel channel;

    public boolean isOk() {
        return ok;
    }


    public MyChannel getChannel() {
        return channel;
    }
    
    public String toString() {
    	return "is ok?: " + ok + ". Username = " + channel.getName();
    }
}
