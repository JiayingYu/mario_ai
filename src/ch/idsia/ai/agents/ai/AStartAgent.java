package ch.idsia.ai.agents.ai;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

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

	private boolean DangerOfGap(byte[][] levelScene) {
		for (int x = 9; x < 13; ++x) {
			boolean f = true;
			for (int y = 12; y < 22; ++y) {
				if (levelScene[y][x] != 0)
					f = false;
			}
			if (f && levelScene[12][11] != 0)
				return true;
		}
		return false;
	}

	@Override
	public boolean[] getAction(Environment observation) {
		if (observation == null) {
			throw new IllegalArgumentException("null observation");
		}

		boolean[] action = new boolean[Environment.numberOfButtons];
		byte[][] levelScene = observation.getCompleteObservation();
		float[] marioPos = observation.getMarioFloatPos();

		AStartBFS(levelScene, new ArrayList<int[]>());
		return action;
	}

	private int[] AStartBFS(byte[][] levelScene, List<int[]> path) {
		PriorityQueue<StateNode> q = new PriorityQueue<StateNode>(0,
				new Comparator<StateNode>() {

					@Override
					public int compare(StateNode o1, StateNode o2) {
						// TODO Auto-generated method stub
						return 0;
					}
				});

		StateNode initialState = createStateNode(Environment.HalfObsHeight, Environment.HalfObsWidth, path);
		q.offer(initialState);
//		HashSet<StateNode> set = new HashSet<StateNode>();
//		set.add(initialState);
		int[][] diff = {{-1, -1, 0, 1, 1}, {0, 1, 1, 1, 0}};
		List<int[]> finalPath = path;
		
		while (q.isEmpty()) {
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
				if (newR < 22 && newC < 22) {
					q.offer(createStateNode(newR, newC, curPath));
				}				
			}
		}
		
		// the first step after the initial state
		return finalPath.get(1);		
	}
	
	private StateNode createStateNode(int row, int col, List<int[]> parentPath) {
		List<int[]> curPath = new ArrayList(parentPath);
		curPath.add(new int[] {row, col});
		return new StateNode(curPath);
	}
	
	// goal test: if the path reach the boundary 
	private boolean reachBoundary(int r, int c) {
		return false;
	}
}
