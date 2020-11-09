package ai2020.group6.parties;

import java.math.BigDecimal;

import ai2020.group6.MADefaultParty;
import ai2020.group6.MAState;
import ai2020.group6.acceptancestratagies.IAcceptanceStrategy;
import ai2020.group6.acceptancestratagies.UtilityBasedAcceptanceStrategy;
import ai2020.group6.biddingstrategies.IBiddingStrategy;
import ai2020.group6.biddingstrategies.UtilityBasedBiddingStrategy;
//import ai2020.group6.opponentmodels.EmptyOpponentModel;
//import ai2020.group6.opponentmodels.IOpponentModel;
import ai2020.group6.optinstrategies.IOptInStrategy;
import ai2020.group6.optinstrategies.NoOptInStrategy;
import geniusweb.inform.Settings;

/**
 * MAStatic is an agent that accepts bids that exceed the minimum threshold,
 * provided to the agent as the "lowerThreshold" parameter (defaulting to 0),
 * and creates random bids between the minimum and maximum thresholds,
 * provided to the agent as the "lowerThreshold" and "upperThreshold" parameters
 * (defaulting to 0 and 1).
 * 
 * @author Group 6
 */
public class MAStatic extends MADefaultParty {
	
	@Override
	protected IAcceptanceStrategy getAccceptanceStrategy(Settings settings) {
		Object val = settings.getParameters().get("minPower");
		Integer minpower = (val instanceof Integer) ? (Integer) val : 2;
		val = settings.getParameters().get("maxPower");
		Integer maxpower = (val instanceof Integer) ? (Integer) val : Integer.MAX_VALUE;
		val = settings.getParameters().get("lowerThreshold");
		Double lowerThreshold = (val instanceof Double) ? (Double) val : 0.0;
		return new UtilityBasedAcceptanceStrategy(minpower, maxpower) {
			@Override
			public BigDecimal getUtilityThreshold ( MAState state ) {
				return BigDecimal.valueOf(lowerThreshold);
			}
		};
	}

	@Override
	protected IBiddingStrategy getBiddingStrategy(Settings settings) {
		Object val = settings.getParameters().get("minPower");
		Integer minpower = (val instanceof Integer) ? (Integer) val : 2;
		val = settings.getParameters().get("maxPower");
		Integer maxpower = (val instanceof Integer) ? (Integer) val : Integer.MAX_VALUE;
		val = settings.getParameters().get("upperThreshold");
		Double upperThreshold = (val instanceof Double) ? (Double) val : 1.0;
		val = settings.getParameters().get("lowerThreshold");
		Double lowerThreshold = (val instanceof Double) ? (Double) val : 0.0;
		return new UtilityBasedBiddingStrategy(minpower, maxpower) {
			@Override
			public BigDecimal getUpperUtilityThreshold ( MAState state ) {
				return BigDecimal.valueOf(upperThreshold);
			}
			@Override
			public BigDecimal getLowerUtilityThreshold ( MAState state ) {
				return BigDecimal.valueOf(lowerThreshold);
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
