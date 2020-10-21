package ai2020.group6.optinstrategies;

import java.math.BigDecimal;
import java.util.List;
import ai2020.group6.MAState;
import ai2020.group6.optinstrategies.IOptInStrategy;
import geniusweb.actions.Votes;

public abstract class UtilityBasedOptInStrategy implements IOptInStrategy {

	
	protected BigDecimal upperThreshold;
	protected BigDecimal lowerThreshold;
	protected Integer minPower;
	protected Integer maxPower;
	
	public UtilityBasedOptInStrategy ( BigDecimal upperThreshold, BigDecimal lowerThreshold, Integer minPower, Integer maxPower ) {
		this.upperThreshold = upperThreshold;
		this.lowerThreshold = lowerThreshold;
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
