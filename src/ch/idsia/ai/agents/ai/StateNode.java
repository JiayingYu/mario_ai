package ch.idsia.ai.agents.ai;

import java.util.ArrayList;
import java.util.List;

// a state node for bfs A* search algorithms
public class StateNode {
	int score;
	List<int[]> posPath;
	
	public StateNode (List<int[]> posPath) {
		this.posPath = posPath;		
	}
	
}
