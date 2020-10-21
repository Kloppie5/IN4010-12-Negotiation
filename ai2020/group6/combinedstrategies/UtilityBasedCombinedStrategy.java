package ai2020.group6.combinedstrategies;

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

public abstract class UtilityBasedCombinedStrategy implements IAcceptanceStrategy, IBiddingStrategy, IOptInStrategy {

	protected BigDecimal upperThreshold;
	protected BigDecimal lowerThreshold;
	protected Integer minPower;
	protected Integer maxPower;
	
	public UtilityBasedCombinedStrategy ( BigDecimal upperThreshold, BigDecimal lowerThreshold, Integer minPower, Integer maxPower ) {
		this.upperThreshold = upperThreshold;
		this.lowerThreshold = lowerThreshold;
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

	@Override
	public Bid generateBid(MAState state) {
		BigDecimal util = getUtilityThreshold(state);
		BidsWithUtility bidutils = new BidsWithUtility((LinearAdditive) state.getUtilitySpace());
		ImmutableList<Bid> bids = bidutils.getBids(new Interval(util, upperThreshold));
		if ( bids.size().intValue() == 0 )
			return bidutils.getExtremeBid(true);
		int i = (new Random()).nextInt(bids.size().intValue());
		return bids.get(i);
	}
	
	@Override
	public Votes optInVote ( MAState state, List<Votes> votes ) {
		return votes.stream()
			.filter(vote -> vote.getActor() == state.getId())
			.findFirst().get();
	}

}
