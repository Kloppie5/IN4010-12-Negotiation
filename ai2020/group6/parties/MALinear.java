package ai2020.group6.parties;

import java.math.BigDecimal;
import java.util.logging.Level;

import ai2020.group6.MADefaultParty;
import ai2020.group6.acceptancestratagies.IAcceptanceStrategy;
import ai2020.group6.biddingstrategies.IBiddingStrategy;
import ai2020.group6.combinedstrategies.LinearConcedingCombinedStrategy;
import ai2020.group6.opponentmodels.EmptyOpponentModel;
import ai2020.group6.opponentmodels.IOpponentModel;
import ai2020.group6.optinstrategies.IOptInStrategy;
import geniusweb.inform.Finished;
import geniusweb.inform.Inform;
import geniusweb.inform.OptIn;
import geniusweb.inform.Settings;
import geniusweb.inform.Voting;
import geniusweb.inform.YourTurn;
import tudelft.utilities.logging.Reporter;

public class MALinear extends MADefaultParty {

	LinearConcedingCombinedStrategy lccs = new LinearConcedingCombinedStrategy(BigDecimal.valueOf(1.0), BigDecimal.valueOf(0.5), 2, Integer.MAX_VALUE);
	
	public MALinear ( ) {
		super();
		reporter.log(Level.FINEST, "MALinear constructed");
	}
	
	public MALinear ( Reporter reporter ) {
		super(reporter);
		reporter.log(Level.FINEST, "MALinear constructed with reporter");
	}
	
	@Override
	public void notifyChange ( Inform info ) {
		reporter.log(Level.FINEST, "Recieved Inform["+info.toString()+"]");
		super.notifyChange(info);
	}
	
	@Override
	protected IAcceptanceStrategy getAccceptanceStrategy ( Settings settings ) {
		reporter.log(Level.FINEST, "MALinear getAccceptanceStrategy("+settings.toString()+")");
		return lccs;
	}

	@Override
	protected IBiddingStrategy getBiddingStrategy ( Settings settings ) {
		reporter.log(Level.FINEST, "MALinear getBiddingStrategy("+settings.toString()+")");
		return lccs;
	}
	
	@Override
	protected IOptInStrategy getOptInStrategy ( Settings settings ) {
		reporter.log(Level.FINEST, "MALinear getOptInStrategy("+settings.toString()+")");
		return lccs;
	}

	@Override
	protected IOpponentModel initNewOpponentModel ( Settings settings ) {
		return new EmptyOpponentModel();
	}

	

}
