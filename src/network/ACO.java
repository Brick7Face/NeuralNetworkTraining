package network;

import java.util.ArrayList;

public class ACO {
	private int numAnts;
	private ArrayList<Ant> ants = new ArrayList<Ant>();

	public ArrayList<Cluster> cluster(ArrayList<DataPoint> data) {
		ArrayList<Cluster> clusters = new ArrayList<Cluster>();
		ArrayList<Cluster> bestClusters = new ArrayList<Cluster>();
		for (int t = 0; t < 500; t++) {
			//initialize pheromones to small random numbers
			for (int i = 0; i < numAnts; i++) {					//initialize ants with starting position
				ants.add(new Ant(data));
			}
			for (Ant a : ants) {			 					//for each ant
				//select next node based on position equation
				//add that path
			}
			for (Cluster c : clusters) {						//for every path
				//evaporate pheromone level --> pher = (1 - evapFactor) * pher
			}
			for (Ant a : ants) {								//for each ant
				for (Cluster c : clusters) { 					//for each path
					//deposit pheromone based on fitness of cluster
					//update pheromone level
				}
			}
		}
		return bestClusters;									//return the best clustering list
	}
}
