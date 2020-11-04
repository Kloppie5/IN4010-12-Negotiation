package ai2020.group6.opponentmodels;

import ai2020.group6.MAState;
import geniusweb.actions.Action;

public interface IOpponentModel {
	
	public IOpponentModel updateOpponentModel ( MAState state, Action action );
	
}
