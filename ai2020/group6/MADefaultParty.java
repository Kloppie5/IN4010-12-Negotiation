package ai2020.group6;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import ai2020.group6.acceptancestratagies.IAcceptanceStrategy;
import ai2020.group6.biddingstrategies.IBiddingStrategy;
//import ai2020.group6.opponentmodels.IOpponentModel;
import ai2020.group6.optinstrategies.IOptInStrategy;
import geniusweb.actions.Action;
import geniusweb.actions.Offer;
import geniusweb.actions.PartyId;
import geniusweb.inform.Finished;
import geniusweb.inform.Inform;
import geniusweb.inform.OptIn;
import geniusweb.inform.Settings;
import geniusweb.inform.Voting;
import geniusweb.inform.YourTurn;
import geniusweb.issuevalue.Bid;
import geniusweb.party.Capabilities;
import geniusweb.party.DefaultParty;
import geniusweb.profile.Profile;
import geniusweb.profile.utilityspace.LinearAdditive;
import geniusweb.profile.utilityspace.UtilitySpace;
import geniusweb.profileconnection.ProfileConnectionFactory;
import geniusweb.profileconnection.ProfileInterface;
import geniusweb.progress.Progress;
import geniusweb.progress.ProgressRounds;

import tudelft.utilities.logging.Reporter;

public abstract class MADefaultParty extends DefaultParty implements MAState {

	public PartyId id;
	protected ProfileInterface profileint;
		
	public Settings settings;
	public Progress progress;

	public List<Action> actionHistory;

	public IAcceptanceStrategy acceptanceStrategy;
	protected abstract IAcceptanceStrategy getAccceptanceStrategy ( Settings settings );
	
	public IBiddingStrategy biddingStrategy;
	protected abstract IBiddingStrategy getBiddingStrategy ( Settings settings );
	
	public IOptInStrategy optinStrategy;
	protected abstract IOptInStrategy getOptInStrategy ( Settings settings );
	
	// public Map<PartyId, IOpponentModel> opponentModels;
	// protected abstract IOpponentModel initNewOpponentModel ( Settings settings ) ;
	public Map<PartyId, Integer> powers;
	
	public MADefaultParty ( ) {
		super();
	}

	public MADefaultParty ( Reporter reporter ) {
		super(reporter);
	}
	
	@Override
	public void notifyChange ( Inform info ) {
		if ( info instanceof Settings ) {
			handleSettings((Settings) info); return; }
		
		if ( info instanceof YourTurn ) {
			handleYourTurn(); return; }
		
		if ( info instanceof Voting ) {
			handleVoting((Voting) info); return; }
		
		if ( info instanceof OptIn ) {
			handleOptIn((OptIn) info); return; }
		
		if ( info instanceof Finished ) {
			handleFinished((Finished) info); return; }
		
	}

	public void handleSettings ( Settings settings ) {
		id = settings.getID();
		try {
			profileint = ProfileConnectionFactory.create(settings.getProfile().getURI(), getReporter());
		} catch (Exception e) { e.printStackTrace(); }
		this.settings = settings;
		progress = settings.getProgress();
		
		actionHistory = new ArrayList<Action>();
		
		acceptanceStrategy = getAccceptanceStrategy(settings);
		biddingStrategy = getBiddingStrategy(settings);
		optinStrategy = getOptInStrategy(settings);
		// opponentModels = new HashMap<PartyId, IOpponentModel>();
	}
	public void handleYourTurn ( ) {
		Bid bid = biddingStrategy.generateBid(this);
		
		Action action = new Offer(id, bid);
		actionHistory.add(action);
		
		try {
			getConnection().send(action);
		} catch (IOException e) { e.printStackTrace(); }
	}
	public void handleVoting ( Voting voting ) {
		// voting.getBids().stream().forEach(offer -> {
		// 	PartyId oid = offer.getActor();
		// 	IOpponentModel om = opponentModels.getOrDefault(oid, initNewOpponentModel(settings));
		// 	IOpponentModel nom = om.updateOpponentModel(this, offer);
		// 	opponentModels.replace(oid, nom);
		// });
		
		powers = voting.getPowers();
		
		Action action = acceptanceStrategy.acceptanceVote(this, voting.getBids());
		
		actionHistory.add(action);
		
		try {
			getConnection().send(action);
		} catch (IOException e) { e.printStackTrace(); }
	}
	public void handleOptIn ( OptIn optin ) {
		// optin.getVotes().stream().forEach(votes -> {
		// 	PartyId oid = votes.getActor();
		// 	if (oid.equals(id)) return;
		// 	IOpponentModel om = opponentModels.get(oid);
		// 	IOpponentModel nom = om.updateOpponentModel(this, votes);
		// 	opponentModels.replace(oid, nom);
		// });
		
		
		Action action = optinStrategy.optInVote(this, optin.getVotes());

		actionHistory.add(action);
		
		try {
			getConnection().send(action);
		} catch (IOException e) { e.printStackTrace(); }
		
		if (progress instanceof ProgressRounds)
			progress = ((ProgressRounds) progress).advance();
	}
	public void handleFinished ( Finished finished ) {
		getReporter().log(Level.INFO, "Final ourcome:" + finished);
	}

	public PartyId getId ( ) {
		return id;
	}
	
	public Profile getProfile ( ) {
		try {
			return profileint.getProfile();
		} catch (IOException e) { e.printStackTrace(); return null; }
	}
	
	public Action getLastAction ( ) {
		return actionHistory.get(actionHistory.size()-1);
	}
	
	public Map<PartyId, Integer> getPowerMap ( ) {
		return powers;
	}
	
	public BigDecimal getProgressTime ( ) {
		return BigDecimal.valueOf(progress.get(System.currentTimeMillis())).setScale(6, RoundingMode.HALF_UP);
	}
	
	public UtilitySpace getUtilitySpace() {
		try {
			return (UtilitySpace) profileint.getProfile();
		} catch (IOException e) { e.printStackTrace(); return null; }
	}
	
	@Override
	public Capabilities getCapabilities() {
		return new Capabilities(Collections.singleton("MOPAC"),
				Collections.singleton(LinearAdditive.class));
	}

	@Override
	public String getDescription() {
		return "Modular MOPaC agent";
	}
}
