package com.rsi.devjam.utilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.rsi.devjam.models.Participant;
import com.rsi.devjam.repository.ParticipantRepository;
import com.rsi.slack.MyEvent;

import me.ramswaroop.jbot.core.slack.models.User;

@Component
public class MiscCommands extends BaseCommand {

	@Autowired
	ParticipantRepository particpantRepository;

	@Value("${faqUrl}")
	private String faqUrl;

	@Value("${helpUrl}")
	private String helpUrl;

	@Value("${deadlinesUrl}")
	private String deadlinesUrl;

	@Value("${techRubricUrl}")
	private String techRubricUrl;

	@Value("${nonTechRubricUrl}")
	private String nonTechRubricUrl;

	@Value("${exampleProjectTech}")
	private String exampleProjectTech;

	@Value("${exampleProjectNonTech}")
	private String exampleProjectNonTech;

	private static final String FAQ = "FAQ";
	private static final String HELP = "Help Document";
	private static final String DEADLINES = "Important Upcoming Dates";
	private static final String URL_TEMPLATE = "*The %s can be found here: %s *\n";
	private static final String RUBRIC_TEMPLATE = "*The judging rubrics can be found here:*\n - *Technical Rubric:* %s\n - *Non-Technical Rubric:* %s:\n";
	private static final String EXAMPLE_PROJECTS_TEMPLATE = "*The example projects can be found here:*\n - *Technical Project:* %s\n - *Non-Technical Project:* %s:\n";
	private static final String DEADLINES_FILE = "deadlines.txt";

	public String getFaq(MyEvent event) {
		StringBuilder output = new StringBuilder();
		if (validateInput(event)) {
			output.append(String.format(URL_TEMPLATE, new Object[] { FAQ, faqUrl }));
			return output.toString();
		}
		return null;
	}

	public String getHelp(MyEvent event) {
		StringBuilder output = new StringBuilder();
		if (validateInput(event)) {
			output.append(String.format(URL_TEMPLATE, new Object[] { HELP, helpUrl }));
			return output.toString();
		}
		return null;
	}

	public String getDeadlines(MyEvent event) {
		StringBuilder output = new StringBuilder();
		if (validateInput(event)) {
			output.append(getFileAsString(DEADLINES_FILE));
			return output.toString();
		}
		return null;
	}

	public String getRubric(MyEvent event) {
		StringBuilder output = new StringBuilder();
		if (validateInput(event)) {
			output.append(String.format(RUBRIC_TEMPLATE, new Object[] { techRubricUrl, nonTechRubricUrl }));
			return output.toString();
		}
		return null;
	}

	public String getExampleProjects(MyEvent event) {
		StringBuilder output = new StringBuilder();
		if (validateInput(event)) {
			output.append(String.format(EXAMPLE_PROJECTS_TEMPLATE,
					new Object[] { exampleProjectTech, exampleProjectNonTech }));
			return output.toString();
		}
		return null;
	}

	public void upsertUser(MyEvent event) {
		if (validateInput(event)) {
			User userData = getUser(event);
			Participant currentUser = particpantRepository.findByUser(event.getUserId());
			if (userData != null) {
				String username = userData.getProfile().getRealName();
				if (currentUser == null) {
					currentUser = new Participant(event.getUserId(), username);
				}
				currentUser.setName(username);
				currentUser.setEmail(userData.getProfile().getEmail());
				particpantRepository.save(currentUser);
			}
		}
	}

	public void upsertUser(String userId) {
		User userData = getUser(userId);
		Participant currentUser = particpantRepository.findByUser(userId);
		if (userData != null) {
			String username = userData.getProfile().getRealName();
			if (currentUser == null) {
				currentUser = new Participant(userId, username);
			}
			currentUser.setName(username);
			currentUser.setEmail(userData.getProfile().getEmail());
			particpantRepository.save(currentUser);
		}

	}

}
