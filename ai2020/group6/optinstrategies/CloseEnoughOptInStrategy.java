package ai2020.group6.optinstrategies;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ai2020.group6.MAState;
import geniusweb.actions.PartyId;
import geniusweb.actions.Votes;
import geniusweb.issuevalue.Bid;

public class CloseEnoughOptInStrategy implements IOptInStrategy {

	@Override
	public Votes optInVote(MAState state, List<Votes> votes) {
		//util * (power / minpower)
		Map<Bid, Set<PartyId>> greatdesign = new HashMap<>();
		votes.stream().forEach(agentvotes -> {
			PartyId agentid = agentvotes.getActor();
			agentvotes.getVotes().forEach(vote -> {
				Set<PartyId> accepting = greatdesign.getOrDefault(vote.getBid(), new HashSet<PartyId>());
				accepting.add(agentid);
				greatdesign.replace(vote.getBid(), accepting);
			});
		});
		greatdesign.forEach((bid, accepting) -> {
			
		});
		return null;
	}

}
