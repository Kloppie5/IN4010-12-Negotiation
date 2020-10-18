package ai2020.group6;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

import ai2020.group6.acceptancestratagies.IAcceptanceStrategy;
import ai2020.group6.biddingstrategies.IBiddingStrategy;
import ai2020.group6.opponentmodels.IOpponentModel;
import geniusweb.actions.Action;
import geniusweb.actions.Offer;
import geniusweb.actions.PartyId;
import geniusweb.actions.Vote;
import geniusweb.actions.Votes;
import geniusweb.inform.Finished;
import geniusweb.inform.Inform;
import geniusweb.inform.OptIn;
import geniusweb.inform.Settings;
import geniusweb.inform.Voting;
import geniusweb.inform.YourTurn;
import geniusweb.issuevalue.Bid;
import geniusweb.party.Capabilities;
import geniusweb.party.DefaultParty;
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
	
	public Map<PartyId, IOpponentModel> opponentModels;
	protected abstract Map<PartyId, IOpponentModel> getOpponentModels ( Settings settings ) ;
	
	public MADefaultParty ( ) { }

	public MADefaultParty ( Reporter reporter ) {
		super(reporter);
	}
	
	@Override
	public void notifyChange ( Inform info ) {
		if ( info instanceof Settings ) {
			handleSettings((Settings) settings); return; }
		
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
		opponentModels = new HashMap<PartyId, IOpponentModel>();
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
		Object val = settings.getParameters().get("minPower");
		Integer minpower = (val instanceof Integer) ? (Integer) val : 2;
		val = settings.getParameters().get("maxPower");
		Integer maxpower = (val instanceof Integer) ? (Integer) val
				: Integer.MAX_VALUE;

		/*
		voting.getBids().stream().forEach(action -> {
			PartyId oid = action.getActor();
			IOpponentModel om = opponentModels.get(oid);
			IOpponentModel nom = om.updateOpponentModel(state, action);
			opponentModels.replace(oid, nom);
		});
		*/
		
		Set<Vote> votes = voting.getBids().stream().distinct()
				.filter(offer -> acceptanceStrategy.isAcceptable(this, offer))
				.map(offer -> new Vote(id, offer.getBid(), minpower, maxpower))
				.collect(Collectors.toSet());
		
		Action action = new Votes(id, votes);
		actionHistory.add(action);
		
		try {
			getConnection().send(action);
		} catch (IOException e) { e.printStackTrace(); }
	}
	public void handleOptIn ( OptIn optin ) {
		Action action = actionHistory.get(actionHistory.size()-1);
		
		try {
			getConnection().send(action);
		} catch (IOException e) { e.printStackTrace(); }
		
		if (progress instanceof ProgressRounds)
			progress = ((ProgressRounds) progress).advance();
	}
	public void handleFinished ( Finished finished ) {
		getReporter().log(Level.INFO, "Final ourcome:" + finished);
	}

	public UtilitySpace getUtilitySpace() {
		try {
			return (UtilitySpace) profileint.getProfile();
		} catch (IOException e) { e.printStackTrace(); return null; }
	}
	
	@Override
	public Capabilities getCapabilities() {
		return new Capabilities(new HashSet<>(Arrays.asList("MOPAC")));
	}

	@Override
	public String getDescription() {
		return "Modular MOPaC agent";
	}
}
