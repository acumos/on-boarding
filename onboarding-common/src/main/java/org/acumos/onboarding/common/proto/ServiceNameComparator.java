package org.acumos.onboarding.common.proto;

import java.util.Comparator;


public class ServiceNameComparator implements Comparator<ProtobufServiceOperation> {

	@Override
	public int compare(ProtobufServiceOperation pso1, ProtobufServiceOperation pso2) {
		return pso1.getName().compareTo(pso2.getName());
	}

}
