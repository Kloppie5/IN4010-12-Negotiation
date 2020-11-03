package ai2020.group6.parties;

import java.math.BigDecimal;
import java.util.logging.Level;

import ai2020.group6.MADefaultParty;
import ai2020.group6.MAState;
import ai2020.group6.acceptancestratagies.IAcceptanceStrategy;
import ai2020.group6.acceptancestratagies.UtilityBasedAcceptanceStrategy;
import ai2020.group6.biddingstrategies.IBiddingStrategy;
import ai2020.group6.biddingstrategies.UtilityBasedBiddingStrategy;
import ai2020.group6.optinstrategies.PowerWeightedOptInStrategy;
// import ai2020.group6.opponentmodels.EmptyOpponentModel;
// import ai2020.group6.opponentmodels.IOpponentModel;
import ai2020.group6.optinstrategies.IOptInStrategy;
import ai2020.group6.optinstrategies.NoOptInStrategy;
import geniusweb.actions.Vote;
import geniusweb.inform.Inform;
import geniusweb.inform.Settings;
import geniusweb.issuevalue.Bid;
import tudelft.utilities.logging.Reporter;

public class MAPowerWeightedExponential extends MADefaultParty {
	
	public MAPowerWeightedExponential ( ) {
		super();
		reporter.log(Level.FINEST, "MAExponent constructed");
	}
	
	public MAPowerWeightedExponential ( Reporter reporter ) {
		super(reporter);
	}
	
	@Override
	public void notifyChange ( Inform info ) {
		super.notifyChange(info);
	}
	
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
				Double scale = Math.pow(1-t, e);
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
				Double scale = Math.pow(1-t, e);
				return BigDecimal.valueOf(lowerThreshold + scale * (upperThreshold - lowerThreshold));
			}
		};
	}
	
	@Override
	protected IOptInStrategy getOptInStrategy ( Settings settings ) {
		Object val = settings.getParameters().get("minPower");
		Integer minvotepower = (val instanceof Integer) ? (Integer) val : 2;
		val = settings.getParameters().get("maxPower");
		Integer maxpower = (val instanceof Integer) ? (Integer) val : Integer.MAX_VALUE;
		val = settings.getParameters().get("lowerThreshold");
		Double lowerThreshold = (val instanceof Double) ? (Double) val : 0.7;
		val = settings.getParameters().get("e");
		Double e = (val instanceof Double) ? (Double) val : 1.0;
		val = settings.getParameters().get("ve");
		Double ve = (val instanceof Double) ? (Double) val : 1.0;
		return new PowerWeightedOptInStrategy() {

			@Override
			public Vote vote(MAState state, Bid bid, Integer power, Integer minpower) {
				Double t = state.getProgressTime().doubleValue();
				if (state.getUtilitySpace().getUtility(bid).doubleValue() * (1+Math.pow(power/(minpower*1.0), ve * Math.pow(1-t, e))) > lowerThreshold )
					return new Vote(state.getId(), bid, minvotepower, maxpower);
				return null;
			}
			
		};
	}

	// @Override
	// protected IOpponentModel initNewOpponentModel ( Settings settings ) {
	// 	return new EmptyOpponentModel();
	// }

}
