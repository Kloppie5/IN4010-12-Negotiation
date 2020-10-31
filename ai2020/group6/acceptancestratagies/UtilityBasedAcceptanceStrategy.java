package ai2020.group6.acceptancestratagies;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import ai2020.group6.MAState;
import ai2020.group6.acceptancestratagies.IAcceptanceStrategy;
import geniusweb.actions.Offer;
import geniusweb.actions.Vote;
import geniusweb.actions.Votes;

public abstract class UtilityBasedAcceptanceStrategy implements IAcceptanceStrategy {

	protected Integer minPower;
	protected Integer maxPower;
	
	public UtilityBasedAcceptanceStrategy ( Integer minPower, Integer maxPower ) {
		this.minPower = minPower;
		this.maxPower = maxPower;
	}
	
	public abstract BigDecimal getUtilityThreshold ( MAState state );
	
	@Override
	public Votes acceptanceVote ( MAState state, List<Offer> offers ) {
		BigDecimal util = getUtilityThreshold(state);
		return new Votes(state.getId(), offers.stream()
				.filter(offer -> {
					return state.getUtilitySpace().getUtility(offer.getBid()).compareTo(util) >= 0;
				})
				.map(offer -> new Vote(state.getId(), offer.getBid(), minPower, maxPower))
				.collect(Collectors.toSet()));
	}

}
