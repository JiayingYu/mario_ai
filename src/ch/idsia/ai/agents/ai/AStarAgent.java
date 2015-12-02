package ch.idsia.ai.agents.ai;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.Environment;

public class AStarAgent extends BasicAIAgent {
	int count;

	public AStarAgent() {
		super("A* Agent");
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

		int[] nextPos = AStartBFS(levelScene, new ArrayList<int[]>());
		
		
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
		
		if (nextPos[1] < 11) {
			action[Mario.KEY_LEFT] = true;
		}
		
    //action[Mario.KEY_SPEED] = DangerOfGap(levelScene);
		
		return action;
	}

	private int[] AStartBFS(byte[][] levelScene, List<int[]> path) {
		PriorityQueue<StateNode> q = new PriorityQueue<StateNode>(10000, new Comparator<StateNode>() {

					@Override
					public int compare(StateNode n1, StateNode n2) {
						return n1.score - n2.score;
					}
				});

		StateNode initialState = createStateNode(Environment.HalfObsHeight, Environment.HalfObsWidth, 0, path);
		q.offer(initialState);
//		HashSet<StateNode> set = new HashSet<StateNode>();
//		set.add(initialState);
		int[][] diff = {{-1, -1, 0, 1, 1}, {0, 1, 1, 1, 0}};
		List<int[]> finalPath = initialState.posPath;
		
		while (!q.isEmpty()) {
			StateNode curNode = q.poll();
			List<int[]> curPath = curNode.posPath;
			int[] curPos = curPath.get(curPath.size() - 1);
			int r = curPos[0];
			int c = curPos[1];
			
			// goal test
			if (reachBoundary(r, c)) {
				finalPath = curPath;
				break;
			}
			
			// push 5 forward positions into the queue
			for (int i = 0; i < 5; i++) {
				int newR = r + diff[0][i];
				int newC = c + diff[1][i];
				if (newR < 22 && newR >= 0 && newC >= 0 && newC < 22) {
					q.offer(createStateNode(newR, newC, levelScene[newR][newC], curPath));
				}				
			}
		}
		
		// the first step after the initial state
		return finalPath.get(1);		
	}
	
	private StateNode createStateNode(int row, int col, int cellVal, List<int[]> parentPath) {
		List<int[]> curPath = copyList(parentPath);
		curPath.add(new int[] {row, col});
	  // if current position is obstacle -> negative score
		int score = col - 11 + cellVal == 0 ? 0 : -100; 
		return new StateNode(curPath, score);
	}
	
	// deep copy of the path
	private List<int[]> copyList(List<int[]> originalList) {
		List<int[]> newList = new ArrayList<int[]>();
		
		for (int[] e : originalList) {
			newList.add(new int[] {e[0], e[1]});
		}
		
		return newList;
	}
	
	// goal test: if the path reach the boundary 
	private boolean reachBoundary(int r, int c) {
		if (r >= 21 || r <= 0 || c >= 21 || c <= 0) return true;
		return false;
	}
}
