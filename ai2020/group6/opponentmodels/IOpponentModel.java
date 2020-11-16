package ai2020.group6.opponentmodels;

import ai2020.group6.MAState;
import geniusweb.actions.Action;

/**
 * Opponent models are a scrapped component of the modular agent.
 * A problem early in development was the realization that there are
 * too few rounds to properly model an agent, and an agent aware of our
 * strategy for modelling other agents could utilise a strategy to trick
 * us into giving it its highest bid.
 * 
 * @author Group 6
 */
public interface IOpponentModel {
	
	public IOpponentModel updateOpponentModel ( MAState state, Action action );
	
}
