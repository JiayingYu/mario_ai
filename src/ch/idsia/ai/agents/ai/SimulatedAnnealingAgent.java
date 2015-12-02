package ch.idsia.ai.agents.ai;

import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.Environment;

public class SimulatedAnnealingAgent extends BasicAIAgent{

	public SimulatedAnnealingAgent() {
		super("Simulated Annealing");
		action = new boolean[Environment.numberOfButtons];
		reset();
	}
	
	public void reset() {
		action[Mario.KEY_RIGHT] = true;
		action[Mario.KEY_SPEED] = true;
	}
	
	@Override 
	public boolean[] getAction(Environment observation) {		
		
	}

}
