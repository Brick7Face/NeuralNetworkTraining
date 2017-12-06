package network;


public class Ant {
	private Cluster thisCluster;
	private double pheromone;

	public Ant(Cluster c) {
		thisCluster = c;
		pheromone = Math.random();											
	}

	public double calcPheromone(Cluster c) {
		double counter = 0;
		double total = 0;
		double ave = 0;
		for (int i = 0; i < c.getMembers().size(); i++) {
			for (int j = 0; j < c.getMembers().size(); j++) {
				total += c.getMembers().get(i).calcDistance(c.getMembers().get(j));				//calc distance to each point
				counter++;	
			}
		}
		ave = total / counter;										//then calculate the average distance between points

		//now give fitness value based on average
		double normalizedAve = 1 / (1 + Math.exp(-ave));			//normalize the average to be between 0 and 1 with sigmoidal function
		pheromone = 1 + normalizedAve;								//assign a fitness value based on that average distance
		return pheromone;
	}
	
	public Cluster getCluster() {
		return thisCluster;
	}
	
	public void setCluster(Cluster c) {
		thisCluster = c;
	}
}
