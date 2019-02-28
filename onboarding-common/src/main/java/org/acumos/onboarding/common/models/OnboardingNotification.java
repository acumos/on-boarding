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
	}

	public OnboardingNotification(String cmnDataSvcEndPoinURL, String cmnDataSvcUser, String cmnDataSvcPwd, String requestId) {

		cdmsClient = new CommonDataServiceRestClientImpl(cmnDataSvcEndPoinURL, cmnDataSvcUser, cmnDataSvcPwd,null);
		cdmsClient.setRequestId(requestId);
	}

	// current step, status and description sent to be logged.
	public void notifyOnboardingStatus(String currentstep, String currentStatus, String currentDescription) {
		logger.debug(EELFLoggerDelegate.debugLogger,"Notify" + currentDescription);
		if (trackingId != null) {

			String desc;

			MLPTask task = new MLPTask();
			MLPTaskStepResult taskResult = new MLPTaskStepResult();

			task.setUserId(this.userId);
			task.setStatusCode(currentStatus);
			task.setTrackingId(this.trackingId);
			task.setName(currentstep);
			task.setTaskCode("OB");

			taskResult.setStartDate(Instant.now());
			taskResult.setEndDate(Instant.now());
			taskResult.setStatusCode(currentStatus);
			taskResult.setName(currentstep);

			logger.debug(EELFLoggerDelegate.debugLogger,"Setting values to Task and Task Step Result");

			if (currentDescription != null && !currentDescription.isEmpty()) {
				desc = currentDescription.substring(0, Math.min(currentDescription.length(), 8000));
				taskResult.setResult(desc);
			}
			if (this.solutionId != null && !this.solutionId.isEmpty()) {
				task.setSolutionId(this.solutionId);
			}
			if (this.revisionId != null && !this.revisionId.isEmpty()) {
				task.setRevisionId(this.revisionId);
			}

			logger.debug(EELFLoggerDelegate.debugLogger,"Setting values to CDS Client");
			MLPTask ts = cdmsClient.createTask(task);
			logger.debug(EELFLoggerDelegate.debugLogger,"TaskID: "+ ts.getTaskId());
			taskResult.setTaskId(ts.getTaskId());
			cdmsClient.createTaskStepResult(taskResult);
		}
		logger.debug(EELFLoggerDelegate.debugLogger,"Send Notification to DB Ended");
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
	}

	public String getRevisionId() {
		return revisionId;
	}

	public void setRevisionId(String revisionId) {
		this.revisionId = revisionId;
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
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
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
