package ai2020.group6.acceptancestratagies;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import ai2020.group6.MAState;
import geniusweb.actions.Offer;
import geniusweb.actions.Vote;
import geniusweb.actions.Votes;

public class StaticThresholdAcceptanceStrategy implements IAcceptanceStrategy {

	
	private BigDecimal threshold;
	private Integer minPower;
	private Integer maxPower;
	
	public StaticThresholdAcceptanceStrategy ( BigDecimal threshold, Integer minPower, Integer maxPower ) {
		this.threshold = threshold;
		this.minPower = minPower;
		this.maxPower = maxPower;
	}
	
	@Override
	public Votes acceptanceVote ( MAState state, List<Offer> offers ) {
		return new Votes(state.getId(), offers.stream()
			.filter(offer -> {
				return state.getUtilitySpace().getUtility(offer.getBid()).compareTo(threshold) >= 0;
			})
			.map(offer -> new Vote(state.getId(), offer.getBid(), minPower, maxPower))
			.collect(Collectors.toSet()));
	}

}
