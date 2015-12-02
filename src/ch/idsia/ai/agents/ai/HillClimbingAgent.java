package ch.idsia.ai.agents.ai;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.Environment;


public class HillClimbingAgent extends BasicAIAgent{
	public HillClimbingAgent() {
		super("Hill Climbing Agent");
		action = new boolean[Environment.numberOfButtons];
		reset();
	}
	
	public void reset() {
		action[Mario.KEY_RIGHT] = true;
		action[Mario.KEY_SPEED] = true;
	}

	@Override 
	public boolean[] getAction(Environment observation) {		
		if (observation == null) {
			throw new IllegalArgumentException("null observation");
		}

		byte[][] levelScene = observation.getCompleteObservation();
		int[] nextPos = getNextPosition(levelScene);
		
		int count = 0;
		
		if (nextPos[0] < 11 || DangerOfGap(levelScene)) {
			if (observation.mayMarioJump() || ( !observation.isMarioOnGround() && action[Mario.KEY_JUMP])) {
				action[Mario.KEY_SPEED] = true;
				action[Mario.KEY_JUMP] = true;
			}         
			
			count++;
		} else {
			action[Mario.KEY_JUMP] = false;
			count = 0;
		}
		
		if (count > 16) {
			action[Mario.KEY_JUMP] = false;
			count = 0;
		}
		
//		if (nextPos[0] > 11 && !DangerOfGap(levelScene)) {
//			action[Mario.KEY_DOWN] = true;
//		} else {
//			action[Mario.KEY_DOWN] = false;
//		}
		
		if (nextPos[1] < 11) {
			action[Mario.KEY_LEFT] = true;
		}
				
		return action;
		
	}

	private int[] getNextPosition(byte[][] levelScene) {
		int[][] diff = {{-1, -1, 0, 1, 1}, {0, 1, 1, 1, 0}};
		int r = 11, c = 11;
		int[] bestNeighbor = new int[2];
		int maxScore = Integer.MIN_VALUE;
		
		for (int i = 0; i < 5; i++) {
			int newR = r + diff[0][i];
			int newC = c + diff[1][i];
			
			if (newR >= 0 && newR <= 21 && newC >= 0 && newC <= 21) {
				int score = getScore(newC, newR, levelScene[newR][newC]);
				if (score > maxScore) {
					maxScore = score;
					bestNeighbor[0] = newR;
					bestNeighbor[1] = newC;
				}
			}
		}
		return bestNeighbor;
	}

	private int getScore(int c, int r, byte cellVal) {
		return c - 11 + 11 - r + (int) cellVal == 0 ? 0 : -100;
	}
}
