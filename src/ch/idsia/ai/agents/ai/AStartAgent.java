package ch.idsia.ai.agents.ai;

import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.Environment;

public class AStartAgent extends BasicAIAgent {

	public AStartAgent() {
		super("A* Agent");
		reset();
	}
	
	public void reset() {
      action = new boolean[Environment.numberOfButtons];
      action[Mario.KEY_RIGHT] = true;
      action[Mario.KEY_SPEED] = true;
  }
	
	

}
