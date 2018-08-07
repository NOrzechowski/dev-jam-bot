package com.rsi.devjam.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import me.ramswaroop.jbot.core.slack.models.Event;
import me.ramswaroop.jbot.core.slack.models.User;

public class BaseCommand {

	@Value("${slackApi}")
	private String slackApi;

	@Value("${slackBotToken}")
	private String token;

	protected static boolean validateInput(Event event) {
		return (event.getUserId() != null && !StringUtils.isEmpty(event.getText()));
	}

	protected static int getRandomNumber(int max) {
		Random rand = new Random();
		return rand.nextInt(max);
	}

	private String getUserConnectApi() {
		return String.format("%s/users.info?token={%s}", new Object[] { slackApi, token });
	}

	protected User getUser(Event event) {
		UserResponse userResponse = new RestTemplate()
				.getForEntity(getUserConnectApi() + "&user=" + event.getUserId(), UserResponse.class, token).getBody();
		return userResponse.getUser();
	}

	protected String getFileAsString(String filename) {
		StringBuilder data = new StringBuilder();
		try {
		     ClassPathResource resource = new ClassPathResource(filename);
             BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
             data.append(reader.lines().collect(Collectors.joining("\n"))).append("\n");
             reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data.toString().trim();
	}
}
