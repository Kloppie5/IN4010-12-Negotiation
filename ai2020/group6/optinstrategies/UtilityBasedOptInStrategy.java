package ai2020.group6.optinstrategies;

import java.math.BigDecimal;
import java.util.List;
import ai2020.group6.MAState;
import ai2020.group6.optinstrategies.IOptInStrategy;
import geniusweb.actions.Votes;

public abstract class UtilityBasedOptInStrategy implements IOptInStrategy {

	protected Integer minPower;
	protected Integer maxPower;
	
	public UtilityBasedOptInStrategy ( Integer minPower, Integer maxPower ) {
		this.minPower = minPower;
		this.maxPower = maxPower;
	}
	
	public abstract BigDecimal getUtilityThreshold ( MAState state );
	
	@Override
	public Votes optInVote ( MAState state, List<Votes> votes ) {
		return votes.stream()
			.filter(vote -> vote.getActor() == state.getId())
			.findFirst().get();
	}
}
