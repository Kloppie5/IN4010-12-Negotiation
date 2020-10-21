package ai2020.group6.optinstrategies;

import java.util.List;

import ai2020.group6.MAState;
import geniusweb.actions.Votes;

public class NoOptInStrategy implements IOptInStrategy {

	@Override
	public Votes optInVote ( MAState state, List<Votes> votes ) {
		return votes.stream()
			.filter(vote -> vote.getActor() == state.getId())
			.findFirst().get();
	}

}
