package ch.idsia.scenarios;

import java.util.ArrayList;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.AgentsPool;
import ch.idsia.ai.agents.human.HumanKeyboardAgent;
import ch.idsia.ai.tasks.ProgressTask;
import ch.idsia.ai.tasks.Task;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.tools.EvaluationOptions;

public class TestAgents {
	public static void main(String[] args) {
		ArrayList<Double> scoreList = new ArrayList<Double>();
		
		System.out.println(args);
		for (int i = 0 ; i < 20; i ++) {
			Agent controller = new HumanKeyboardAgent();
      if (args.length > 0) {
          controller = AgentsPool.load (args[0]);
          AgentsPool.addAgent(controller);
      }
      EvaluationOptions options = new CmdLineOptions(new String[0]);
      options.setAgent(controller);
      Task task = new ProgressTask(options);
      options.setMaxFPS(false);
      options.setVisualization(true);
      options.setNumberOfTrials(1);
      options.setMatlabFileName("");
      options.setLevelRandSeed((int) (Math.random () * Integer.MAX_VALUE));
      options.setLevelDifficulty(3);
      task.setOptions(options);
      
      Double score = task.evaluate (controller)[0];
      scoreList.add(score);
      System.out.println ("Score: " + score);
		}
		
		double avg = getAvg(scoreList);
		System.out.println("Average: " + avg);		
	}
	
	public static double getAvg(ArrayList<Double> scoreList) {
		double sum = 0.0;
		
		for (double x : scoreList) {
			sum += x;
		}
		
		return sum / scoreList.size();
	}
}
