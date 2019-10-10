package org.acumos.onboarding.common.proto;

import java.util.Comparator;

import org.acumos.cds.domain.MLPSolutionRevision;

public class RevisionListDateComaparator implements Comparator<MLPSolutionRevision> {

	@Override
	public int compare(MLPSolutionRevision r1, MLPSolutionRevision r2) {
		return r1.getModified().compareTo(r2.getModified());
	}

}
