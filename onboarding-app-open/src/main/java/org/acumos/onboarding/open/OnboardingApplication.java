package org.acumos.onboarding.open;

import org.acumos.onboarding.common.utils.EELFLoggerDelegate;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.ConfigurableEnvironment;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
@ComponentScan("org.acumos.onboarding")
public class OnboardingApplication implements ApplicationContextAware
{
	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(OnboardingApplication.class);

	public static final String CONFIG_ENV_VAR_NAME = "SPRING_APPLICATION_JSON";

	
	
	public static void main(String[] args) throws Exception {
		final String springApplicationJson = System.getenv(CONFIG_ENV_VAR_NAME);

		if (springApplicationJson != null && springApplicationJson.contains("{")) {
			final ObjectMapper mapper = new ObjectMapper();
			// ensure it's valid
			mapper.readTree(springApplicationJson);
			logger.info("Successfully parsed configuration from environment {" + CONFIG_ENV_VAR_NAME + "}");
		} else {

			logger.warn("No configuration found in environment {" + CONFIG_ENV_VAR_NAME + "}");
		}
		
		SpringApplication.run(OnboardingApplication.class, args);
	}


	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		((ConfigurableEnvironment) context.getEnvironment()).setActiveProfiles("src");
	}

}
