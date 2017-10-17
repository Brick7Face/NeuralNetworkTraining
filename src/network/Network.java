package network;

import java.util.ArrayList;
import java.util.Random;

public class Network {
	private Random random = new Random();
	private ArrayList<Layer> layers;
	private double learningRate;

	/*
	 * Create an MLP network
	 * @param numInputs: number of input nodes
	 * @param numHidLayers: number of hidden layers
	 * @param numHidNodes: number of nodes in hidden layers
	 * @param numOutputs: number of output nodes
	 * @param actFun: type of activation function for nodes
	 */
	public Network(int numInputs, int numHidLayers, int numHidNodes, int numOutputs, int actFun, double learningRate) {
		layers = new ArrayList<Layer>();
		//create input layer with inputs number of nodes and a linear activation function
		layers.add(new Layer(numInputs, 1));
		
		//create hidden layers with hidNode number of nodes and given activation function
		for(int i = 0; i < numHidLayers; i++) {
			layers.add(new Layer(numHidNodes, actFun));
		}
		
		//create output layer with outputs number of nodes, node type of 0, and linear activation function
		layers.add(new Layer(numOutputs, 1));
		
		//add connections between layers
		for(int i = 0; i < layers.size()-1; i++) {
			for(int j = 0; j < layers.get(i).size(); j++) {
				for(int k = 0; k < layers.get(i+1).size(); k++) {
					double weight = (random.nextDouble()*2)-1;
					layers.get(i).getNeuron(j).addWeight(weight);
				}
			}
		}
		
		this.learningRate = learningRate;
	}

	//Randomly reset weights in network
	public void reset(){	
		for(int i = 0; i < layers.size()-1; i++) {
			for(int j = 0; j < layers.get(i).size(); j++) {
				for(int k = 0; k < layers.get(i+1).size(); k++) {
					double weight = (random.nextDouble()*2)-1;
					layers.get(i).getNeuron(j).setWeightTo(k, weight);
				}
			}
		}
	}

	/*
	 * Backpropogates through the network, updating all weights based on the output of the network
	 * @param output: the expected output of network and nodes
	 */
	public void backprop(double output){
		//adjust all weights for MLP
		ArrayList<Double> oldDeltas = new ArrayList<Double>();
		for(int i = layers.size()-1; i > 0; i--) {
			ArrayList<Double> deltas = new ArrayList<Double>();
	
			for(int j = 0; j < layers.get(i).size(); j++) {	//iterate through output neurons
				Neuron outNeuron = layers.get(i).getNeuron(j);
				double delta;
				if(i == layers.size()-1) {	//updating output layer
					delta = output-outNeuron.getOutput()*outNeuron.derivActivate();
				}
				else {	//updating hidden layers
					delta = 0;
					for(int k = 0; k < oldDeltas.size(); k++) {
						delta += oldDeltas.get(k)*outNeuron.getWeightTo(k);
					}
					delta *= outNeuron.derivActivate();
				}
				deltas.add(delta);
				
				for(int k = 0; k < layers.get(i-1).size(); k++) {	//iterate through previous layer neurons
					Neuron inNeuron = layers.get(i-1).getNeuron(k);	
					double weight = inNeuron.getWeightTo(j);								
					weight += learningRate*delta*inNeuron.getOutput();
					layers.get(i-1).getNeuron(k).setWeightTo(j, weight);
				}
			}
			oldDeltas = deltas;
		}
	}
	
	public void calcOutputs(double[] inputs) {
		//initialize input layer
		for(int i = 0; i < layers.get(0).size(); i++){
			layers.get(0).getNeuron(i).setOutput(inputs[i]);
		}
		
		//calculate output
		for(int i = 1; i < layers.size(); i++) {
			for(int j = 0; j < layers.get(i).size(); j++){
				ArrayList<Double> ins = new ArrayList<Double>();	//inputs to the neuron
				ArrayList<Double> weights = new ArrayList<Double>();//corresponding weights to the neuron
				for(int k = 0; k < layers.get(i-1).size(); k++) {
					ins.add(layers.get(i-1).getNeuron(k).getOutput());
					weights.add(layers.get(i-1).getNeuron(k).getWeightTo(j));
				}
				layers.get(i).getNeuron(j).calculate(ins, weights);
			}
		}
	}

	/*
	 * Trains the neural network
	 * @param inputs: an array which stores the input values of a Rosenbrock function
	 * @param output: stores the output value from the Rosenbrock function with given x values
	 */
	public double train(double inputs[], double output){
		calcOutputs(inputs);

		//calculate error and back propagate
		double actualOutput = layers.get(layers.size()-1).getNeuron(0).getOutput();
		double error = Math.abs(actualOutput - output);
		backprop(output);
		return error;	//return absolute error
	}

	public double evaluate(double inputs[], double output){		
		calcOutputs(inputs);
		
		double actualOutput = layers.get(layers.size()-1).getNeuron(0).getOutput();
		double error = Math.abs(actualOutput - output);
		return error;	//return absolute error
	}

	//prints out information about network
	public void printNetwork(){
		for(Layer l : layers) {
			l.printLayer(layers.indexOf(l)+1);
		}
	}
}

