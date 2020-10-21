package ai2020.group6.combinedstrategies;

import java.math.BigDecimal;
import ai2020.group6.MAState;

public class BoulwareConcedingCombinedStrategy extends UtilityBasedCombinedStrategy {

	public BoulwareConcedingCombinedStrategy(BigDecimal upperThreshold, BigDecimal lowerThreshold, Integer minPower, Integer maxPower) {
		super(upperThreshold, lowerThreshold, minPower, maxPower);
	}

	public BigDecimal getUtilityThreshold ( MAState state ) {
		BigDecimal t = state.getProgressTime();
		return upperThreshold.subtract(t.pow(5).multiply(upperThreshold.min(lowerThreshold)));
	}
}
