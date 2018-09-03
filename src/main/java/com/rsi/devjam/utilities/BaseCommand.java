package com.rsi.devjam.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.google.common.collect.Lists;
import com.rsi.devjam.models.MyUser;
import com.rsi.devjam.models.Participant;
import com.rsi.devjam.repository.ParticipantRepository;
import com.rsi.slack.MyEvent;

import me.ramswaroop.jbot.core.slack.models.Event;

public class BaseCommand {
	protected static String ASTERISKS = "*********************************************************\n";
	protected static String DASHES = "------------------------------";
	protected static String SPACE = "        ";

	@Value("${slackApi}")
	private String slackApi;

	@Value("${slackBotToken}")
	private String token;
	
	@Autowired
	ParticipantRepository particpantRepository;

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

	private String getUserByEmailConnectApi() {
		return String.format("%s/users.lookupByEmail?token={%s}", new Object[] { slackApi, token });
	}

	private String getChannelInfo() {
		return String.format("%s/channels.info?token={%s}", new Object[] { slackApi, token });
	}

	protected MyUser getUser(Event event) {
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

	protected MyUser getUserByEmail(String email) {
		RestTemplate template = new RestTemplate();
		template = new RestTemplate();
		ResponseEntity<UserResponse> obj = template.getForEntity(getUserByEmailConnectApi() + "&email=" + email,
				UserResponse.class, token);

		if (obj != null) {
			UserResponse userResponse = obj.getBody();
			return userResponse.getUser();
		} else {
			return null;
		}
	}
	//mob_general = CBW8RJUU9, mob_dev_jam = CC25W819R
	protected LinkedList<MyUser> getUsersByChannel(String channel) {
		RestTemplate template = new RestTemplate();
		template = new RestTemplate();
		ResponseEntity<ChannelResponse> obj = template.getForEntity(getChannelInfo() + "&channel=" + channel,
				ChannelResponse.class, token);

		if (obj != null) {
			ChannelResponse resp = obj.getBody();
			LinkedList<MyUser> users = new LinkedList<MyUser>();
			for(String member: resp.getChannel().getMembers()) {
				System.out.println("user: " + member);
				users.add(getUser(member));
			}
			return users;
		} else {
			return null;
		}
	}
	
	protected Participant getRandomUser() {
		Iterable<Participant> users = particpantRepository.findAll();
		List<Participant> userList = Lists.newArrayList(users);
		return userList.get(getRandomNumber(userList.size()));
	}

	protected MyUser getUser(String userId) {
		RestTemplate template = new RestTemplate();
		template = new RestTemplate();
		ResponseEntity<UserResponse> obj = template.getForEntity(getUserConnectApi() + "&user=" + userId,
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
