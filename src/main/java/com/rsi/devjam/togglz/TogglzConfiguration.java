package com.rsi.devjam.togglz;

import org.springframework.stereotype.Component;
import org.togglz.core.Feature;
import org.togglz.core.manager.TogglzConfig;
import org.togglz.core.repository.StateRepository;
import org.togglz.core.user.UserProvider;
import org.togglz.servlet.user.ServletUserProvider;

@Component
public class TogglzConfiguration implements TogglzConfig {

    public Class<? extends Feature> getFeatureClass() {
        return Features.class;
    }

    public StateRepository getStateRepository() {
        return null;
    }

    public UserProvider getUserProvider() {
        return new ServletUserProvider("ROLE_ADMIN");
    }

}