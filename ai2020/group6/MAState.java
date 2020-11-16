package ai2020.group6;

import java.math.BigDecimal;
import java.util.Map;

import geniusweb.actions.Action;
import geniusweb.actions.PartyId;
import geniusweb.profile.Profile;
import geniusweb.profile.utilityspace.UtilitySpace;

/**
 * MAState is used to define the access a strategy needs to the agent.
 * 
 * @author Group 6
 */
public interface MAState {

	public PartyId getId ( );
	public Profile getProfile ( );
	public Action getLastAction ( );
	public Map<PartyId, Integer> getPowerMap ( );
	public BigDecimal getProgressTime ( );
	public UtilitySpace getUtilitySpace ( );
	
}
