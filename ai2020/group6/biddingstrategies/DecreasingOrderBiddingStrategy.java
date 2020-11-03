package ai2020.group6.biddingstrategies;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ai2020.group6.MAState;
import geniusweb.issuevalue.Bid;
import geniusweb.profile.DefaultPartialOrdering;

public class DecreasingOrderBiddingStrategy implements IBiddingStrategy {
	
	List<Bid> bidlist = null;
	
	@Override
	public Bid generateBid ( MAState state ) {
		if ( bidlist == null ) {
			DefaultPartialOrdering dpo = (DefaultPartialOrdering) state.getProfile();
			bidlist = dpo.getBids();
			Collections.sort(bidlist, new Comparator<Bid>() {
				@Override
				public int compare(Bid b1, Bid b2) {
					return dpo.isPreferredOrEqual(b1, b2) ? -1 : 1;
				}
			});
		}
		
		return bidlist.remove(0);
	}

}
