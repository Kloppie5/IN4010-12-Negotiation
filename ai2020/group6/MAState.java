package ai2020.group6;

import java.math.BigDecimal;

import geniusweb.actions.PartyId;
import geniusweb.profile.Profile;
import geniusweb.profile.utilityspace.UtilitySpace;

public interface MAState {

	public PartyId getId ( );
	public Profile getProfile ( );
	public BigDecimal getProgressTime ( );
	public UtilitySpace getUtilitySpace ( );
	
}
