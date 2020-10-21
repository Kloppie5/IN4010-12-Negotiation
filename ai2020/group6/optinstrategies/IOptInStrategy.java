package ai2020.group6.optinstrategies;

import java.util.List;

import ai2020.group6.MAState;
import geniusweb.actions.Votes;

public interface IOptInStrategy {

	public Votes optInVote ( MAState state, List<Votes> votes );
	
}
