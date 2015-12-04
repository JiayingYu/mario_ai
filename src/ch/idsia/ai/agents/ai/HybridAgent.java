package ch.idsia.ai.agents.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.Environment;

// combination of A*, evolution strategy and forward Agent
public class HybridAgent extends BasicAIAgent {
	int count;
	byte[][] levelScene;

	public HybridAgent() {
		super("Hybrid Agent");
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

		levelScene = observation.getCompleteObservation();
		int[] nextPos = hybridSearch();

		if (nextPos[0] < 11 || DangerOfGap(levelScene) || levelScene[11][13] != 0
				|| levelScene[11][12] != 0) {
			if (observation.mayMarioJump()
					|| (!observation.isMarioOnGround() && action[Mario.KEY_JUMP])) {
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

		// action[Mario.KEY_SPEED] = DangerOfGap(levelScene);

		return action;
	}

	private int[] hybridSearch() {
		List<StateNode> solutions = initPopulation();
		sortSolutions(solutions);
		return solutions.get(0).posPath.get(1);
	}

	private void sortSolutions(List<StateNode> solutions) {
		Collections.sort(solutions, new Comparator<StateNode>() {

			@Override
			public int compare(StateNode s1, StateNode s2) {
				return s2.score - s1.score;
			}

		});

	}

	// find all the possible path using bfs
	private List<StateNode> initPopulation() {
		Queue<StateNode> q = new LinkedList<StateNode>();
		StateNode initialState = createStateNode(Environment.HalfObsHeight,
				Environment.HalfObsWidth, 0, new ArrayList<int[]>());
		q.offer(initialState);

		// initial population
		List<StateNode> population = new ArrayList<StateNode>();
		HashSet<Integer> visitedIndex = new HashSet<Integer>();
		visitedIndex.add(11 * 11 + 11);

		int[][] diff = { { -1, -1, 0, 1, 1 }, { 0, 1, 1, 1, 0 } };

		while (!q.isEmpty()) {
			StateNode curNode = q.poll();
			List<int[]> curPath = curNode.posPath;
			int[] curPos = curPath.get(curPath.size() - 1);
			int r = curPos[0];
			int c = curPos[1];

			// goal test
			if (reachBoundary(r, c)) {
				population.add(curNode);
			}

			// push 5 forward positions into the queue
			for (int i = 0; i < 5; i++) {
				int newR = r + diff[0][i];
				int newC = c + diff[1][i];
				int index = newR * 11 + newC;
				if (newR < 22 && newR >= 0 && newC >= 0 && newC < 22
						&& !visitedIndex.contains(index)) {
					q.offer(createStateNode(newR, newC, levelScene[newR][newC], curPath));
					visitedIndex.add(index);
				}
			}
		}
		return population;
	}

	private boolean reachBoundary(int r, int c) {
		// if (r >= 21 || r <= 0 || c >= 21 || c <= 0) return true;
		if (r >= 21 || c >= 21)
			return true;
		return false;
	}

	private StateNode createStateNode(int row, int col, int cellVal,
			List<int[]> parentPath) {
		List<int[]> curPath = copyList(parentPath);
		curPath.add(new int[] { row, col });
		// if current position is obstacle -> negative score
		int score = col - 11 + cellVal == 0 ? 0 : -100;
		return new StateNode(curPath, score);
	}

	// deep copy of the path
	private List<int[]> copyList(List<int[]> originalList) {
		List<int[]> newList = new ArrayList<int[]>();

		for (int[] e : originalList) {
			newList.add(new int[] { e[0], e[1] });
		}

		return newList;
	}
}
