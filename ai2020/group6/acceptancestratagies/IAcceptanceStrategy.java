package ai2020.group6.acceptancestratagies;

import ai2020.group6.MAState;
import geniusweb.actions.Offer;

public interface IAcceptanceStrategy {

	public boolean isAcceptable ( MAState state, Offer offer );
	
}
