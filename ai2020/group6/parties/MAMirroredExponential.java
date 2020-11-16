package ai2020.group6.parties;

import java.math.BigDecimal;

import ai2020.group6.MADefaultParty;
import ai2020.group6.MAState;
import ai2020.group6.acceptancestratagies.IAcceptanceStrategy;
import ai2020.group6.acceptancestratagies.UtilityBasedAcceptanceStrategy;
import ai2020.group6.biddingstrategies.IBiddingStrategy;
import ai2020.group6.biddingstrategies.UtilityBasedBiddingStrategy;
// import ai2020.group6.opponentmodels.EmptyOpponentModel;
// import ai2020.group6.opponentmodels.IOpponentModel;
import ai2020.group6.optinstrategies.IOptInStrategy;
import ai2020.group6.optinstrategies.NoOptInStrategy;
import geniusweb.inform.Settings;

/**
 * MAMirroredExponential accepts and generates bids above a threshold exponentially
 * decreasing as a function of time.
 * The threshold starts at the "upperThreshold" (defaulting to 1) and decreases
 * to the "lowerThreshold" (defaulting to 0) by scaling with 1-t^(1/e), where t is
 * the progress over time and e is the parameter "e" (defaulting to 1).
 * 
 * This is the default behaviour of the exampleparties provided by the geniusweb
 * framework;
 * Hardliner:          e = 0'
 * Boulware:           e = 0.2
 * Linear:             e = 1
 * TimeDependentParty: e = 1.2
 * Conceder:           e = 2
 * 
 * ' e = 0 would theoretically give the same result, but is not allowed for this agent,
 * as this would leave this agent with a static threshold of "upperThreshold" and
 * is therefore replaceable with the MAStatic agent.
 * 
 * @author Group 6
 */
public class MAMirroredExponential extends MADefaultParty {
	
	@Override
	protected IAcceptanceStrategy getAccceptanceStrategy ( Settings settings ) {
		Object val = settings.getParameters().get("minPower");
		Integer minpower = (val instanceof Integer) ? (Integer) val : 2;
		val = settings.getParameters().get("maxPower");
		Integer maxpower = (val instanceof Integer) ? (Integer) val : Integer.MAX_VALUE;
		val = settings.getParameters().get("upperThreshold");
		Double upperThreshold = (val instanceof Double) ? (Double) val : 1.0;
		val = settings.getParameters().get("lowerThreshold");
		Double lowerThreshold = (val instanceof Double) ? (Double) val : 0.0;
		val = settings.getParameters().get("e");
		Double e = (val instanceof Double) ? (Double) val : 1.0;
		return new UtilityBasedAcceptanceStrategy(minpower, maxpower) {
			@Override
			public BigDecimal getUtilityThreshold ( MAState state ) {
				Double t = state.getProgressTime().doubleValue();
				Double scale = 1-Math.pow(t, 1/e);
				return BigDecimal.valueOf(lowerThreshold + scale * (upperThreshold - lowerThreshold));
			}
		};
	}

	@Override
	protected IBiddingStrategy getBiddingStrategy ( Settings settings ) {
		Object val = settings.getParameters().get("minPower");
		Integer minpower = (val instanceof Integer) ? (Integer) val : 2;
		val = settings.getParameters().get("maxPower");
		Integer maxpower = (val instanceof Integer) ? (Integer) val : Integer.MAX_VALUE;
		val = settings.getParameters().get("upperThreshold");
		Double upperThreshold = (val instanceof Double) ? (Double) val : 1.0;
		val = settings.getParameters().get("lowerThreshold");
		Double lowerThreshold = (val instanceof Double) ? (Double) val : 0.0;
		val = settings.getParameters().get("e");
		Double e = (val instanceof Double) ? (Double) val : 1.0;
		return new UtilityBasedBiddingStrategy(minpower, maxpower) {
			@Override
			public BigDecimal getUpperUtilityThreshold ( MAState state ) {
				return BigDecimal.valueOf(upperThreshold);
			}
			@Override
			public BigDecimal getLowerUtilityThreshold ( MAState state ) {
				Double t = state.getProgressTime().doubleValue();
				Double scale = 1-Math.pow(t, 1/e);
				return BigDecimal.valueOf(lowerThreshold + scale * (upperThreshold - lowerThreshold));
			}
		};
	}
	
	@Override
	protected IOptInStrategy getOptInStrategy ( Settings settings ) {
		return new NoOptInStrategy();
	}

	// @Override
	// protected IOpponentModel initNewOpponentModel ( Settings settings ) {
	// 	return new EmptyOpponentModel();
	// }

}
