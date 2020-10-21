package ai2020.group6;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import ai2020.group6.optinstrategies.IOptInStrategy;
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
import geniusweb.profile.Profile;
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
	
	public Map<PartyId, IOpponentModel> opponentModels;
	protected abstract IOpponentModel initNewOpponentModel ( Settings settings ) ;
	
	public MADefaultParty ( ) {
		super();
	}

	public MADefaultParty ( Reporter reporter ) {
		super(reporter);
	}
	
	@Override
	public void notifyChange ( Inform info ) {
		reporter.log(Level.FINEST, "Recieved Inform["+info.toString()+"]");
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
		
		reporter.log(Level.SEVERE, "Unknown Inform["+info.toString()+"]");
	}

	public void handleSettings ( Settings settings ) {
		reporter.log(Level.FINEST, "MADefaultParty handleSettings("+settings.toString()+")");
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
		opponentModels = new HashMap<PartyId, IOpponentModel>();
	}
	public void handleYourTurn ( ) {
		reporter.log(Level.FINEST, "MADefaultParty handleYourTurn()");
		Bid bid = biddingStrategy.generateBid(this);
		
		Action action = new Offer(id, bid);
		actionHistory.add(action);
		
		try {
			getConnection().send(action);
		} catch (IOException e) { e.printStackTrace(); }
	}
	public void handleVoting ( Voting voting ) {
		reporter.log(Level.FINEST, "MADefaultParty handleVoting("+voting.toString()+")");
		voting.getBids().stream().forEach(offer -> {
			PartyId oid = offer.getActor();
			IOpponentModel om = opponentModels.getOrDefault(oid, initNewOpponentModel(settings));
			IOpponentModel nom = om.updateOpponentModel(this, offer);
			opponentModels.replace(oid, nom);
		});
		
		Action action = acceptanceStrategy.acceptanceVote(this, voting.getBids());
		
		actionHistory.add(action);
		
		try {
			getConnection().send(action);
		} catch (IOException e) { e.printStackTrace(); }
	}
	public void handleOptIn ( OptIn optin ) {
		reporter.log(Level.FINEST, "MADefaultParty handleOptIn("+optin.toString()+")");
		optin.getVotes().stream().forEach(votes -> {
			PartyId oid = votes.getActor();
			IOpponentModel om = opponentModels.get(oid);
			IOpponentModel nom = om.updateOpponentModel(this, votes);
			opponentModels.replace(oid, nom);
		});
		
		
		Action action = optinStrategy.optInVote(this, optin.getVotes());

		actionHistory.add(action);
		
		try {
			getConnection().send(action);
		} catch (IOException e) { e.printStackTrace(); }
		
		if (progress instanceof ProgressRounds)
			progress = ((ProgressRounds) progress).advance();
	}
	public void handleFinished ( Finished finished ) {
		reporter.log(Level.FINEST, "MADefaultParty handleFinished("+finished.toString()+")");
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
		return new Capabilities(new HashSet<>(Arrays.asList("SAOP", "AMOP", "MOPAC")));
	}

	@Override
	public String getDescription() {
		return "Modular MOPaC agent";
	}
}
