package ch.idsia.ai.agents.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.Environment;

public class GeneticAgent extends BasicAIAgent {
	int count;
	byte[][] levelScene;
	
	public GeneticAgent() {
		super("EvolutionAgent");
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
		//float[] marioPos = observation.getMarioFloatPos();

		int[] nextPos = GeneticSearch();
		
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

	private int[] GeneticSearch() {
		List<StateNode> population = initPopulation();
		int time = 0;
		
		while (time++ < 100) {
			List<StateNode> nextGen = new ArrayList<StateNode>();
			
			for (int i = 0; i < population.size(); i++) {
				StateNode parent1 = selectParent(population);
				StateNode parent2 = selectParent(population);
				StateNode child = reproduce(parent1, parent2);
				if (child.score > 11) {
					return child.posPath.get(1);
				}
				nextGen.add(child);
			}
			
			population = nextGen;
		}
		
		Collections.sort(population, new Comparator<StateNode>() {

			@Override
			public int compare(StateNode s1, StateNode s2) {
				return s2.score - s1.score;
			}
		
		});
		
		return population.get(0).posPath.get(1);
	}
	
	private StateNode reproduce(StateNode parent1, StateNode parent2) {
		int size = Math.min(parent1.posPath.size(), parent2.posPath.size());
		List<int[]> newPath = new ArrayList<int[]>();
		Random rd = new Random();
		int cutoff = rd.nextInt(size);
		
		for (int i = 0; i <= cutoff; i++) {
			newPath.add(parent1.posPath.get(i));
		}
		
		for (int i = cutoff + 1; i < size; i++) {
			newPath.add(parent2.posPath.get(i));
		}
		
		int score = fitness(newPath);
		return new StateNode(newPath, score);
	}
	
	private int fitness(List<int[]> path) {
		int[] lastPos = path.get(path.size() - 1);
		int col = lastPos[1];
		int row = lastPos[0];		
		int cellVal = (int) levelScene[row][col];
		int score = col - 11 + cellVal == 0 ? 0 : -100; 
		return score;
	}

	private StateNode selectParent(List<StateNode> population) {
		Random rd = new Random();
		int size = population.size();
		StateNode parent = null;
		
		while (parent == null) {
			int next = rd.nextInt(size);
			double randomFactor = rd.nextDouble();
			double p = Math.exp(population.get(next).score);
			p = p * randomFactor / 10;
			
			if (p <= 0.5) {
				parent = population.get(next);
			}
		}
		
		return parent;
	}

	private List<StateNode> initPopulation() {
		Queue<StateNode> q = new LinkedList<StateNode>();
		StateNode initialState = createStateNode(Environment.HalfObsHeight, 
					Environment.HalfObsWidth, 0, new ArrayList<int[]>());
		q.offer(initialState);
		
		// initial population
		List<StateNode> population = new ArrayList<StateNode>();
		HashSet<Integer> visitedIndex = new HashSet<Integer>();
		visitedIndex.add(11* 11 + 11);
		
		int[][] diff = {{-1, -1, 0, 1, 1}, {0, 1, 1, 1, 0}};
		
		while (!q.isEmpty() && population.size() < 20) {
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
				if (newR < 22 && newR >= 0 && newC >= 0 && newC < 22 && !visitedIndex.contains(index)) {
					q.offer(createStateNode(newR, newC, levelScene[newR][newC], curPath));
					visitedIndex.add(index);
				}				
			}
		}
		
		return population;
	}
		
		private StateNode createStateNode(int row, int col, int cellVal, List<int[]> parentPath) {
			List<int[]> curPath = copyList(parentPath);
			curPath.add(new int[] {row, col});
		  // if current position is obstacle -> negative score
			int score = col - 11 + cellVal == 0 ? 0 : -100; 
			return new StateNode(curPath, score);
		}
		
		private List<int[]> copyList(List<int[]> originalList) {
			List<int[]> newList = new ArrayList<int[]>();
			
			for (int[] e : originalList) {
				newList.add(new int[] {e[0], e[1]});
			}
			
			return newList;
		}
		
		private boolean reachBoundary(int r, int c) {
			//if (r >= 21 || r <= 0 || c >= 21 || c <= 0) return true;
			if (r <= 0 || c <= 0 || r >= 21 || c >= 21) return true;
			return false;
		}	
	
}
