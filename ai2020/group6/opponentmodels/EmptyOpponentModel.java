package ai2020.group6.opponentmodels;

import ai2020.group6.MAState;
import geniusweb.actions.Action;

public class EmptyOpponentModel implements IOpponentModel {

	public EmptyOpponentModel ( ) { }
	
	@Override
	public IOpponentModel updateOpponentModel(MAState state, Action action) {
		return this;
	}

}
