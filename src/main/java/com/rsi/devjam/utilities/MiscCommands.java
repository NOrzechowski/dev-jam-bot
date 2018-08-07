package com.rsi.devjam.utilities;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import me.ramswaroop.jbot.core.slack.models.Event;

@Component
public class MiscCommands extends BaseCommand {

	@Value("${faqUrl}")
	private String faqUrl;

	@Value("${helpUrl}")
	private String helpUrl;

	@Value("${deadlinesUrl}")
	private String deadlinesUrl;

	private static final String FAQ = "FAQ";
	private static final String HELP = "Help Document";
	private static final String DEADLINES = "Important Upcoming Dates";
	private static final String URL_TEMPLATE = "*The %s can be found here: %s *\n";
	private static final String DEADLINES_FILE = "deadlines.txt";

	public String getFaq(Event event) {
		StringBuilder output = new StringBuilder();
		if (validateInput(event)) {
			output.append(String.format(URL_TEMPLATE, new Object[] { FAQ, faqUrl }));
			return output.toString();
		}
		return null;
	}

	public String getHelp(Event event) {
		StringBuilder output = new StringBuilder();
		if (validateInput(event)) {
			output.append(String.format(URL_TEMPLATE, new Object[] { HELP, helpUrl }));
			return output.toString();
		}
		return null;
	}

	public String getDeadlines(Event event) {
		StringBuilder output = new StringBuilder();
		if (validateInput(event)) {
			output.append(getFileAsString(DEADLINES_FILE));
			return output.toString();
		}
		return null;
	}

}
