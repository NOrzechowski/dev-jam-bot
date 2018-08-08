package com.rsi.devjam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.togglz.core.manager.EnumBasedFeatureProvider;
import org.togglz.core.spi.FeatureProvider;

import com.rsi.devjam.utilities.Features;

@SpringBootApplication(scanBasePackages = { "me.ramswaroop.jbot", "com.rsi" })
public class ProjectBotApplication {
	public static void main(String[] args) {
		SpringApplication.run(ProjectBotApplication.class, args);
	}

	@SuppressWarnings("unchecked")
	@Bean
	public FeatureProvider featureProvider() {
		return new EnumBasedFeatureProvider(Features.class);
	}
}
