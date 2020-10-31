package ai2020.group6.biddingstrategies;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import ai2020.group6.MAState;
import ai2020.group6.acceptancestratagies.IAcceptanceStrategy;
import ai2020.group6.biddingstrategies.IBiddingStrategy;
import ai2020.group6.optinstrategies.IOptInStrategy;
import geniusweb.actions.Offer;
import geniusweb.actions.Vote;
import geniusweb.actions.Votes;
import geniusweb.bidspace.BidsWithUtility;
import geniusweb.bidspace.Interval;
import geniusweb.issuevalue.Bid;
import geniusweb.profile.utilityspace.LinearAdditive;
import tudelft.utilities.immutablelist.ImmutableList;

public abstract class UtilityBasedBiddingStrategy implements IBiddingStrategy {

	protected Integer minPower;
	protected Integer maxPower;
	
	public UtilityBasedBiddingStrategy ( Integer minPower, Integer maxPower ) {
		this.minPower = minPower;
		this.maxPower = maxPower;
	}
	
	public abstract BigDecimal getUpperUtilityThreshold ( MAState state );
	public abstract BigDecimal getLowerUtilityThreshold ( MAState state );

	@Override
	public Bid generateBid(MAState state) {
		BigDecimal upperThreshold = getUpperUtilityThreshold(state);
		BigDecimal lowerThreshold = getLowerUtilityThreshold(state);
		BidsWithUtility bidutils = new BidsWithUtility((LinearAdditive) state.getUtilitySpace());
		ImmutableList<Bid> bids = bidutils.getBids(new Interval(lowerThreshold, upperThreshold));
		if ( bids.size().intValue() == 0 )
			return bidutils.getExtremeBid(true);
		int i = (new Random()).nextInt(bids.size().intValue());
		return bids.get(i);
	}

}
