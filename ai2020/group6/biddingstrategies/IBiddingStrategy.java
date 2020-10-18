package ai2020.group6.biddingstrategies;

import ai2020.group6.MAState;
import geniusweb.issuevalue.Bid;

public interface IBiddingStrategy {

	public Bid generateBid(MAState state);
	
}
