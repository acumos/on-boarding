/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
 * ===================================================================================
 * This Acumos software file is distributed by AT&T and Tech Mahindra
 * under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * This file is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===============LICENSE_END=========================================================
 */
package org.acumos.onboarding.common.models;

import java.time.Instant;
import java.util.Date;

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.domain.MLPTask;
import org.acumos.cds.domain.MLPTaskStepResult;
import org.acumos.onboarding.common.utils.EELFLoggerDelegate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@javax.xml.bind.annotation.XmlRootElement
@JsonInclude(Include.NON_NULL)
public class OnboardingNotification {

	private Long stepResultId;
	private String trackingId;
	private String stepCode;
	private String solutionId;
	private String revisionId;
	private String artifactId;
	private String userId;
	private String name;
	private String statusCode;
	private String result;
	private Date startDate;
	private Date endDate;
	private String requestId;
	private MLPTask task;

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	private CommonDataServiceRestClientImpl cdmsClient;
	private static EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(OnboardingNotification.class);

	public OnboardingNotification(String cmnDataSvcEndPoinURL, String cmnDataSvcUser, String cmnDataSvcPwd) {

		cdmsClient = new CommonDataServiceRestClientImpl(cmnDataSvcEndPoinURL, cmnDataSvcUser, cmnDataSvcPwd,null);
		this.initMLPTask();
	}

	public OnboardingNotification(String cmnDataSvcEndPoinURL, String cmnDataSvcUser, String cmnDataSvcPwd, String requestId) {

		cdmsClient = new CommonDataServiceRestClientImpl(cmnDataSvcEndPoinURL, cmnDataSvcUser, cmnDataSvcPwd,null);
		cdmsClient.setRequestId(requestId);
		this.initMLPTask();
	}

	// current step, status and description sent to be logged.
	public void notifyOnboardingStatus(String currentstep, String currentStatus, String currentDescription) {
		logger.debug(EELFLoggerDelegate.debugLogger, "Notify " + currentDescription);

		try {
			if (trackingId != null) {

				String desc;

				MLPTaskStepResult taskResult = new MLPTaskStepResult();

				taskResult.setTaskId(task.getTaskId());
				taskResult.setStartDate(Instant.now());
				taskResult.setEndDate(Instant.now());
				taskResult.setStatusCode(currentStatus);
				taskResult.setName(currentstep);

				if (currentDescription != null && !currentDescription.isEmpty()) {
					desc = currentDescription.substring(0, Math.min(currentDescription.length(), 8000));
					taskResult.setResult(desc);
				}
				
				logger.debug(EELFLoggerDelegate.debugLogger, "Sending Notification for Task: " + task.getTaskId() + "with Description: " + currentDescription);
				cdmsClient.createTaskStepResult(taskResult);
			}
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "Failed to Notify");
		}
	}
	
	public void initMLPTask() {

		try {
			MLPTask task = new MLPTask();
			task.setTaskCode("OB");
			task = cdmsClient.createTask(task);
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "Error creating Task Object");
		}
	}
	// Get/Set methods ------

	public Long getStepResultId() {
		return stepResultId;
	}

	public void setStepResultId(Long stepResultId) {
		this.stepResultId = stepResultId;
		
	}

	public String getTrackingId() {
		return trackingId;
	}

	public void setTrackingId(String trackingId) {
		this.trackingId = trackingId;
		task.setTrackingId(trackingId);
	}

	public String getStepCode() {
		return stepCode;
	}

	public void setStepCode(String stepCode) {
		this.stepCode = stepCode;
	}

	public String getSolutionId() {
		return solutionId;
	}

	public void setSolutionId(String solutionId) {
		this.solutionId = solutionId;
		task.setSolutionId(solutionId);
	}

	public String getRevisionId() {
		return revisionId;
	}

	public void setRevisionId(String revisionId) {
		this.revisionId = revisionId;
		task.setRevisionId(revisionId);
	}

	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
		task.setUserId(userId);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		task.setName(name);
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
		task.setStatusCode(statusCode);
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}
