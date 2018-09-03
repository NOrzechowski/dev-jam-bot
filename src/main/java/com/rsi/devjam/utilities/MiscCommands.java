package com.rsi.devjam.utilities;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.rsi.devjam.models.MyUser;
import com.rsi.devjam.models.Participant;
import com.rsi.devjam.models.Question;
import com.rsi.devjam.repository.ParticipantRepository;
import com.rsi.devjam.repository.QuestionRepository;
import com.rsi.slack.MyEvent;

@Component
public class MiscCommands extends BaseCommand {

	@Autowired
	ParticipantRepository particpantRepository;

	@Autowired
	QuestionRepository questionRepository;

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

	@Value("${projectSubmissionUrl}")
	private String projectSubmissionUrl;

	@Value("${exampleProjectNonTech}")
	private String exampleProjectNonTech;

	private static final String FAQ = "FAQ";
	private static final String HELP = "Help Document";
	private static final String DEADLINES = "Important Upcoming Dates";
	private static final String URL_TEMPLATE = "*The %s can be found here: %s *\n";
	private static final String RUBRIC_TEMPLATE = "*The judging rubrics can be found here:*\n - *Technical Rubric:* %s\n - *Non-Technical Rubric:* %s:\n";
	private static final String EXAMPLE_PROJECTS_TEMPLATE = "*The example projects can be found here:*\n - *Technical Project:* %s\n - *Non-Technical Project:* %s:\n";
	private static final String DEADLINES_FILE = "deadlines.txt";

	private static String[] EIGHT_BALL_ANSWERS = new String[] { "It is certain.", "It is decidedly so.",
			"Without a doubt.", "Yes - definitely.", "You may rely on it.", "As I see it, yes.", "Most likely.",
			" Outlook good.", "Yes.", "Signs point to yes.", "Reply hazy, try again", "Ask again later.",
			"Better not tell you now.", "Cannot predict now.", "Concentrate and ask again.", "Don't count on it.",
			"My reply is no.", "My sources say no", "Outlook not so good.", "Very doubtful." };

	private static String[] SIZES = new String[] { "s", "m", "l", "xl", "xxl", "S", "M", "L", "XL", "XXL" };

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

	public CompositeResponse eightBall(MyEvent event) {
		StringBuilder output = new StringBuilder();
		boolean errorOccured = false;
		if (validateInput(event)) {
			Participant currentUser = particpantRepository.findByUser(event.getUserId());
			if (event.getText().split(":8ball:").length >= 1) {
				System.out.println("size: " + event.getText().split(":8ball:").length);
				String question = event.getText().substring(event.getText().indexOf(":8ball:", 0));
				System.out.println("question: " + question);

				if (question.contains("?")) {
					Question q = new Question();
					String answer = EIGHT_BALL_ANSWERS[getRandomNumber(EIGHT_BALL_ANSWERS.length)];
					q.setQuestion(question);
					q.setAnswer(answer);
					q.setIs8Ball(true);
					q.setAskedBy(currentUser);
					output.append("_ :8ball: - \"" + answer + "\"_\n");
				} else {
					errorOccured = true;
					output.append("your question must have a question mark in it, silly\n");
				}
			} else {
				errorOccured = true;
			}

			return new CompositeResponse(output.toString(), errorOccured);
		}
		return null;
	}

	public String getProjectSubmission(MyEvent event) {
		StringBuilder output = new StringBuilder();
		if (validateInput(event)) {
			output.append(String.format(URL_TEMPLATE, new Object[] { "Submission Form", projectSubmissionUrl }));
			return output.toString();
		}
		return null;
	}

	public CompositeResponse tShirtInit(MyEvent event) {
		StringBuilder output = new StringBuilder();
		boolean errorOccured = false;
		if (validateInput(event)) {
			output.append(
					"*If you would like to sign up for a t-shirt, please enter a size (S, M, L, XL, XXL, or 'cancel' to cancel or to un-sign up)*\n");
			return new CompositeResponse(output.toString(), errorOccured);
		}
		return null;
	}

	public String tShirtFinal(MyEvent event) {
		StringBuilder output = new StringBuilder();
		if (validateInput(event)) {
			Participant currentUser = particpantRepository.findByUser(event.getUserId());
			String size = event.getText();
			if (!Arrays.asList(SIZES).contains(size)) {
				currentUser.settShirtSize(null);
				currentUser.setWantsTShirt(false);
				output.append(
						"*You have entered cancel or an invalid size and will not be signed up to receive a t-shirt.*\n");
			} else {
				currentUser.settShirtSize(size);
				currentUser.setWantsTShirt(true);
				output.append(String.format("*Thanks <@%s>. You are now signed up to recieve a super cool, size '"
						+ size + ",' dev jam t-shirt *\n", new Object[] { currentUser.getUser() }));
			}
			particpantRepository.save(currentUser);
			return output.toString();
		}
		return null;
	}

	public String signedUpForTShirt(MyEvent event) {
		StringBuilder output = new StringBuilder();
		if (validateInput(event)) {
			output.append(
					"*If you would like to sign up for a t-shirt, please enter a size (S, M, L, XL, or 'cancel' to cancel or to un-sign up)*\n");
			return output.toString();
		}
		return null;
	}

