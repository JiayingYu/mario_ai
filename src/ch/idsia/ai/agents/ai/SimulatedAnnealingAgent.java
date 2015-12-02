package ch.idsia.ai.agents.ai;

import java.util.Random;

import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.Environment;

public class SimulatedAnnealingAgent extends BasicAIAgent{
	int count;
	
	public SimulatedAnnealingAgent() {
		super("Simulated Annealing");
		count = 0;
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
		//float[] marioPos = observation.getMarioFloatPos();

		int[] nextPos = simulatedAnnealing(levelScene);
		
		if (nextPos[1] >= 11) {
			action[Mario.KEY_LEFT] = false;
			action[Mario.KEY_RIGHT] = true;
		} else {
			action[Mario.KEY_LEFT] = true;
			action[Mario.KEY_RIGHT] = false;
		}
		
		if (nextPos[0] < 11 || DangerOfGap(levelScene) || levelScene[11][13] != 0 || levelScene[11][12] != 0 ) {
			if (observation.mayMarioJump() || ( !observation.isMarioOnGround() && action[Mario.KEY_JUMP])) {			
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
				
		return action;
	}
	
	private int[] simulatedAnnealing(byte[][] levelScene) {
		State curState = new State(new int[] {11, 11}, Integer.MIN_VALUE);
		int time = 0;
		
		while (time < 10000) {
			int temp = schedule(time++);
			if (temp == 0) return curState.pos;
			
			State nextState = nextRandomState(curState.pos, levelScene);
			int scoreDiff = nextState.score - curState.score;
			
			if (scoreDiff > 0) {
			} else {
				double p = getProbability(scoreDiff, temp);
				if (p > 0.5) curState = nextState;
			}
		}
		
		return curState.pos;
	}
	
	private double getProbability(int scoreDiff, int temp) {
		double p = Math.exp((double) scoreDiff / temp);
		return p;
	}
	
	private State nextRandomState(int[] curPos, byte[][] levelScene) {
		Random random = new Random();
		int next = random.nextInt(5);
		int[][] diff = {{-1, -1, 0, 1, 1}, {0, 1, 1, 1, 0}};
		int[] randomPos = new int[] {curPos[0] + diff[0][next], curPos[1] + diff[1][next]};
		State randomState = new State(randomPos, levelScene);
		return randomState;
	}
	
	
	// the schedule returns a temperature as a function of time
	private int schedule(int time) {
		int temperature = 100 - time;
		return temperature;
	}

}

class State {
	int[] pos;
	int score;
	
	public State(int[] pos, byte[][] levelScene) {
		this.pos = pos;
		setScore(levelScene);
	}
	
	public State(int[] pos, int score) {
		this.pos = pos;
		this.score = score;
	}
	
	protected void setScore(byte[][] levelScene) {
		int r = pos[0];
		int c = pos[1];
		int cellVal = (int) levelScene[r][c];
		score = c - 11 + 11 - r + (int) cellVal == 0 ? 0 : -100;
	}
}
