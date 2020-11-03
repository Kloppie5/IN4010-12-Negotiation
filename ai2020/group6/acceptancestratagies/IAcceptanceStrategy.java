package ai2020.group6.acceptancestratagies;

import java.util.List;

import ai2020.group6.MAState;
import geniusweb.actions.Offer;
import geniusweb.actions.Votes;

public interface IAcceptanceStrategy {

	public Votes acceptanceVote ( MAState state, List<Offer> offers );
	
}
