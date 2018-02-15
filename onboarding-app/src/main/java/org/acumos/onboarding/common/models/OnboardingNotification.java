package org.acumos.onboarding.common.models;

import java.util.Date;

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.domain.MLPStepResult;

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
	
	private CommonDataServiceRestClientImpl cdmsClient;
    
	public OnboardingNotification(String cmnDataSvcEndPoinURL,String cmnDataSvcUser,String cmnDataSvcPwd) 
	{
		
		cdmsClient = new  CommonDataServiceRestClientImpl(cmnDataSvcEndPoinURL,cmnDataSvcUser,cmnDataSvcPwd);
	}

	// current step, status and description sent to be logged.
	public void notifyOnboardingStatus(String currentstep, String currentStatus, String currentDescription,OnboardingNotification onboardingStatus) {
		if (trackingId != null) {

			MLPStepResult stepResult = new MLPStepResult();
			
			
			stepResult.setSolutionId(onboardingStatus.getSolutionId());
			stepResult.setRevisionId(onboardingStatus.getRevisionId());
			stepResult.setArtifactId(null);// right now it is null in portal we need to get this from somewhere.
			stepResult.setUserId(onboardingStatus.getUserId());
			stepResult.setStatusCode(currentStatus);
			stepResult.setTrackingId(onboardingStatus.getTrackingId());
			stepResult.setName(currentstep);
			stepResult.setStartDate(new Date());
			stepResult.setEndDate(new Date());
			stepResult.setStepCode("OB");
			cdmsClient.createStepResult(stepResult);
	
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
