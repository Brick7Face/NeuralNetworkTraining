package network;

import java.util.ArrayList;
import java.util.List;
/*
 * 
	Initialize k centroids u randomly
	Do
		for all data points x in samples
		assign a label to the data point x (the label is the nearest cluster centroid u)
		assign x to the cluster cu
		Calculate new centroids based on new clusters
			Centroid is the geometric mean of the points that have that centroids label. If a centroid has no points belonging to it, re-initialize it
	until no change in centroids 1-k
	return centroids
 */
public class KMeans {
	private ArrayList<Cluster> oldClusters;
	private ArrayList<Cluster> clusters;
	private int iterations;
	private int k;
	private int numFeatures;
	
	public KMeans(ArrayList<DataPoint> dataSet, int k) {
		this.k = k;
		this.clusters = new ArrayList<>();		//Create new arraylist of clusters
		this.numFeatures = dataSet.get(0).getNumFeatures();		//get number of features in datapoint to create random centroids with the same number of features
		for(int i = 0; i < 5; i++) {							//Initially doing 5 clusters, with random centroids
			Cluster newCluster = new Cluster(numFeatures, dataSet);		//Create new cluster with random centroid
			this.clusters.add(newCluster);							//add cluster to clusters
		}
		
		ArrayList<Cluster> oldClusters = new ArrayList<>();		//Keep track of old clusters to check to see if they chagne between iterations
		this.iterations = 0;
	}
	
	public ArrayList<Cluster> cluster(ArrayList<DataPoint> dataSet) {
		while(shouldStop(oldClusters, clusters, iterations) == false) {
			oldClusters = clusters;								//Set old clusters to current clusters
			iterations++;										//Increment iterations
			
			setLabels(dataSet, clusters);	//Set labels of each data point in data set to the centroids
			
			clusters = getCentroids(clusters);			//Generate new centroids based on data point connected to the centroids and return the clusters
		}
		return clusters;
	}
	
	//Returns true or false if k-means is done.
	public boolean shouldStop(ArrayList<Cluster> oldClusters, ArrayList<Cluster> clusters, int iterations) {
		if(oldClusters == clusters)
			return oldClusters == clusters;	//TODO
		else if(iterations == 500) {
			for(int i = 0; i < clusters.size(); i++) {
				for(int j = 0; j < numFeatures; j++) {
					System.out.printf("%.2f", clusters.get(i).getCenter().getFeature(j));
					System.out.print("	");
				}
				System.out.println("");
			}
			return true;
		}
		return false;
	}
	
	//Sets, and returns a label for each piece of data in the set
	public void setLabels(ArrayList<DataPoint> dataSet, ArrayList<Cluster> clusters) {
		int selectedCluster = 0;													//Current cluster
		for(int i = 0; i < dataSet.size(); i++) {									
			Cluster nearestCluster = new Cluster(numFeatures);						//Create temporary cluster with the specified number of features
			for(int j = 0; j < k; j++) {											//for each cluster
				if(getDistanceTo(dataSet.get(i), clusters.get(j)) < getDistanceTo(dataSet.get(i), nearestCluster) && getDistanceTo(dataSet.get(i), nearestCluster) != 0) {		//if distance to next cluster is closer than current nearestCluster, change them
					nearestCluster = clusters.get(j);								//Set nearestCluster to the new closer cluster
					selectedCluster = j;											//Set the selectedCluster integer to whatever iteration the for loop is in
					dataSet.get(i).setLabel(nearestCluster);						//Add the label to the datapoint
				}
			}
			clusters.get(selectedCluster).addPoint(dataSet.get(i));					//Add the datapoint to the clusters members
		}
	}
	
	//Generates new centroids based on data points connected to each centroid and returns the centroids
	public ArrayList<Cluster> getCentroids(ArrayList<Cluster> clusters) {
		ArrayList<Cluster> centroids = new ArrayList<>();				//Create temporary centroids
		for(int i = 0; i < clusters.size(); i++) {						//for each cluster
			Cluster currentCluster = clusters.get(i);					//Set current cluster to i
			currentCluster = clusters.get(i).updateCenter(numFeatures);		//Update current cluster using updateCenter and the number of features
			centroids.add(currentCluster);									//Add updated cluster to temporary centroids
		}
		return centroids;
	}
	
	//Returns distance from point to cluster centroid
	public double getDistanceTo(DataPoint point, Cluster cluster) {
		double distance = 0;
		DataPoint clusterCenter = cluster.getCenter();
		for(int i = 0; i < point.getNumFeatures(); i++) {
			double difference = clusterCenter.getFeature(i) - point.getFeature(i);
			difference = Math.pow(difference, 2);
			distance += difference;
		}
		distance = Math.sqrt(distance);
		return distance;
	}
}
	
