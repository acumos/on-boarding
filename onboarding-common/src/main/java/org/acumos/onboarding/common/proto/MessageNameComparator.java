package org.acumos.onboarding.common.proto;

import java.util.Comparator;


public class MessageNameComparator implements Comparator<ProtobufMessage> {

	@Override
	public int compare(ProtobufMessage pm1, ProtobufMessage pm2) {
		return pm1.getName().compareTo(pm2.getName());
	}

}
