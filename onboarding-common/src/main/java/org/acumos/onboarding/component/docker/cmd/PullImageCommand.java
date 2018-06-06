package org.acumos.onboarding.component.docker.cmd;

import org.acumos.onboarding.common.utils.EELFLoggerDelegate;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.PullImageCmd;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.PullResponseItem;
import com.github.dockerjava.core.command.PullImageResultCallback;

public class PullImageCommand extends DockerCommand{
	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(PullImageCommand.class);
	
	String repository;

	public PullImageCommand(String repository) {
		this.repository = repository;
	} 
	
	public String getRepository() {
		return repository;
	}

	@Override
	public void execute() throws DockerException {
		String imageFullName = "nexus3.acumos.org:10004/onboarding-base-r:1.0";
		logger.debug("Full Image Name: " + imageFullName);
		final DockerClient client = getClient();
		PullImageCmd pullImageCmd = client.pullImageCmd(imageFullName);
		PullImageResultCallback callback = new PullImageResultCallback() {
			@Override
			public void onNext(PullResponseItem item) {
				super.onNext(item);
			}

			@Override
			public void onError(Throwable throwable) {
				logger.error(EELFLoggerDelegate.errorLogger,"Failed to pull image:" + throwable.getMessage());
				super.onError(throwable);
			}
		};
		pullImageCmd.exec(callback).awaitSuccess();
		
	}

	@Override
	public String getDisplayName() {
		// TODO Auto-generated method stub
		return "Pull image";
	}
	

}
