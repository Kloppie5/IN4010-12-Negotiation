package ai2020.group6.biddingstrategies;

import ai2020.group6.MAState;
import geniusweb.bidspace.BidsWithUtility;
import geniusweb.issuevalue.Bid;
import geniusweb.profile.utilityspace.LinearAdditive;

public class MaxUtilBiddingStrategy implements IBiddingStrategy {

	@Override
	public Bid generateBid(MAState state) {
		BidsWithUtility bidutils = new BidsWithUtility((LinearAdditive) state.getUtilitySpace());
		return bidutils.getExtremeBid(true);
	}

}
