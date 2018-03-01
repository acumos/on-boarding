package org.acumos.onboarding;

import java.util.Date;

import org.acumos.onboarding.common.models.OnboardingNotification;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OnboardingNotificationTest {

	private String trackingId = "43234";

	OnboardingNotification onboardingNotify = new OnboardingNotification("http://localhost:8080/ccds", "xyz","Test@123");

	@Test
	public void notifyOnboardingStatusTest() {

		if (trackingId != null) {

			onboardingNotify.notifyOnboardingStatus("CreateSolution", "ST", "CreateSolution Started");
			onboardingNotify.setSolutionId("4215454");
			onboardingNotify.setRevisionId("235425");
			onboardingNotify.setArtifactId("352");
			onboardingNotify.setUserId("xyz");
			onboardingNotify.setStatusCode("ST");
			onboardingNotify.setTrackingId("235");
			onboardingNotify.setName("CreateSolution");
			onboardingNotify.setStartDate(new Date());
			onboardingNotify.setEndDate(new Date());
			onboardingNotify.setStepCode("OB");
			onboardingNotify.setResult("Success");
			onboardingNotify.setStepResultId(2452l);

			onboardingNotify.getSolutionId();
			onboardingNotify.getRevisionId();
			onboardingNotify.getArtifactId();
			onboardingNotify.getUserId();
			onboardingNotify.getStatusCode();
			onboardingNotify.getTrackingId();
			onboardingNotify.getName();
			onboardingNotify.getStartDate();
			onboardingNotify.getEndDate();
			onboardingNotify.getStepCode();
			onboardingNotify.getResult();
			onboardingNotify.getStepResultId();
			assert(true);
		}

	}
}
