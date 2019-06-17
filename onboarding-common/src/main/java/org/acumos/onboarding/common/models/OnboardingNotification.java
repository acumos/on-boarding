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
import org.acumos.onboarding.common.utils.LogBean;
import org.acumos.onboarding.common.utils.LoggerDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private Long taskId;
	private MLPTask task;

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	private CommonDataServiceRestClientImpl cdmsClient;
	private static Logger log = LoggerFactory.getLogger(OnboardingNotification.class);
    LoggerDelegate logger = new LoggerDelegate(log);
    
	public OnboardingNotification(String cmnDataSvcEndPoinURL, String cmnDataSvcUser, String cmnDataSvcPwd) {

		cdmsClient = new CommonDataServiceRestClientImpl(cmnDataSvcEndPoinURL, cmnDataSvcUser, cmnDataSvcPwd,null);
		task = new MLPTask();
	}

	public OnboardingNotification(String cmnDataSvcEndPoinURL, String cmnDataSvcUser, String cmnDataSvcPwd, String requestId) {

		cdmsClient = new CommonDataServiceRestClientImpl(cmnDataSvcEndPoinURL, cmnDataSvcUser, cmnDataSvcPwd,null);
		cdmsClient.setRequestId(requestId);
		task = new MLPTask();
	}

	// current step, status and description sent to be logged.
	public void notifyOnboardingStatus(String currentstep, String currentStatus, String currentDescription) throws Exception{
		logger.debug("Notify " + currentDescription);

		try {
			if (trackingId != null) {

				String desc;

				MLPTaskStepResult taskResult = new MLPTaskStepResult();

				taskResult.setTaskId(getTaskId());
				taskResult.setStartDate(Instant.now());
				taskResult.setEndDate(Instant.now());
				taskResult.setStatusCode(currentStatus);
				taskResult.setName(currentstep);

				if (currentDescription != null && !currentDescription.isEmpty()) {
					desc = currentDescription.substring(0, Math.min(currentDescription.length(), 8000));
					taskResult.setResult(desc);
				}
				logger.debug("Step: " + currentstep + " with Status: " + currentStatus);

				logger.debug("Sending Notification for Task: " + getTaskId() + " with Description: " + currentDescription);
				cdmsClient.createTaskStepResult(taskResult);
			}
		} catch (Exception e) {
			logger.error("Failed to Notify");
		}
	}
	
	public void notifyOnboardingStatus(String currentstep, String currentStatus, String currentDescription,
			LogBean logBean) {
		try {
			logger.debug("Notify " + currentDescription, logBean);
			notifyOnboardingStatus(currentstep, currentStatus, currentDescription);
			logger.debug("Step: " + currentstep + " with Status: " + currentStatus, logBean);
			logger.debug("Sending Notification for Task: " + getTaskId() + " with Description: " + currentDescription, logBean);
		}catch (Exception e) {
			logger.error("Failed to Notify");
		}
	}

	// Get/Set methods ------

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

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
