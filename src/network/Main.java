package network;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Main {
	public static void main(String args[]) {
		ArrayList<Sample> samples = new ArrayList<Sample>();			//create list of samples to use - data set essentially
		int numInputs = 0;
		int numDataPoints = 0;

		numDataPoints = 0;
		try {
			Scanner s = new Scanner(new File("data.txt"));							//create a new scanner, checks lines of data in file
			while (s.hasNextLine()) {												//loop while there is another line
				String line = s.nextLine();											//grab the next line
				ArrayList<Double> inputs = new ArrayList<Double>();					//create an arraylist for the inputs
				int counter = 0;													//counter to determine size of input array
				Scanner lineScan = new Scanner(line);								//create new scanner that checks individual pieces of a line
				lineScan.useDelimiter(",");										//separates tokens with a comma, space (as they were inputted that way)
				while (lineScan.hasNext()) {										//loop while there are still tokens to be read
					counter++;														//update counter (1 more input)
					double element = Double.parseDouble(lineScan.next());
					inputs.add(element);				//parse the token to be a double and add to the input arraylist
				}														//update counter to reflect input size (total - 1, since last token is output
				counter--;
				double[] passIn = new double[counter];								//this is the array that will be passed to sample class
				for (int i = 0; i < counter; i++) {
					passIn[i] = inputs.get(i);										//initialize the input array
				}
				samples.add(new Sample(passIn, inputs.get(counter)));				//create new sample with input array, output, and add that sample to the list of samples
				numInputs = passIn.length;
				numDataPoints ++;
				lineScan.close();
			}
			s.close();
			System.out.println("Number of Inputs: " + numInputs);
			System.out.println("Number of Data Points: " + numDataPoints);
		}
		catch (Exception e) {
			System.out.println("File not found.");
		}

		int numHidLayers = 2;
		int numHidNodes = 20;
		int numOutputs = 1;
		int hiddenActivation = 2; //sigmoidal
		int outputActivation = 1; //linear for function approximation
		int popSize = 50;
		
		Scanner in = new Scanner(System.in);
		System.out.println("What algorithm do you want to use?");
		System.out.println("\t1)Backprop"); 
		System.out.println("\t2)Genetic Algorithm");
		System.out.println("\t3)Evolution Strategies");
		int selection = in.nextInt();
		
		switch(selection){
			case 1:
				//create network and train with backprop
				Network network = new Network(numInputs, numHidLayers, numHidNodes, numOutputs, hiddenActivation, outputActivation);
				trainWithBackprop(network, samples);
				break;
			case 2:
				//create initial population of networks and train with genetic algorithm
				ArrayList<Network> population1 = new ArrayList<Network>(); //initial population
				for(int i = 0; i < popSize; i++)
					population1.add(new Network(numInputs, numHidLayers, numHidNodes, numOutputs, hiddenActivation, outputActivation));
				trainWithGA(population1, samples);
				break;
			case 3:	
				//create initial population of networks and train with evolution strategies
				ArrayList<Network> population2 = new ArrayList<Network>(); //initial population
				for(int i = 0; i < popSize; i++)
					population2.add(new Network(numInputs, numHidLayers, numHidNodes, numOutputs, hiddenActivation, outputActivation));
				trainWithES(population2, samples, popSize);
		}

	}
	
	//Train a network on the given list of samples with backpropagation
	public static void trainNetwork(Network network, List<Sample> samples) {
		for(int j = 0; j < 20; j++){
			double error = 0;
			for(int k = j*(samples.size()/20); k < (j+1)*(samples.size()/20); k++) {
				error += network.train(samples.get(k).getInputs(), samples.get(k).getOutput());
			}
			System.out.println("\tAverage Error: " + (error/(samples.size()/20)));
		}
	}
	
	//train a network with backpropagation and evaluate with 5x2 cross validation
	public static void trainWithBackprop(Network network, ArrayList<Sample> samples) {
		//5x2 cross validation
		double averageError = 0;	//error over all trials
		for(int i = 0; i < 10; i+=2){
			network.reset();
			Collections.shuffle(samples);

			//train network with first half of samples
			trainNetwork(network, samples.subList(0, samples.size()/2));
			
			//evaluate network on second half of samples
			double error = network.evaluate(samples.subList(samples.size()/2, samples.size()));
			System.out.println("Average Error of Trial " + (i+1) + ": " + (error) + "\n");
			averageError += error;
			
			network.reset();
			Collections.shuffle(samples);

			
			//train network with second half of samples
			trainNetwork(network, samples.subList(samples.size()/2, samples.size()));

			//evaluate network on first half of samples
			error = network.evaluate(samples.subList(0, samples.size()/2));
			System.out.println("Average Error of Trial " + (i+2) + ": " + (error) + "\n");
			averageError += error;
		}
		//average error of all trials
		averageError = averageError / 10;
		System.out.println("Average Error of 10 Trials: " + averageError);
	}
	
	public static void evaluatePopulation(ArrayList<Network> population, List<Sample> samples, int genNum){
		double averageFitness = 0;
		double bestFitness = Double.MAX_VALUE;
		for(Network network : population) {	//iterate through population of networks
			double fitness = network.evaluate(samples);
			network.setFitness(fitness);	//set the fitness of each to the average error of the test set
			averageFitness += fitness;		//add together each fitness of population
			if(fitness < bestFitness)	//find best fit individual - lowest average error
				bestFitness = fitness;
		}
		averageFitness /= population.size();	//calculate average fitness of population
		System.out.println("Best Fitness of Generation    " + (genNum) + ": " + bestFitness);
		System.out.println("Average Fitness of Generation " + (genNum) + ": " + averageFitness + "\n");
		System.out.println();
	}
	
	//train a population of networks with a genetic algorithm and evaluate
	public static void trainWithGA(ArrayList<Network> population, ArrayList<Sample> samples) {
		int genNum = 1;
		for(int i = 0; i < 100; i++){	//a new generation is created every iteration
			evaluatePopulation(population, samples.subList(0,  samples.size()/2), genNum);
			genNum++;
			Collections.sort(population);	//sort networks by fitness from highest average error to lowest -- worst to best
			for(int j = 0; j < population.size(); j++)	//give worst network a fitness of 1, the next a fitness of 2, and so on
				population.get(j).setFitness(j+1);		//the best individual ends up with the highest fitness
			
			population = GeneticAlgorithm.createNextGeneration(population);	//create a new population of offspring
		}
	}
	
	public static void trainWithES(ArrayList<Network> population, ArrayList<Sample> samples, int popSize){
		int genNum = 1;
		for(int i = 0; i < 100; i++){
			evaluatePopulation(population, samples.subList(0,  samples.size()/2), genNum);	//calculate the fitness of the population
			
			Collections.sort(population);	//sort population by fitness
			Collections.reverse(population);	//reverse order to go from best to worst
			ArrayList<Network> bestPop = new ArrayList<Network>();
			for(int j = 0; j < popSize; j++){
				bestPop.add(population.get(j));	//select the best individuals
			}
			
			bestPop.addAll(EvolutionStrategies.createNextGeneration(bestPop));	//add offspring to population
			population = bestPop;	//set population to best individuals + offspring
			
			genNum++;
		}
	}
}