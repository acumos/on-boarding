package org.acumos.onboarding;

import static org.mockito.Mockito.mock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.onboarding.common.utils.UtilityFunction;
import org.acumos.onboarding.logging.OnboardingLogAdapter;
import org.acumos.onboarding.logging.OnboardingLogAdapter.HttpServletRequestAdapter;
import org.acumos.onboarding.logging.OnboardingLogAdapter.RequestAdapter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(MockitoJUnitRunner.class)
public class OnboardingLogAdapterTest {
	Logger logger = LoggerFactory.getLogger(OnboardingLogAdapter.class);
	OnboardingLogAdapter obrdLA = new OnboardingLogAdapter(logger);
	final HttpServletRequest request = mock(HttpServletRequest.class);

	@Test
	public void setEnteringMDCsTest() {
		request.setAttribute("OnboardingLogAdapter", obrdLA);
		obrdLA.setEnteringMDCs(new HttpServletRequestAdapter(request));
	}

	@Test
	public void setMDCsTest() {
		try {
			obrdLA.getResponseDescriptor().setMDCs();
		} catch (Exception e) {
			Assert.fail("Failed while testing setMDCsTest - " + e.getMessage());
		}
	}
	
	@Test
	public void enteringTest() {
		try {
			obrdLA.entering(request, "format", Mockito.anyObject());
			obrdLA.entering(request, "String", Mockito.anyObject());
			obrdLA.entering(request);
		} catch (Exception e) {
			Assert.fail("Failed while testing enteringTest - " + e.getMessage());
		}
	}
}
