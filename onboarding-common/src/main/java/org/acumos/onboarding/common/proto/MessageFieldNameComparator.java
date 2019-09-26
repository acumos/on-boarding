package org.acumos.onboarding.common.proto;

import java.util.Comparator;

public class MessageFieldNameComparator implements Comparator<ProtobufMessageField> {

	@Override
	public int compare(ProtobufMessageField pmf1, ProtobufMessageField pmf2) {
		//System.out.println(pmf1.getName());
		return pmf1.getName().compareTo(pmf2.getName());
	}

}
