package com.rsi.devjam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"me.ramswaroop.jbot", "com.rsi"})
public class ProjectBotApplication {
	public static void main(String[] args) {
		SpringApplication.run(ProjectBotApplication.class, args);
	}
}
