package ai2020.group6.acceptancestratagies;

import java.math.BigDecimal;

import ai2020.group6.MAState;
import geniusweb.actions.Offer;

public class StaticThresholdAcceptanceStrategy implements IAcceptanceStrategy {

	private BigDecimal threshold;
	
	public StaticThresholdAcceptanceStrategy ( BigDecimal threshold ) {
		this.threshold = threshold;
	}
	
	@Override
	public boolean isAcceptable(MAState state, Offer offer) {
		return state.getUtilitySpace().getUtility(offer.getBid()).compareTo(threshold) >= 0;
	}

}
