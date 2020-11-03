package ai2020.group6.optinstrategies;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ai2020.group6.MAState;
import geniusweb.actions.PartyId;
import geniusweb.actions.Vote;
import geniusweb.actions.Votes;
import geniusweb.issuevalue.Bid;

public abstract class PowerWeightedOptInStrategy implements IOptInStrategy {

	public abstract Vote vote ( MAState state, Bid bid, Integer power, Integer minpower );

	@Override
	public Votes optInVote(MAState state, List<Votes> votes) {
		Map<PartyId, Integer> agentpowers = state.getPowerMap();
		Map<Bid, Integer> bidpowers = new HashMap<>();
		Map<Bid, Integer> bidthresholds = new HashMap<>();
		votes.stream().forEach(agentvotes -> {
			PartyId agentid = agentvotes.getActor();
			agentvotes.getVotes().forEach(vote -> {
				Integer power = bidpowers.getOrDefault(vote.getBid(), 0);
				Integer minpower = bidthresholds.getOrDefault(vote.getBid(), 0);
				power += agentpowers.getOrDefault(agentid, 0);
				bidpowers.put(vote.getBid(), power);
				bidthresholds.put(vote.getBid(), Math.max(vote.getMinPower(), minpower));
			});
		});
		Set<Vote> optin = new HashSet<Vote>();
		bidpowers.forEach((bid, power) -> {
			Vote vote = vote(state, bid, bidpowers.getOrDefault(bid, 0), bidthresholds.getOrDefault(bid, 0));
			if (vote != null)
				optin.add(vote);
		});

		return new Votes(state.getId(), optin);		
	}

}
