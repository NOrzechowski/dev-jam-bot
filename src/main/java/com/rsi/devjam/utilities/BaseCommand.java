package com.rsi.devjam.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.rsi.slack.MyEvent;

import me.ramswaroop.jbot.core.slack.models.Event;
import me.ramswaroop.jbot.core.slack.models.User;

public class BaseCommand {
	protected static String ASTERISKS = "*********************************************************\n";
	protected static String DASHES = "------------------------------";
	protected static String SPACE = "        ";

	@Value("${slackApi}")
	private String slackApi;

	@Value("${slackBotToken}")
	private String token;

	protected static boolean validateInput(MyEvent event) {
		return (event.getUserId() != null && !StringUtils.isEmpty(event.getText()));
	}
	protected static boolean validateInput(Event event) {
		return (event.getUserId() != null && !StringUtils.isEmpty(event.getText()));
	}

	protected static boolean validateTextInput(MyEvent event) {
		return !StringUtils.isEmpty(event.getText());
	}

	protected static int getRandomNumber(int max) {
		Random rand = new Random();
		return rand.nextInt(max);
	}

	private String getUserConnectApi() {
		return String.format("%s/users.info?token={%s}", new Object[] { slackApi, token });
	}

	protected User getUser(Event event) {
		RestTemplate template = new RestTemplate();
		template = new RestTemplate();
		ResponseEntity<UserResponse> obj = template.getForEntity(getUserConnectApi() + "&user=" + event.getUserId(),
				UserResponse.class, token);

		if (obj != null) {
			UserResponse userResponse = obj.getBody();
			return userResponse.getUser();
		} else {
			return null;
		}
	}
	
	protected User getUser(String userId) {
		RestTemplate template = new RestTemplate();
		template = new RestTemplate();
		ResponseEntity<UserResponse> obj = template.getForEntity(getUserConnectApi() + "&user=" +userId,
				UserResponse.class, token);

		if (obj != null) {
			UserResponse userResponse = obj.getBody();
			return userResponse.getUser();
		} else {
			return null;
		}
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