	public void upsertUser(MyEvent event) {
		if (validateInput(event)) {
			MyUser userData = getUser(event);
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

	public String getTShirtParticipants(MyEvent event) {
		StringBuilder output = new StringBuilder();
		if (validateInput(event)) {
			Iterable<Participant> currentlySignedUp = particpantRepository.findAll();
			output.append(ASTERISKS);
			output.append("*This is who is currently signed up for a t-shirt: *\n");
			output.append(ASTERISKS);
			if (currentlySignedUp.iterator().hasNext()) {
				currentlySignedUp.forEach(participant -> {
					if (participant.isWantsTShirt()) {
						output.append("- *name:* " + participant.getName() + ", *size:* " + participant.gettShirtSize()
								+ "\n");
					}
				});
			}
			output.append(ASTERISKS);
			return output.toString();
		}
		return null;

	}

	public void upsertUser(String userId) {
		MyUser userData = getUser(userId);
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

	public String getUserByEmail(MyEvent event) {
		StringBuilder output = new StringBuilder();
		if (validateInput(event)) {
			String[] entries = event.getText().split(" ");

			String email = entries[1].trim();
			System.out.println("email: " + email);
			MyUser userData = getUserByEmail(email);
			System.out.println("found: " + userData.getName());
			output.append(userData.getProfile().getTeam());

			return output.toString();
		}
		return null;
	}

	public String loadUsersByChannel(MyEvent event) {
		StringBuilder output = new StringBuilder();
		if (validateInput(event)) {
			String[] entries = event.getText().split(" ");
			String channel = entries[1].trim();
			LinkedList<MyUser> userData = getUsersByChannel(channel);
			for (MyUser user : userData) {
				upsertUser(user.getId());
			}
			output.append(userData.size() + " users loaded");
			return output.toString();
		}
		return null;
	}

	/*
	 * -- !doorPrize <random user> won <random item>
	 * 
	 * //items an old shoe a lime green phone case for a 3 year old Android
	 * phone. The Parking Spot! (Not really) the privilege of being here. free
	 * coffee! nothing... bragging rights. pocket lint
	 */
	private static final String[] DOOR_PRIZES = new String[] { "an old shoe",
			"a lime green phone case for a 3 year old Android phone.", "the Parking Spot! (Not really)",
			"the privilege of being here.", "free coffee!", "nothing...", "bragging rights", "pocket lint" };

	public String doorPrize(MyEvent event) {
		String winningFormat = "*%s* won %s\n";
		StringBuilder output = new StringBuilder();
		if (validateInput(event)) {
			output.append(String.format(winningFormat,
					new Object[] { getRandomUser().getName(), DOOR_PRIZES[getRandomNumber(DOOR_PRIZES.length)] }));
			return output.toString();
		}
		return null;
	}

	/*
	 * !pixieStick // Limit to once per day You've had <count> pixie sticks.
	 * <phrase> 1st phrase: You've just eaten your first pixie stick. It was
	 * super delicious!
	 * 
	 * 2 - 5 & 7-11 & 12+ phrases Sugar! Also know as unicorn sneezes It taste
	 * like Candyland Coding fuel No one can beat Laura's Dev Jam pixie stick
	 * record This is the stuff dreams are made of Digital SUGAR!
	 * 
	 * 6 (special phrase) After eating a half dozen pixie sticks, you can now
	 * smell color.
	 * 
	 * 12 (special phrase) You've had a dozen pixie sticks. I think you've
	 * beaten Laura's Dev Jam pixie stick record. Though we all lost count after
	 * a while.
	 * 
	 * 20 (special phrase) I think we're just making a mountain of sugar at this
	 * point.
	 * 
	 * 20+ all the pixie sticks are gone :(
	 */
	public CompositeResponse pixieStix(MyEvent event) {
		String stixFormat = "*%s*\n";
		StringBuilder output = new StringBuilder();
		boolean errsOccured = false;
		if (validateInput(event)) {
			Participant currentUser = particpantRepository.findByUser(event.getUserId());
			int n = currentUser.getPixieStixCount();
			LocalDate currDate = currentUser.getPixieStixLastRun();
			if (currDate == null) {
				currDate = LocalDate.now().minusDays(1);
			}
			LocalDate lastRunPlusOne = LocalDate.now();//currDate.plusDays(1);
			if (!lastRunPlusOne.isAfter(LocalDate.now())) {
				n = n + 1;
				currentUser.setPixieStixCount(n);
				currentUser.setPixieStixLastRun(LocalDate.now());
				particpantRepository.save(currentUser);
				output.append(String.format(stixFormat, new Object[] { getPixieStixPhrase(n) }));
			} else {
				errsOccured = true;
				output.append("Only one pixie stick per day!!!\n");
			}

			return new CompositeResponse(output.toString(), errsOccured);
		}
		return null;
	}

	private String getPixieStixPhrase(int n) {
		String defaultPhrase = "Sugar! Also know as unicorn sneezes It taste like Candyland Coding fuel No one can beat Laura's Dev Jam pixie stick record This is the stuff dreams are made of - Digital SUGAR!";

		switch (n) {
		case 1:
			return "You've just eaten your first pixie stick. It was super delicious!";
		case 6:
			return "After eating a half dozen pixie sticks, you can now smell color.";

		case 12:
			return "You've had a dozen pixie sticks. I think you've beaten Laura's Dev Jam pixie stick record. Though we all lost count after a while.";
		case 20:
			return "I think we're just making a mountain of sugar at this point.";
		default:
			if ((2 <= n && n <= 5) || (7 <= n && n < 12) || (12 < n && n < 20)) {
				return defaultPhrase;
			} else if (n > 20) {
				return "All your pixie sticks are gone! :(";
			}
			return "";
		}
	}

}
